
<adyen:adyenLibrary dfUrl="${dfUrl}" showDefaultCss="${true}"/>
<script type="text/javascript">

    const {
        AdyenCheckout, Dropin, Card, PayPal, GooglePay,
        ApplePay, CashAppPay, Sepa, Redirect, OnlineBankingIN,
        OnlineBankingPL, Ideal, EPS, Pix, WalletIN, AfterPay, Bcmc,
        Pos, PayBright, Boleto, SepaDirectDebit, RatePay, Paytm, Giftcard, Blik
    } = AdyenWeb;

    <c:if test="${not empty allowedCards}">
    //Set the allowed cards
    const allowedCards = [];
    <c:forEach items="${allowedCards}" var="allowedCard">
    allowedCards.push("${allowedCard}");
    </c:forEach>

    const initConfig = {
        shopperLocale: "${shopperLocale}",
        environment: "${environmentMode}",
        clientKey: "${clientKey}",
        session: {
            id: "${sessionData.id}",
            sessionData: "${sessionData.sessionData}",
        }
    };
    const paymentMethodConfigs = {};

    /**
     * Generate array of available payment methods to initialize
     */
    paymentMethodConfigs['createCard'] = {
        allowedCards,
        showRememberDetails: ${showRememberTheseDetails},
        cardHolderNameRequired: ${cardHolderNameRequired}
    }

    const adyenCheckout = new AdyenCheckoutHelper();

    <c:if test="${sepadirectdebit}">
    paymentMethodConfigs['createSepaDirectDebit'] = null;
    </c:if>

    <c:if test="${not empty issuerLists['ideal']}">
    paymentMethodConfigs['createIdeal'] = ${issuerLists['ideal']};
    </c:if>

    <c:if test="${not empty issuerLists['onlinebanking_IN']}">
    paymentMethodConfigs['createOnlinebankingIN'] = null;
    </c:if>

    <c:if test="${not empty issuerLists['onlineBanking_PL']}">
    paymentMethodConfigs['createOnlineBankingPL'] = null;
    </c:if>

    <c:if test="${not empty issuerLists['eps']}">
    paymentMethodConfigs['createEps'] = ${issuerLists['eps']};
    </c:if>

    <c:if test="${not empty issuerLists['pix']}">
    paymentMethodConfigs['createPix'] = {
        label: null,
        issuers: ${issuerLists['pix']}
    };
    </c:if>

    <c:forEach var="paymentMethod" items="${paymentMethods}">

    <c:if test="${paymentMethod.type eq 'wallet_IN'}">
    paymentMethodConfigs['createWalletIN'] = null;
    </c:if>
    //TO-DO Refactor the code to get a returnUrl service and add another endpoint to manage the result
    //Adding here paytm payment method, there is already an createPaytm function defined on the adyen.checkout.js
    <c:if test="${paymentMethod.type eq 'afterpay_default'}">
    paymentMethodConfigs['createAfterPay'] = "${countryCode}";
    </c:if>

    <c:if test="${paymentMethod.type eq 'bcmc'}">
    paymentMethodConfigs['createBcmc'] = "${countryCode}";
    </c:if>

    </c:forEach>

    let storedCardJS;

    <c:forEach items="${storedCards}" var="storedCard">

    storedCardJS = {
        storedPaymentMethodId: "${storedCard.id}",
        name: "${storedCard.name}",
        type: "${storedCard.type}",
        brand: "${storedCard.brand}",
        lastFour: "${storedCard.lastFour}",
        expiryMonth: "${storedCard.expiryMonth}",
        expiryYear: "${storedCard.expiryYear}",
        holderName: "${storedCard.holderName}",
        supportedShopperInteractions: "${storedCard.supportedShopperInteractions}",
        shopperEmail: "${storedCard.shopperEmail}"
    };

    if (paymentMethodConfigs['createOneClickCard']) {
        paymentMethodConfigs['createOneClickCard'].push(storedCardJS);
    } else {
        paymentMethodConfigs['createOneClickCard'] = [storedCardJS];
    }
    </c:forEach>

    adyenCheckout.initiateCheckout(initConfig, paymentMethodConfigs);
    </c:if>

    //Handle form submission
    $(".submit_silentOrderPostForm").click(function () {
        if (!adyenCheckout.validateForm()) {
            return false;
        }
        adyenCheckout.setCustomPaymentMethodValues();
        adyenCheckout.addRiskData();

        $("#adyen-encrypted-form").submit();
    });

    <c:if test="${not empty selectedPaymentMethod}">
    adyenCheckout.togglePaymentMethod("${selectedPaymentMethod}");
    $('input[type=radio][name=paymentMethod][value="${selectedPaymentMethod}"]').prop("checked", true);
    </c:if>

    // Toggle payment method specific areas (credit card form and issuers list)
    $('input[type=radio][name=paymentMethod]').change(function () {
        adyenCheckout.togglePaymentMethod(this.value);
    });

    adyenCheckout.createDobDatePicker("p_method_adyen_hpp_dob");
    adyenCheckout.createDfValue();
</script>