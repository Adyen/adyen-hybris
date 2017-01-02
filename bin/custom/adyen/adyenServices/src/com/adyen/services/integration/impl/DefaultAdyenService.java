/**
 *
 */
package com.adyen.services.integration.impl;

import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.cms2.servicelayer.services.CMSSiteService;
import de.hybris.platform.commerceservices.strategies.GenerateMerchantTransactionCodeStrategy;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.payment.AdyenPaymentInfoModel;
import de.hybris.platform.core.model.order.payment.PaymentInfoModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.payment.dto.TransactionStatus;
import de.hybris.platform.payment.dto.TransactionStatusDetails;
import de.hybris.platform.payment.enums.PaymentTransactionType;
import de.hybris.platform.payment.model.AdyenPaymentTransactionEntryModel;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.model.ModelService;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.zip.GZIPOutputStream;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ObjectWriter;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;

import com.adyen.services.impl.DefaultAdyenPaymentService;
import com.adyen.services.integration.AdyenService;
import com.adyen.services.integration.data.PaymentMethods;
import com.adyen.services.integration.data.ResultCode;
import com.adyen.services.integration.data.request.AdyenListRecurringDetailsRequest;
import com.adyen.services.integration.data.request.AdyenModificationRequest;
import com.adyen.services.integration.data.request.AdyenPaymentRequest;
import com.adyen.services.integration.data.response.AdyenListRecurringDetailsResponse;
import com.adyen.services.integration.data.response.AdyenModificationResponse;
import com.adyen.services.integration.data.response.AdyenPaymentResponse;
import com.adyen.services.integration.exception.AdyenIs3DSecurityPaymentException;


/**
 * @author Kenneth Zhou
 *
 */
public class DefaultAdyenService implements AdyenService
{
	private static final Logger LOG = Logger.getLogger(DefaultAdyenPaymentService.class);
	private ObjectMapper objectMapper;
	private ConfigurationService configurationService;
	private GenerateMerchantTransactionCodeStrategy generateMerchantTransactionCodeStrategy;
	private ModelService modelService;
	private CommonI18NService commonI18NService;
	private CartService cartService;
	private CMSSiteService cmsSiteService;

