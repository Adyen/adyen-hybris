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
package com.adyen.cockpits.cmscockpit.sitewizard;

import de.hybris.platform.catalog.jalo.SyncAttributeDescriptorConfig;
import de.hybris.platform.catalog.jalo.SyncItemCronJob;
import de.hybris.platform.catalog.jalo.SyncItemJob;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.catalog.model.synchronization.CatalogVersionSyncJobModel;
import de.hybris.platform.cms2.constants.Cms2Constants;
import de.hybris.platform.cms2.model.CMSPageTypeModel;
import de.hybris.platform.cms2.model.contents.ContentCatalogModel;
import de.hybris.platform.cms2.model.contents.ContentSlotNameModel;
import de.hybris.platform.cms2.model.contents.components.AbstractCMSComponentModel;
import de.hybris.platform.cms2.model.contents.contentslot.ContentSlotModel;
import de.hybris.platform.cms2.model.pages.ContentPageModel;
import de.hybris.platform.cms2.model.pages.PageTemplateModel;
import de.hybris.platform.cms2.model.relations.ContentSlotForTemplateModel;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.cmscockpit.services.GenericRandomNameProducer;
import de.hybris.platform.cockpit.session.UISessionUtils;
import de.hybris.platform.core.Registry;
import de.hybris.platform.core.model.type.TypeModel;
import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.jalo.c2l.C2LManager;
import de.hybris.platform.jalo.media.Media;
import de.hybris.platform.jalo.type.CollectionType;
import de.hybris.platform.jalo.type.ComposedType;
import de.hybris.platform.jalo.type.Type;
import de.hybris.platform.jalo.type.TypeManager;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.type.TypeService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.zkoss.zkplus.spring.SpringUtil;

import com.google.common.base.Joiner;


/**
 * Similar class to that provided with CmsCockpit with following exceptions:
 * <ul>
 * <li>Homepage uid is set to "homepage"</li>
 * <li>Best matching template for home page is applied</li>
 * <li>Cloned templates have the same uid as original templates</li>
 * </ul>
 */
public class CMSSiteUtils
{
	private static final Logger LOG = Logger.getLogger(CMSSiteUtils.class);

	private static final String CONTENT_PAGE = "ContentPage";
	private static final String LANDING_PAGE = "LandingPage";
	protected static final String CMSITEM_UID_PREFIX = "comp";

	private static volatile GenericRandomNameProducer uidGenerator;
	private static final Object LOCK = new Object();

	public static GenericRandomNameProducer getGenericRandomNameProducer()
	{
		if (uidGenerator == null)
		{
			synchronized (LOCK)
			{
				if (uidGenerator == null)
				{
					uidGenerator = (GenericRandomNameProducer) SpringUtil.getBean("genericRandomNameProducer");
				}
			}
		}
		return uidGenerator;
	}

	public static void populateCmsSite(final List<PageTemplateModel> sourceTemplates,
			final Set<CatalogVersionModel> targetCatalogVersions, final ContentCatalogModel contentCatalog,
			final CMSSiteModel cmsSiteModel, final String homepageName, final String homepageLabel)
	{
		for (final CatalogVersionModel catVersion : targetCatalogVersions)
		{
			final List<PageTemplateModel> clonedTemplates = copyPageTemplatesDeep(sourceTemplates, catVersion, contentCatalog);
			createHomepage(homepageName, homepageLabel, catVersion, contentCatalog, cmsSiteModel, clonedTemplates);
		}
	}


	public static void createHomepage(final String uid, final String label, final CatalogVersionModel catVersion,
			final ContentCatalogModel contentCatalog, final CMSSiteModel cmsSiteModel, final List<PageTemplateModel> clonedTemplates)
	{
		final PageTemplateModel firstTemplate = clonedTemplates.iterator().next();
		final ModelService modelService = UISessionUtils.getCurrentSession().getModelService();

		final ContentPageModel contentPage = modelService.create(CONTENT_PAGE);
		contentPage.setUid(uid);
		contentPage.setName(uid);
		contentPage.setLabel(label);
		contentPage.setHomepage(true);
		contentPage.setCatalogVersion(catVersion);
		contentPage.setMasterTemplate(firstTemplate);
		contentPage.setDefaultPage(Boolean.TRUE);
		cmsSiteModel.setStartingPage(contentPage);
		adjustHomePageTemplate(contentPage, clonedTemplates);
		modelService.save(contentPage);
	}

	/**
	 * Changes homepage template if possible. First tries to find first landing page template in selected templates, if
	 * none found then looks for first template restricted to contentPages, if not found, homepage template is not
	 * changed
	 */
	protected static void adjustHomePageTemplate(final ContentPageModel homepage, final List<PageTemplateModel> pageTemplates)
	{
		//iterate over landing pages if any - take first one
		final PageTemplateModel landingPageTemplate = getLandingPageTemplate(pageTemplates);
		if (landingPageTemplate == null)
		{
			//no landing page? then iterate over ContentPages templates - take first one
			final PageTemplateModel contentPageTemplate = getContentPageTemplate(pageTemplates);
			if (contentPageTemplate != null)
			{
				homepage.setMasterTemplate(contentPageTemplate);
			}
		}
		else
		{
			homepage.setMasterTemplate(landingPageTemplate);
		}
	}

