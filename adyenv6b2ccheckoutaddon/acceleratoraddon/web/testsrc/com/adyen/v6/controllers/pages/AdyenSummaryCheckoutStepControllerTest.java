package com.adyen.v6.controllers.pages;

import com.adyen.model.checkout.PaymentsDetailsResponse;
import com.adyen.model.checkout.PaymentsResponse;
import com.adyen.v6.facades.AdyenCheckoutFacade;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.order.OrderFacade;
import de.hybris.platform.commercefacades.order.data.OrderData;
import de.hybris.platform.commerceservices.strategies.CheckoutCustomerStrategy;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;

import static com.adyen.v6.constants.AdyenControllerConstants.CART_PREFIX;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class AdyenSummaryCheckoutStepControllerTest {

    private static final String PAYLOAD = "payload";
    private static final String PAYLOAD_VALUE = "value";
    private static final String REDIRECT_RESULT = "redirectResult";
    private static final String MERCHANT_REFERENCE = "merchantReference";
    private static final String REDIRECT_PREFIX = "redirect:";
    private static final String REDIRECT_URL_ORDER_CONFIRMATION = REDIRECT_PREFIX + "/checkout/orderConfirmation/";

    @InjectMocks
    private AdyenSummaryCheckoutStepController testObj;
    public static final String ORDER_CODE = "orderCode";

    @Mock
    private AdyenCheckoutFacade adyenCheckoutFacadeMock;
    @Mock
    private OrderFacade orderFacadeMock;
    @Mock
    private CheckoutCustomerStrategy checkoutCustomerStrategyMock;

    @Mock
    private HttpServletRequest requestMock;
    @Mock
    private PaymentsDetailsResponse responseMock;
    @Mock
    private RedirectAttributes redirectModelMock;
    @Mock
    private OrderData orderDataMock;

    @Test
    public void handleRedirectPayload_shouldReturnResponse_whenRequestContainsPayload() throws Exception {
        final HashMap<String, String> details = new HashMap<>();
        details.put(REDIRECT_RESULT, PAYLOAD_VALUE);

        when(requestMock.getParameter(PAYLOAD)).thenReturn(PAYLOAD_VALUE);
        when(responseMock.getMerchantReference()).thenReturn(MERCHANT_REFERENCE);
        when(responseMock.getResultCode()).thenReturn(PaymentsResponse.ResultCodeEnum.AUTHORISED);
        when(orderDataMock.getCode()).thenReturn(ORDER_CODE);
        when(adyenCheckoutFacadeMock.handleRedirectPayload(details)).thenReturn(responseMock);
        when(orderFacadeMock.getOrderDetailsForCodeWithoutUser(responseMock.getMerchantReference())).thenReturn(orderDataMock);
        when(checkoutCustomerStrategyMock.isAnonymousCheckout()).thenReturn(false);

        final String result =testObj.handleAdyenResponse(requestMock, redirectModelMock);

        assertThat(result).isEqualTo(REDIRECT_URL_ORDER_CONFIRMATION + orderDataMock.getCode());
    }

    @Test
    public void handleRedirectPayload_shouldThrowException_whenRequestNotContainsPayload() throws Exception {
        when(requestMock.getParameter(PAYLOAD)).thenReturn(null);

       final String result = testObj.handleAdyenResponse(requestMock, redirectModelMock);

       assertThat(result).isEqualTo(REDIRECT_PREFIX + CART_PREFIX);
    }
}
