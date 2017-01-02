/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2014 hybris AG
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of hybris
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with hybris.
 *
 *  
 */
package com.adyen.fulfilmentprocess.test;

import static junit.framework.Assert.assertTrue;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commerceservices.order.CommerceCheckoutService;
import de.hybris.platform.core.Registry;
import de.hybris.platform.core.enums.CreditCardType;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.order.payment.DebitPaymentInfoModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.cronjob.model.CronJobModel;
import de.hybris.platform.deliveryzone.model.ZoneDeliveryModeModel;
import de.hybris.platform.deliveryzone.model.ZoneDeliveryModeValueModel;
import de.hybris.platform.deliveryzone.model.ZoneModel;
import de.hybris.platform.fraud.events.OrderFraudEmployeeNotificationEvent;
import de.hybris.platform.order.CalculationService;
import de.hybris.platform.order.CartService;
import de.hybris.platform.order.InvalidCartException;
import de.hybris.platform.order.exceptions.CalculationException;
import de.hybris.platform.orderprocessing.events.OrderFraudCustomerNotificationEvent;
import de.hybris.platform.orderprocessing.events.OrderPlacedEvent;
import de.hybris.platform.orderprocessing.model.OrderProcessModel;
import de.hybris.platform.payment.PaymentService;
import de.hybris.platform.payment.commands.factory.CommandFactory;
import de.hybris.platform.payment.commands.factory.impl.DefaultCommandFactoryRegistryImpl;
import de.hybris.platform.payment.dto.CardInfo;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import de.hybris.platform.processengine.BusinessProcessService;
import de.hybris.platform.processengine.enums.ProcessState;
import de.hybris.platform.processengine.model.BusinessProcessModel;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.servicelayer.cronjob.CronJobService;
import de.hybris.platform.servicelayer.event.EventService;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.internal.model.ServicelayerJobModel;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;
import de.hybris.platform.servicelayer.type.TypeService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.site.BaseSiteService;
import de.hybris.platform.spring.ctx.ScopeTenantIgnoreDocReader;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.testframework.TestUtils;
import de.hybris.platform.util.Config;
import de.hybris.platform.util.Utilities;
import com.adyen.fulfilmentprocess.constants.AdyenFulfilmentProcessConstants;
import com.adyen.fulfilmentprocess.test.events.TestEventListenerCountingEvents;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Currency;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import junit.framework.Assert;
import junit.framework.AssertionFailedError;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.io.ClassPathResource;


/**
 * Integration test which test flow of order-process process when fraudCheck node return FRAUD
 */
@Ignore
//Not a good test for CI
@IntegrationTest
public class FraudCheckIntegrationTest extends ServicelayerTest
{
	@Resource
	protected BaseSiteService baseSiteService;
	@Resource
	protected CommerceCheckoutService commerceCheckoutService;
	@Resource
	protected ModelService modelService;
	@Resource
	protected UserService userService;
	@Resource
	protected ProductService productService;
	@Resource
	protected CartService cartService;
	@Resource
	protected PaymentService paymentService;
	@Resource
	protected CalculationService calculationService;
	@Resource
	protected EventService eventService;
	@Resource
	protected CronJobService cronJobService;
	@Resource
	protected BusinessProcessService businessProcessService;
	@Resource
	protected FlexibleSearchService flexibleSearchService;
	@Resource
	protected TypeService typeService;
	@Resource
	protected CommonI18NService commonI18NService;
	@Resource
	protected OrderFraudCustomerEventListener listenerOrderFraudCustomerNotificationEvent;
	@Resource
	protected OrderPlacedEventListener listenerOrderPlacedEvent;
	@Resource
	protected OrderFraudEmployeeEventListener listenerOrderFraudEmployeeNotificationEvent;

	public static class OrderFraudCustomerEventListener extends
			TestEventListenerCountingEvents<OrderFraudCustomerNotificationEvent>
	{
	}

