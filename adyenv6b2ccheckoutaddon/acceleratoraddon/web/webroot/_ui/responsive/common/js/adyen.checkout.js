var AdyenCheckout = (function () {
    var setBrandCode = function (brandCode) {
        $("#brandCode").val(brandCode);
    };

    var updateCardType = function(cardType, friendlyName) {
        $(".cse-cardtype").removeClass("cse-cardtype-style-active");

        if(cardType == "unknown") {
            return;
        }

        var activeCard = document.getElementById('cse-card-' + cardType);
        if(activeCard != null) {
            activeCard.className = "cse-cardtype cse-cardtype-style-active cse-cardtype-" + cardType;
        }
    };

    var enableCardTypeDetection = function(allowedCards, cardLogosContainer, encryptedForm) {
        var cardTypesHTML = "";
        for (var i = allowedCards.length; i-- > 0;) {
            cardTypesHTML = cardTypesHTML + getCardSpan(allowedCards[i]);
        }

        cardLogosContainer.innerHTML = cardTypesHTML;

        encryptedForm.addCardTypeDetection(updateCardType);
    };

    var getCardSpan = function(type) {
        return "<span id=\"cse-card-" + type + "\" class=\"cse-cardtype cse-cardtype-style-active cse-cardtype-" + type + "\"></span>";
    };

    var getCardNumber = function() {
        var creditCardNumberElement = document.getElementById('creditCardNumber');
        return creditCardNumberElement.value.replace(/ /g, '');
    };

    var isAllowedCard = function(allowedCards) {
        var creditCardNumber = getCardNumber();
        var cardType = adyen.cardTypes.determine(creditCardNumber);

        return (cardType.cardtype != null && allowedCards.indexOf(cardType.cardtype) != -1);
    };

    var validateForm = function() {
        var paymentMethod = $('input[type=radio][name=paymentMethod]:checked').val();

        //Check if it is a valid card and encrypt
        if (paymentMethod == "adyen_cc") {
            if(!AdyenCheckout.isAllowedCard(allowedCards)) {
                alert('This credit card is not allowed');
                return false;
            }

            var encryptedData = encryptedForm.encrypt();
            if(encryptedData == null) {
                alert('This credit card is not valid');
                return false;
            }

            $("#cseToken").val(encryptedData);
        }

        return true;
    };

    var createForm = function() {
        // The form element to encrypt.
        var form = document.getElementById('adyen-encrypted-form');

        // Form and encryption options. See adyen.encrypt.simple.html for details.
        var options = {};
        options.cvcIgnoreBins = '6703'; // Ignore CVC for BCMC

        // Create the form.
        // Note that the method is on the adyen object, not the adyen.encrypt object.
        var encryptedForm = adyen.createEncryptedForm(form, options);

        return encryptedForm;
    };

    return {
        setBrandCode: setBrandCode,
        enableCardTypeDetection: enableCardTypeDetection,
        isAllowedCard: isAllowedCard,
        validateForm: validateForm,
        createForm: createForm
    }
})();
