/*
 * Surevine Limited.
 * 
 * Copyright 2009. All Rights Reserved.
 */
package com.surevine.chat.openfire.auth;

import org.jasig.cas.client.validation.Cas20ProxyTicketValidator;
import org.jasig.cas.client.validation.ProxyList;

/**
 * Responsible for constructing instances of <code>CASAuthTicketValidator</code>
 */
public class CASAuthTicketValidatorFactory {

	/**
	 * Create a new instance of <code>CASAuthTicketValidator</code> using the
	 * provided configuration.
	 * 
	 * @param config
	 *            The configuration to use when constructing the instance.
	 * @return A new instance of <code>CASAuthTicketValidator</code>.
	 */
	public CASAuthTicketValidator createCASAuthTicketValidator(
			final CASAuthProviderConfig config) {
		if (config == null) {
			throw new IllegalArgumentException("CAS configuration not defined");
		}

		Cas20ProxyTicketValidator proxyValidator = new Cas20ProxyTicketValidator(
				config.getCASServerUrlPrefix());

		proxyValidator.setAllowedProxyChains(new ProxyList(config
				.getProxyChain()));

		return new CASAuthTicketValidator(proxyValidator, config
				.getServiceName());
	}

}
