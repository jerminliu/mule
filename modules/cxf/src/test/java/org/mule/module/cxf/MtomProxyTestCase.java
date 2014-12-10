/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.cxf;

import org.junit.Ignore;

@Ignore("Broken on removing services")
public class MtomProxyTestCase extends MtomTestCase
{

    @Override
    protected String getConfigFile()
    {
        return "mtom-proxy-conf.xml";
    }
}
