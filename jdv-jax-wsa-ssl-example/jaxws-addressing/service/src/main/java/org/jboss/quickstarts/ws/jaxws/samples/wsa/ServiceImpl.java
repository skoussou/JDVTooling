/*
 * JBoss, Home of Professional Open Source
 * Copyright 2015, Red Hat, Inc. and/or its affiliates, and individual
 * contributors by the @authors tag. See the copyright.txt in the
 * distribution for a full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.quickstarts.ws.jaxws.samples.wsa;

import org.apache.cxf.interceptor.InInterceptors;
import org.apache.cxf.interceptor.OutInterceptors;
import org.jboss.quickstarts.ws.jaxws.samples.wsa.ServiceIface;

import javax.jws.WebService;
import javax.xml.ws.soap.Addressing;

/**
 * @author rsearls@redhat.com
 */
@WebService(
    portName = "AddressingServicePort",
    serviceName = "AddressingService",
    wsdlLocation = "WEB-INF/wsdl/AddressingService.wsdl",
    targetNamespace = "http://www.jboss.org/jbossws/ws-extensions/wsaddressing",
    endpointInterface = "org.jboss.quickstarts.ws.jaxws.samples.wsa.ServiceIface")
@Addressing(enabled = true, required = true)
@InInterceptors(interceptors = "org.apache.cxf.interceptor.LoggingInInterceptor")
@OutInterceptors(interceptors = "org.apache.cxf.interceptor.LoggingOutInterceptor")
@org.apache.cxf.annotations.Logging(pretty=true, limit=1024*1024)
public class ServiceImpl implements ServiceIface
{
	
    public String sayHello()
    {
        return "Hello World!";
    }
}
