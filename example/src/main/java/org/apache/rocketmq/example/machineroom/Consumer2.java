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
package org.apache.rocketmq.example.machineroom;

import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.consumer.rebalance.AllocateMessageQueueByMachineRoom;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;

import java.util.Arrays;
import java.util.HashSet;

/**
 * This example shows how to subscribe and consume messages using providing {@link DefaultMQPushConsumer}.
 */
public class Consumer2 {

    public static final String CONSUMER_GROUP = "please_rename_unique_group_name_10";
    public static final String DEFAULT_NAMESRVADDR = "127.0.0.1:9876";
    public static final String TOPIC = "TopicTest10";

    /**
     * 两个master broker
     * unit2@broker-b
     * unit1@broker-a
     * @param args
     * @throws InterruptedException
     * @throws MQClientException
     */
    public static void main(String[] args) throws InterruptedException, MQClientException {
        startConsumerForUnit("unit1");
        startConsumerForUnit("unit2");
        startConsumerForUnit("unit3");
    }

    public static void startConsumerForUnit(String unitFlag) throws InterruptedException, MQClientException{

        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer(CONSUMER_GROUP);
        consumer.setNamesrvAddr(DEFAULT_NAMESRVADDR);
        consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);
        consumer.subscribe(TOPIC, "*");

        // AllocateMessageQueueByMachineRoom
        AllocateMessageQueueByMachineRoom allocateMachineRoom = new AllocateMessageQueueByMachineRoom();
        allocateMachineRoom.setConsumeridcs(new HashSet<>(Arrays.asList(unitFlag)));
        consumer.setAllocateMessageQueueStrategy(allocateMachineRoom);

        // AllocateMachineRoomNearby TODO

        consumer.registerMessageListener((MessageListenerConcurrently) (msg, context) -> {
            System.out.printf("%s => %s Receive New Messages: %s %n", unitFlag, Thread.currentThread().getName(), msg);
            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        });

        consumer.start();

        System.out.printf("Consumer Started for %s.%n",unitFlag);
    }
}
