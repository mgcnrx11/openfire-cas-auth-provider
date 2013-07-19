package com.surevine.chat.openfire.auth;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.surevine.chat.openfire.auth.CASAuthProviderConfig;

public class CASAuthProviderConfigTest {

    private static String TEST_CAS_SERVER_URL_PREFIX = "http://test.cas.server/cas/";
    private static String TEST_SERVICE_NAME = "http://test.chat.server/chat/";
    private static String TEST_PROXY_CLIENT = "http://test.chat.server/chat/proxy";

    /**
     * Class under test
     */
    private CASAuthProviderConfig casAuthProviderConfig;

    /**
     * Mocked JiveProperties
     */
    private Map<String, String> jiveProperties;

    @Before
    public void setUp() throws Exception {
	jiveProperties = new HashMap<String, String>();

	jiveProperties.put("casAuthProvider.casServerUrlPrefix",
		TEST_CAS_SERVER_URL_PREFIX);
	jiveProperties.put("casAuthProvider.serviceName", TEST_SERVICE_NAME);

	casAuthProviderConfig = new CASAuthProviderConfig(jiveProperties);
    }

    // /**
    // * Test the singleton pattern
    // * @see CASAuthProviderConfig#getInstance()
    // */
    // @Test
    // public void testGetInstance() {
    // // Ensure that getInstance returns the same object
    // CASAuthProviderConfig config1 = CASAuthProviderConfig.getInstance();
    // CASAuthProviderConfig config2 = CASAuthProviderConfig.getInstance();
    //		
    // assertNotNull("getInstance() returns null", config1);
    // assertTrue("getInstance() returns different objects", config1 ==
    // config2);
    // }

    /**
     * Test the getCASServerUrl method
     * 
     * @see CASAuthProviderConfig#getCASServerUrlPrefix()
     */
    @Test
    public void testGetCASServerUrlPrefix() {
	assertEquals("getCASServerUrlPrefix() returns the wrong string",
		TEST_CAS_SERVER_URL_PREFIX, casAuthProviderConfig
			.getCASServerUrlPrefix());
    };

    /**
     * Test the getServiceName method
     * 
     * @see CASAuthProviderConfig#getServiceName()
     */
    @Test
    public void testGetServiceName() {
	assertEquals("getServiceName() returns the wrong string",
		TEST_SERVICE_NAME, casAuthProviderConfig.getServiceName());
    }

    /**
     * Test the getProxyChain method
     * 
     * @see CASAuthProviderConfig#getProxyChain()
     */
    @Test
    public void testGetProxyChain() {
	// We test adding various numbers of proxies
	for (int i = 0; i < 10; ++i) {
	    // Add the extra property
	    jiveProperties.put("casAuthProvider.proxyClient" + i,
		    TEST_PROXY_CLIENT + i);

	    List<String[]> proxies = casAuthProviderConfig.getProxyChain();

	    // Ensure the number of proxies returned is correct
	    assertEquals(
		    "getProxyChain() returned wrong number of proxies for " + i
			    + "proxies", i + 1, proxies.size());

	    // And go through and check they are all correct
	    for (int j = 0; j <= i; ++j) {
		assertEquals("getProxyChain() returned incorrect proxy for "
			+ j + " with " + i + " proxies", TEST_PROXY_CLIENT + j,
			proxies.get(j)[0]);
	    }
	}
    }
}
