/*
 * Copyright 2015 Willian Oki
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package com.adyen.services.integration.data;

import java.io.Serializable;


/**
 *
 */
@SuppressWarnings("serial")
public class BrowserInfo implements Serializable
{
	private String userAgent;
	private String acceptHeader;

	public BrowserInfo()
	{
	}

	public BrowserInfo(final String userAgent, final String acceptHeader)
	{
		this.userAgent = userAgent;
		this.acceptHeader = acceptHeader;
	}

	/**
	 * @return the userAgent
	 */
	public String getUserAgent()
	{
		return userAgent;
	}

	/**
	 * @param userAgent
	 *           the userAgent to set
	 */
	public void setUserAgent(final String userAgent)
	{
		this.userAgent = userAgent;
	}

	/**
	 * @return the acceptHeader
	 */
	public String getAcceptHeader()
	{
		return acceptHeader;
	}

	/**
	 * @param acceptHeader
	 *           the acceptHeader to set
	 */
	public void setAcceptHeader(final String acceptHeader)
	{
		this.acceptHeader = acceptHeader;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		final StringBuilder builder = new StringBuilder();
		builder.append("BrowserInfo [");
		if (userAgent != null)
		{
			builder.append("userAgent=");
			builder.append(userAgent);
			builder.append(", ");
		}
		if (acceptHeader != null)
		{
			builder.append("acceptHeader=");
			builder.append(acceptHeader);
		}
		builder.append("]");
		return builder.toString();
	}
}
