package com.adyen.services.integration.data;

import org.codehaus.jackson.annotate.JsonProperty;


public class RecurringDetailDto implements java.io.Serializable
{

	@JsonProperty("RecurringDetail")
	private RecurringDetailData recurringDetail;

	public RecurringDetailDto()
	{
		// default constructor
	}


	public void setRecurringDetail(final RecurringDetailData recurringDetail)
	{
		this.recurringDetail = recurringDetail;
	}


	public RecurringDetailData getRecurringDetail()
	{
		return recurringDetail;
	}


}