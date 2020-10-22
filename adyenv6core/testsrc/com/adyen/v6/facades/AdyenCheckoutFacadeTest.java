package com.adyen.v6.facades;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class AdyenCheckoutFacadeTest {

	@InjectMocks
	private DefaultAdyenCheckoutFacade adyenCheckoutFacade;

	@Before
	public void setUp() {
		adyenCheckoutFacade = new DefaultAdyenCheckoutFacade();
	}

	@After
	public void tearDown() {
		// implement here code executed after each test
	}
	
	/**
     * Test dependency of addressPopulator
     * It should accept all populators which implements interface Populator<AddressModel, AddressData>
     */
    @Test
    public void testDependencyAddressPopulator() throws Exception {
    	adyenCheckoutFacade.setAddressPopulator(new Populator<AddressModel, AddressData>() {
    		@Override
    		public void populate(AddressModel source, AddressData target) throws ConversionException {
    			// not relevant
    		}
    	});
    }

}
