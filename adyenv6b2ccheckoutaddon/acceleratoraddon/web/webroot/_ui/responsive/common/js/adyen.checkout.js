var AdyenCheckout = (function () {
    var setBrandCode = function (brandCode) {
        $("#brandCode").val(brandCode);
    };

    var updateCardTypeDetection = function(ev) {
        $(".cse-cardtype").removeClass("cse-cardtype-style-active");

        var creditCardNumber = getCardNumber();
        var cardType = adyen.cardTypes.determine(creditCardNumber);

        if(cardType == null) {
            return;
        }

        var activeCard = document.getElementById('cse-card-' + cardType.cardtype);
        if(activeCard != null) {
            activeCard.className = "cse-cardtype cse-cardtype-style-active cse-cardtype-" + cardType.cardtype;
        }
    };

    var addEvent = function(element, event, callback, capture) {
        if (typeof element.addEventListener === 'function') {
            element.addEventListener(event, callback, capture);
        } else if (element.attachEvent) {
            element.attachEvent('on' + event, callback);
        } else {
            throw new Error(encrypt.errors.UNABLETOBIND + ": Unable to bind " + event + "-event");
        }
    };

    var enableCardTypeDetection = function(allowedCards, cardLogosContainer) {
        var cardTypesHTML = "";
        for (var i = allowedCards.length; i-- > 0;) {
            cardTypesHTML = cardTypesHTML + getCardSpan(allowedCards[i]);
        }

        cardLogosContainer.innerHTML = cardTypesHTML;

        var creditCardNumberElement = document.getElementById('creditCardNumber');
        addEvent(creditCardNumberElement, 'change', updateCardTypeDetection, false);
        addEvent(creditCardNumberElement, 'input', updateCardTypeDetection, false);
        addEvent(creditCardNumberElement, 'keyup', updateCardTypeDetection, false);
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

    return {
        setBrandCode: setBrandCode,
        enableCardTypeDetection: enableCardTypeDetection,
        isAllowedCard: isAllowedCard
    }
})();
