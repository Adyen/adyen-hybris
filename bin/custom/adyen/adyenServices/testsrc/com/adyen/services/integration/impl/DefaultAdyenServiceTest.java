/**
 *
 */
package com.adyen.services.integration.impl;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.servicelayer.config.ConfigurationService;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import junit.framework.Assert;

import org.apache.commons.configuration.BaseConfiguration;
import org.apache.commons.configuration.Configuration;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.DefaultHttpClient;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Before;
import org.junit.Test;

import com.adyen.services.integration.data.AdditionalData;
import com.adyen.services.integration.data.AmountData;
import com.adyen.services.integration.data.ContractType;
import com.adyen.services.integration.data.RecurringData;
import com.adyen.services.integration.data.request.AdyenListRecurringDetailsRequest;
import com.adyen.services.integration.data.request.AdyenPaymentRequest;
import com.adyen.services.integration.data.response.AdyenListRecurringDetailsResponse;


/**
 * @author Kenneth Zhou
 * 
 */
@UnitTest
public class DefaultAdyenServiceTest
{

	private DefaultAdyenService adyenService;
	private ConfigurationService configurationService;

	private AdyenListRecurringDetailsRequest listRecurringDetailsRequest;
	private RecurringData recurringData;
	private ObjectMapper objectMapper;
	private Configuration config;
	private DefaultHttpClient httpClient;
	private CredentialsProvider provider;

	private final String requestRecurringPaymentDetailsUrl = "https://pal-test.adyen.com/pal/servlet/Recurring/v12/listRecurringDetails";
	private final String authoriseUrl = "https://pal-test.adyen.com/pal/servlet/Payment/v12/authorise";

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception
	{
		adyenService = new DefaultAdyenService();
		configurationService = mock(ConfigurationService.class);

		provider = new BasicCredentialsProvider();
		final UsernamePasswordCredentials credentials = new UsernamePasswordCredentials("ws@Company.Sceneric",
				"#/A/xJxyD=e@7NF#7DHp#%9tW");
		provider.setCredentials(AuthScope.ANY, credentials);
		httpClient = new DefaultHttpClient();
		httpClient.setCredentialsProvider(provider);
		objectMapper = new ObjectMapper();

		adyenService.setConfigurationService(configurationService);
		adyenService.setObjectMapper(objectMapper);

		listRecurringDetailsRequest = new AdyenListRecurringDetailsRequest();
		recurringData = new RecurringData();
		recurringData.setContract(ContractType.RECURRING.name());
		listRecurringDetailsRequest.setMerchantAccount("ScenericCOM");
		listRecurringDetailsRequest.setShopperReference("kenneth.zhou@sceneric.com");
		listRecurringDetailsRequest.setRecurring(recurringData);
		config = new BaseConfiguration();
		config.setProperty("integration.adyen.requestRecurringPaymentDetails.url", requestRecurringPaymentDetailsUrl);
		config.setProperty("integration.adyen.authorise.url", authoriseUrl);
	}

	@Test
	public void test()
	{
		when(configurationService.getConfiguration()).thenReturn(config);
		final AdyenListRecurringDetailsResponse response = adyenService.requestRecurringPaymentDetails(listRecurringDetailsRequest);
		Assert.assertNotNull(response);
	}

	@Test
	public void testAuthorise()
	{
		when(configurationService.getConfiguration()).thenReturn(config);

		final AdyenPaymentRequest paymentRequest = new AdyenPaymentRequest();
		final AmountData amount = new AmountData();
		amount.setCurrency("EUR");
		amount.setValue(new Integer(199));
		paymentRequest.setAmount(amount);
		/* number:5555444433331111 cvc:737 Expiration Month :06 Expiration Month :2016 cardHolder: John Doe */
		final AdditionalData additionalData = new AdditionalData();
		additionalData
				.setCardEncryptedJson("adyenjs_0_1_2$O4HF1RYxSxBUYb+U8X3iM/e3asV1b1FvrNEh5S2eEHXOr2NWXS+cQ1oCVs3/G73tyZEYHh/1YSUEuAs16X29G2UqTF5lnAqg55XTBTQfVvMMQQaezzL/djwUEJHSGM6vDtI3Ql5GXhNEwx82wfbZELhM5tToui357cmvb0TTaPlNdOZ36JO9go5Jtt2zj57hcWkfsV4lOGq32kkOyH8MNRXp8t/pDGKoXwQrY3HWRS1HkabSRmbmgfUIkl/0XtTvZlYqiKlokdVJn89SzSK6N1oA/FL0/bbLzqPjpF4Uz4AjunLKjUNzuTfcGbXTUs1dka9FbYuxKXT9DXMyL7+eJw==$cUcMoA+Jx9CfzvD2uIrRaf/6kywY77UF7gPrNUGiLPNmJnm5AObW1jxVjrT23vVvUZQLrwmEe6p4TKyTEW4OAfQoYNhfvd5ed/IBb/f9LaKmnSyfRo+2mVd0ZlxsaGrA/LfPJVs3UKFwDPLXYGuuxG+b++oNbcmekNVSZlULrmt6drDJvU39krcWxTPzj9N6xcTM4rtRoUnaWzKr2MzaO4mCSTfkGafVkw==");
		paymentRequest.setReference("PAYMENT-"
				+ new SimpleDateFormat("yyyy-MM-dd").format(new Date() + "-" + UUID.randomUUID().toString()));
		paymentRequest.setAdditionalData(additionalData);
		paymentRequest.setShopperEmail("test@example.com");
		paymentRequest.setShopperIP("123.123.123.123");
		paymentRequest.setMerchantAccount("ScenericCOM");
		paymentRequest.setShopperReference("YourReference");
		paymentRequest.setFraudOffset(new Integer(0));

		//final AdyenPaymentTransactionEntryModel entry = null;//= adyenService.authorise(paymentRequest);
		Assert.assertNotNull(null);
	}

}
