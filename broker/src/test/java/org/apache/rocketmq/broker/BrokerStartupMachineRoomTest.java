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

public class BrokerStartupMachineRoomTest {

    @Test
    public void startUnit1BrokerA() throws IOException {
        String[] args = {"-c","/Users/xmly/work/git/Neumann789/rocketmq/broker/src/test/resources/machineroom/unit1-broker-a.properties"};
        BrokerStartup.main(args);
        System.in.read();
    }

    @Test
    public void startUnit2BrokerA() throws IOException {
        String[] args = {"-c","/Users/xmly/work/git/Neumann789/rocketmq/broker/src/test/resources/machineroom/unit2-broker-a.properties"};
        BrokerStartup.main(args);
        System.in.read();
    }

    @Test
    public void startUnit3BrokerA() throws IOException {
        String[] args = {"-c","/Users/xmly/work/git/Neumann789/rocketmq/broker/src/test/resources/machineroom/unit3-broker-a.properties"};
        BrokerStartup.main(args);
        System.in.read();
    }

}