	public static class OrderPlacedEventListener extends TestEventListenerCountingEvents<OrderPlacedEvent>
	{
	}

	public static class OrderFraudEmployeeEventListener extends
			TestEventListenerCountingEvents<OrderFraudEmployeeNotificationEvent>
	{
	}

	private static final Logger LOG = Logger.getLogger(FraudCheckIntegrationTest.class);

	protected OrderModel order = null;
	protected CronJobModel cronJob = null;

	protected static final long MAX_WAITING_TIME = 60;

	protected static final long ONE_SECOND = 1000;

	/**
	 * Load bean definition from adyenfulfilmentprocess-spring-test-fraudcheck.xml
	 */
	@BeforeClass
	public static void beforeClass()
	{
		Registry.activateStandaloneMode();
		Utilities.setJUnitTenant();
		LOG.debug("Preparing...");

		final ApplicationContext appCtx = Registry.getApplicationContext();
		assertTrue("Application context of type " + appCtx.getClass() + " is not a subclass of "
				+ ConfigurableApplicationContext.class, appCtx instanceof ConfigurableApplicationContext);

		final ConfigurableApplicationContext applicationContext = (ConfigurableApplicationContext) appCtx;
		final ConfigurableListableBeanFactory beanFactory = applicationContext.getBeanFactory();
		assertTrue("Bean Factory of type " + beanFactory.getClass() + " is not of type " + BeanDefinitionRegistry.class,
				beanFactory instanceof BeanDefinitionRegistry);
		final XmlBeanDefinitionReader xmlReader = new XmlBeanDefinitionReader((BeanDefinitionRegistry) beanFactory);
		xmlReader.setDocumentReaderClass(ScopeTenantIgnoreDocReader.class);
		xmlReader.loadBeanDefinitions(new ClassPathResource(
				"/adyenfulfilmentprocess/test/adyenfulfilmentprocess-spring-test-fraudcheck.xml"));
		final DefaultCommandFactoryRegistryImpl commandFactoryReg = appCtx.getBean(DefaultCommandFactoryRegistryImpl.class);
		commandFactoryReg.setCommandFactoryList(Arrays.asList((CommandFactory) appCtx.getBean("mockupCommandFactory")));
	}


	/**
	 * revert changes made {@link #beforeClass()}
	 */
	@AfterClass
	public static void afterClass()
	{
		LOG.debug("cleanup...");


		final ApplicationContext appCtx = Registry.getApplicationContext();

		assertTrue("Application context of type " + appCtx.getClass() + " is not a subclass of "
				+ ConfigurableApplicationContext.class, appCtx instanceof ConfigurableApplicationContext);

		final ConfigurableApplicationContext applicationContext = (ConfigurableApplicationContext) appCtx;
		final ConfigurableListableBeanFactory beanFactory = applicationContext.getBeanFactory();
		assertTrue("Bean Factory of type " + beanFactory.getClass() + " is not of type " + BeanDefinitionRegistry.class,
				beanFactory instanceof BeanDefinitionRegistry);

		//cleanup command factory
		final Map<String, CommandFactory> commandFactoryList = applicationContext.getBeansOfType(CommandFactory.class);
		commandFactoryList.remove("mockupCommandFactory");
		final DefaultCommandFactoryRegistryImpl commandFactoryReg = appCtx.getBean(DefaultCommandFactoryRegistryImpl.class);
		commandFactoryReg.setCommandFactoryList(commandFactoryList.values());
	}

	protected void registerEvents()
	{
		eventService.registerEventListener(listenerOrderFraudCustomerNotificationEvent);
		eventService.registerEventListener(listenerOrderPlacedEvent);
		eventService.registerEventListener(listenerOrderFraudEmployeeNotificationEvent);
	}

	protected void unregisterEvents()
	{
		eventService.unregisterEventListener(listenerOrderFraudCustomerNotificationEvent);
		eventService.unregisterEventListener(listenerOrderPlacedEvent);
		eventService.unregisterEventListener(listenerOrderFraudEmployeeNotificationEvent);
	}

