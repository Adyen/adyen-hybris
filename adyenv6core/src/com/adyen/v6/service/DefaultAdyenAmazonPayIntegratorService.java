package com.adyen.v6.service;

import com.adyen.v6.enums.AmazonpayEnvironment;
import com.adyen.v6.enums.AmazonpayRegion;
import com.amazon.pay.api.AmazonPayResponse;
import com.amazon.pay.api.PayConfiguration;
import com.amazon.pay.api.WebstoreClient;
import com.amazon.pay.api.exceptions.AmazonPayClientException;
import com.amazon.pay.api.types.Environment;
import com.amazon.pay.api.types.Region;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.store.services.BaseStoreService;
import org.apache.log4j.Logger;
import org.apache.logging.log4j.util.Strings;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.util.Assert;
import org.springframework.util.ResourceUtils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;


/**
 * {@inheritDoc}
 */
public class DefaultAdyenAmazonPayIntegratorService implements AdyenAmazonPayIntegratorService {

    private static final Logger LOGGER = Logger.getLogger(DefaultAdyenAmazonPayIntegratorService.class);

    protected final BaseStoreService baseStoreService;

    public DefaultAdyenAmazonPayIntegratorService(final BaseStoreService baseStoreService) {
        this.baseStoreService = baseStoreService;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getAmazonPayTokenByCheckoutSessionId(final String checkoutSessionId) {
        Assert.hasText(checkoutSessionId,"Amazonpaytoken cannot be retrieved since the checkoutSessionId is null");

        final BaseStoreModel currentBaseStore = baseStoreService.getCurrentBaseStore();
        Assert.notNull(currentBaseStore,"Amazonpaytoken cannot be retrieved since the current baseStore is null");

        final AmazonpayEnvironment amazonpayEnvironment = currentBaseStore.getAmazonpayEnvironment();
        final AmazonpayRegion amazonpayRegion = currentBaseStore.getAmazonpayRegion();
        final String amazonpayPublicKey = currentBaseStore.getAmazonpayPublicKey();

        Assert.hasText(amazonpayPublicKey,"Amazonpaytoken cannot be retrieved since the amazonpay public key configuration is not set on the current baseStore");
        Assert.notNull(amazonpayRegion,"Amazonpaytoken cannot be retrieved since the amazonpay region configuration is not set on the current baseStore");
        Assert.notNull(amazonpayEnvironment,"Amazonpaytoken cannot be retrieved since the amazonpay environment configuration is not set on the current baseStore");

        final PayConfiguration payConfiguration;
        try {
            payConfiguration = new PayConfiguration()
                    .setPublicKeyId(amazonpayPublicKey)
                    .setRegion(Region.valueOf(amazonpayRegion.getCode()))
                    .setPrivateKey(new String(Files.readAllBytes(ResourceUtils.getFile("classpath:certificates/amazonpay/DummyCertificate.pem").toPath())).toCharArray())
                    .setEnvironment(Environment.valueOf(amazonpayEnvironment.getCode()));
        } catch (AmazonPayClientException e) {
            LOGGER.error("The AmazonPayConfiguration cannot be created, please, review the amazonpay configuration set on the baseStore as well as the private key",e);
            return Strings.EMPTY;
        } catch (FileNotFoundException e) {
            LOGGER.error("The AmazonPayCertificate.pem file cannot be found under /resources/certificates/amazonpay/AmazonPayCertificate.pm path",e);
            return Strings.EMPTY;
        } catch (IOException e) {
            LOGGER.error("The AmazonPayCertificate.pem file cannot be readed, please provide a valid private key under /resources/certificates/amazonpay/AmazonPayCertificate.pm path",e);
            return Strings.EMPTY;
        }
        try {
            final WebstoreClient webstoreClient = new WebstoreClient(payConfiguration);
            final AmazonPayResponse amazonPayResponse = webstoreClient.getCheckoutSession(checkoutSessionId);
            final JSONObject response = amazonPayResponse.getResponse();
            return (String) response.get("amazonPayToken");
        } catch (AmazonPayClientException e) {
            LOGGER.error("The AmazonPayToken cannot be found given the " + checkoutSessionId + "there were an error during the API Call to get the checkoutSession data" ,e);
        } catch (JSONException e) {
            LOGGER.error("The amazonPayToken is not on the given session",e);
        }
        return Strings.EMPTY;
    }
}
