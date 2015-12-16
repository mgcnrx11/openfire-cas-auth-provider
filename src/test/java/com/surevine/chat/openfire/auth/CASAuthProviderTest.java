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

import org.jivesoftware.openfire.auth.ConnectionException;
import org.jivesoftware.openfire.auth.InternalUnauthenticatedException;
import org.jivesoftware.openfire.auth.UnauthorizedException;
import org.junit.Before;
import org.junit.Test;

import javax.naming.ConfigurationException;

import static org.junit.Assert.*;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;

/**
 * Test case for CASAuthProvider
 *
 * @see CASAuthProvider
 */
public class CASAuthProviderTest {

    private static String AUTH_SUCCESS_USERNAME = "authorised_user";
    private static String AUTH_SUCCESS_PASSWORD = "authorised_password";
    private static String AUTH_FAILURE_USERNAME = "unauthorised_user";
    private static String AUTH_FAILURE_PASSWORD = "unauthorised_password";

    /**
     * The class under test
     */
    CASAuthProvider authProvider;

    /**
     * A mock configuration
     */
    CASAuthProviderConfig config;

    /**
     * The mocked ticket validator for the class under test
     */
    CASAuthTicketValidator ticketValidator;

    /**
     * Set up the test object structure
     *
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception {
        ticketValidator = mock(CASAuthTicketValidator.class);

        authProvider = new CASAuthProvider(ticketValidator);

        doThrow(
                new org.jivesoftware.openfire.auth.UnauthorizedException(
                        "Could not authenticate user")).when(ticketValidator)
                .authenticateCASProxyTicket(AUTH_FAILURE_USERNAME,
                        AUTH_FAILURE_PASSWORD);

        authProvider.ticketValidator = ticketValidator;
    }

//	/**
//	 * Test the default constructor
//	 */
//	@Test
//	public void testCASAuthProvider() throws ConfigurationException {
//		this.authProvider = new CASAuthProvider();
//
//		assertNotNull("ticketValidator has not been initialised",
//				authProvider.ticketValidator);
//	}

    /**
     * Test the constructor with a CASAuthTicketValidator parameter
     */
    @Test
    public void testCASAuthProviderCASAuthTicketValidator()
            throws ConfigurationException {
        assertEquals("The ticket validator has not been correctly initialised",
                authProvider.ticketValidator, ticketValidator);
    }

    /**
     * Test the isPlainSupported method
     */
    @Test
    public void testIsPlainSupported() {
        assertTrue("Plain should be supported", authProvider.isPlainSupported());
    }

    @Test
    public void testIsDigestSupported() {
        assertFalse("Digest should not be supported", authProvider
                .isDigestSupported());
    }

    @Test
    public void testAuthenticateStringStringSuccess() throws Exception {
        // The mocked ticket validator will do nothing, representing
        // success. We just need to ensure it doesn't throw an
        // exception
        authProvider.authenticate(AUTH_SUCCESS_USERNAME, AUTH_SUCCESS_PASSWORD);
    }

    @Test(expected = UnauthorizedException.class)
    public void testAuthenticateStringStringFailure()
            throws UnauthorizedException, ConnectionException,
            InternalUnauthenticatedException {
        authProvider.authenticate(AUTH_FAILURE_USERNAME, AUTH_FAILURE_PASSWORD);
    }

    @Test(expected = java.lang.UnsupportedOperationException.class)
    public void testAuthenticateStringStringString() throws Exception {
        // Doesn't really matter what the username/password is
        // So we'll just use the failure username and password
        authProvider.authenticate(AUTH_FAILURE_USERNAME, AUTH_FAILURE_PASSWORD,
                "digest");
    }

    @Test(expected = java.lang.UnsupportedOperationException.class)
    public void testGetPassword() throws Exception {
        // Doesn't really matter what the username/password is
        // So we'll just use the failure username and password
        authProvider.getPassword(AUTH_FAILURE_USERNAME);
    }

    @Test(expected = java.lang.UnsupportedOperationException.class)
    public void testSetPassword() throws Exception {
        // Doesn't really matter what the username/password is
        // So we'll just use the failure username and password
        authProvider.setPassword(AUTH_FAILURE_USERNAME, AUTH_FAILURE_PASSWORD);
    }

    @Test
    public void testSupportsPasswordRetrieval() {
        assertFalse("Password retrieval should not be supported", authProvider
                .supportsPasswordRetrieval());
    }

}