	protected static final String PARAM_NAME_MIN_PERIOD_WAITING_FOR_CLEANUP_IN_SECONDS = "adyenfulfilmentprocess.fraud.minPeriodWaitingForCleanUpInSeconds";
	protected static final String PARAM_NAME_FRAUD_SCORE_LIMIT = "adyenfulfilmentprocess.fraud.scoreLimit";
	protected static final String PARAM_NAME_FRAUD_SCORE_TOLERANCE = "adyenfulfilmentprocess.fraud.scoreTolerance";


	private Integer minPeriodWaitingForCleanUpInSecondsOldValue = null;
	private Integer fraudScoreLimitOldValue = null;
	private Integer fraudScoreToleranceOldValue = null;


	protected void revertOldConfigParameterValues()
	{
		if (minPeriodWaitingForCleanUpInSecondsOldValue != null)
		{
			Config.setParameter(PARAM_NAME_MIN_PERIOD_WAITING_FOR_CLEANUP_IN_SECONDS,
					Integer.toString(minPeriodWaitingForCleanUpInSecondsOldValue.intValue()));
			minPeriodWaitingForCleanUpInSecondsOldValue = null;
		}
		if (fraudScoreLimitOldValue != null)
		{
			Config.setParameter(PARAM_NAME_FRAUD_SCORE_LIMIT, Integer.toString(fraudScoreLimitOldValue.intValue()));
			fraudScoreLimitOldValue = null;
		}
		if (fraudScoreToleranceOldValue != null)
		{
			Config.setParameter(PARAM_NAME_FRAUD_SCORE_TOLERANCE, Integer.toString(fraudScoreToleranceOldValue.intValue()));
			fraudScoreToleranceOldValue = null;
		}
	}

	protected void setFraudScoreLimit(final int newScoreLimit)
	{
		fraudScoreLimitOldValue = Integer.valueOf(Config.getParameter(PARAM_NAME_FRAUD_SCORE_LIMIT));
		Config.setParameter(PARAM_NAME_FRAUD_SCORE_LIMIT, Integer.toString(newScoreLimit));
	}

	protected void setFraudScoreTolerance(final int newScoreTolerance)
	{
		fraudScoreToleranceOldValue = Integer.valueOf(Config.getParameter(PARAM_NAME_FRAUD_SCORE_TOLERANCE));
		Config.setParameter(PARAM_NAME_FRAUD_SCORE_TOLERANCE, Integer.toString(newScoreTolerance));
	}

	protected void setMinPeriodWaitingForCleanUpConfigParam(final int numberOfSeconds)
	{
		minPeriodWaitingForCleanUpInSecondsOldValue = Integer.valueOf(Config
				.getParameter(PARAM_NAME_MIN_PERIOD_WAITING_FOR_CLEANUP_IN_SECONDS));
		Config.setParameter(PARAM_NAME_MIN_PERIOD_WAITING_FOR_CLEANUP_IN_SECONDS, Integer.toString(numberOfSeconds));
	}

	protected static int codeNo = 1;

