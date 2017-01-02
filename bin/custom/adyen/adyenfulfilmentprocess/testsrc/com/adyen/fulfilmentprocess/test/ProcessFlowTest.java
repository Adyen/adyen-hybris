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

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.core.Registry;
import de.hybris.platform.payment.commands.factory.CommandFactory;
import de.hybris.platform.payment.commands.factory.impl.DefaultCommandFactoryRegistryImpl;
import de.hybris.platform.processengine.definition.ProcessDefinitionFactory;
import de.hybris.platform.processengine.enums.ProcessState;
import de.hybris.platform.processengine.impl.DefaultBusinessProcessService;
import de.hybris.platform.processengine.model.BusinessProcessModel;
import de.hybris.platform.processengine.model.ProcessTaskModel;
import de.hybris.platform.processengine.spring.Action;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.spring.ctx.ScopeTenantIgnoreDocReader;
import de.hybris.platform.task.RetryLaterException;
import de.hybris.platform.task.TaskModel;
import de.hybris.platform.task.impl.DefaultTaskService;
import de.hybris.platform.testframework.HybrisJUnit4Test;
import de.hybris.platform.testframework.TestUtils;
import de.hybris.platform.util.Utilities;
import com.adyen.fulfilmentprocess.test.actions.TestActionTemp;
import org.apache.log4j.Logger;
import org.junit.*;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.io.ClassPathResource;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static junit.framework.Assert.*;


@IntegrationTest
public class ProcessFlowTest extends HybrisJUnit4Test
{
	private static final Logger LOG = Logger.getLogger(ProcessFlowTest.class);

	private static TaskServiceStub taskServiceStub;

	private static DefaultBusinessProcessService processService;
	private static ProcessDefinitionFactory definitonFactory;
	private static ModelService modelService;


	@BeforeClass
	public static void prepare() throws Exception //NOPMD
	{
		Registry.activateStandaloneMode();
		Utilities.setJUnitTenant();
		LOG.debug("Preparing...");



		final ApplicationContext appCtx = Registry.getApplicationContext();

		//		final ConfigurationService configurationService = (ConfigurationService) appCtx.getBean("configurationService");
		//		configurationService.getConfiguration().setProperty("processengine.event.lockProcess", "true");

		assertTrue("Application context of type " + appCtx.getClass() + " is not a subclass of "
				+ ConfigurableApplicationContext.class, appCtx instanceof ConfigurableApplicationContext);

		final ConfigurableApplicationContext applicationContext = (ConfigurableApplicationContext) appCtx;
		final ConfigurableListableBeanFactory beanFactory = applicationContext.getBeanFactory();
		assertTrue("Bean Factory of type " + beanFactory.getClass() + " is not of type " + BeanDefinitionRegistry.class,
				beanFactory instanceof BeanDefinitionRegistry);
		final XmlBeanDefinitionReader xmlReader = new XmlBeanDefinitionReader((BeanDefinitionRegistry) beanFactory);
		xmlReader.setDocumentReaderClass(ScopeTenantIgnoreDocReader.class);
		xmlReader.loadBeanDefinitions(new ClassPathResource(
				"/adyenfulfilmentprocess/test/adyenfulfilmentprocess-spring-test.xml"));
		xmlReader
				.loadBeanDefinitions(new ClassPathResource("/adyenfulfilmentprocess/test/process/order-process-spring.xml"));
		xmlReader.loadBeanDefinitions(new ClassPathResource(
				"/adyenfulfilmentprocess/test/process/consignment-process-spring.xml"));


		modelService = (ModelService) getBean("modelService");
		processService = (DefaultBusinessProcessService) getBean("businessProcessService");
		definitonFactory = processService.getProcessDefinitionFactory();



		LOG.warn("Prepare Process Definition factory...");
		definitonFactory.add("classpath:/adyenfulfilmentprocess/test/process/order-process.xml");
		definitonFactory.add("classpath:/adyenfulfilmentprocess/test/process/consignment-process.xml");
		LOG.warn("loaded 'order-process-test':" + definitonFactory.getProcessDefinition("order-process-test") + " in factory "
				+ definitonFactory);


		//setup command factory to mock
		taskServiceStub = appCtx.getBean(TaskServiceStub.class);
		processService.setTaskService(taskServiceStub);

		final DefaultCommandFactoryRegistryImpl commandFactoryReg = appCtx.getBean(DefaultCommandFactoryRegistryImpl.class);
		commandFactoryReg.setCommandFactoryList(Arrays.asList((CommandFactory) appCtx.getBean("mockupCommandFactory")));

	}

