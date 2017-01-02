/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2015 hybris AG
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of hybris
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with hybris.
 *
 *
 */
package com.adyen.core.integration;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.commerceservices.externaltax.impl.DefaultExternalTaxesService;
import de.hybris.platform.commerceservices.order.impl.DefaultCommerceCheckoutService;
import de.hybris.platform.commerceservices.service.data.CommerceCheckoutParameter;
import de.hybris.platform.core.Registry;
import de.hybris.platform.core.enums.CreditCardType;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.payment.CreditCardPaymentInfoModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.order.DeliveryModeService;
import de.hybris.platform.order.InvalidCartException;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.site.BaseSiteService;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.store.services.BaseStoreService;
import com.adyen.core.externaltax.impl.AcceleratorDetermineExternalTaxStrategy;
import com.adyen.core.externaltax.mock.MockCalculateExternalTaxesStrategy;

import java.util.Collection;

import javax.annotation.Resource;

import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;


/**
 * Integration test with an mock external tax service.
 * 
 */
@IntegrationTest
public class CheckoutWithExternalTaxesIntegrationTest extends ServicelayerTransactionalTest
{

	private static final String TEST_BASESITE_UID = "testSite";
	private static final String TEST_BASESTORE_UID = "testStore";

	@Resource
	private DefaultCommerceCheckoutService defaultCommerceCheckoutService;

	@Resource
	private BaseSiteService baseSiteService;

	@Resource
	private BaseStoreService baseStoreService;

	@Resource
	private UserService userService;

	@Resource
	private ModelService modelService;

	@Resource
	private CommonI18NService commonI18NService;

	@Resource
	private DeliveryModeService deliveryModeService;

	@Resource
	private DefaultExternalTaxesService defaultExternalTaxesService;

	@BeforeClass
	public static void beforeClass()
	{
		Registry.setCurrentTenantByID("junit");
	}

	@Before
	public void setUp() throws Exception
	{
		importCsv("/test/testCheckoutExternalTaxes.csv", "utf-8");
		baseSiteService.setCurrentBaseSite(baseSiteService.getBaseSiteForUID(TEST_BASESITE_UID), false);
		defaultExternalTaxesService.setDecideExternalTaxesStrategy(new AcceleratorDetermineExternalTaxStrategy());
		defaultExternalTaxesService.setCalculateExternalTaxesStrategy(new MockCalculateExternalTaxesStrategy());
	}

	@Test
	public void testCheckoutNetStore() throws InvalidCartException
	{
		//final CatalogVersionModel catalogVersionModel = catalogVersionService.getCatalogVersion("testCatalog", "Online");
		//final ProductModel productModel = productService.getProductForCode(catalogVersionModel, "HW1210-3423");
		//final UnitModel unitModel = unitService.getUnitForCode("pieces");
		final UserModel ahertz = userService.getUserForUID("ahertz");
		final Collection<CartModel> cartModels = ahertz.getCarts();
		final BaseStoreModel store = baseStoreService.getBaseStoreForUid(TEST_BASESTORE_UID);
		store.setNet(true);
		modelService.save(store);
		Assert.assertEquals(cartModels.size(), 1);
		final CartModel cart = cartModels.iterator().next();

		Assert.assertEquals(Boolean.FALSE, cart.getCalculated());
		Assert.assertTrue(cart.getDeliveryAddress() == null);
		Assert.assertEquals(Double.valueOf(0), cart.getTotalTax());
		Assert.assertEquals(Double.valueOf(0), cart.getTotalPrice());

		//set delivery address on cart
		final AddressModel addressModel = new AddressModel();
		addressModel.setBillingAddress(Boolean.FALSE);
		addressModel.setCountry(commonI18NService.getCountry("US"));
		addressModel.setStreetname("streetName");
		addressModel.setStreetnumber("streetNumber");
		addressModel.setPostalcode("postalCode");
		addressModel.setTown("town");
		addressModel.setFirstname("firstName");
		addressModel.setLastname("lastName");
		addressModel.setOwner(ahertz);
		modelService.save(addressModel);

		final CommerceCheckoutParameter parameter1 = new CommerceCheckoutParameter();
		parameter1.setCart(cart);
		parameter1.setAddress(addressModel);
		parameter1.setIsDeliveryAddress(true);
		defaultCommerceCheckoutService.setDeliveryAddress(parameter1);
		Assert.assertEquals(Boolean.TRUE, cart.getCalculated());
		Assert.assertEquals(addressModel, cart.getDeliveryAddress());
		Assert.assertEquals(Double.valueOf(0), cart.getTotalTax());
		Assert.assertThat(cart.getTotalPrice(), Matchers.greaterThan(Double.valueOf(0)));
		Double previousPrice = cart.getTotalPrice();

		//set delivery mode
		final CommerceCheckoutParameter parameter2 = new CommerceCheckoutParameter();
		parameter2.setDeliveryMode(deliveryModeService.getDeliveryModeForCode("premium-gross"));
		parameter2.setCart(cart);
		defaultCommerceCheckoutService.setDeliveryMode(parameter2);
		Assert.assertEquals(Boolean.TRUE, cart.getCalculated());
		Assert.assertEquals(addressModel, cart.getDeliveryAddress());
		Assert.assertThat(cart.getTotalTax(), Matchers.not(Matchers.equalTo(Double.valueOf(0))));
		Assert.assertNotSame(cart.getTotalPrice(), Matchers.not(Matchers.equalTo(previousPrice)));
		previousPrice = cart.getTotalPrice();

		//set payment method
		final CommerceCheckoutParameter parameter3 = new CommerceCheckoutParameter();
		final CreditCardPaymentInfoModel paymentInfo = new CreditCardPaymentInfoModel();
		paymentInfo.setBillingAddress(addressModel);
		paymentInfo.setCode("1234");
		paymentInfo.setOwner(ahertz);
		paymentInfo.setSubscriptionId("1234");
		paymentInfo.setType(CreditCardType.VISA);
		paymentInfo.setValidToMonth("01");
		paymentInfo.setValidToYear("18");
		paymentInfo.setSubscriptionValidated(true);
		paymentInfo.setCcOwner("owner");
		paymentInfo.setNumber("4111111111111111");
		paymentInfo.setUser(ahertz);
		parameter3.setPaymentInfo(paymentInfo);
		parameter3.setCart(cart);
		defaultCommerceCheckoutService.setPaymentInfo(parameter3);
		Assert.assertEquals(Boolean.TRUE, cart.getCalculated());
		Assert.assertEquals(addressModel, cart.getDeliveryAddress());
		Assert.assertThat(cart.getTotalTax(), Matchers.not(Matchers.equalTo(Double.valueOf(0))));
		Assert.assertEquals(previousPrice, cart.getTotalPrice());
		previousPrice = cart.getTotalPrice();

		//place order
		final CommerceCheckoutParameter parameter4 = new CommerceCheckoutParameter();
		parameter4.setCart(cart);
		defaultCommerceCheckoutService.placeOrder(parameter4);

	}
}
