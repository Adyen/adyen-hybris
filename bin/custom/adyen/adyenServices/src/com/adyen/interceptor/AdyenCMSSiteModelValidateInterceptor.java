/**
 *
 */
package com.adyen.interceptor;

import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.servicelayer.i18n.L10NService;
import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.servicelayer.interceptor.ValidateInterceptor;

import org.apache.commons.lang.StringUtils;


/**
 * @author delli
 *
 */
public class AdyenCMSSiteModelValidateInterceptor implements ValidateInterceptor<CMSSiteModel>
{
	private L10NService l10NService;

	@Override
	public void onValidate(final CMSSiteModel cmsSiteModel, final InterceptorContext paramInterceptorContext)
			throws InterceptorException
	{
		if (!cmsSiteModel.isAdyenUseAPI() && !cmsSiteModel.isAdyenUseHPP())
		{
			throw new InterceptorException(getL10NService().getLocalizedString("error.cmssite.api.hpp.both.disabled"));
		}

		if (cmsSiteModel.isAdyenUseHPP())
		{
			if (StringUtils.isEmpty(cmsSiteModel.getAdyenHmacKey()))
			{
				throw new InterceptorException(getL10NService().getLocalizedString("error.cmssite.api.hpp.hmackey.required"));
			}
			if (StringUtils.isEmpty(cmsSiteModel.getAdyenSkinCode()))
			{
				throw new InterceptorException(getL10NService().getLocalizedString("error.cmssite.api.hpp.skincode.required"));
			}
		}
	}

	/**
	 * @return the l10NService
	 */
	public L10NService getL10NService()
	{
		return l10NService;
	}

	/**
	 * @param l10nService
	 *           the l10NService to set
	 */
	public void setL10NService(final L10NService l10nService)
	{
		l10NService = l10nService;
	}

}
