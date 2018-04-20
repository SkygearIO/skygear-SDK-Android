/*
 * Copyright 2017 Oursky Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package io.skygear.skygear;

import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.security.InvalidParameterException;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class ConfigurationUnitTest {
    @Test
    public void testDefaultConfigurationNormalFlow() throws Exception {
        Configuration defaultConfig = Configuration.testConfiguration();

        assertEquals("http://skygear.dev/", defaultConfig.endpoint);
        assertEquals("changeme", defaultConfig.apiKey);
        assertNull(defaultConfig.gcmSenderId);
        assertFalse(defaultConfig.pubsubHandlerExecutionInBackground);
    }

    @Test
    public void testConfigurationBuilderNormalFlow() throws Exception {
        Configuration config = new Configuration.Builder()
                .endPoint("http://my-endpoint.skygeario.com/")
                .apiKey("my-api-key")
                .gcmSenderId("my-sender-id")
                .pubsubHandlerExecutionInBackground(true)
                .build();

        assertEquals("http://my-endpoint.skygeario.com/", config.endpoint);
        assertEquals("my-api-key", config.apiKey);
        assertEquals("my-sender-id", config.gcmSenderId);
        assertTrue(config.pubsubHandlerExecutionInBackground);
    }

    @Test(expected = InvalidParameterException.class)
    public void testConfigurationBuilderNotAllowNullEndpoint() throws Exception {
        new Configuration.Builder()
                .apiKey("my-api-key")
                .build();
    }

    @Test(expected = InvalidParameterException.class)
    public void testConfigurationBuilderNotAllowNullApiKey() throws Exception {
        new Configuration.Builder()
                .endPoint("http://my-endpoint.skygeario.com/")
                .build();
    }
}
