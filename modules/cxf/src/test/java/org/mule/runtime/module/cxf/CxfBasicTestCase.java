/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.runtime.module.cxf;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mule.runtime.module.http.api.client.HttpRequestOptionsBuilder.newOptions;
import org.mule.functional.junit4.FunctionalTestCase;
import org.mule.runtime.api.metadata.MediaType;
import org.mule.runtime.core.api.message.InternalMessage;
import org.mule.runtime.core.api.client.MuleClient;
import org.mule.runtime.core.util.IOUtils;
import org.mule.runtime.module.http.api.HttpConstants.Methods;
import org.mule.runtime.module.http.api.client.HttpRequestOptions;
import org.mule.runtime.module.xml.util.XMLUtils;
import org.mule.tck.junit4.rule.DynamicPort;

import java.io.InputStream;

import javax.xml.transform.TransformerFactoryConfigurationError;

import org.custommonkey.xmlunit.XMLUnit;
import org.junit.Rule;
import org.junit.Test;

public class CxfBasicTestCase extends FunctionalTestCase {

  public static final MediaType APP_SOAP_XML = MediaType.create("application", "soap+xml");

  private static final HttpRequestOptions HTTP_REQUEST_OPTIONS = newOptions().method(Methods.POST.name()).build();

  private String echoWsdl;

  @Rule
  public DynamicPort dynamicPort = new DynamicPort("port1");

  @Override
  protected String getConfigFile() {
    return "basic-conf-flow-httpn.xml";
  }

  @Override
  protected void doSetUp() throws Exception {
    super.doSetUp();
    echoWsdl = IOUtils.getResourceAsString("cxf-echo-service.wsdl", getClass());
    XMLUnit.setIgnoreWhitespace(true);
    try {
      XMLUnit.getTransformerFactory();
    } catch (TransformerFactoryConfigurationError e) {
      XMLUnit.setTransformerFactory(XMLUtils.TRANSFORMER_FACTORY_JDK5);
    }
  }

  @Test
  public void testEchoService() throws Exception {
    MuleClient client = muleContext.getClient();
    InputStream xml = getClass().getResourceAsStream("/direct/direct-request.xml");
    InternalMessage result = client.send("http://localhost:" + dynamicPort.getNumber() + "/services/Echo",
                                         InternalMessage.builder().payload(xml).mediaType(APP_SOAP_XML).build(),
                                         HTTP_REQUEST_OPTIONS)
        .getRight();
    assertTrue(getPayloadAsString(result).contains("Hello!"));
    String ct = result.getPayload().getDataType().getMediaType().toRfcString();
    assertEquals("text/xml; charset=UTF-8", ct);
  }

  @Test
  public void testEchoWsdl() throws Exception {
    MuleClient client = muleContext.getClient();
    InternalMessage result = client.send("http://localhost:" + dynamicPort.getNumber() + "/services/Echo" + "?wsdl",
                                         InternalMessage.builder().nullPayload().build(), HTTP_REQUEST_OPTIONS)
        .getRight();
    assertNotNull(result.getPayload().getValue());
    XMLUnit.compareXML(echoWsdl, getPayloadAsString(result));
  }
}