	@Before
	public void setupActions()
	{
		setResultForAction("test.checkAuthorizeOrderPaymentAction", "OK");
		setThrowExceptionForAction("test.reserveOrderAmountAction", false);
		setResultForAction("test.reserveOrderAmountAction", "OK");
		setResultForAction("test.checkTransactionReviewStatusAction", "OK");
		setResultForAction("test.fraudCheckOrderInternalAction", "OK");
		setResultForAction("test.fraudCheckOrderAction", "OK");
		setResultForAction("test.receiveConsignmentStatusAction", "OK");
		setResultForAction("test.takePaymentAction", "OK");
		setResultForAction("test.waitBeforeTransmissionAction", "OK");
	}

	@AfterClass
	public static void removeProcessDefinitions()
	{
		LOG.debug("cleanup...");


		final ApplicationContext appCtx = Registry.getApplicationContext();

		assertTrue("Application context of type " + appCtx.getClass() + " is not a subclass of "
				+ ConfigurableApplicationContext.class, appCtx instanceof ConfigurableApplicationContext);

		final ConfigurableApplicationContext applicationContext = (ConfigurableApplicationContext) appCtx;
		final ConfigurableListableBeanFactory beanFactory = applicationContext.getBeanFactory();
		assertTrue("Bean Factory of type " + beanFactory.getClass() + " is not of type " + BeanDefinitionRegistry.class,
				beanFactory instanceof BeanDefinitionRegistry);
		final XmlBeanDefinitionReader xmlReader = new XmlBeanDefinitionReader((BeanDefinitionRegistry) beanFactory);
		xmlReader.loadBeanDefinitions(new ClassPathResource(
				"/adyenfulfilmentprocess/test/adyenfulfilmentprocess-spring-testcleanup.xml"));


		//cleanup command factory
		final Map<String, CommandFactory> commandFactoryList = applicationContext.getBeansOfType(CommandFactory.class);
		commandFactoryList.remove("mockupCommandFactory");
		final DefaultCommandFactoryRegistryImpl commandFactoryReg = appCtx.getBean(DefaultCommandFactoryRegistryImpl.class);
		commandFactoryReg.setCommandFactoryList(commandFactoryList.values());

		processService.setTaskService(appCtx.getBean(DefaultTaskService.class));
		definitonFactory = null;
		processService = null;
	}

	@After
	public void resetServices()
	{
		final List<TaskModel> tasks = taskServiceStub.cleanup();
		final StringBuffer msg = new StringBuffer();
		for (final TaskModel task : tasks)
		{
			final ProcessTaskModel processTask = (ProcessTaskModel) task;

			msg.append(processTask.getAction()).append(", ");
		}
	}

	@Test
	public void testErrorCode() throws InterruptedException
	{
		setResultForAction("test.checkOrderAction", Action.ERROR_RETURN_CODE);
		try
		{
			final BusinessProcessModel process = createProcess("order-process-test");
			TestUtils.disableFileAnalyzer(400);
			assertStep(process, "checkOrder");

		}
		finally
		{
			setResultForAction("test.checkOrderAction", "OK");
			TestUtils.enableFileAnalyzer();
		}
	}

	@Test
	public void testProcessPaymentNotTaken() throws InterruptedException
	{
		setResultForAction("test.takePaymentAction", "NOK");

		final BusinessProcessModel process = createProcess("order-process-test");

		assertStep(process, "checkOrder");
		assertStep(process, "checkAuthorizeOrderPayment");
		assertStep(process, "reserveAmount");
		assertStep(process, "checkTransactionReviewStatus");
		assertStep(process, "fraudCheck");
		assertStep(process, "sendOrderPlacedNotification");
		assertStep(process, "takePayment");
		assertStep(process, "sendPaymentFailedNotification");
	}

