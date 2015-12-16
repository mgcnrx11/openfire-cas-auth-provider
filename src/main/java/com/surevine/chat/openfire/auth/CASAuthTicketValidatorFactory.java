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
     * @param config The configuration to use when constructing the instance.
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
