/*
 * ----------------------------------------------------------------
 * --- WARNING: THIS FILE IS GENERATED AND WILL BE OVERWRITTEN! ---
 * --- Generated at Sep 21, 2023, 10:39:19 AM                   ---
 * ----------------------------------------------------------------
 */
package com.adyen.v6.jalo;

import com.adyen.v6.constants.Adyenv6subscriptionConstants;
import de.hybris.platform.directpersistence.annotation.SLDSafe;
import de.hybris.platform.jalo.GenericItem;
import de.hybris.platform.jalo.Item;
import de.hybris.platform.jalo.Item.AttributeMode;
import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.jalo.SessionContext;
import de.hybris.platform.jalo.extension.Extension;
import de.hybris.platform.jalo.extension.ExtensionManager;
import de.hybris.platform.jalo.order.AbstractOrderEntry;
import de.hybris.platform.store.BaseStore;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Generated class for type <code>Adyenv6subscriptionManager</code>.
 */
@SuppressWarnings({"unused","cast"})
@SLDSafe
public class Adyenv6subscriptionManager extends Extension
{
	protected static final Map<String, Map<String, AttributeMode>> DEFAULT_INITIAL_ATTRIBUTES;
	static
	{
		final Map<String, Map<String, AttributeMode>> ttmp = new HashMap();
		Map<String, AttributeMode> tmp = new HashMap<String, AttributeMode>();
		tmp.put("isSubscriptionEntry", AttributeMode.INITIAL);
		ttmp.put("de.hybris.platform.jalo.order.AbstractOrderEntry", Collections.unmodifiableMap(tmp));
		tmp = new HashMap<String, AttributeMode>();
		tmp.put("subscriptionAllowedPaymentMethods", AttributeMode.INITIAL);
		ttmp.put("de.hybris.platform.store.BaseStore", Collections.unmodifiableMap(tmp));
		DEFAULT_INITIAL_ATTRIBUTES = ttmp;
	}
	@Override
	public Map<String, AttributeMode> getDefaultAttributeModes(final Class<? extends Item> itemClass)
	{
		Map<String, AttributeMode> ret = new HashMap<>();
		final Map<String, AttributeMode> attr = DEFAULT_INITIAL_ATTRIBUTES.get(itemClass.getName());
		if (attr != null)
		{
			ret.putAll(attr);
		}
		return ret;
	}
	
	public static final Adyenv6subscriptionManager getInstance()
	{
		ExtensionManager em = JaloSession.getCurrentSession().getExtensionManager();
		return (Adyenv6subscriptionManager) em.getExtension(Adyenv6subscriptionConstants.EXTENSIONNAME);
	}
	
	@Override
	public String getName()
	{
		return Adyenv6subscriptionConstants.EXTENSIONNAME;
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>AbstractOrderEntry.isSubscriptionEntry</code> attribute.
	 * @return the isSubscriptionEntry - If the entry is added as a subscription
	 */
	public Boolean isIsSubscriptionEntry(final SessionContext ctx, final AbstractOrderEntry item)
	{
		return (Boolean)item.getProperty( ctx, Adyenv6subscriptionConstants.Attributes.AbstractOrderEntry.ISSUBSCRIPTIONENTRY);
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>AbstractOrderEntry.isSubscriptionEntry</code> attribute.
	 * @return the isSubscriptionEntry - If the entry is added as a subscription
	 */
	public Boolean isIsSubscriptionEntry(final AbstractOrderEntry item)
	{
		return isIsSubscriptionEntry( getSession().getSessionContext(), item );
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>AbstractOrderEntry.isSubscriptionEntry</code> attribute. 
	 * @return the isSubscriptionEntry - If the entry is added as a subscription
	 */
	public boolean isIsSubscriptionEntryAsPrimitive(final SessionContext ctx, final AbstractOrderEntry item)
	{
		Boolean value = isIsSubscriptionEntry( ctx,item );
		return value != null ? value.booleanValue() : false;
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>AbstractOrderEntry.isSubscriptionEntry</code> attribute. 
	 * @return the isSubscriptionEntry - If the entry is added as a subscription
	 */
	public boolean isIsSubscriptionEntryAsPrimitive(final AbstractOrderEntry item)
	{
		return isIsSubscriptionEntryAsPrimitive( getSession().getSessionContext(), item );
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>AbstractOrderEntry.isSubscriptionEntry</code> attribute. 
	 * @param value the isSubscriptionEntry - If the entry is added as a subscription
	 */
	public void setIsSubscriptionEntry(final SessionContext ctx, final AbstractOrderEntry item, final Boolean value)
	{
		item.setProperty(ctx, Adyenv6subscriptionConstants.Attributes.AbstractOrderEntry.ISSUBSCRIPTIONENTRY,value);
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>AbstractOrderEntry.isSubscriptionEntry</code> attribute. 
	 * @param value the isSubscriptionEntry - If the entry is added as a subscription
	 */
	public void setIsSubscriptionEntry(final AbstractOrderEntry item, final Boolean value)
	{
		setIsSubscriptionEntry( getSession().getSessionContext(), item, value );
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>AbstractOrderEntry.isSubscriptionEntry</code> attribute. 
	 * @param value the isSubscriptionEntry - If the entry is added as a subscription
	 */
	public void setIsSubscriptionEntry(final SessionContext ctx, final AbstractOrderEntry item, final boolean value)
	{
		setIsSubscriptionEntry( ctx, item, Boolean.valueOf( value ) );
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>AbstractOrderEntry.isSubscriptionEntry</code> attribute. 
	 * @param value the isSubscriptionEntry - If the entry is added as a subscription
	 */
	public void setIsSubscriptionEntry(final AbstractOrderEntry item, final boolean value)
	{
		setIsSubscriptionEntry( getSession().getSessionContext(), item, value );
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>BaseStore.subscriptionAllowedPaymentMethods</code> attribute.
	 * @return the subscriptionAllowedPaymentMethods - Subscription Allowed Payment Methods
	 */
	public Set<String> getSubscriptionAllowedPaymentMethods(final SessionContext ctx, final BaseStore item)
	{
		Set<String> coll = (Set<String>)item.getProperty( ctx, Adyenv6subscriptionConstants.Attributes.BaseStore.SUBSCRIPTIONALLOWEDPAYMENTMETHODS);
		return coll != null ? coll : Collections.EMPTY_SET;
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>BaseStore.subscriptionAllowedPaymentMethods</code> attribute.
	 * @return the subscriptionAllowedPaymentMethods - Subscription Allowed Payment Methods
	 */
	public Set<String> getSubscriptionAllowedPaymentMethods(final BaseStore item)
	{
		return getSubscriptionAllowedPaymentMethods( getSession().getSessionContext(), item );
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>BaseStore.subscriptionAllowedPaymentMethods</code> attribute. 
	 * @param value the subscriptionAllowedPaymentMethods - Subscription Allowed Payment Methods
	 */
	public void setSubscriptionAllowedPaymentMethods(final SessionContext ctx, final BaseStore item, final Set<String> value)
	{
		item.setProperty(ctx, Adyenv6subscriptionConstants.Attributes.BaseStore.SUBSCRIPTIONALLOWEDPAYMENTMETHODS,value == null || !value.isEmpty() ? value : null );
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>BaseStore.subscriptionAllowedPaymentMethods</code> attribute. 
	 * @param value the subscriptionAllowedPaymentMethods - Subscription Allowed Payment Methods
	 */
	public void setSubscriptionAllowedPaymentMethods(final BaseStore item, final Set<String> value)
	{
		setSubscriptionAllowedPaymentMethods( getSession().getSessionContext(), item, value );
	}
	
}