	/**
	 * @return first template from given list that is restricted to ContentPage
	 */
	protected static PageTemplateModel getContentPageTemplate(final Collection<PageTemplateModel> pageTemplates)
	{
		final TypeModel contentPageType = Registry.getApplicationContext().getBean("typeService", TypeService.class)
				.getTypeForCode(CONTENT_PAGE);
		for (final PageTemplateModel pageTemplateModel : pageTemplates)
		{
			final Set<CMSPageTypeModel> restrictedPageTypes = pageTemplateModel.getRestrictedPageTypes();
			if (CollectionUtils.isNotEmpty(restrictedPageTypes) && restrictedPageTypes.contains(contentPageType))
			{
				return pageTemplateModel;
			}
		}
		return null;
	}

	/**
	 * @return first page templates that is of type 'landing page'
	 */
	protected static PageTemplateModel getLandingPageTemplate(final Collection<PageTemplateModel> pageTemplates)
	{
		for (final PageTemplateModel pageTemplateModel : pageTemplates)
		{
			if (StringUtils.contains(pageTemplateModel.getUid(), LANDING_PAGE))
			{
				return pageTemplateModel;
			}
		}
		return null;
	}

	public static synchronized List<PageTemplateModel> copyPageTemplatesDeep(final List<PageTemplateModel> sourceTemplates,
			final CatalogVersionModel catVersion, final ContentCatalogModel contentCatalog)
	{
		final ModelService modelService = UISessionUtils.getCurrentSession().getModelService();

		return copyPageTemplatesDeepImpl(sourceTemplates, catVersion, contentCatalog, modelService, new RandomProducer()
		{

			@Override
			public String getSequenceUidValue(final String typeCode, final String prefix)
			{
				return getGenericRandomNameProducer().generateSequence(typeCode, prefix);
			}
		});
	}

	/**
	 * internals for {@link #copyPageTemplatesDeep(List, CatalogVersionModel, ContentCatalogModel)} separated from
	 * platform so could be tested separately somehow
	 */
	protected static List<PageTemplateModel> copyPageTemplatesDeepImpl(final List<PageTemplateModel> sourceTemplates,
			final CatalogVersionModel catVersion, final ContentCatalogModel contentCatalog, final ModelService modelService,
			final RandomProducer random)
	{
		final List<PageTemplateModel> clonedTemplates = new ArrayList<PageTemplateModel>();

		for (final PageTemplateModel template : sourceTemplates)
		{
			final String catalogId = template.getCatalogVersion().getCatalog().getId();
			final String catalogVersionId = template.getCatalogVersion().getVersion();
			final PageTemplateModel clonedPageTemplate = modelService.clone(template);
			clonedPageTemplate.setUid(template.getUid());
			clonedPageTemplate.setName(getUidWithSuffix(template.getName(), contentCatalog.getName()));
			clonedPageTemplate.setCatalogVersion(catVersion);

			final List<ContentSlotNameModel> slotsNameList = new ArrayList<ContentSlotNameModel>(16);
			for (final ContentSlotNameModel contentSlotNameModel : template.getAvailableContentSlots())//possible unique problem
			{
				final ContentSlotNameModel clonedSlotNameModel = modelService.create(ContentSlotNameModel.class);
				clonedSlotNameModel.setName(contentSlotNameModel.getName());
				clonedSlotNameModel.setTemplate(clonedPageTemplate);
				slotsNameList.add(clonedSlotNameModel);
			}
			clonedPageTemplate.setAvailableContentSlots(slotsNameList);

			for (final ContentSlotForTemplateModel contentSlotForTemplate : template.getContentSlots())
			{
				final ContentSlotForTemplateModel clonedContentSlotForTemplate = modelService.clone(contentSlotForTemplate);
				clonedContentSlotForTemplate.setCatalogVersion(catVersion);
				clonedContentSlotForTemplate.setPageTemplate(clonedPageTemplate);
				clonedContentSlotForTemplate.setUid(getUidWithSuffix(contentSlotForTemplate.getUid(), catalogId, catalogVersionId));

				final ContentSlotModel contentSlotModel = contentSlotForTemplate.getContentSlot();
				final ContentSlotModel clonedContentSlotModel = modelService.clone(contentSlotModel);
				clonedContentSlotModel.setUid(getUidWithSuffix(template.getUid(), contentSlotModel.getUid(), catalogId,
						catalogVersionId));
				clonedContentSlotModel.setCatalogVersion(catVersion);

				final List<AbstractCMSComponentModel> clonedComponents = new ArrayList<AbstractCMSComponentModel>(16);
				for (final AbstractCMSComponentModel component : contentSlotModel.getCmsComponents())
				{
					final AbstractCMSComponentModel clonedComponent = modelService.clone(component);
					clonedComponent.setUid(random.getSequenceUidValue(AbstractCMSComponentModel._TYPECODE, CMSITEM_UID_PREFIX));
					clonedComponent.setSlots(Collections.<ContentSlotModel> emptyList());
					clonedComponent.setCatalogVersion(catVersion);
					clonedComponents.add(clonedComponent);
				}
				clonedContentSlotModel.setCmsComponents(clonedComponents);

				clonedContentSlotForTemplate.setContentSlot(clonedContentSlotModel);
				modelService.save(clonedContentSlotForTemplate);
			}

			modelService.save(clonedPageTemplate);
			clonedTemplates.add(clonedPageTemplate);
		}
		return clonedTemplates;
	}