	@Test
	public void testErrorInProcess() throws InterruptedException
	{
		setResultForAction("test.reserveOrderAmountAction", Action.ERROR_RETURN_CODE);

		final BusinessProcessModel process = createProcess("order-process-test");

		assertStep(process, "checkOrder");
		assertStep(process, "checkAuthorizeOrderPayment");
		TestUtils.disableFileAnalyzer(400);
		try
		{
			assertStep(process, "reserveAmount");
		}
		finally
		{
			TestUtils.enableFileAnalyzer();
		}
		Thread.sleep(1000);

		modelService.refresh(process);
		assertEquals("Process state", ProcessState.ERROR, process.getProcessState());
	}

	@Test
	public void testExceptionInProcess() throws InterruptedException
	{
		setThrowExceptionForAction("test.reserveOrderAmountAction", true);

		final BusinessProcessModel process = createProcess("order-process-test");

		assertStep(process, "checkOrder");
		assertStep(process, "checkAuthorizeOrderPayment");
		TestUtils.disableFileAnalyzer(400);
		try
		{
			assertStep(process, "reserveAmount");
		}
		finally
		{
			TestUtils.enableFileAnalyzer();
		}

		Thread.sleep(1000);

		modelService.refresh(process);
		assertEquals("Process state", ProcessState.ERROR, process.getProcessState());
	}



	@Test
	public void testConsignmentStatusCancel() throws InterruptedException
	{
		setResultForAction("test.fraudCheckOrderInternalAction", "FRAUD");
		setResultForAction("test.receiveConsignmentStatusAction", "CANCEL");
		setResultForAction("test.scheduleForCleanUpAction", "NOK");

		final BusinessProcessModel process = createProcess("order-process-test");

		assertStep(process, "checkOrder");
		assertStep(process, "checkAuthorizeOrderPayment");
		assertStep(process, "reserveAmount");
		assertStep(process, "checkTransactionReviewStatus");
		assertStep(process, "fraudCheck");
		assertStep(process, "notifyCustomer");
		assertStep(process, "waitForCleanUp");
		assertStep(process, "scheduleForCleanUp");
		assertStep(process, "orderManualChecked");
		assertStep(process, "sendOrderPlacedNotification");
		assertStep(process, "takePayment");
		assertStep(process, "splitOrder");
		assertStep(process, "waitForWarehouseSubprocessEnd");
		assertStep(process, "waitBeforeTransmission");
		assertStep(process, "isProcessCompleted");
		assertStep(process, "sendConsignmentToWarehouse");
		assertStep(process, "waitForWarehouse");
		assertStep(process, "receiveConsignmentStatus");
		assertStep(process, "cancelConsignment");
		assertStep(process, "sendCancelMessage");
		assertStep(process, "subprocessEnd");
		assertStep(process, "isProcessCompleted");
		assertStep(process, "sendOrderCompletedNotification");
	}

	@Test
	public void testProcessFraudFinalFraudScheduledForCleanup() throws InterruptedException
	{
		setResultForAction("test.fraudCheckOrderInternalAction", "FRAUD");
		setResultForAction("test.scheduleForCleanUpAction", "OK");
		final BusinessProcessModel process = createProcess("order-process-test");

		assertStep(process, "checkOrder");
		assertStep(process, "checkAuthorizeOrderPayment");
		assertStep(process, "reserveAmount");
		assertStep(process, "checkTransactionReviewStatus");
		assertStep(process, "fraudCheck");
		assertStep(process, "notifyCustomer");
		assertStep(process, "waitForCleanUp");
		assertStep(process, "scheduleForCleanUp");
		assertStep(process, "cancelOrder");
	}

	@Test
	public void testProcessFraudFinalFraudNotScheduledForCleanup() throws InterruptedException
	{
		setResultForAction("test.fraudCheckOrderInternalAction", "FRAUD");
		setResultForAction("test.scheduleForCleanUpAction", "NOK");
		final BusinessProcessModel process = createProcess("order-process-test");

		assertStep(process, "checkOrder");
		assertStep(process, "checkAuthorizeOrderPayment");
		assertStep(process, "reserveAmount");
		assertStep(process, "checkTransactionReviewStatus");
		assertStep(process, "fraudCheck");
		assertStep(process, "notifyCustomer");
		assertStep(process, "waitForCleanUp");
		assertStep(process, "scheduleForCleanUp");
		assertStep(process, "orderManualChecked");
		assertStep(process, "sendOrderPlacedNotification");
	}

