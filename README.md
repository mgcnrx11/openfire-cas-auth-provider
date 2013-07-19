openfire-cas-auth-provider
==========================

An AuthProvider for openfire which will accept a CAS proxy ticket, instead of a password, for a user. The ticket will be validated against CAS and the use will be authenticated if all of the following are true:

* The ticket is valid
* This ticket is for the correct user
* The proxy chain is correct

Installation
------------
1. Build the package

		mvn clean package

2. Copy the jar-with-dependencies into the openfire/lib directory

		cp target/openfire-cas-plugin-x.x.x-with-dependencies.jar /opt/openfire/lib
		
3. Add/update the following openfire server properties:

		hybridAuthProvider.primaryProvider.className = [current value of provider.auth.className]
		
		provider.auth.className = org.jivesoftware.openfire.auth.HybridAuthProvider
	
		hybridAuthProvider.secondaryProvider.className = com.surevine.chat.openfire.auth.CASAuthProvider

4. Add any extra configuration properties as required (see *Configuration* below)

5. Restart openfire

		/etc/init.d/openfire restart
		
Configuration
-------------
The following properties are available:

| Property | Description | Example | Required? |
| -------- | ----------- | ------- | --------- |
| `casAuthProvider.casServerUrlPrefix` | Tells openfire where it can find the cas services | https://cas.example.com/cas | Yes |
| `casAuthProvider.proxyClient0` | Tells openfire what urls to expect in the proxy chain. You can add more urls to the proxy chain by adding more properties with incremental numbers, e.g. `casAuthProvider.proxyClient1` | https://chat.example.com/chat/proxyCallback | Yes |
| `casAuthProvider.serviceName` | Tells openfire the service used by the original application. CAS will expect this to match the service used when generating the ticket. | https://chat.example.com/chat/ | Yes |

