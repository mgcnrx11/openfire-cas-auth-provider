/*
 * Openfire CAS Auth Provider - Allows authentication with Openfire via CAS
 * Copyright (C) 2009 Surevine Ltd.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
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

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.jasig.cas.client.authentication.AttributePrincipal;
import org.jasig.cas.client.validation.Assertion;
import org.jasig.cas.client.validation.Cas20ProxyTicketValidator;
import org.jasig.cas.client.validation.TicketValidationException;
import org.junit.Before;
import org.junit.Test;

import com.surevine.chat.openfire.auth.CASAuthTicketValidator;

public class CASAuthTicketValidatorTest
{
	/**
	 * The service URL to test with
	 */
	static String SERVICE_URL = "http://test.service.url/";

	/**
	 * The default username to use in the tests
	 */
	static String TEST_USERNAME = "test_user";

	/**
	 * The default username to use in the tests
	 */
	static String TEST_USERNAME_MIXED_CASE = "tEst_UseR";
	
	/**
	 * The default service ticket to use in the tests
	 */
	static String TEST_PROXY_TICKET = "ST-TEST-SERVICE-TICKET";
	
	/**
	 * Class under test
	 */
	CASAuthTicketValidator validator;
	
	/**
	 * The mocked proxy ticket validator
	 */
	Cas20ProxyTicketValidator proxyValidator;

	/**
	 * The mocked assertion
	 */
	Assertion assertion;

	/**
	 * The mocked attribute principal
	 */
	AttributePrincipal principal;

	/**
	 * Set up the objects for the test case
	 * @throws Exception
	 */
	@Before
	public void setUp() throws Exception
	{
		proxyValidator = mock(Cas20ProxyTicketValidator.class);
		validator = new CASAuthTicketValidator(proxyValidator, SERVICE_URL);
		assertion = mock(Assertion.class);
		principal = mock(AttributePrincipal.class);
		
		when(proxyValidator.validate(TEST_PROXY_TICKET, SERVICE_URL))
			.thenReturn(assertion);
		
		when(assertion.getPrincipal()).thenReturn(principal);
		
		when(principal.getName()).thenReturn(TEST_USERNAME);
	}

	/**
	 * Test that the class has been correctly initialised
	 */
	@Test
	public void testCorrectlyInitilised()
	{
		assertEquals("Proxy Validator not correctly initialised", proxyValidator, validator.getProxyValidator());
		assertEquals("Service URL not correctly initialised", SERVICE_URL, validator.getServiceUrl());
	}

	/**
	 * Test the authenticateCASProxyTicket method happy flow
	 * @see CASAuthTicketValidator#authenticateCASProxyTicket(String, String)
	 */
	@Test
	public void testAuthenticateCASProxyTicket() throws Exception
	{
		validator.authenticateCASProxyTicket(TEST_USERNAME, TEST_PROXY_TICKET);
	}
	
	/**
	 * Test the authenticateCASProxyTicket method happy flow
	 * @see CASAuthTicketValidator#authenticateCASProxyTicket(String, String)
	 */
	@Test
	public void testAuthenticateCASProxyTicketMixedCaseUsername() throws Exception
	{
		validator.authenticateCASProxyTicket(TEST_USERNAME_MIXED_CASE, TEST_PROXY_TICKET);
	}	

	/**
	 * Test the authenticateCASProxyTicket method with failed validation
	 * @see CASAuthTicketValidator#authenticateCASProxyTicket(String, String)
	 */
	@Test(expected = org.jivesoftware.openfire.auth.UnauthorizedException.class)
	public void testAuthenticateCASProxyTicketWithFailedValidation() throws Exception
	{
		when(proxyValidator.validate(TEST_PROXY_TICKET, SERVICE_URL))
			.thenThrow(new TicketValidationException("Failed validation"));
		
		validator.authenticateCASProxyTicket(TEST_USERNAME, TEST_PROXY_TICKET);
	}

	/**
	 * Test the authenticateCASProxyTicket method with incorrect username
	 * @see CASAuthTicketValidator#authenticateCASProxyTicket(String, String)
	 */
	@Test(expected = org.jivesoftware.openfire.auth.UnauthorizedException.class)
	public void testAuthenticateCASProxyTicketWithIncorrectUsername() throws Exception
	{
		validator.authenticateCASProxyTicket("incorrect_username", TEST_PROXY_TICKET);
	}
}
