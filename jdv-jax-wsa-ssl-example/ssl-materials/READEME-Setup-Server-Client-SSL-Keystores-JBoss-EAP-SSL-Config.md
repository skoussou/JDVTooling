Configure Jboss AS 7 Server

See: http://www.examclouds.com/publications/ssl-in-soap-web-service.jsp

1. Generate a Keystore

First we need generate a secret key/certificate and store it in a "key store" file. It can be done with keytool utility. The password for encryption is "clouds".

	[stkousso@stkousso helloworld-ws-ssl]$ keytool -genkey -alias helloworldkey -keyalg RSA -keystore ssl.keystore
	Enter keystore password:  sslworkshop
	Re-enter new password: sslworkshop
	What is your first and last name?
	  [redhat.com]:  
	What is the name of your organizational unit?
	  [Red Hat]:  RedHat
	What is the name of your organization?
	  [Red Hat]:  RedHat
	What is the name of your City or Locality?
	  [London]:  Farnborough
	What is the name of your State or Province?
	  [Hampshire]:  Hampshire
	What is the two-letter country code for this unit?
	  [UK]:  UK
	Is CN=redhat.com, OU=RedHat, O=RedHat, L=Farnborough, ST=Hampshire, C=UK correct?
	  [no]:  yes

Enter key password for <helloworldkey>
	(RETURN if same as keystore password):  sslworkshop


2. Configure SSL Support on Jboss

To do it add a "SSL HTTP/1.1 Connector" entry in standalone/configuration/ standalone.xml file:

	<subsystem xmlns="urn:jboss:domain:web:1.1" default-virtual-server="default-host" native="false">
	    <connector name="http" protocol="HTTP/1.1" scheme="http" socket-binding="http"/>

	    <connector name="https" protocol="HTTP/1.1" scheme="https" socket-binding="https" secure="true">
		<ssl name="workshop-ssl" key-alias="helloworldkey" password="sslworkshop"
		certificate-key-file="../standalone/configuration/ssl.keystore" protocol="TLSv1"/>
	    </connector>

	    <virtual-server name="default-host" enable-welcome-root="true">
		       ...
	    </virtual-server>
	</subsystem>

3. Configure SSL to be required on the WEB Service by setting CONFIDENTIAL transport-guarantee

