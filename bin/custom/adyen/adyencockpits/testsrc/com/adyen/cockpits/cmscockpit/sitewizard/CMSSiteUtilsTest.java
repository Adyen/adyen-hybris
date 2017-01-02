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
package com.adyen.cockpits.cmscockpit.sitewizard;

import de.hybris.platform.catalog.model.CatalogModel;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cms2.model.contents.ContentCatalogModel;
import de.hybris.platform.cms2.model.contents.ContentSlotNameModel;
import de.hybris.platform.cms2.model.contents.components.AbstractCMSComponentModel;
import de.hybris.platform.cms2.model.contents.components.SimpleCMSComponentModel;
import de.hybris.platform.cms2.model.contents.contentslot.ContentSlotModel;
import de.hybris.platform.cms2.model.pages.PageTemplateModel;
import de.hybris.platform.cms2.model.relations.ContentSlotForTemplateModel;
import de.hybris.platform.servicelayer.model.ModelService;
import com.adyen.cockpits.cmscockpit.sitewizard.CMSSiteUtils.RandomProducer;

import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;


public class CMSSiteUtilsTest
{


	@Mock
	private CatalogVersionModel targetVersion;

	@Mock
	private ContentCatalogModel contentCatalog;

	@Mock
	private ModelService modelService;

	private final RandomProducer randomProvider = new RandomProducerImpl();


	@Mock
	private ContentSlotForTemplateModel slotTemplateClone;

	@Mock
	private PageTemplateModel pageTemplateClone;

	@Mock
	private ContentSlotModel contentSlotClone;

	@Mock
	private AbstractCMSComponentModel componentClone;

	@Before
	public void prepare()
	{
		MockitoAnnotations.initMocks(this);

		BDDMockito.given(modelService.create(Mockito.any(Class.class))).willAnswer(new Answer<Object>()
		{

			@Override
			public Object answer(final InvocationOnMock invocation) throws Throwable
			{

				final Class clazz = (Class) invocation.getArguments()[0];
				return clazz.newInstance();
			}
		});

		BDDMockito.given(contentCatalog.getName()).willReturn("root_content_catalog");

		BDDMockito.given(modelService.clone(Mockito.isA(ContentSlotForTemplateModel.class))).willAnswer(new Answer<Object>()
		{

			@Override
			public Object answer(final InvocationOnMock invocation) throws Throwable
			{

				return slotTemplateClone;
			}
		});

		BDDMockito.given(modelService.clone(Mockito.isA(PageTemplateModel.class))).willAnswer(new Answer<Object>()
		{

			@Override
			public Object answer(final InvocationOnMock invocation) throws Throwable
			{

				return pageTemplateClone;
			}
		});

		BDDMockito.given(modelService.clone(Mockito.isA(SimpleCMSComponentModel.class))).willAnswer(new Answer<Object>()
		{

			@Override
			public Object answer(final InvocationOnMock invocation) throws Throwable
			{

				return componentClone;
			}
		});

		BDDMockito.given(modelService.clone(Mockito.isA(ContentSlotModel.class))).willAnswer(new Answer<Object>()
		{

			@Override
			public Object answer(final InvocationOnMock invocation) throws Throwable
			{

				return contentSlotClone;
			}
		});


	}

	@Test
	public void testCloneAUniquePageTemplate()
	{

		final AbstractCMSComponentModel compOne = new SimpleCMSComponentModel();
		final AbstractCMSComponentModel compTwo = new SimpleCMSComponentModel();

		final ContentSlotModel slot = new ContentSlotModel();
		slot.setCmsComponents(Arrays.asList(compOne, compTwo));
		slot.setName("slotName");
		slot.setUid("slotUid");

		final ContentSlotNameModel slotName = prepareContentSlotName("name1", null);

		final ContentSlotForTemplateModel contentSlotForTemplate = new ContentSlotForTemplateModel();
		contentSlotForTemplate.setUid("cs4Template");
		contentSlotForTemplate.setContentSlot(slot);

		final CatalogVersionModel templateCatalogVersion = prepareCatalogVersion("sourceCatalogVersion", "catalog_id");

		final PageTemplateModel oneTemplate = preparePageTemplate("uid1", "name1", templateCatalogVersion);

		BDDMockito.given(oneTemplate.getContentSlots()).willReturn(Arrays.asList(contentSlotForTemplate));
		BDDMockito.given(oneTemplate.getAvailableContentSlots()).willReturn(Arrays.asList(slotName));

		final List<PageTemplateModel> sourceTemplates = Arrays.asList(oneTemplate);

		final List<PageTemplateModel> result = CMSSiteUtils.copyPageTemplatesDeepImpl(sourceTemplates, targetVersion,
				contentCatalog, modelService, randomProvider);

		Assert.assertEquals(result.size(), sourceTemplates.size());

		Mockito.verify(pageTemplateClone).setUid("uid1");
		Mockito.verify(pageTemplateClone).setName("name1 root_content_catalog");
		Mockito.verify(pageTemplateClone).setCatalogVersion(targetVersion);
		//Mockito.verify(pageTemplateClone).setAvailableContentSlots(Mockito.argThat(matcher));

		//assure called
		Mockito.verify(slotTemplateClone).setUid("cs4Template catalog_id sourceCatalogVersion");
		Mockito.verify(slotTemplateClone).setCatalogVersion(targetVersion);
		Mockito.verify(slotTemplateClone).setPageTemplate(pageTemplateClone);

		Mockito.verify(contentSlotClone).setUid("uid1 slotUid catalog_id sourceCatalogVersion");
		Mockito.verify(contentSlotClone).setCatalogVersion(targetVersion);

		Mockito.verify(componentClone).setUid("AbstractCMSComponent_comp_0");
		Mockito.verify(componentClone).setUid("AbstractCMSComponent_comp_1");
		Mockito.verify(componentClone, Mockito.times(2)).setSlots(java.util.Collections.<ContentSlotModel> emptyList());
		Mockito.verify(componentClone, Mockito.times(2)).setCatalogVersion(targetVersion);



	}

	/**
	 * @return
	 */
	private CatalogVersionModel prepareCatalogVersion(final String version, final String catalogId)
	{
		final CatalogModel cat = new CatalogModel();
		cat.setId(catalogId);
		final CatalogVersionModel templateCatalogVersion = new CatalogVersionModel();
		templateCatalogVersion.setVersion(version);
		templateCatalogVersion.setCatalog(cat);

		return templateCatalogVersion;
	}

	private ContentSlotNameModel prepareContentSlotName(final String name, final PageTemplateModel template)
	{
		final ContentSlotNameModel slotName = new ContentSlotNameModel();
		slotName.setName(name);
		slotName.setTemplate(template);
		return slotName;
	}

	private PageTemplateModel preparePageTemplate(final String uid, final String name,
			final CatalogVersionModel templateCatalogVersion)
	{
		final PageTemplateModel oneTemplate = Mockito.spy(new PageTemplateModel());
		oneTemplate.setUid(uid);
		oneTemplate.setName(name);
		oneTemplate.setCatalogVersion(templateCatalogVersion);
		return oneTemplate;
	}



	class RandomProducerImpl implements RandomProducer
	{

		private int idx = 0;


		@Override
		public String getSequenceUidValue(final String typeCode, final String prefix)
		{
			return typeCode + "_" + prefix + "_" + idx++;
		}

	}

}
