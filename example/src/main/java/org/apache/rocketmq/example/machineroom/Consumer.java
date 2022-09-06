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
import org.apache.rocketmq.client.consumer.rebalance.AllocateMachineRoomNearby;
import org.apache.rocketmq.client.consumer.rebalance.AllocateMessageQueueAveragely;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.message.MessageQueue;
import org.apache.rocketmq.remoting.common.RemotingUtil;
import org.junit.Test;

import java.io.IOException;
import java.util.Random;

/**
 * This example shows how to subscribe and consume messages using providing {@link DefaultMQPushConsumer}.
 */
public class Consumer {

    public static final String CONSUMER_GROUP = "please_rename_unique_group_name_10";
    public static final String DEFAULT_NAMESRVADDR = "127.0.0.1:9876";
    public static final String TOPIC = "TopicTest10";


    /**
     * 三个master broker
     * unit1@broker-a
     * unit2@broker-a
     * unit3@broker-a
     *
     * @param args
     * @throws InterruptedException
     * @throws MQClientException
     */
    public static void main(String[] args) throws InterruptedException, MQClientException {
//        startConsumerForUnit("unit1");
//        startConsumerForUnit("unit2");
//        startConsumerForUnit("unit3");
    }

    @Test
    public void startConsumerAtUnit1() throws MQClientException, InterruptedException, IOException {
        startConsumerForUnit("unit1");
        System.in.read();
    }

    @Test
    public void startConsumerAtUnit2() throws MQClientException, InterruptedException, IOException {
        startConsumerForUnit("unit2");
        System.in.read();
    }

    @Test
    public void startConsumerAtUnit3() throws MQClientException, InterruptedException, IOException {
        startConsumerForUnit("unit3");
        System.in.read();
    }

    public void startConsumerForUnit(String unitFlag) throws InterruptedException, MQClientException {

        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer(CONSUMER_GROUP);
        consumer.setNamesrvAddr(DEFAULT_NAMESRVADDR);
        consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);
        consumer.subscribe(TOPIC, "*");
        consumer.setClientIP(unitFlag + "@" + RemotingUtil.getLocalAddress());


        // 负载均衡平均算法
        AllocateMessageQueueAveragely averagely = new AllocateMessageQueueAveragely();
        // 多机房解析器
        CustomAllocateMachineRoomNearby.MachineRoomResolver machineRoomResolver = new
                CustomAllocateMachineRoomNearby.MachineRoomResolver() {
                    @Override
                    public String brokerDeployIn(MessageQueue messageQueue) {
                        // brokerName约定unit2@broker-b
                        return messageQueue.getBrokerName().split("@")[0];
                    }

                    @Override
                    public String consumerDeployIn(String clientID) {
                        // 从clientId中截取
                        return clientID.split("@")[0];
                    }
                };
        consumer.setAllocateMessageQueueStrategy(new CustomAllocateMachineRoomNearby(averagely, machineRoomResolver));
        // 异常重试
        consumer.setMaxReconsumeTimes(2);
        consumer.registerMessageListener((MessageListenerConcurrently) (msg, context) -> {
            System.out.printf("consumer %s => %s Receive New Messages: %s %n", unitFlag, Thread.currentThread().getName(), msg);
            String msgId = msg.get(0).getMsgId();
            if(msgId.hashCode()%4==0){
                return ConsumeConcurrentlyStatus.RECONSUME_LATER;
            }
            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        });

        consumer.start();

        System.out.printf("Consumer Started for %s.%n", unitFlag);
    }
}
