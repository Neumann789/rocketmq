/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.rocketmq.broker;

import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Properties;

public class BrokerStartup2M2SSyncTest {

    @Test
    public void starta() throws IOException {
        String[] args = {"-c","/Users/xmly/work/git/Neumann789/rocketmq/broker/src/test/resources/2m-2s-sync/broker-a.properties"};
        BrokerStartup.main(args);
        System.in.read();
    }

    @Test
    public void startas() throws IOException {
        String[] args = {"-c","/Users/xmly/work/git/Neumann789/rocketmq/broker/src/test/resources/2m-2s-sync/broker-a-s.properties"};
        BrokerStartup.main(args);
        System.in.read();
    }

    @Test
    public void startb() throws IOException {
        String[] args = {"-c","/Users/xmly/work/git/Neumann789/rocketmq/broker/src/test/resources/2m-2s-sync/broker-b.properties"};
        BrokerStartup.main(args);
        System.in.read();
    }

    @Test
    public void startbs() throws IOException {
        String[] args = {"-c","/Users/xmly/work/git/Neumann789/rocketmq/broker/src/test/resources/2m-2s-sync/broker-b-s.properties"};
        BrokerStartup.main(args);
        System.in.read();
    }

    @Test
    public void starttrace() throws IOException {
        String[] args = {"-c","/Users/xmly/work/git/Neumann789/rocketmq/broker/src/test/resources/2m-2s-sync/broker-trace.properties"};
        BrokerStartup.main(args);
        System.in.read();
    }

}
