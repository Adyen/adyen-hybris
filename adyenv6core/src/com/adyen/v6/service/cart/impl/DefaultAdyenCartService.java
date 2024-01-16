package com.adyen.v6.service.cart.impl;

import com.adyen.v6.service.cart.AdyenCartService;
import de.hybris.platform.b2bacceleratorservices.enums.DocumentStatus;
import de.hybris.platform.b2bacceleratorservices.model.B2BDocumentModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.order.impl.DefaultCartService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;
import org.apache.commons.collections.CollectionUtils;

import java.util.List;
import java.util.Optional;

public class DefaultAdyenCartService implements AdyenCartService {

    private static final String FIND_CART_BY_CART_CODE = "SELECT { " + CartModel.PK + " } FROM {" + CartModel._TYPECODE +
            "} WHERE {" + CartModel.CODE + "} = ?code";


    private FlexibleSearchService flexibleSearchService;

    @Override
    public AbstractOrderModel getAbstractOrderByCode(final String code) {
        final FlexibleSearchQuery query = new FlexibleSearchQuery(FIND_CART_BY_CART_CODE);
        query.addQueryParameter(CartModel.CODE, code);
        final SearchResult<AbstractOrderModel> results = flexibleSearchService.search(query);
        return CollectionUtils.isNotEmpty(results.getResult()) ? results.getResult().get(0): null;
    }

    public void setFlexibleSearchService(FlexibleSearchService flexibleSearchService) {
        this.flexibleSearchService = flexibleSearchService;
    }
}