	protected void placeTestOrder() throws InvalidCartException, CalculationException
	{
		final CartModel cart = cartService.getSessionCart();
		final UserModel user = userService.getCurrentUser();
		cartService.addNewEntry(cart, productService.getProductForCode("testProduct1"), 1, null);
		cartService.addNewEntry(cart, productService.getProductForCode("testProduct2"), 2, null);
		cartService.addNewEntry(cart, productService.getProductForCode("testProduct3"), 3, null);

		final AddressModel deliveryAddress = new AddressModel();
		deliveryAddress.setOwner(user);
		deliveryAddress.setFirstname("Der");
		deliveryAddress.setLastname("Buck");
		deliveryAddress.setTown("Muenchen");
		deliveryAddress.setCountry(commonI18NService.getCountry("DE"));
		modelService.save(deliveryAddress);

		final DebitPaymentInfoModel paymentInfo = new DebitPaymentInfoModel();
		paymentInfo.setOwner(cart);
		paymentInfo.setBank("MeineBank");
		paymentInfo.setUser(user);
		paymentInfo.setAccountNumber("34434");
		paymentInfo.setBankIDNumber("1111112");
		paymentInfo.setBaOwner("Ich");
		paymentInfo.setCode("testPaymentInfo1");
		modelService.save(paymentInfo);

		final ZoneDeliveryModeModel zoneDeliveryModeModel = new ZoneDeliveryModeModel();
		zoneDeliveryModeModel.setCode("free");
		zoneDeliveryModeModel.setNet(Boolean.TRUE);
		final ZoneDeliveryModeValueModel zoneDeliveryModeValueModel = new ZoneDeliveryModeValueModel();
		zoneDeliveryModeValueModel.setDeliveryMode(zoneDeliveryModeModel);
		zoneDeliveryModeValueModel.setValue(Double.valueOf(0.00));
		zoneDeliveryModeValueModel.setCurrency(commonI18NService.getCurrency("EUR"));
		zoneDeliveryModeValueModel.setMinimum(Double.valueOf(0.00));
		final ZoneModel zoneModel = new ZoneModel();
		zoneModel.setCode("de");
		zoneModel.setCountries(Collections.singleton(commonI18NService.getCountry("DE")));
		modelService.save(zoneModel);
		zoneDeliveryModeValueModel.setZone(zoneModel);
		modelService.save(zoneDeliveryModeModel);
		zoneDeliveryModeModel.setValues(Collections.singleton(zoneDeliveryModeValueModel));
		modelService.save(zoneDeliveryModeValueModel);
		modelService.save(zoneDeliveryModeModel);


		cart.setDeliveryMode(zoneDeliveryModeModel);
		cart.setDeliveryAddress(deliveryAddress);
		cart.setPaymentInfo(paymentInfo);

		final CardInfo card = new CardInfo();
		card.setCardType(CreditCardType.VISA);
		card.setCardNumber("4111111111111111");
		card.setExpirationMonth(Integer.valueOf(12));
		card.setExpirationYear(Integer.valueOf(Calendar.getInstance().get(Calendar.YEAR) + 2));
		card.setCv2Number("123");
		final PaymentTransactionModel paymentTransaction = paymentService.authorize("code3" + codeNo++, BigDecimal.ONE,
				Currency.getInstance("EUR"), deliveryAddress, deliveryAddress, card).getPaymentTransaction();

		cart.setPaymentTransactions(Collections.singletonList(paymentTransaction));
		modelService.save(cart);
		calculationService.calculate(cart);

		order = commerceCheckoutService.placeOrder(cart);
	}

	protected void addReallyBudGuyAndSetAsCurrentUser()
	{
		final CustomerModel user = new CustomerModel();
		user.setUid("bad.guy@gmail.com");
		modelService.save(user);
		userService.setCurrentUser(user);
	}

	protected void addNormalUserAndSetAsCurrentUser()
	{
		final CustomerModel user = new CustomerModel();
		user.setUid("average.customer");
		modelService.save(user);
		userService.setCurrentUser(user);
	}

	protected void createCronJob()
	{
		final ServicelayerJobModel jobModel = new ServicelayerJobModel();
		jobModel.setCode("cleanUpFraudOrderJobTest");
		jobModel.setSpringId("cleanUpFraudOrderJob");
		modelService.save(jobModel);

		cronJob = new CronJobModel();
		cronJob.setCode("cleanUpFraudOrderCronJobTest");
		cronJob.setJob(jobModel);
		cronJob.setSingleExecutable(Boolean.FALSE);
		modelService.save(cronJob);
	}

