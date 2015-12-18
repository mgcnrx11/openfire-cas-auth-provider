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

import org.apache.http.NameValuePair;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import org.jasig.cas.client.authentication.AttributePrincipal;
import org.jasig.cas.client.validation.Assertion;
import org.jasig.cas.client.validation.Cas10TicketValidator;
import org.jasig.cas.client.validation.Cas20ProxyTicketValidator;
import org.jasig.cas.client.validation.TicketValidationException;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CASAuthTicketValidatorTest {
    /**
     * The CAS Server URL Prefix to test with
     */
    static String TEST_CAS_SERVER_URL_PREFIX = "http://192.168.52.176:14562/cas/";
    /**
     * The CAS REST Server URL to get the TGT and ST
     */
    static String TEST_CAS_REST_SERVER_URL = "http://192.168.52.176:14562/cas/v1/tickets";
    /**
     * The service URL to test with
     */
    static String SERVICE_URL = "http://test.service.url/";

    /**
     * The default username to use in the tests
     */
    static String TEST_USERNAME = "hn01";
    /**
     * The default password to use in the tests
     */
    static String TEST_PASSWORD = "123456";
    /**
     * The default username to use in the tests
     */
    static String TEST_USERNAME_MIXED_CASE = "tEst_UseR";

    /**
     * The default service ticket to use in the tests
     */
    static String TEST_PROXY_TICKET = "ST-TEST-SERVICE-TICKET";

    /**
     * The blank service ticket to write to in the test
     */
    static String TEST_SERVICE_TICKET = "";

    /**
     * The switch to use Proxy Validator or not
     */
    static boolean isUseProxyValidator = false;

    /**
     * Class under test
     */
    CASAuthTicketValidator validator;

    /**
     * The CAS 1.0 ticket validator
     */
    Cas10TicketValidator ticketValidator;

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
     *
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception {
        if (isUseProxyValidator) {
            proxyValidator = mock(Cas20ProxyTicketValidator.class);
            validator = new CASAuthTicketValidator(proxyValidator, SERVICE_URL);
            assertion = mock(Assertion.class);
            principal = mock(AttributePrincipal.class);

            when(proxyValidator.validate(TEST_PROXY_TICKET, SERVICE_URL))
                    .thenReturn(assertion);

            when(assertion.getPrincipal()).thenReturn(principal);

            when(principal.getName()).thenReturn(TEST_USERNAME);
        } else {
            // CAS10Ticket
            ticketValidator = new Cas10TicketValidator(TEST_CAS_SERVER_URL_PREFIX);
            validator = new CASAuthTicketValidator(ticketValidator, SERVICE_URL);

            assertion = mock(Assertion.class);
            principal = mock(AttributePrincipal.class);

        }

        // Use REST API to get the TEST SERVICE TICKET
        String tgt = getTicketGrantingTicket(TEST_CAS_REST_SERVER_URL, TEST_USERNAME, TEST_PASSWORD);
        TEST_SERVICE_TICKET = getServiceTicket(TEST_CAS_REST_SERVER_URL, tgt, SERVICE_URL);

    }

    /**
     * Test that the class has been correctly initialised
     */
    @Test
    public void testCorrectlyInitilised() {
        assertEquals("Proxy Validator not correctly initialised", proxyValidator, validator.getProxyValidator());
        assertEquals("Service URL not correctly initialised", SERVICE_URL, validator.getServiceUrl());
    }

    /**
     * Test the authenticateCASProxyTicket method happy flow
     *
     * @see CASAuthTicketValidator#authenticateCASTicket(String, String)
     */
    @Test
    public void testAuthenticateCASTicket() throws Exception {
        validator.authenticateCASTicket(TEST_USERNAME, TEST_SERVICE_TICKET);
    }

    /**
     * Test the authenticateCASProxyTicket method happy flow
     *
     * @see CASAuthTicketValidator#authenticateCASTicket(String, String)
     */
    @Test
    public void testAuthenticateCASProxyTicketMixedCaseUsername() throws Exception {
        validator.authenticateCASTicket(TEST_USERNAME_MIXED_CASE, TEST_SERVICE_TICKET);
    }

    /**
     * Test the authenticateCASProxyTicket method with failed validation
     *
     * @see CASAuthTicketValidator#authenticateCASTicket(String, String)
     */
    @Test(expected = org.jivesoftware.openfire.auth.UnauthorizedException.class)
    public void testAuthenticateCASProxyTicketWithFailedValidation() throws Exception {
        when(proxyValidator.validate(TEST_PROXY_TICKET, SERVICE_URL))
                .thenThrow(new TicketValidationException("Failed validation"));

        validator.authenticateCASTicket(TEST_USERNAME, TEST_SERVICE_TICKET);
    }

    /**
     * Test the authenticateCASProxyTicket method with incorrect username
     *
     * @see CASAuthTicketValidator#authenticateCASTicket(String, String)
     */
    @Test(expected = org.jivesoftware.openfire.auth.UnauthorizedException.class)
    public void testAuthenticateCASProxyTicketWithIncorrectUsername() throws Exception {
        validator.authenticateCASTicket("incorrect_username", TEST_SERVICE_TICKET);
    }

    /**
     * 根据用户名密码登录获取GTG票据
     *
     * @param server   单点登录地址
     * @param username 用户名
     * @param password 密码
     * @return
     */
    public String getTicketGrantingTicket(String server, String username, String password) {
        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost post = new HttpPost(server);
        //构建用户名密码，模拟登录
        NameValuePair[] nvs = new NameValuePair[2];
        nvs[0] = new BasicNameValuePair("username", username);
        nvs[1] = new BasicNameValuePair("password", password);
        StringEntity entity = new StringEntity(nvs[0].toString() + "&" + nvs[1].toString(), ContentType.APPLICATION_FORM_URLENCODED);
        post.setEntity(entity);
        try {
            CloseableHttpResponse response = client.execute(post);
            String resString = EntityUtils.toString(response.getEntity());
            switch (response.getStatusLine().getStatusCode()) {
                case 201: {
                    final Matcher matcher = Pattern.compile(".*action=\".*/(.*?)\".*").matcher(resString);
                    if (matcher.matches()) {
                        //返回GTG票据
                        return matcher.group(1);
                    }
                    break;
                }
                default:
                    final Matcher matcher = Pattern.compile(".*action=\".*/(.*?)\".*").matcher(resString);
                    if (matcher.matches()) {
                        //返回GTG票据
                        return matcher.group(1);
                    }
                    break;
            }
        } catch (final IOException e) {
        } finally {
            post.releaseConnection();
        }
        return null;
    }

    /**
     * 根据获取的票据，到单点登录服务器获取 ST票据
     *
     * @param server               单点登录服务器登录地址
     * @param ticketGrantingTicket GTG票据
     * @param service              要登录的服务地址
     * @return
     */
    public String getServiceTicket(String server, String ticketGrantingTicket, String service) {
        if (null == ticketGrantingTicket || ticketGrantingTicket.isEmpty()) {
            return null;
        }
        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost post = new HttpPost(server + "/" + ticketGrantingTicket);
        NameValuePair[] serviceList = new NameValuePair[1];
        serviceList[0] = new BasicNameValuePair("service", service);
        StringEntity entity = new StringEntity(serviceList[0].toString(), ContentType.APPLICATION_FORM_URLENCODED);
        post.setEntity(entity);
        try {
            CloseableHttpResponse response = client.execute(post);
            String resString = EntityUtils.toString(response.getEntity());
            switch (response.getStatusLine().getStatusCode()) {
                case 200:
                    //返回ST票据
                    return resString;
                default:
                    break;
            }
        } catch (final IOException e) {
        } finally {
            post.releaseConnection();
        }
        return null;
    }

}
