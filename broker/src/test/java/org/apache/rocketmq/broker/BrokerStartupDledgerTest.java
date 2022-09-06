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

import org.junit.Test;

import java.io.IOException;

public class BrokerStartupDledgerTest {

    @Test
    public void startN0() throws IOException {
        String[] args = {"-c","/Users/xmly/work/git/Neumann789/rocketmq/broker/src/test/resources/dledger/broker-n0.conf"};
        BrokerStartup.main(args);
        System.in.read();
    }

    @Test
    public void startN1() throws IOException {
        String[] args = {"-c","/Users/xmly/work/git/Neumann789/rocketmq/broker/src/test/resources/dledger/broker-n1.conf"};
        BrokerStartup.main(args);
        System.in.read();
    }

    @Test
    public void startN2() throws IOException {
        String[] args = {"-c","/Users/xmly/work/git/Neumann789/rocketmq/broker/src/test/resources/dledger/broker-n2.conf"};
        BrokerStartup.main(args);
        System.in.read();
    }

}