	/*
	 * (non-Javadoc)
	 *
	 * @see com.adyen.services.integration.AdyenPaymentService#requestRecurringPaymentDetails(com.adyen.services.data.
	 * integration .request.AdyenListRecurringDetailsRequest)
	 */
	@Override
	public AdyenListRecurringDetailsResponse requestRecurringPaymentDetails(final AdyenListRecurringDetailsRequest request)
	{
		final String requestUrl = configurationService.getConfiguration()
				.getString("integration.adyen.requestRecurringPaymentDetails.url");

		try
		{


			final HttpPost httpRequest = new HttpPost(requestUrl);
			final ObjectWriter ow = getObjectMapper().writer();
			getObjectMapper().setSerializationInclusion(Inclusion.NON_NULL);
			final String json = ow.writeValueAsString(request);
			LOG.info("Sending Recurring Request:" + json);
			httpRequest.setHeader("Content-Type", "application/json");
			httpRequest.setEntity(new StringEntity(json, "UTF-8"));
			final HttpResponse httpResponse = callAdyenAPI(httpRequest, cmsSiteService.getCurrentSite());
			final String recurringResponse = EntityUtils.toString(httpResponse.getEntity(), "UTF-8");
			LOG.info("Receiving Recurring Response:" + recurringResponse);
			getObjectMapper().configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);

			return getObjectMapper().readValue(recurringResponse, AdyenListRecurringDetailsResponse.class);

		}
		catch (final JsonParseException e)
		{
			LOG.error(e);
			return null;
		}
		catch (final JsonMappingException e)
		{
			LOG.error(e);
			return null;
		}
		catch (final IOException e)
		{
			LOG.error(e);
			return null;
		}
	}

	@Override
	public AdyenPaymentTransactionEntryModel authorise(final PaymentTransactionModel transaction,
			final AdyenPaymentRequest request, final boolean is3DSecure)
	{
		final String key = (is3DSecure) ? "integration.adyen.authorise.3d.url" : "integration.adyen.authorise.url";
		final String requestUrl = configurationService.getConfiguration().getString(key);

		try
		{
			final HttpPost httpRequest = new HttpPost(requestUrl);
			final ObjectWriter ow = getObjectMapper().writer();
			//getObjectMapper().configure(DeserializationConfig.Feature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true);
			final String json = ow.writeValueAsString(request);
			LOG.info("Sending Authorise Request:" + json);
			httpRequest.setHeader("Content-Type", "application/json");
			httpRequest.setEntity(new StringEntity(json, "UTF-8"));
			final HttpResponse httpResponse = callAdyenAPI(httpRequest, cmsSiteService.getCurrentSite());
			final String response = EntityUtils.toString(httpResponse.getEntity(), "UTF-8");
			LOG.info("Receiving Authorise Response:" + response);
			final AdyenPaymentResponse paymentResponse = getObjectMapper().readValue(response, AdyenPaymentResponse.class);

			final AdyenPaymentTransactionEntryModel entry = (AdyenPaymentTransactionEntryModel) this.modelService
					.create(AdyenPaymentTransactionEntryModel.class);


			entry.setTime(new Date());
			entry.setPaymentTransaction(transaction);
			entry.setRequestId(paymentResponse.getPspReference());

			if (httpResponse.getStatusLine().getStatusCode() != 200)
			{
				final String faultString = paymentResponse.getErrorType() + " " + paymentResponse.getErrorCode() + " "
						+ paymentResponse.getMessage();
				LOG.error(faultString);
				entry.setAdyenErrorCode(paymentResponse.getErrorCode());
				entry.setAdyenMessage(paymentResponse.getMessage());
				entry.setTransactionStatus("ERROR");
				return entry;
			}
			else
			{
				transaction.setRequestId(paymentResponse.getPspReference());
				transaction.setPaymentProvider("Adyen");
				this.modelService.save(transaction);
				entry.setCode(getNewEntryCode(transaction));
				if (ResultCode.RedirectShopper.name().equals(paymentResponse.getResultCode().name()))
				{
					throw new AdyenIs3DSecurityPaymentException(paymentResponse);
				}

				entry.setTransactionStatus((paymentResponse.getResultCode() != null) ? paymentResponse.getResultCode().name() : "");
				entry.setTransactionStatusDetails(paymentResponse.getRefusalReason());
				entry.setAdyenAuthCode(paymentResponse.getAuthCode());

				final CartModel cartModel = getCartService().getSessionCart();
				if (cartModel != null)
				{
					entry.setCurrency(cartModel.getCurrency());
					entry.setAmount(new BigDecimal(cartModel.getTotalPrice().doubleValue()));
				}
				if ((ResultCode.Authorised.name().equals(entry.getTransactionStatus()))
						|| (ResultCode.Received.name().equals(entry.getTransactionStatus())))
				{
					transaction.setOrder(cartModel);
					this.modelService.save(transaction);
					if (is3DSecure)
					{
						entry.setType(PaymentTransactionType.AUTHORIZATION_REQUESTED_3DSECURE);
					}
					else
					{
						entry.setType(PaymentTransactionType.AUTHORIZATION_REQUESTED);
					}
				}
				else if (ResultCode.Refused.name().equals(entry.getTransactionStatus())
						&& !"Refused".equals(paymentResponse.getRefusalReason()))
				{
					if (StringUtils.isNotEmpty(paymentResponse.getRefusalReason()))
					{
						entry.setAdyenMessage(paymentResponse.getRefusalReason());
					}
					return entry;
				}

				String boletoPDFUrl = null;
				if (paymentResponse.getAdditionalData() != null
						&& paymentResponse.getAdditionalData().get("boletobancario.url") != null)
				{
					boletoPDFUrl = paymentResponse.getAdditionalData().get("boletobancario.url");
					entry.setAdyenBoloToPDFUrl(boletoPDFUrl);
					entry.setAdyenBoloToPDFUrl(paymentResponse.getAdditionalData().get("boletobancario.url"));
				}

				final PaymentInfoModel paymentInfo = cartModel.getPaymentInfo();
				{
					final AdyenPaymentInfoModel adyenPaymentInfoModel = (AdyenPaymentInfoModel) paymentInfo;
					adyenPaymentInfoModel.setAuthCode(paymentResponse.getAuthCode());
					adyenPaymentInfoModel.setAdyenBoletoPDFUrl(boletoPDFUrl);
					modelService.save(adyenPaymentInfoModel);
					this.modelService.refresh(adyenPaymentInfoModel);
				}
			}
			this.modelService.save(entry);
			this.modelService.refresh(transaction);
			return entry;
		}
		catch (final IOException e)
		{
			e.printStackTrace();
		}
		finally
		{
			//TODO remove security code from session
		}
		return null;
	}

	@Override
	public AdyenPaymentTransactionEntryModel cancelOrRefund(final PaymentTransactionModel transaction,
			final AdyenModificationRequest request)
	{
		final String requestUrl = (request.getModificationAmount() == null)
				? configurationService.getConfiguration().getString("integration.adyen.cancel.or.refund.url")
				: configurationService.getConfiguration().getString("integration.adyen.refund.url");
		try
		{
			final HttpResponse httpResponse = callAdyenModificationAPI(request, requestUrl,
					(CMSSiteModel) transaction.getOrder().getSite());
			final String response = EntityUtils.toString(httpResponse.getEntity(), "UTF-8");

			final String api = getApiName(requestUrl);
			LOG.info(String.format("%s payment Response: %s", api, response));

			final AdyenModificationResponse modiResponse = getObjectMapper().readValue(response, AdyenModificationResponse.class);

			if (AdyenModificationResponse.ADYEN_MODIFICATION_CANCEL_OR_REFUND_RESPONSE.equals(modiResponse.getResponse()))
			{
				return createCancelOrRefundTransactionEntry(transaction, modiResponse,
						PaymentTransactionType.CANCEL_OR_REFUND_REQUESTED,
						new BigDecimal(transaction.getOrder().getTotalPrice().doubleValue()));
			}
			else if (AdyenModificationResponse.ADYEN_MODIFICATION_REFUND_RESPONSE.equals(modiResponse.getResponse()))
			{
				return createCancelOrRefundTransactionEntry(transaction, modiResponse, PaymentTransactionType.REFUND_REQUESTED,
						new BigDecimal(request.getModificationAmount().getValue().doubleValue() / 100));
			}
			else if (AdyenModificationResponse.ADYEN_MODIFICATION_CANCEL_RESPONSE.equals(modiResponse.getResponse()))
			{
				//todo
			}
			else
			{
				LOG.info(String.format("cancel order %s failed , response: %s", transaction.getOrder().getCode(),
						modiResponse.getResponse()));
			}
		}
		catch (final IOException e)
		{
			e.printStackTrace();
			LOG.error(e.getMessage());
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.adyen.services.integration.AdyenService#capturePayment(de.hybris.platform.payment.model.
	 * PaymentTransactionModel , com.adyen.services.integration.data.request.AdyenModificationRequest)
	 */
	@Override
	public AdyenModificationResponse capturePayment(final AdyenModificationRequest request, final CMSSiteModel site)
	{
		final String requestUrl = configurationService.getConfiguration().getString("integration.adyen.capture.url");
		try
		{
			final HttpResponse httpResponse = callAdyenModificationAPI(request, requestUrl, site);
			final String response = EntityUtils.toString(httpResponse.getEntity(), "UTF-8");
			LOG.info("Capture payment Response:" + response);
			final AdyenModificationResponse modiResponse = getObjectMapper().readValue(response, AdyenModificationResponse.class);
			return modiResponse;
		}
		catch (final IOException e)
		{
			e.printStackTrace();
			LOG.error(e.getMessage());
		}
		return null;
	}

	/**
	 * @param requestUrl
	 * @param response
	 */
	private String getApiName(final String requestUrl)
	{
		if (StringUtils.lastIndexOf(requestUrl, "/") != -1)
		{
			final String api = requestUrl.substring(StringUtils.lastIndexOf(requestUrl, "/") + 1, requestUrl.length());
			return api;
		}
		return null;
	}

	/**
	 * @param transaction
	 * @param modiResponse
	 * @return
	 */
	public AdyenPaymentTransactionEntryModel createCancelOrRefundTransactionEntry(final PaymentTransactionModel transaction,
			final AdyenModificationResponse modiResponse, final PaymentTransactionType type, final BigDecimal amount)
	{
		final AdyenPaymentTransactionEntryModel entry = (AdyenPaymentTransactionEntryModel) this.modelService
				.create(AdyenPaymentTransactionEntryModel.class);
		entry.setTime(new Date());
		entry.setRequestId(modiResponse.getPspReference());
		entry.setType(type);
		entry.setTransactionStatus(TransactionStatus.ACCEPTED.name());
		entry.setTransactionStatusDetails(TransactionStatusDetails.SUCCESFULL.name());
		entry.setCurrency(transaction.getOrder().getCurrency());
		entry.setAmount(amount);
		entry.setCode(getNewEntryCode(transaction));
		entry.setPaymentTransaction(transaction);
		this.modelService.save(entry);
		this.modelService.refresh(transaction);
		return entry;
	}

	@Override
	public PaymentMethods directory()
	{
		try
		{
			final CMSSiteModel site = getCmsSiteService().getCurrentSite();
			final String apiUrl = getConfigurationService().getConfiguration().getString("integration.adyen.hpp.directory.url");
			final String hmacKey = site.getAdyenHmacKey();

			// Generate date
			final Calendar calendar = Calendar.getInstance();
			calendar.add(Calendar.DATE, 1);
			final Date sessionDate = calendar.getTime(); // current date + 1 day

			// Define variables
			final CartModel cart = getCartService().getSessionCart();
			final String merchantReference = cart.getCode();
			final String paymentAmount = new BigDecimal(100).multiply(new BigDecimal(cart.getTotalPrice().toString())).intValue()
					+ "";
			final String currencyCode = cart.getCurrency().getIsocode();
			final String skinCode = site.getAdyenSkinCode();
			final String merchantAccount = site.getAdyenMerchantAccount();
			final String sessionValidity = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX").format(sessionDate);

			final String countryCode = getCountryCode();

			// Calculate merchant signature
			final String signingString = paymentAmount + currencyCode + merchantReference + skinCode + merchantAccount
					+ sessionValidity;

			String merchantSig;
			merchantSig = calculateHMAC(hmacKey, signingString);

			// Set HTTP Post variables
			final List<NameValuePair> postParameters = new ArrayList<NameValuePair>();
			Collections.addAll(postParameters, new BasicNameValuePair("merchantReference", merchantReference),
					new BasicNameValuePair("paymentAmount", paymentAmount), new BasicNameValuePair("currencyCode", currencyCode),
					new BasicNameValuePair("skinCode", skinCode), new BasicNameValuePair("merchantAccount", merchantAccount),
					new BasicNameValuePair("sessionValidity", sessionValidity), new BasicNameValuePair("countryCode", countryCode),
					new BasicNameValuePair("merchantSig", merchantSig));

			/**
			 * Create HTTP Client (using Apache HttpComponents library) and send the request with the specified variables.
			 */
					final HttpPost httpPost = new HttpPost(apiUrl);
			httpPost.setEntity(new UrlEncodedFormEntity(postParameters));

			final HttpClient client = new DefaultHttpClient();
			LOG.info("request hpp payment methods:\n" + StringUtils.join(postParameters, "\n"));
			final HttpResponse httpResponse = client.execute(httpPost);
			final String result = EntityUtils.toString(httpResponse.getEntity(), "UTF-8");
			LOG.info("hpp payment methods:" + result);
			final ObjectMapper objectMapper = new ObjectMapper();
			return objectMapper.readValue(result, PaymentMethods.class);
		}
		catch (final Exception e)
		{
			LOG.error(e.getMessage(), e);
		}
		return null;
	}

	/**
	 * Computes the Base64 encoded signature using the HMAC algorithm with the SHA-1 hashing function.
	 */
	@Override
	public String calculateHMAC(final String hmacKey, final String signingString)
	{
		LOG.info("HMAC Source String : " + signingString);
		final SecretKeySpec keySpec = new SecretKeySpec(hmacKey.getBytes(), "HmacSHA1");
		Mac mac;
		try
		{
			mac = Mac.getInstance("HmacSHA1");
			mac.init(keySpec);

			final byte[] result = mac.doFinal(signingString.getBytes("UTF-8"));
			LOG.info("HMAC Target String : " + Base64.encodeBase64String(result));
			return Base64.encodeBase64String(result);
		}
		catch (final NoSuchAlgorithmException e)
		{
			e.printStackTrace();
		}
		catch (final InvalidKeyException e)
		{
			e.printStackTrace();
		}
		catch (final IllegalStateException e)
		{
			e.printStackTrace();
		}
		catch (final UnsupportedEncodingException e)
		{
			e.printStackTrace();
		}
		return "";
	}

	@Override
	public String compressString(final String input)
	{
		final ByteArrayOutputStream output = new ByteArrayOutputStream();
		GZIPOutputStream gzip;
		try
		{
			gzip = new GZIPOutputStream(output);
			gzip.write(input.getBytes("UTF-8"));
			gzip.close();
			output.close();
			return Base64.encodeBase64String(output.toByteArray());
		}
		catch (final IOException e)
		{
			e.printStackTrace();
		}
		return "";
	}

	/**
	 * @param request
	 * @param requestUrl
	 * @return
	 * @throws IOException
	 * @throws JsonGenerationException
	 * @throws JsonMappingException
	 * @throws UnsupportedEncodingException
	 * @throws ClientProtocolException
	 */
	private HttpResponse callAdyenModificationAPI(final AdyenModificationRequest request, final String requestUrl,
			final CMSSiteModel site) throws IOException, JsonGenerationException, JsonMappingException, UnsupportedEncodingException,
					ClientProtocolException
	{
		final HttpPost httpRequest = new HttpPost(requestUrl);
		final ObjectWriter ow = getObjectMapper().writer();
		final String json = ow.writeValueAsString(request);
		final String api = getApiName(requestUrl);
		LOG.info(String.format("Sending %s Request: %s", api, json));
		httpRequest.setHeader("Content-Type", "application/json");
		httpRequest.setEntity(new StringEntity(json, "UTF-8"));
		return callAdyenAPI(httpRequest, site);
	}


	private HttpResponse callAdyenAPI(final HttpPost httpRequest, final CMSSiteModel site)
			throws ClientProtocolException, IOException
	{
		final CredentialsProvider provider = new BasicCredentialsProvider();
		final UsernamePasswordCredentials adyenUsernamePasswordCredentials = new UsernamePasswordCredentials(
				site.getAdyenAPIAccount(), site.getAdyenAPIPassword());
		provider.setCredentials(AuthScope.ANY, adyenUsernamePasswordCredentials);

		final DefaultHttpClient client = new DefaultHttpClient();
		client.setCredentialsProvider(provider);
		return client.execute(httpRequest);
	}

	protected String getNewEntryCode(final PaymentTransactionModel transaction)
	{
		if (transaction.getEntries() == null)
		{
			return transaction.getCode() + "-1";
		}
		return transaction.getCode() + "-" + (transaction.getEntries().size() + 1);
	}
	/* ADY-115 start */


	@Override
	public String getCountryCode()
	{
		final CustomerModel user = (CustomerModel) cartService.getSessionCart().getUser();
		 final String isocode  = getCountryCode(user.getDefaultPaymentAddress());
		if (StringUtils.isNotEmpty(isocode))
		{
			return isocode;
		}
		else
		{
			return getCountryCode(cartService.getSessionCart().getDeliveryAddress());
		}
	}

	private String getCountryCode(final AddressModel addr)
	{
		if (addr != null && addr.getCountry() != null)
		{
			return addr.getCountry().getIsocode();
		}
		return null;
	}

	@Override
	public LinkedHashMap<String, String> getBillingAddressData()
	{
		final LinkedHashMap<String, String> addrData = new LinkedHashMap<String, String>();
		final CartModel cart = cartService.getSessionCart();
		final AddressModel addr = cart.getPaymentInfo().getBillingAddress();
		addrData.put("billingAddressStreet", addr.getStreetname());
		addrData.put("billingAddressHouseNumberOrName", addr.getStreetnumber());
		addrData.put("billingAddressCity", addr.getTown());
		addrData.put("billingAddressPostalCode", addr.getPostalcode());
		addrData.put("billingAddressStateOrProvince", "");
		addrData.put("billingAddressCountry", addr.getCountry().getIsocode());
		return addrData;
	}

	@Override
	public LinkedHashMap<String, String> getDeliveryAddrData()
	{
		final LinkedHashMap<String, String> addrData = new LinkedHashMap<String, String>();
		final CartModel cart = cartService.getSessionCart();
		final AddressModel addr = cart.getDeliveryAddress();
		addrData.put("deliveryAddressStreet", addr.getStreetname());
		addrData.put("deliveryAddressHouseNumberOrName", addr.getStreetnumber());
		addrData.put("deliveryAddressCity", addr.getTown());
		addrData.put("deliveryAddressPostalCode", addr.getPostalcode());
		addrData.put("deliveryAddressStateOrProvince", "");
		addrData.put("deliveryAddressCountry", addr.getCountry().getIsocode());
		return addrData;
	}

	/* ADY-115 end */

	/*
	 * (non-Javadoc)
	 *
	 * @see com.adyen.services.integration.AdyenService#buildOpenInvoiceDataSig(java.lang.String,
	 * java.util.LinkedHashMap)
	 */
	@Override
	public String buildOpenInvoiceDataSig(final String merchantSig, final LinkedHashMap<String, String> openInvoiceData,
			final String hmacKey)
	{
		String keyStr = "merchantSig";
		String valueStr = merchantSig;
		for (final String key : openInvoiceData.keySet())
		{
			keyStr = keyStr + ":" + key;
			valueStr = valueStr + ":" + openInvoiceData.get(key);
		}

		return calculateHMAC(hmacKey, keyStr + "|" + valueStr);
	}

	/**
	 * @return the objectMapper
	 */
	public ObjectMapper getObjectMapper()
	{
		return objectMapper;
	}



	/**
	 * @param objectMapper
	 *           the objectMapper to set
	 */
	public void setObjectMapper(final ObjectMapper objectMapper)
	{
		this.objectMapper = objectMapper;
	}

	/**
	 * @return the configurationService
	 */
	public ConfigurationService getConfigurationService()
	{
		return configurationService;
	}

	/**
	 * @param configurationService
	 *           the configurationService to set
	 */
	public void setConfigurationService(final ConfigurationService configurationService)
	{
		this.configurationService = configurationService;
	}

	/**
	 * @return the generateMerchantTransactionCodeStrategy
	 */
	public GenerateMerchantTransactionCodeStrategy getGenerateMerchantTransactionCodeStrategy()
	{
		return generateMerchantTransactionCodeStrategy;
	}

	/**
	 * @param generateMerchantTransactionCodeStrategy
	 *           the generateMerchantTransactionCodeStrategy to set
	 */
	public void setGenerateMerchantTransactionCodeStrategy(
			final GenerateMerchantTransactionCodeStrategy generateMerchantTransactionCodeStrategy)
	{
		this.generateMerchantTransactionCodeStrategy = generateMerchantTransactionCodeStrategy;
	}

	/**
	 * @return the modelService
	 */
	public ModelService getModelService()
	{
		return modelService;
	}

	/**
	 * @param modelService
	 *           the modelService to set
	 */
	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}

	/**
	 * @return the commonI18NService
	 */
	public CommonI18NService getCommonI18NService()
	{
		return commonI18NService;
	}

	/**
	 * @param commonI18NService
	 *           the commonI18NService to set
	 */
	public void setCommonI18NService(final CommonI18NService commonI18NService)
	{
		this.commonI18NService = commonI18NService;
	}

	/**
	 * @return the cartService
	 */
	public CartService getCartService()
	{
		return cartService;
	}

	/**
	 * @param cartService
	 *           the cartService to set
	 */
	public void setCartService(final CartService cartService)
	{
		this.cartService = cartService;
	}

	/**
	 * @return the cmsSiteService
	 */
	public CMSSiteService getCmsSiteService()
	{
		return cmsSiteService;
	}

	/**
	 * @param cmsSiteService
	 *           the cmsSiteService to set
	 */
	public void setCmsSiteService(final CMSSiteService cmsSiteService)
	{
		this.cmsSiteService = cmsSiteService;
	}




}
