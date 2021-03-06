/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.test.integration.exceptions;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.mule.runtime.core.api.lifecycle.LifecycleException;
import org.mule.runtime.core.construct.Flow;
import org.mule.tck.testmodels.mule.TestConnector;

import java.util.concurrent.TimeUnit;

import org.junit.Ignore;
import org.junit.Test;

@Ignore("BL-186")
public class SystemExceptionStrategyTestCase extends AbstractExceptionStrategyTestCase {

  @Override
  protected String getConfigFile() {
    return "org/mule/test/integration/exceptions/system-exception-strategy.xml";
  }

  @Test
  public void testConnectorStartup() throws Exception {
    try {
      TestConnector c = (TestConnector) muleContext.getRegistry().lookupObject("testConnector");
      c.setInitialStateStopped(false);
      c.start();
      fail("Connector should have thrown an exception");
    } catch (LifecycleException e) {
      // expected
    }
    systemExceptionListener.waitUntilAllNotificationsAreReceived();
    exceptionListener.assertNotInvoked();
  }

  @Test
  public void testConnectorPolling() throws Exception {
    ((Flow) muleContext.getRegistry().lookupFlowConstruct("Polling")).start();
    Thread.sleep(3000);
    systemExceptionListener.waitUntilAllNotificationsAreReceived();
    exceptionListener.assertNotInvoked();
  }
}


