This is a JDV to SOAP Webservice with 2-way SSL setup and WS-Addressing Headers example
==========================================================================================

* jaxws-addressing: Exposes A WS-addressing JAX-WS Web Service and has had the web.xml modified to be authenticated by a Client Certificate
* ssl-materials   : Contains all the materials to setup a JDV (6.3) Client and an EAP server side with Certificates for both Server and Client 2-way SSL handshakes
* JDV-MultiSource-Project : Contains AddressingServiceView_4 model which connects via the AddressingServiceView_4 resource adapter to the 2-way ssl authenticated jaxws-addressing web service. It has a WSA-Models.vdb which you can deploy on JDV 6.3 and Teiid-JDBC-Test-Queries.sql which you need to execute over TEIID JDBC Driver against the VDB to call the web service
* PATCH: There is currently a bug https://issues.jboss.org/browse/TEIID-4755 due to the CXF behavior change. Patch the JDV server with this in order to ensure the translator correctly transforms the SOAP Response

For more details read: Example-JDV-JAX-WSA-2-WAY-SSL-Instructions.odt