	/**
	 * Create core data, add default users, register events, create cron jobs
	 * 
	 * @throws Exception
	 */
	@Before
	public void before() throws Exception
	{
		createCoreData();
		createDefaultUsers();
		createDefaultCatalog();
		setupSite();
		registerEvents();
		createCronJob();
		if (Registry.getCurrentTenant().getTenantSpecificExtensionNames().contains("ticketsystem"))
		{
			importCsv("/adyenfulfilmentprocess/test/testTicketEssentialData.csv", "utf-8");
		}
		importCsv("/adyenfulfilmentprocess/test/testWarehouses.csv", "utf-8");
		TestUtils.disableFileAnalyzer("It can be some problems with order-process process concerned configuration");

		listenerOrderFraudCustomerNotificationEvent.resetCounter();
		listenerOrderPlacedEvent.resetCounter();
		listenerOrderFraudEmployeeNotificationEvent.resetCounter();
	}

	protected void setupSite()
	{
		final BaseStoreModel baseStore = modelService.create(BaseStoreModel.class);
		baseStore.setUid("testStore");
		modelService.save(baseStore);
		final BaseSiteModel baseSite = modelService.create(BaseSiteModel.class);
		baseSite.setUid("testSite");
		baseSite.setStores(Collections.singletonList(baseStore));
		modelService.save(baseSite);

		baseSiteService.setCurrentBaseSite(baseSite, false);
	}

	/**
	 * unregister events registered in {@link #before()}, and revert config parameter values which was changed during
	 * test
	 */
	@After
	public void after()
	{
		unregisterEvents();
		revertOldConfigParameterValues();
		cleanOrder();
		TestUtils.enableFileAnalyzer();
	}

	protected void cleanOrder()
	{
		if (order != null && !modelService.isRemoved(order))
		{
			modelService.remove(order);
		}
	}

	protected void csAgentMarkOrderAsNoFraudulentAndTriggerCleanUpEvent()
	{
		order.setFraudulent(Boolean.FALSE);
		order.setPotentiallyFraudulent(Boolean.FALSE);
		modelService.save(order);

		for (final BusinessProcessModel bpm : getAllPlaceOrderProcessWithCurrentActionWaitForCleanUp())
		{
			businessProcessService.triggerEvent(bpm.getCode() + "_CleanUpEvent");
		}

	}

	protected void csAgentMarkOrderAsNoFraudulentAndTriggerCSAOrderVerifiedEvent()
	{
		order.setFraudulent(Boolean.FALSE);
		order.setPotentiallyFraudulent(Boolean.FALSE);
		modelService.save(order);

		for (final BusinessProcessModel bpm : getAllPlaceOrderProcessWithCurrentActionWaitForManualOrderCheckCSA())
		{
			businessProcessService.triggerEvent(bpm.getCode() + "_CSAOrderVerified");
		}
	}

	protected void csAgentMarkOrderAsFraudulentAndTriggerCSAOrderVerifiedEvent()
	{
		order.setFraudulent(Boolean.TRUE);
		order.setPotentiallyFraudulent(Boolean.TRUE);
		modelService.save(order);

		for (final BusinessProcessModel bpm : getAllPlaceOrderProcessWithCurrentActionWaitForManualOrderCheckCSA())
		{
			businessProcessService.triggerEvent(bpm.getCode() + "_CSAOrderVerified");
		}
	}

	protected List<BusinessProcessModel> getAllPlaceOrderProcessWithCurrentActionWaitForCleanUp()
	{
		final String processCurrentAction = "waitForCleanUp";
		return getAllPlaceOrderProcessWithSpecificActionName(processCurrentAction);
	}

	protected List<BusinessProcessModel> getAllPlaceOrderProcessWithCurrentActionWaitForManualOrderCheckCSA()
	{
		final String processCurrentAction = "waitForManualOrderCheckCSA";
		return getAllPlaceOrderProcessWithSpecificActionName(processCurrentAction);
	}