---	
	  <security-constraint>
	    <web-resource-collection>
	      <web-resource-name>AddressingService</web-resource-name>
	      <url-pattern>/*</url-pattern>
		    <http-method>POST</http-method>
		    <http-method>PUT</http-method>
		    <http-method>GET</http-method>
		    <http-method>DELETE</http-method>
	    </web-resource-collection>
	      <user-data-constraint>
		    <transport-guarantee>CONFIDENTIAL</transport-guarantee>
	      </user-data-constraint>
	    <!--auth-constraint>
	      <role-name>JBossAdmin</role-name>
	      <role-name>STSClient</role-name>
	    </auth-constraint-->
	  </security-constraint>
---

4. Import a Server Certificate to the Client Truststore

a) Export the Server Certificate
	
	keytool -export -alias helloworldkey -keystore ssl.keystore  -storepass sslworkshop -file server.cer@
	Certificate stored in file <server.cer>

b) Deliver the Server Certificate to the Client

- Copy generated on the previous step file <server.cer> to the client location.
- Create the Client Truststore and Import the Server Certificate to the Client Truststore

---	
	[stkousso@stkousso helloworld-ws-ssl]$ keytool -import -v -trustcacerts -alias helloworldkey -keystore client.jks -storepass mypass -keypass sslworkshop -file server.cer 
	Owner: CN=redhat.com, OU=RedHat, O=RedHat, L=Farnborough, ST=Hampshire, C=UK
	Issuer: CN=redhat.com, OU=RedHat, O=RedHat, L=Farnborough, ST=Hampshire, C=UK
	Serial number: 2ef44eb2
	Valid from: Wed Feb 08 22:23:38 GMT 2017 until: Tue May 09 23:23:38 BST 2017
	Certificate fingerprints:
		 MD5:  7E:3A:05:FD:5C:C9:0E:99:0F:E6:78:AD:F3:0F:F9:BA
		 SHA1: 31:E0:2A:BB:9A:C8:71:52:E8:91:D6:F2:DD:B4:91:95:B4:F9:BD:B9
		 SHA256: 73:48:79:E7:9A:02:C1:DE:48:BA:92:4F:6F:0C:DE:FF:B2:64:0D:A0:E6:F5:58:99:F6:EC:55:6A:0B:66:8F:5F
		 Signature algorithm name: SHA256withRSA
		 Version: 3

	Extensions: 

	#1: ObjectId: 2.5.29.14 Criticality=false
	SubjectKeyIdentifier [
	KeyIdentifier [
	0000: 00 1E A5 4D 6F FB 5A 22   B3 53 8D 4F 7C 16 93 3C  ...Mo.Z".S.O...<
	0010: 1F 55 0A B8                                        .U..
	]
	]

	Trust this certificate? [no]:  yes
	Certificate was added to keystore
	[Storing client.jks]
---
										       
5. Create client and use Client Truststore

- On eclipse add to "Run Configurations" for the main in SSLClient the following JVM settings

----
	-Djavax.net.ssl.trustStore=client_ts.jks
	-Djavax.net.ssl.trustStorePassword=mypass
	-Djavax.net.debug=all
----

6. CXF Clients can contain a ssl-jbossws-cxf.xml file

	<beans xmlns="http://www.springframework.org/schema/beans"
	    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	    xmlns:sec="http://cxf.apache.org/configuration/security"
	    xmlns:http-conf="http://cxf.apache.org/transports/http/configuration"
	    xmlns:jaxws="http://java.sun.com/xml/ns/jaxws"
	    xsi:schemaLocation="http://cxf.apache.org/transports/http/configuration http://cxf.apache.org/schemas/configuration/http-conf.xsd http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans-2.0.xsd http://cxf.apache.org/configuration/security http://cxf.apache.org/schemas/configuration/security.xsd">

	    <http-conf:conduit name="*.http-conduit">
		<http-conf:client ConnectionTimeout="120000" ReceiveTimeout="240000"/>
		<http-conf:tlsClientParameters secureSocketProtocol="SSL">
		  <sec:trustManagers>
		    <sec:keyStore type="JKS" password="mypass" file="../standalone/configuration/client.jks"/>
		  </sec:trustManagers>
		</http-conf:tlsClientParameters>
	    </http-conf:conduit>
	</beans>

- Configure TEIID client with this CXF configuration

----	
	batch
	/subsystem=resource-adapters/resource-adapter=webservice/connection-definitions=wsDS:add(jndi-name=java:/wsDS, class-name=org.teiid.resource.adapter.ws.WSManagedConnectionFactory, enabled=true, use-java-context=true)
	/subsystem=resource-adapters/resource-adapter=webservice/connection-definitions=wsDS/config-properties=ConfigFile:add(value=${jboss.server.home.dir}/standalone/configuration/xxx-jbossws-cxf.xml)
	/subsystem=resource-adapters/resource-adapter=webservice/connection-definitions=wsDS/config-properties=ConfigName:add(value=port_x)
	/subsystem=resource-adapters/resource-adapter=webservice:activate
	runbatch
----

7. Enable Client authentication (via certificate) on JBoss 

a)  Create a Client Keystore and Key (on the JDV side)

	[stkousso@stkousso configuration]$ keytool -genkey -alias client -keypass sslworkshop -storepass sslworkshop -keystore clientauth_ks.jks
	What is your first and last name?
	  [Unknown]:  SSL Wokshop Client
	What is the name of your organizational unit?
	  [Unknown]:  Red Hat Services
	What is the name of your organization?
	  [Unknown]:  Red Hat
	What is the name of your City or Locality?
	  [Unknown]:  London
	What is the name of your State or Province?
	  [Unknown]:  London
	What is the two-letter country code for this unit?
	  [Unknown]:  UK
	Is CN=SSL Wokshop Client, OU=Red Hat Services, O=Red Hat, L=London, ST=London, C=UK correct?

b) Import a Client Certificate to the Server Truststore (on the JDV Side and Trasnfer on the Server side ie. the EAP server to be authenticate the client)

	keytool -export -alias client -keystore clientauth_ks.jks -storepass sslworkshop -file client.cer
	Certificate stored in file <client.cer>


c) Deliver Client Certificate to the Server

	The <client.cer> file should be stored on the server, in "../standalone/configuration" directory for Jboss AS 7.
	Add the Client Certificate to the Server Truststore.

d) Import client certificate with keytool utility to the server truststore

	[stkousso@stkousso configuration]$ keytool -import -v -trustcacerts -alias client -keystore ssl.keystore -keypass sslworkshop -file client.cer
	Enter keystore password:  sslworkshop
	Owner: CN=SSL Wokshop Client, OU=Red Hat Services, O=Red Hat, L=London, ST=London, C=UK
	Issuer: CN=SSL Wokshop Client, OU=Red Hat Services, O=Red Hat, L=London, ST=London, C=UK
	Serial number: ce8bbc8
	Valid from: Fri Mar 03 06:41:26 GMT 2017 until: Thu Jun 01 07:41:26 BST 2017
	Certificate fingerprints:
		 MD5:  89:C1:DD:5E:A4:A4:0F:03:E9:7B:2E:04:0C:FB:46:52
		 SHA1: D7:93:10:03:6D:7A:DE:4D:CF:E4:F9:0B:9B:E5:9C:FE:12:34:27:F1
		 SHA256: B6:38:A5:97:B9:73:91:37:FC:A8:D0:C3:BC:A5:70:A6:62:F9:99:7B:AE:DF:97:57:D8:9F:6F:72:D4:D2:68:03
		 Signature algorithm name: SHA1withDSA
		 Version: 3

	Extensions: 

	#1: ObjectId: 2.5.29.14 Criticality=false
	SubjectKeyIdentifier [
	KeyIdentifier [
	0000: 52 3A D4 74 D9 FE 16 0B   EE 49 A9 5B A3 F4 FE A1  R:.t.....I.[....
	0010: 51 6D 95 A2                                        Qm..
	]
	]

	Trust this certificate? [no]:  yes
	Certificate was added to keystore
	[Storing ssl.keystore]

e) Configure Client Certificate Auth on Jboss (2-way SSL)

To do it add a "SSL HTTP/1.1 Connector" entry in standalone/configuration/ standalone.xml file:

	<subsystem xmlns="urn:jboss:domain:web:1.1" default-virtual-server="default-host" native="false">
	    <connector name="http" protocol="HTTP/1.1" scheme="http" socket-binding="http"/>

	    <connector name="https" protocol="HTTP/1.1" scheme="https" socket-binding="https" secure="true">
		<ssl name="workshop-ssl" key-alias="helloworldkey" password="sslworkshop"
		certificate-key-file="../standalone/configuration/ssl.keystore" protocol="TLSv1"
		 verify-client="true" ca-certificate-file="../standalone/configuration/ssl.keystore"/>
	    </connector>

	    <virtual-server name="default-host" enable-welcome-root="true">
		       ...
	    </virtual-server>
	</subsystem>

f) Enable Application auth via Client Certificate - Modify web.xml

Authentication method should be set to CLIENT-CERT.

	<security-constraint>
	      <web-resource-collection>
		    <web-resource-name>ECCollection</web-resource-name>
		    <url-pattern>/ExamClouds</url-pattern>
		    <http-method>POST</http-method>
	      </web-resource-collection>
	      <user-data-constraint>
		    <transport-guarantee>CONFIDENTIAL</transport-guarantee>
	      </user-data-constraint>
	</security-constraint>

	<login-config>
	      <auth-method>CLIENT-CERT</auth-method>
	</login-config>


2.8 Run a Web Service Client

The same as in 1.6. The only change that should be done is adding client keystore to the JVM parameters:

8. CXF Clients can contain a ssl-jbossws-cxf.xml file

	<beans xmlns="http://www.springframework.org/schema/beans"
	    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	    xmlns:sec="http://cxf.apache.org/configuration/security"
	    xmlns:http-conf="http://cxf.apache.org/transports/http/configuration"
	    xmlns:jaxws="http://java.sun.com/xml/ns/jaxws"
	    xsi:schemaLocation="http://cxf.apache.org/transports/http/configuration http://cxf.apache.org/schemas/configuration/http-conf.xsd http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans-2.0.xsd http://cxf.apache.org/configuration/security http://cxf.apache.org/schemas/configuration/security.xsd">

	    <http-conf:conduit name="*.http-conduit">
	<!-- WARNING ! disableCNcheck=true should NOT be used in production disableCNcheck="true"  -->

		<http-conf:client ConnectionTimeout="120000" ReceiveTimeout="240000"/>
		<http-conf:tlsClientParameters disableCNCheck="true" secureSocketProtocol="SSL"  >
		  <sec:trustManagers>
		    <sec:keyStore type="JKS" password="mypass" file="../standalone/configuration/client.jks"/>
		  </sec:trustManagers>
		  <sec:keyManagers keyPassword="sslworkshop">
		    <sec:keyStore type="JKS" password="sslworkshop" file="../standalone/configuration/clientauth_ks.jks"/>
		  </sec:keyManagers>
		</http-conf:tlsClientParameters>
	    </http-conf:conduit>
	</beans>


9. Create client and use Client Truststore

	-Djavax.net.ssl.trustStore=client_ts.jks
	-Djavax.net.ssl.trustStorePassword=mypass
	-Djavax.net.ssl.keyStore=clientauth_ks.jks
	-Djavax.net.ssl.keyStorePassword=sslworkshop
	-Djavax.net.debug=all
