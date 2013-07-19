/*
 * Surevine Limited.
 * 
 * Copyright 2009. All Rights Reserved.
 */
package com.surevine.chat.openfire.auth;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Provides access to the configuration required for creating
 * <code>CASAuthProvider</code> instances.
 */
public class CASAuthProviderConfig {

	/**
	 * Key corresponding to the CAS server URL prefix.
	 */
	private static final String CAS_SERVER_URL_PREFIX = "casAuthProvider.casServerUrlPrefix";

	/**
	 * Key corresponding to the service name.
	 */
	private static final String SERVICE_NAME = "casAuthProvider.serviceName";

	/**
	 * Key corresponding to the proxy client.
	 */
	private static final String PROXY_CLIENT = "casAuthProvider.proxyClient";

	/**
	 * Map of configuration properties.
	 */
	private Map<String, String> configProperties;

	/**
	 * Constructs instances of <code>CASAuthProviderConfig</code> with the map
	 * of configuration properties used to configure the instance.
	 * 
	 * @param configProperties
	 *            The configuration properties for the instance.
	 */
	CASAuthProviderConfig(final Map<String, String> configProperties) {
		if (configProperties == null) {
			throw new IllegalArgumentException(
					"Configuration properties not defined");
		}
		this.configProperties = configProperties;
	}

	/**
	 * Get the prefix of the CAS server which should be of the form
	 * <code>protocol://server:port</code>. <br />
	 * This corresponds to the configuration value
	 * <code>casAuthProvider.casServerUrlPrefix</code>.
	 * 
	 * @return The prefix of the CAS server if defined, otherwise
	 *         <code>null</code>.
	 */
	public String getCASServerUrlPrefix() {
		return configProperties.get(CAS_SERVER_URL_PREFIX);
	}

	/**
	 * Get the service name which defines the service to validate the ticket
	 * against with CAS. <br />
	 * This corresponds to the configuration value
	 * <code>casAuthProvider.serviceName</code>.
	 * 
	 * @return The service name to validate the ticket against if defined,
	 *         otherwise <code>null</code>.
	 */
	public String getServiceName() {
		return configProperties.get(SERVICE_NAME);
	}

	/**
	 * Get the proxy chain which defines the CAS proxy route. <br />
	 * This corresponds to the configuration value
	 * <code>casAuthProvider.proxyClient[n]</code> where <code>n</code> is the
	 * index of the client starting from 0.
	 * 
	 * @return A <code>List</code> of string arrays defining the proxy chain if
	 *         defined, otherwise an empty <code>List</code>.
	 */
	public List<String[]> getProxyChain() {
		List<String[]> proxyChain = new ArrayList<String[]>();
		int i = 0;
		String proxyClient = null;

		// Go through all configuration properties and build up the proxy list
		while ((proxyClient = configProperties.get(PROXY_CLIENT + i)) != null) {
			String[] proxyEntry = new String[1];
			proxyEntry[0] = proxyClient;
			proxyChain.add(proxyEntry);
			++i;
		}

		return proxyChain;
	}
}
