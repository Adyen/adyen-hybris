/**
 * 
 */
package com.adyen.services.integration;

import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.cms2.servicelayer.services.CMSSiteService;

import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.FactoryBean;


/**
 * @author Kenneth Zhou
 * 
 */
public class AdyenHttpClientFactory implements FactoryBean<HttpClient>
{
	private static final Logger LOG = Logger.getLogger(AdyenHttpClientFactory.class);
	private CMSSiteService cmsSiteService;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.beans.factory.FactoryBean#getObject()
	 */
	@Override
	public HttpClient getObject() throws Exception
	{
		try
		{
			final CMSSiteModel site = cmsSiteService.getCurrentSite();
			final CredentialsProvider provider = new BasicCredentialsProvider();
			final UsernamePasswordCredentials adyenUsernamePasswordCredentials = new UsernamePasswordCredentials(
					site.getAdyenAPIAccount(), site.getAdyenAPIPassword());
			provider.setCredentials(AuthScope.ANY, adyenUsernamePasswordCredentials);

			final DefaultHttpClient client = new DefaultHttpClient();
			client.setCredentialsProvider(provider);

			return client;
		}
		catch (final Exception e)
		{
			LOG.error(e);
			return null;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.beans.factory.FactoryBean#getObjectType()
	 */
	@Override
	public Class<HttpClient> getObjectType()
	{
		// YTODO Auto-generated method stub
		return HttpClient.class;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.beans.factory.FactoryBean#isSingleton()
	 */
	@Override
	public boolean isSingleton()
	{
		// YTODO Auto-generated method stub
		return true;
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