	protected List<BusinessProcessModel> getAllPlaceOrderProcessWithSpecificActionName(final String processCurrentAction)
	{
		final String processDefinitionName = AdyenFulfilmentProcessConstants.ORDER_PROCESS_NAME;
		final String query = "select {bp.PK} " + "from {BusinessProcess AS bp  JOIN ProcessTask AS pt ON {bp.pk} = {pt.process} } "
				+ "WHERE {bp.processDefinitionName} = ?processDefinitionName and {pt.action} = ?processCurrentAction";

		final FlexibleSearchQuery searchQuery = new FlexibleSearchQuery(query);
		searchQuery.addQueryParameter("processDefinitionName", processDefinitionName);
		searchQuery.addQueryParameter("processCurrentAction", processCurrentAction);
		final SearchResult<BusinessProcessModel> processes = flexibleSearchService.search(searchQuery);
		return processes.getResult();
	}

	protected String submitOrderMock(final OrderModel order)
	{
		final OrderProcessModel businessProcessModel = (OrderProcessModel) businessProcessService.createProcess(
				AdyenFulfilmentProcessConstants.ORDER_PROCESS_NAME + order.getCode() + +System.currentTimeMillis(),
				AdyenFulfilmentProcessConstants.ORDER_PROCESS_NAME);
		businessProcessModel.setOrder(order);
		modelService.save(businessProcessModel);
		businessProcessService.startProcess(businessProcessModel);
		return businessProcessModel.getCode();
	}

	/**
	 * Test scenario: fraudCheckNode return FRAUD, but customer intervene and csAgent mark order as no fraudulent
	 * 
	 * @throws CalculationException
	 * @throws InvalidCartException
	 */

	@Test
	public void testUsersOrderIsEvaluatedAsFraudButUserIntervene() throws InvalidCartException, CalculationException
	{
		final int newScoreLimit = 20;
		final int newScoreTolerance = 30;
		setFraudScoreLimit(newScoreLimit);
		setFraudScoreTolerance(newScoreTolerance);

		addReallyBudGuyAndSetAsCurrentUser();
		placeTestOrder();
		final String processCode = submitOrderMock(order);

		final AbstractAssertionLooper looper = new AbstractAssertionLooper()
		{

			@Override
			protected void checkCondition()
			{
				Assert.assertEquals("Not received expected event OrderFraudCustomerNotificationEvent", 1,
						listenerOrderFraudCustomerNotificationEvent.getNumberOfEvents());
			}
		};

		looper.waitUntilConditionIsTrue(MAX_WAITING_TIME);

		modelService.refresh(order);
		Assert.assertTrue(Boolean.TRUE.equals(order.getFraudulent()));
		csAgentMarkOrderAsNoFraudulentAndTriggerCleanUpEvent();

		final AbstractAssertionLooper looperOrderPlacedEvent = new AbstractAssertionLooper()
		{
			@Override
			protected void checkCondition()
			{
				Assert.assertEquals("Not received expected event OrderPlacedEvent", 1, listenerOrderPlacedEvent.getNumberOfEvents());
			}
		};

		looperOrderPlacedEvent.waitUntilConditionIsTrue(MAX_WAITING_TIME);
		waitToTheEndOfProcess(processCode);
	}

