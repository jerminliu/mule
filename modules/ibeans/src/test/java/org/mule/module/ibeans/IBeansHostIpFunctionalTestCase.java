/*
 * $Id$
 * -------------------------------------------------------------------------------------
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.ibeans;

import org.mule.api.MuleMessage;
import org.mule.api.client.MuleClient;
import org.mule.api.transport.DispatchException;
import org.mule.module.xml.util.XMLUtils;
import org.mule.tck.FunctionalTestCase;

import org.w3c.dom.Document;

public class IBeansHostIpFunctionalTestCase extends FunctionalTestCase
{
    public IBeansHostIpFunctionalTestCase()
    {
        setDisposeManagerPerSuite(true);
    }

    @Override
    protected String getConfigResources()
    {
        return "hostip-functional-test.xml";
    }


    public void testHostIp() throws Exception
    {
        String ip = "192.215.42.198";
        MuleClient client = muleContext.getClient();
        MuleMessage response = client.send("vm://in", ip, null);
        assertNotNull(response.getPayload());
        Document result = response.getPayload(Document.class);
        assertEquals(ip, XMLUtils.selectValue("//ip", result));
        assertEquals("-117.136,32.8149", XMLUtils.selectValue("//gml:coordinates", result));
    }

    public void testHostIpFromClient() throws Exception
    {
        String ip = "192.215.42.198";
        MuleClient client = muleContext.getClient();
        MuleMessage response = client.send("ibean://hostip.getHostInfo", ip, null);
        assertNotNull(response.getPayload());
        Document result = response.getPayload(Document.class);
        assertEquals(ip, XMLUtils.selectValue("//ip", result));
        assertEquals("-117.136,32.8149", XMLUtils.selectValue("//gml:coordinates", result));
    }

    public void testHostIpWrongNumberOfArguments() throws Exception
    {
        Object[] params = new Object[]{"192.215.42.198", new Integer(12)};
        MuleClient client = muleContext.getClient();
        MuleMessage response = client.send("vm://in", params, null);
        assertNotNull(response.getExceptionPayload());
        assertTrue(response.getExceptionPayload().getException() instanceof DispatchException);        
        assertTrue(response.getExceptionPayload().getRootException() instanceof NoSuchMethodException);
    }

    public void testHostIpBadArgumentType() throws Exception
    {
        MuleClient client = muleContext.getClient();
        MuleMessage response = client.send("vm://in", new StringBuffer(), null);
        assertNotNull(response.getExceptionPayload());
        assertTrue(response.getExceptionPayload().getException() instanceof DispatchException);
        assertTrue(response.getExceptionPayload().getRootException() instanceof NoSuchMethodException);
    }

    public void testHostIpWrongNumberOfArgumentsDirectClient() throws Exception
    {
        Object[] params = new Object[]{"192.215.42.198", new Integer(12)};
        MuleClient client = muleContext.getClient();

        MuleMessage result = client.send("ibean://hostip.getHostInfo", params, null);
        assertNotNull(result);
        assertNotNull("Local call should throw exception since there will be no matching method called getHostInfo(String, Integer)", 
                      result.getExceptionPayload());
        assertTrue(result.getExceptionPayload().getException() instanceof DispatchException);
        assertTrue(result.getExceptionPayload().getRootException() instanceof NoSuchMethodException);
    }

    public void testHostIpBadArgumentTypeDirectClient() throws Exception
    {
        MuleClient client = muleContext.getClient();

        MuleMessage result = client.send("ibean://hostip.getHostInfo", new StringBuffer(), null);
        assertNotNull(result);
        assertNotNull("Local call should throw exception since there will be no matching method called getHostInfo(StringBuffer)", 
                      result.getExceptionPayload());
        assertTrue(result.getExceptionPayload().getException() instanceof DispatchException);
        assertTrue(result.getExceptionPayload().getRootException() instanceof NoSuchMethodException);
    }
}
