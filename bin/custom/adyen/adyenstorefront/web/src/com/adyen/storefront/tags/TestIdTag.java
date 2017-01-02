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
package com.adyen.storefront.tags;


import de.hybris.platform.util.Config;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.SimpleTagSupport;


/**
 * Tag that generates a wrapping div with the specified id. The id is suffixed with an incrementing counter for the page
 * request to ensure that it is unique. The wrapper divs can be turned on and off via a configuration property.
 */
public class TestIdTag extends SimpleTagSupport
{
	protected static final String ENABLE_TEST_IDS_PROPERTY = "adyenstorefront.testIds.enable";
	protected static final String TEST_ID_TAG_NEXT = "__test_id_tag_next__";

	private String code;

	protected String getCode()
	{
		return code;
	}

	public void setCode(final String code)
	{
		this.code = code;
	}

	@Override
	public void doTag() throws JspException, IOException
	{
		final boolean enabled = Config.getBoolean(ENABLE_TEST_IDS_PROPERTY, false);
		if (enabled)
		{
			final PageContext pageContext = (PageContext) getJspContext();
			final JspWriter jspWriter = pageContext.getOut();

			final int nextUniqueId = getNextUniqueId(pageContext);

			jspWriter.append("<div id=\"").append("test_").append(cleanupHtmlId(getCode())).append("_$")
					.append(String.valueOf(nextUniqueId)).append("\" style=\"display:inline\">");

			// Write the body out
			getJspBody().invoke(jspWriter);

			jspWriter.println("</div>");
		}
		else
		{
			// Just render the contents
			getJspBody().invoke(getJspContext().getOut());
		}
	}

	protected int getNextUniqueId(final PageContext pageContext)
	{
		final Object value = pageContext.getAttribute(TEST_ID_TAG_NEXT, PageContext.PAGE_SCOPE);
		if (value instanceof Integer)
		{
			final int nextVal = ((Integer) value).intValue();
			pageContext.setAttribute(TEST_ID_TAG_NEXT, Integer.valueOf(nextVal + 1), PageContext.PAGE_SCOPE);
			return nextVal;
		}
		else
		{
			// No attribute found, set next to 2, and return 1.
			pageContext.setAttribute(TEST_ID_TAG_NEXT, Integer.valueOf(2), PageContext.PAGE_SCOPE);
			return 1;
		}
	}

	protected String cleanupHtmlId(final String text)
	{
		final StringBuilder result = new StringBuilder(text.length());

		for (int i = 0; i < text.length(); i++)
		{
			final char c = text.charAt(i); 

			if ((c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z') || (c >= '0' && c <= '9') || c == '-' || c == '_' || c == '.'
					|| c == ':')
			{
				result.append(c);
			}
		}

		return result.toString();
	}
}