	protected static String getUidWithSuffix(final String uid, final String suffix, final String... rest)
	{
		return Joiner.on(" ").join((Object) uid, (Object) suffix, (Object[]) rest);
	}

	interface RandomProducer
	{
		String getSequenceUidValue(String typeCode, final String prefix);
	}

	public static CatalogVersionSyncJobModel createDefaultSyncJob(final String code, final CatalogVersionModel catVerStaged,
			final CatalogVersionModel catVerOnline)
	{
		final ModelService modelService = UISessionUtils.getCurrentSession().getModelService();

		final CatalogVersionSyncJobModel catalogVersionSyncJob = modelService.create("CatalogVersionSyncJob");
		catalogVersionSyncJob.setCode(code);
		catalogVersionSyncJob.setSourceVersion(catVerStaged);
		catalogVersionSyncJob.setTargetVersion(catVerOnline);
		catalogVersionSyncJob.setRemoveMissingItems(Boolean.TRUE);
		catalogVersionSyncJob.setCreateNewItems(Boolean.TRUE);

		return catalogVersionSyncJob;
	}


	@SuppressWarnings("deprecation")
	public static void synchronizeCatVersions(final CatalogVersionSyncJobModel catalogVersionSyncJob,
			final ModelService modelService)
	{
		final SyncItemJob job = setupStoreTemplateSyncJobs(catalogVersionSyncJob, modelService);
		try
		{
			performSynchronization(job);
			LOG.info("\t" + job.getCode() + " - OK");
		}
		catch (final Exception e)
		{
			LOG.warn("\t" + job.getCode() + " - FAILED (Reason: " + e.getMessage() + ")");

		}
	}

	@SuppressWarnings("deprecation")
	protected static void performSynchronization(final SyncItemJob job)
	{
		final SyncItemCronJob cronJob = job.newExecution();
		cronJob.setLogToDatabase(false);
		cronJob.setLogToFile(false);
		cronJob.setForceUpdate(false);
		if (LOG.isDebugEnabled())
		{
			LOG.debug("Generating cronjob " + cronJob.getCode() + " to synchronize staged to online version, configuring ...");
		}
		job.configureFullVersionSync(cronJob);
		if (LOG.isDebugEnabled())
		{
			LOG.debug("Starting synchronization, this may take a while ...");
		}
		job.perform(cronJob, true);
	}

	@SuppressWarnings("deprecation")
	protected static SyncItemJob setupStoreTemplateSyncJobs(final CatalogVersionSyncJobModel syncJobModel,
			final ModelService modelService)
	{
		// configure root types
		final SyncItemJob syncJob = modelService.getSource(syncJobModel);
		if (syncJob == null)
		{
			LOG.warn("Could not setup catalog version synchronization job. Reason: Synchronization job not found.");

		}
		else
		{
			final List<ComposedType> rootTypes = new ArrayList<ComposedType>(2);
			final ComposedType cmsItemType = TypeManager.getInstance().getComposedType(Cms2Constants.TC.CMSITEM);
			rootTypes.add(cmsItemType);
			rootTypes.add(TypeManager.getInstance().getComposedType(Cms2Constants.TC.CMSRELATION));
			rootTypes.add(TypeManager.getInstance().getComposedType(Media.class));
			syncJob.setRootTypes(JaloSession.getCurrentSession().getSessionContext(), rootTypes);
			syncJob
					.setSyncLanguages(JaloSession.getCurrentSession().getSessionContext(), C2LManager.getInstance().getAllLanguages());

			final Collection<SyncAttributeDescriptorConfig> syncAttributeConfigs = syncJob.getSyncAttributeConfigurations();
			for (final SyncAttributeDescriptorConfig syncAttributeDescriptorConfig : syncAttributeConfigs)
			{
				final Type attributeType = syncAttributeDescriptorConfig.getAttributeDescriptor().getAttributeType();
				if ((syncAttributeDescriptorConfig.getAttributeDescriptor().getEnclosingType().isAssignableFrom(cmsItemType) && cmsItemType
						.isAssignableFrom(attributeType))
						|| ((attributeType instanceof CollectionType) && cmsItemType.isAssignableFrom(((CollectionType) attributeType)
								.getElementType())))
				{
					syncAttributeDescriptorConfig.setCopyByValue(true);
				}
			}
		}
		return syncJob;
	}
}
