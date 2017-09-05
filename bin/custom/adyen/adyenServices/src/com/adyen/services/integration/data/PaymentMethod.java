
package com.adyen.services.integration.data;

import java.util.ArrayList;
import java.util.List;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PaymentMethod {

    private String brandCode;
    private String name;

	private final List<Iusser> issuers = new ArrayList<Iusser>();

	/**
	 * @return the issuers
	 */
	public List<Iusser> getIssuers()
	{
		return issuers;
	}

	/**
	 *
	 * @return The brandCode
	 */
    public String getBrandCode() {
        return brandCode;
    }

    /**
     *
     * @param brandCode
     *     The brandCode
     */
    public void setBrandCode(final String brandCode) {
        this.brandCode = brandCode;
    }

    /**
     *
     * @return
     *     The name
     */
    public String getName() {
        return name;
    }

    /**
     *
     * @param name
     *     The name
     */
    public void setName(final String name) {
        this.name = name;
    }

}