	/**
	 * Test scenario: fraudCheck node return FRAUD, and after specific period of time order should have status CANCELLED
	 * 
	 * @throws InvalidCartException
	 * @throws CalculationException
	 * @throws InterruptedException
	 */
	@Test
	public void testUsersOrderIsEvaluatedAsFraudAndIsCleanedUp() throws InvalidCartException, CalculationException,
			InterruptedException
	{
		final int newScoreLimit = 20;
		final int newScoreTolerance = 30;
		setFraudScoreLimit(newScoreLimit);
		setFraudScoreTolerance(newScoreTolerance);

		addReallyBudGuyAndSetAsCurrentUser();
		placeTestOrder();
		submitOrderMock(order);

		AbstractAssertionLooper looper = new AbstractAssertionLooper()
		{
			@Override
			protected void checkCondition()
			{
				Assert.assertEquals("Not received expected event OrderFraudCustomerNotificationEvent", 0,
						listenerOrderFraudCustomerNotificationEvent.getNumberOfEvents());
			}
		};

		looper.waitUntilConditionIsTrue(MAX_WAITING_TIME);

		setMinPeriodWaitingForCleanUpConfigParam(3);

		looper = new AbstractAssertionLooper()
		{

			@Override
			protected void checkCondition()
			{
				modelService.refresh(order);
				Assert.assertEquals(OrderStatus.SUSPENDED, order.getStatus());

			}
		};

		looper.waitUntilConditionIsTrue(MAX_WAITING_TIME);

		looper = new AbstractAssertionLooper()
		{
			@Override
			protected void checkCondition()
			{
				cronJobService.performCronJob(cronJob, true);
				modelService.refresh(order);
				Assert.assertEquals("Incorrect order status", OrderStatus.CANCELLED, order.getStatus());
			}
		};

		looper.waitUntilConditionIsTrue(MAX_WAITING_TIME);
	}

	/** Test scenario: Users order is evaluated as a potential, but CS Agent decide that order is correct **/
	@Test
	public void testUsersOrderIsEvaluatedAsPotentialFraudButCsAgentDecideThatOrderIsCorrect() throws InvalidCartException,
			CalculationException, InterruptedException
	{
		final int newScoreLimit = 0;
		final int newScoreTolerance = 2000;
		setFraudScoreLimit(newScoreLimit);
		setFraudScoreTolerance(newScoreTolerance);

		addNormalUserAndSetAsCurrentUser();
		placeTestOrder();
		final String processCode = submitOrderMock(order);

		final AbstractAssertionLooper looperWaitForStatusWaitFraudManualCheck = new AbstractAssertionLooper()
		{
			@Override
			protected void checkCondition()
			{
				modelService.refresh(order);
				Assert.assertEquals("Incorrectly set fraudulant", Boolean.FALSE, order.getFraudulent());
				Assert.assertEquals("Incorrectly set not potentially fraudulant", Boolean.TRUE, order.getPotentiallyFraudulent());
				Assert.assertEquals("Incorrect order status", OrderStatus.WAIT_FRAUD_MANUAL_CHECK, order.getStatus());
				Assert.assertEquals("Incorrect number of listenerOrderFraudEmployeeNotifEvent onEvent() invocations", 1,
						listenerOrderFraudEmployeeNotificationEvent.getNumberOfEvents());
			}
		};

		looperWaitForStatusWaitFraudManualCheck.waitUntilConditionIsTrue(MAX_WAITING_TIME);

		csAgentMarkOrderAsNoFraudulentAndTriggerCSAOrderVerifiedEvent();

		final AbstractAssertionLooper looperWaitForOrderPlacedEvent = new AbstractAssertionLooper()
		{
			@Override
			protected void checkCondition()
			{
				Assert.assertEquals("Incorrect number of listenerOrderPlacedEvent onEvent() invocation ", 1,
						listenerOrderPlacedEvent.getNumberOfEvents());
			}
		};

		looperWaitForOrderPlacedEvent.waitUntilConditionIsTrue(MAX_WAITING_TIME);
		waitToTheEndOfProcess(processCode);
	}

