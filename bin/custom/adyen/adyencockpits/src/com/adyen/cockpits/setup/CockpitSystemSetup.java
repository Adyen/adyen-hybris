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
package com.adyen.cockpits.setup;

import de.hybris.platform.acceleratorservices.setup.AbstractSystemSetup;
import de.hybris.platform.core.initialization.SystemSetup;
import de.hybris.platform.core.initialization.SystemSetup.Process;
import de.hybris.platform.core.initialization.SystemSetup.Type;
import de.hybris.platform.core.initialization.SystemSetupContext;
import de.hybris.platform.core.initialization.SystemSetupParameter;
import de.hybris.platform.core.initialization.SystemSetupParameterMethod;
import de.hybris.platform.jalo.media.MediaManager;
import de.hybris.platform.util.Config;
import com.adyen.cockpits.constants.AdyenCockpitsConstants;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;


/**
 * This class provides hooks into the system's initialization and update processes.
 * 
 * @see "https://wiki.hybris.com/display/release4/Hooks+for+Initialization+and+Update+Process"
 */
@SystemSetup(extension = AdyenCockpitsConstants.EXTENSIONNAME)
public class CockpitSystemSetup extends AbstractSystemSetup
{
	private static final Logger LOG = Logger.getLogger(CockpitSystemSetup.class.getName());

	public static final String IMPORT_CUSTOM_REPORTS = "importCustomReports";

	/**
	 * This method will be called by system creator during initialization and system update. Be sure that this method can
	 * be called repeatedly.
	 * 
	 * @param context
	 *           the context provides the selected parameters and values
	 */
	@SystemSetup(type = Type.ESSENTIAL, process = Process.ALL)
	public void createEssentialData(final SystemSetupContext context)
	{
		final boolean importCustomReports = getBooleanSystemSetupParameter(context, IMPORT_CUSTOM_REPORTS);

		if (importCustomReports)
		{
			if (MediaManager.getInstance().getMediaFolderByQualifier(AdyenCockpitsConstants.JASPER_REPORTS_MEDIA_FOLDER).size() < 1)
			{
				MediaManager.getInstance().createMediaFolder(AdyenCockpitsConstants.JASPER_REPORTS_MEDIA_FOLDER,
						AdyenCockpitsConstants.JASPER_REPORTS_MEDIA_FOLDER);
			}
			try
			{
				String prefix = null;
				if (Config.isMySQLUsed())
				{
					prefix = "mysql";
				}
				else if (Config.isHSQLDBUsed())
				{
					prefix = "hsqldb";
				}
				else if (Config.isOracleUsed())
				{
					prefix = "oracle";
				}
				else if (Config.isSQLServerUsed())
				{
					prefix = "sqlserver";
				}
				importImpexFile(context, "/adyencockpits/reportcockpit/import/" + prefix + "_jasperreports.impex");
			}
			catch (final Exception e)
			{
				LOG.error("Error during Jasper Report files import " + e);
			}
		}
	}

	/**
	 * Generates the Dropdown and Multi-select boxes for the project data import
	 */
	@Override
	@SystemSetupParameterMethod
	public List<SystemSetupParameter> getInitializationOptions()
	{
		final List<SystemSetupParameter> params = new ArrayList<SystemSetupParameter>();

		params.add(createBooleanSystemSetupParameter(IMPORT_CUSTOM_REPORTS, "Import Custom Reports", true));

		return params;
	}
}
