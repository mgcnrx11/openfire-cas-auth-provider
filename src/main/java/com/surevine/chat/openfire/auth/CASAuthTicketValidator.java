/*
 * Surevine Limited.
 * 
 * Copyright 2009. All Rights Reserved.
 */
package com.surevine.chat.openfire.auth;

import org.jasig.cas.client.authentication.AttributePrincipal;
import org.jasig.cas.client.validation.Assertion;
import org.jasig.cas.client.validation.Cas20ProxyTicketValidator;
import org.jasig.cas.client.validation.TicketValidationException;
import org.jivesoftware.openfire.auth.UnauthorizedException;
import org.jivesoftware.util.Log;

/**
 * Validates CAS Proxy Tickets for <code>CASAuthProvider</code>.
 */
public class CASAuthTicketValidator {

	/**
	 * The CAS proxy ticket validator.
	 */
	private final Cas20ProxyTicketValidator proxyValidator;

	/**
	 * The service name.
	 */
	private final String serviceUrl;

	/**
	 * Construct a new ticket validator with a configuration provider and proxy
	 * validator.
	 * 
	 * @param config
	 *            The configration provider.
	 * @param proxyValidator
	 *            The CAS proxy validator to use.
	 */
	public CASAuthTicketValidator(
			final Cas20ProxyTicketValidator proxyValidator,
			final String serviceUrl) {
		this.proxyValidator = proxyValidator;
		this.serviceUrl = serviceUrl;
	}

	/**
	 * Authenticates the CAS proxy ticket. If the username and ticket are valid
	 * the method returns, otherwise an <code>UnauthorizedException</code> is
	 * thrown.
	 * 
	 * @param username
	 *            The username.
	 * @param proxyTicket
	 *            The CAS proxy ticket.
	 * 
	 * @throws org.jivesoftware.openfire.auth.UnauthorizedException
	 *             If the username and password do not match any existing user.
	 */
	public void authenticateCASProxyTicket(final String username,
			final String proxyTicket) throws UnauthorizedException {
		Assertion assertion = null;
		AttributePrincipal principal = null;

		if (Log.isDebugEnabled()) {
			Log
					.debug("CASAuthProvider: Contact CAS and validate proxy ticket '"
							+ proxyTicket
							+ "' for user '"
							+ username
							+ "' and service '" + serviceUrl + "'...");
		}

		// Connect to CAS and validate the proxy ticket
		try {
			assertion = proxyValidator.validate(proxyTicket, serviceUrl);
		} catch (final TicketValidationException tve) {
			Log.info("CASAuthProvider: TicketValidationException:" + tve);
			throw new UnauthorizedException(tve);
		}

		// Ensure that the user returned by CAS matches the user provided
		if (assertion == null) {
			final String message = "CAS ticket returned null assertion.";
			Log.info("CASAuthProvider: " + message);
			throw new UnauthorizedException(message);
		}

		principal = assertion.getPrincipal();
		if (principal == null) {
			final String message = "CAS ticket returned null principal.";
			Log.info("CASAuthProvider: " + message);
			throw new UnauthorizedException(message);
		}

		final String principalName = principal.getName();
		if (principalName == null) {
			final String message = "CAS ticket returned null user.";
			Log.info("CASAuthProvider: " + message);
			throw new UnauthorizedException(message);
		}

		if (!username.equalsIgnoreCase(principalName)) {
			final String message = "CAS ticket is not valid for user:'"
					+ username + "'.";
			Log.info("CASAuthProvider: " + message);
			throw new UnauthorizedException(message);
		}

		// The user is now authenticated.
		if (Log.isDebugEnabled()) {
			Log.debug("CASAuthProvider: The user '" + username
					+ "' is now authenticated.");
		}
	}

	/**
	 * Get the proxy validator.
	 * 
	 * @return The proxy validator.
	 */
	public Cas20ProxyTicketValidator getProxyValidator() {
		return proxyValidator;
	}

	/**
	 * Get the service URL.
	 * 
	 * @return The service URL.
	 */
	public String getServiceUrl() {
		return serviceUrl;
	}

}
