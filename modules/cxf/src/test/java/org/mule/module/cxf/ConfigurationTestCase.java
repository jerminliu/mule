/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.cxf;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.mule.api.endpoint.InboundEndpoint;
import org.mule.module.cxf.config.FlowConfiguringMessageProcessor;
import org.mule.module.cxf.config.ProxyServiceFactoryBean;
import org.mule.tck.junit4.FunctionalTestCase;

import java.util.List;

import org.apache.cxf.Bus;
import org.apache.cxf.interceptor.Interceptor;
import org.apache.cxf.interceptor.LoggingInInterceptor;
import org.apache.cxf.message.Message;
import org.junit.Test;

public class ConfigurationTestCase extends FunctionalTestCase
{

    @Override
    protected String getConfigFile()
    {
        return "configuration-conf-flow.xml";
    }

    @Test
    public void testBusConfiguration() throws Exception
    {
        CxfConfiguration config = muleContext.getRegistry().get("cxf");

        Bus cxfBus = config.getCxfBus();
        boolean found = false;
        for (Interceptor<? extends Message> i : cxfBus.getInInterceptors())
        {
            if (i instanceof LoggingInInterceptor)
            {
                found = true;
                break;
            }
        }

        assertTrue("Did not find logging interceptor.", found);
    }

    @Test
    public void testSpringRefs() throws Exception
    {
        InboundEndpoint endpoint = muleContext.getRegistry().get("clientEndpoint");
        FlowConfiguringMessageProcessor processor = (FlowConfiguringMessageProcessor) endpoint.getMessageProcessors().get(0);
        List<Interceptor<? extends Message>> inInterceptors =
            ((ProxyServiceFactoryBean) processor.getMessageProcessorBuilder()).getInInterceptors();
        assertEquals(muleContext.getRegistry().get("foo1"), inInterceptors.get(0));
        assertEquals(muleContext.getRegistry().get("foo3"), inInterceptors.get(1));
    }
}