	@Test
	public void testProcessFraudFinalPotential() throws InterruptedException
	{
		setResultForAction("test.fraudCheckOrderInternalAction", "POTENTIAL");
		final BusinessProcessModel process = createProcess("order-process-test");

		assertStep(process, "checkOrder");
		assertStep(process, "checkAuthorizeOrderPayment");
		assertStep(process, "reserveAmount");
		assertStep(process, "checkTransactionReviewStatus");
		assertStep(process, "fraudCheck");
		assertStep(process, "manualOrderCheckCSA");
		assertStep(process, "waitForManualOrderCheckCSA");
		assertStep(process, "orderManualChecked");
		assertStep(process, "sendOrderPlacedNotification");
	}

	@Test
	public void testProcessPaymentFailed() throws InterruptedException
	{
		setResultForAction("test.reserveOrderAmountAction", "NOK");

		final BusinessProcessModel process = createProcess("order-process-test");

		assertStep(process, "checkOrder");
		assertStep(process, "checkAuthorizeOrderPayment");
		assertStep(process, "reserveAmount");
		assertStep(process, "sendPaymentFailedNotification");
	}

	@Test
	public void testProcessAuthorizationFailed() throws InterruptedException
	{
		setResultForAction("test.checkAuthorizeOrderPaymentAction", "NOK");

		final BusinessProcessModel process = createProcess("order-process-test");

		assertStep(process, "checkOrder");
		assertStep(process, "checkAuthorizeOrderPayment");
		assertStep(process, "authorizationFailedNotification");
	}


	@Test
	public void testProcessOk() throws InterruptedException
	{
		final BusinessProcessModel process = createProcess("order-process-test");

		assertStep(process, "checkOrder");
		assertStep(process, "checkAuthorizeOrderPayment");
		assertStep(process, "reserveAmount");
		assertStep(process, "checkTransactionReviewStatus");
		assertStep(process, "fraudCheck");
		assertStep(process, "sendOrderPlacedNotification");
		assertStep(process, "takePayment");
		assertStep(process, "splitOrder");
		assertStep(process, "waitForWarehouseSubprocessEnd");
		assertStep(process, "waitBeforeTransmission");
		assertStep(process, "isProcessCompleted");
		assertStep(process, "sendConsignmentToWarehouse");
		assertStep(process, "waitForWarehouse");
		assertStep(process, "receiveConsignmentStatus");
		assertStep(process, "allowShipment");
		assertStep(process, "sendDeliveryMessage");
		assertStep(process, "subprocessEnd");
		assertStep(process, "isProcessCompleted");
		assertStep(process, "sendOrderCompletedNotification");
	}

	protected BusinessProcessModel createProcess(final String processName)
	{
		final String id = "Test" + (new Date()).getTime();
		final BusinessProcessModel process = processService.startProcess(id, processName);
		assertProcessState(process, ProcessState.RUNNING);
		modelService.save(process);
		return process;
	}

	protected void setResultForAction(final String action, final String result)
	{
		final TestActionTemp tmp = (TestActionTemp) getBean(action);
		tmp.setResult(result);
	}

	protected void setThrowExceptionForAction(final String action, final boolean throwException)
	{
		final TestActionTemp tmp = (TestActionTemp) getBean(action);
		tmp.setThrowException(throwException);
	}


	protected void assertStep(final BusinessProcessModel process, final String bean) throws InterruptedException
	{
		LOG.info("assertStep action = " + bean);

		try
		{
			final ProcessTaskModel processTaskModel = taskServiceStub.runProcessTask(bean);

			if (processTaskModel == null)
			{
				final StringBuffer found = new StringBuffer();

				for (final TaskModel task : taskServiceStub.getTasks())
				{
					if (task instanceof ProcessTaskModel)
					{
						found.append(((ProcessTaskModel) task).getAction()).append("; ");
					}
				}

				assertNotNull("No task found for bean " + bean + ", action(s): " + found, processTaskModel);
			}


		}
		catch (final RetryLaterException e)
		{
			fail(e.toString());
		}

	}

	protected static Object getBean(final String name)
	{
		return Registry.getApplicationContext().getBean(name);
	}

	protected void assertProcessState(final BusinessProcessModel process, final ProcessState state)
	{
		modelService.refresh(process);
		assertEquals("Process state", state, process.getState());
	}

}