	/**
	 * Test scenario: TODO
	 * 
	 * @throws CalculationException
	 * @throws InvalidCartException
	 **/
	@Test
	public void testUsersOrderIsEvaluatedAsPotentialFraudAndCsAgentDecideThatOrderIsFraudulent() throws InvalidCartException,
			CalculationException
	{
		final int newScoreLimit = 0;
		final int newScoreTolerance = 2000;
		setFraudScoreLimit(newScoreLimit);
		setFraudScoreTolerance(newScoreTolerance);

		addNormalUserAndSetAsCurrentUser();
		placeTestOrder();
		submitOrderMock(order);

		final AbstractAssertionLooper looperWaitForStatusWaitFraudManualCheck = new AbstractAssertionLooper()
		{
			@Override
			protected void checkCondition()
			{
				modelService.refresh(order);
				Assert.assertEquals("Incorrectly set fraudulant", Boolean.FALSE, order.getFraudulent());
				Assert.assertEquals("Incorrectly set not potentially fraudulant", Boolean.TRUE, order.getPotentiallyFraudulent());
				Assert.assertEquals("Incorrect order status", OrderStatus.WAIT_FRAUD_MANUAL_CHECK, order.getStatus());
				Assert.assertEquals("Incorrect number of listenerOrderFraudEmployeeNotifEvent onEvent() invocation ", 1,
						listenerOrderFraudEmployeeNotificationEvent.getNumberOfEvents());

			}
		};

		looperWaitForStatusWaitFraudManualCheck.waitUntilConditionIsTrue(MAX_WAITING_TIME);

		csAgentMarkOrderAsFraudulentAndTriggerCSAOrderVerifiedEvent();

		final AbstractAssertionLooper looperWaitForSuspendedStatus = new AbstractAssertionLooper()
		{

			@Override
			protected void checkCondition()
			{
				modelService.refresh(order);
				Assert.assertEquals(OrderStatus.SUSPENDED, order.getStatus());
			}
		};
		looperWaitForSuspendedStatus.waitUntilConditionIsTrue(MAX_WAITING_TIME);
		setMinPeriodWaitingForCleanUpConfigParam(3);
		final AbstractAssertionLooper looperWaitForCancelledStatus = new AbstractAssertionLooper()
		{

			@Override
			protected void checkCondition()
			{
				cronJobService.performCronJob(cronJob, true);
				modelService.refresh(order);
				Assert.assertEquals(OrderStatus.CANCELLED, order.getStatus());
			}
		};
		looperWaitForCancelledStatus.waitUntilConditionIsTrue(MAX_WAITING_TIME);

	}

	protected void waitToTheEndOfProcess(final String processCode)
	{
		final AbstractAssertionLooper looperWaitToEndProcess = new AbstractAssertionLooper()
		{

			@Override
			protected void checkCondition()
			{
				final BusinessProcessModel bpm = businessProcessService.getProcess(processCode);
				modelService.refresh(bpm);
				Assert.assertEquals(ProcessState.SUCCEEDED, bpm.getState());

			}

		};
		looperWaitToEndProcess.waitUntilConditionIsTrue(2 * MAX_WAITING_TIME);
	}

	/**
	 * Auxiliary class which provide waitng till condition is fulfilled functionality. User have to implement
	 * {@link #checkCondition} method and can provide customized error message by {@link #setErrorMessage(String)}
	 */
	protected static abstract class AbstractAssertionLooper
	{
		private final static Logger LOG = Logger.getLogger(AbstractAssertionLooper.class);
		private final static long ONE_SEC = 1000;


		/**
		 * Abstract method which should contain condition(s) which we want to check.
		 */
		protected abstract void checkCondition();

		/**
		 * Wait until condition defined in {@link #checkCondition()} is fulfilled, but no more than maxTimeInSeconds. If
		 * condition is not fulfilled, AssertionFailedError will be thrown.
		 * 
		 * @param maxTimeInSeconds
		 * 
		 */
		public void waitUntilConditionIsTrue(final long maxTimeInSeconds)
		{

			int numberOfIteration = 0;
			while (true)
			{
				try
				{
					checkCondition();
					return;
				}
				catch (final AssertionFailedError assertException)
				{
					if (numberOfIteration > maxTimeInSeconds)
					{
						throw assertException;
					}
					try
					{
						Thread.sleep(ONE_SEC);
					}
					catch (final InterruptedException e)
					{
						LOG.debug(e);
					}
					numberOfIteration++;
				}
			}
		}
	}
}
