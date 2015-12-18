/*
 * Openfire CAS Auth Provider
 * Copyright (C) 2010 Surevine Limited
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see {http://www.gnu.org/licenses/}.
 */

package com.surevine.chat.openfire.auth;

import org.jasig.cas.client.authentication.AttributePrincipal;
import org.jasig.cas.client.validation.Assertion;
import org.jasig.cas.client.validation.Cas10TicketValidator;
import org.jasig.cas.client.validation.Cas20ProxyTicketValidator;
import org.jasig.cas.client.validation.TicketValidationException;
import org.jivesoftware.openfire.auth.UnauthorizedException;
import org.jivesoftware.util.Log;

/**
 * Validates CAS Proxy Tickets for <code>CASAuthProvider</code>.
 */
public class CASAuthTicketValidator {

    /**
     * The CAS 1.0 ticket validator.
     */
    private final Cas10TicketValidator ticketValidator;

    /**
     * The CAS proxy ticket validator.
     */
    private final Cas20ProxyTicketValidator proxyValidator;

    /**
     * The service name.
     */
    private final String serviceUrl;

    /**
     * Indicate use proxy validator whether or not.
     */
    private final boolean isUseProxyValidator;

    /**
     * Construct a new ticket validator with a configuration provider and proxy
     * validator.
     *
     * @param proxyValidator The CAS proxy validator to use.
     * @param serviceUrl     The service Url to validate.
     */
    public CASAuthTicketValidator(final Cas20ProxyTicketValidator proxyValidator, final String serviceUrl) {
        this.proxyValidator = proxyValidator;
        this.serviceUrl = serviceUrl;
        this.ticketValidator = null;
        this.isUseProxyValidator = true;
    }

    /**
     * Construct a new ticket validator with a configuration provider and proxy
     * validator.
     *
     * @param ticketValidator The CAS 1.0 validator to use.
     * @param serviceUrl      The service Url to validate.
     */
    public CASAuthTicketValidator(final Cas10TicketValidator ticketValidator, final String serviceUrl) {
        this.ticketValidator = ticketValidator;
        this.serviceUrl = serviceUrl;
        this.proxyValidator = null;
        this.isUseProxyValidator = false;
    }

    /**
     * Authenticates the CAS proxy ticket. If the username and ticket are valid
     * the method returns, otherwise an <code>UnauthorizedException</code> is
     * thrown.
     *
     * @param username The username.
     * @param ticket   The CAS ticket.
     * @throws org.jivesoftware.openfire.auth.UnauthorizedException If the username and password do not match any existing user.
     */
    public void authenticateCASTicket(final String username,
                                      final String ticket) throws UnauthorizedException {
        Assertion assertion = null;
        AttributePrincipal principal = null;

        if (Log.isDebugEnabled()) {
            Log
                    .debug("CASAuthProvider: Contact CAS and validate proxy ticket '"
                            + ticket
                            + "' for user '"
                            + username
                            + "' and service '" + serviceUrl + "'...");
        }

        // Connect to CAS and validate the proxy ticket
        try {
            if (!this.isUseProxyValidator) {
                assertion = ticketValidator != null ? ticketValidator.validate(ticket, serviceUrl) : null;
            } else {
                assertion = proxyValidator != null ? proxyValidator.validate(ticket, serviceUrl) : null;
            }
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

//        if (!username.equalsIgnoreCase(principalName)) {
//            final String message = "CAS ticket is not valid for user:'"
//                    + username + "'.";
//            Log.info("CASAuthProvider: " + message);
//            throw new UnauthorizedException(message);
//        }

        // The user is now authenticated.
        if (Log.isDebugEnabled()) {
            Log.debug("CASAuthProvider: The user '" + username
                    + "' is now authenticated.");
        }
        Log.info("CASAuthProvider: The user '" + username
                + "' is now authenticated.");
    }

    public Cas10TicketValidator getTicketValidator() {
        return ticketValidator;
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


    public boolean isUseProxyValidator() {
        return isUseProxyValidator;
    }

}
