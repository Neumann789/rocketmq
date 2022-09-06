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
package org.apache.rocketmq.example.quickstart;

import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.common.RemotingHelper;

/**
 * This class demonstrates how to send messages to brokers using provided {@link DefaultMQProducer}.
 */
public class ProducerWithMessageTrace {

    /**
     * The number of produced messages.
     */
    public static final int MESSAGE_COUNT = 1000000;
    public static final String PRODUCER_GROUP = "pg_fhb_test";
    public static final String DEFAULT_NAMESRVADDR = "127.0.0.1:9876";
    public static final String TOPIC = "topic_fhb_test";
    public static final String TAG = "TagA";

    /**
     * 准备阶段
     * nameserver(1台)
     * broker-a(主从), broker-b(主从), broker-trace(主)
     * dev%topic_fhb_test： broker-a(8/16/6), broker-b(10/16/6),broker-trace(8/16/6)
     *
     * 测试一：创建topic时writeQueueNums决定了consumequeue对应topic生成的目录  ok
     * -> broker-a(主和从)的consumequeue/dev%topic_fhb_test目录产生了8个队列目录(编号从0到7)
     * -> broker-b(主和从)的consumequeue/dev%topic_fhb_test目录产生了10个队列目录(编号从0到9)
     * -> broker-trace(主)的consumequeue/dev%topic_fhb_test目录产生了8个队列目录(编号从0到7)
     * 测试二： 开启broker故障延迟机制
     * -> broker-a执行报错后，被延迟600000(即十分钟)后再执行，符合预期。
     * -> 生产者调用时均衡策略是messagequeue维度(写队列)，且每个队列拥有自己的queueOffset
     * SendResult [sendStatus=SEND_OK, msgId=7F000001972318B4AAC25542377006F5, offsetMsgId=C0A86CB3000078BE0000000000005C22, messageQueue=MessageQueue [topic=topic_fhb_test, brokerName=broker-a, queueId=0], queueOffset=20]
     * SendResult [sendStatus=SEND_OK, msgId=7F000001972318B4AAC255423B6406F7, offsetMsgId=C0A86CB3000078BE0000000000005D13, messageQueue=MessageQueue [topic=topic_fhb_test, brokerName=broker-a, queueId=1], queueOffset=12]
     * SendResult [sendStatus=SEND_OK, msgId=7F000001972318B4AAC255423F5306F9, offsetMsgId=C0A86CB3000078BE0000000000005E04, messageQueue=MessageQueue [topic=topic_fhb_test, brokerName=broker-a, queueId=2], queueOffset=11]
     * SendResult [sendStatus=SEND_OK, msgId=7F000001972318B4AAC25542433E06FB, offsetMsgId=C0A86CB3000078BE0000000000005EF5, messageQueue=MessageQueue [topic=topic_fhb_test, brokerName=broker-a, queueId=3], queueOffset=11]
     * SendResult [sendStatus=SEND_OK, msgId=7F000001972318B4AAC25542472C06FD, offsetMsgId=C0A86CB3000078BE0000000000005FE6, messageQueue=MessageQueue [topic=topic_fhb_test, brokerName=broker-a, queueId=4], queueOffset=11]
     * SendResult [sendStatus=SEND_OK, msgId=7F000001972318B4AAC255424B1606FF, offsetMsgId=C0A86CB3000078BE00000000000060D7, messageQueue=MessageQueue [topic=topic_fhb_test, brokerName=broker-a, queueId=5], queueOffset=11]
     * SendResult [sendStatus=SEND_OK, msgId=7F000001972318B4AAC255424F080701, offsetMsgId=C0A86CB3000078BE00000000000061C8, messageQueue=MessageQueue [topic=topic_fhb_test, brokerName=broker-a, queueId=6], queueOffset=11]
     * SendResult [sendStatus=SEND_OK, msgId=7F000001972318B4AAC2554252F30703, offsetMsgId=C0A86CB3000078BE00000000000062B9, messageQueue=MessageQueue [topic=topic_fhb_test, brokerName=broker-a, queueId=7], queueOffset=11]
     * SendResult [sendStatus=SEND_OK, msgId=7F000001972318B4AAC2554256DE0705, offsetMsgId=C0A86CB3000078C8000000000001E728, messageQueue=MessageQueue [topic=topic_fhb_test, brokerName=broker-b, queueId=0], queueOffset=211]
     * SendResult [sendStatus=SEND_OK, msgId=7F000001972318B4AAC255425ACA0707, offsetMsgId=C0A86CB3000078C8000000000001E819, messageQueue=MessageQueue [topic=topic_fhb_test, brokerName=broker-b, queueId=1], queueOffset=34]
     * SendResult [sendStatus=SEND_OK, msgId=7F000001972318B4AAC255425EB90709, offsetMsgId=C0A86CB3000078C8000000000001E90A, messageQueue=MessageQueue [topic=topic_fhb_test, brokerName=broker-b, queueId=2], queueOffset=35]
     * SendResult [sendStatus=SEND_OK, msgId=7F000001972318B4AAC2554262A6070B, offsetMsgId=C0A86CB3000078C8000000000001E9FB, messageQueue=MessageQueue [topic=topic_fhb_test, brokerName=broker-b, queueId=3], queueOffset=34]
     * SendResult [sendStatus=SEND_OK, msgId=7F000001972318B4AAC2554266A0070D, offsetMsgId=C0A86CB3000078C8000000000001EAEC, messageQueue=MessageQueue [topic=topic_fhb_test, brokerName=broker-b, queueId=4], queueOffset=34]
     * SendResult [sendStatus=SEND_OK, msgId=7F000001972318B4AAC255426A91070F, offsetMsgId=C0A86CB3000078C8000000000001EBDD, messageQueue=MessageQueue [topic=topic_fhb_test, brokerName=broker-b, queueId=5], queueOffset=34]
     * SendResult [sendStatus=SEND_OK, msgId=7F000001972318B4AAC255426E820711, offsetMsgId=C0A86CB3000078C8000000000001ECCE, messageQueue=MessageQueue [topic=topic_fhb_test, brokerName=broker-b, queueId=6], queueOffset=34]
     * SendResult [sendStatus=SEND_OK, msgId=7F000001972318B4AAC25542726C0713, offsetMsgId=C0A86CB3000078C8000000000001EDBF, messageQueue=MessageQueue [topic=topic_fhb_test, brokerName=broker-b, queueId=7], queueOffset=34]
     * SendResult [sendStatus=SEND_OK, msgId=7F000001972318B4AAC25542765C0715, offsetMsgId=C0A86CB3000078C8000000000001EEB0, messageQueue=MessageQueue [topic=topic_fhb_test, brokerName=broker-b, queueId=8], queueOffset=34]
     * SendResult [sendStatus=SEND_OK, msgId=7F000001972318B4AAC255427A480717, offsetMsgId=C0A86CB3000078C8000000000001EFA1, messageQueue=MessageQueue [topic=topic_fhb_test, brokerName=broker-b, queueId=9], queueOffset=34]
     * SendResult [sendStatus=SEND_OK, msgId=7F000001972318B4AAC255427E360719, offsetMsgId=C0A86CB3000078D200000000000101BC, messageQueue=MessageQueue [topic=topic_fhb_test, brokerName=broker-trace, queueId=0], queueOffset=36]
     * SendResult [sendStatus=SEND_OK, msgId=7F000001972318B4AAC255428222071B, offsetMsgId=C0A86CB3000078D200000000000102AD, messageQueue=MessageQueue [topic=topic_fhb_test, brokerName=broker-trace, queueId=1], queueOffset=34]
     * SendResult [sendStatus=SEND_OK, msgId=7F000001972318B4AAC255428614071D, offsetMsgId=C0A86CB3000078D2000000000001039E, messageQueue=MessageQueue [topic=topic_fhb_test, brokerName=broker-trace, queueId=2], queueOffset=34]
     * SendResult [sendStatus=SEND_OK, msgId=7F000001972318B4AAC255428A05071F, offsetMsgId=C0A86CB3000078D2000000000001048F, messageQueue=MessageQueue [topic=topic_fhb_test, brokerName=broker-trace, queueId=3], queueOffset=34]
     * SendResult [sendStatus=SEND_OK, msgId=7F000001972318B4AAC255428DF50721, offsetMsgId=C0A86CB3000078D20000000000010580, messageQueue=MessageQueue [topic=topic_fhb_test, brokerName=broker-trace, queueId=4], queueOffset=34]
     * SendResult [sendStatus=SEND_OK, msgId=7F000001972318B4AAC2554291E50723, offsetMsgId=C0A86CB3000078D20000000000010671, messageQueue=MessageQueue [topic=topic_fhb_test, brokerName=broker-trace, queueId=5], queueOffset=34]
     * SendResult [sendStatus=SEND_OK, msgId=7F000001972318B4AAC2554295D30725, offsetMsgId=C0A86CB3000078D20000000000010762, messageQueue=MessageQueue [topic=topic_fhb_test, brokerName=broker-trace, queueId=6], queueOffset=34]
     * SendResult [sendStatus=SEND_OK, msgId=7F000001972318B4AAC2554299C00727, offsetMsgId=C0A86CB3000078D20000000000010853, messageQueue=MessageQueue [topic=topic_fhb_test, brokerName=broker-trace, queueId=7], queueOffset=34]
     * SendResult [sendStatus=SEND_OK, msgId=7F000001972318B4AAC255429DAE0729, offsetMsgId=C0A86CB3000078BE00000000000063AA, messageQueue=MessageQueue [topic=topic_fhb_test, brokerName=broker-a, queueId=0], queueOffset=21]
     * SendResult [sendStatus=SEND_OK, msgId=7F000001972318B4AAC25542A1A1072B, offsetMsgId=C0A86CB3000078BE000000000000649B, messageQueue=MessageQueue [topic=topic_fhb_test, brokerName=broker-a, queueId=1], queueOffset=13]
     * SendResult [sendStatus=SEND_OK, msgId=7F000001972318B4AAC25542A58F072D, offsetMsgId=C0A86CB3000078BE000000000000658C, messageQueue=MessageQueue [topic=topic_fhb_test, brokerName=broker-a, queueId=2], queueOffset=12]
     * SendResult [sendStatus=SEND_OK, msgId=7F000001972318B4AAC25542A97B072F, offsetMsgId=C0A86CB3000078BE000000000000667D, messageQueue=MessageQueue [topic=topic_fhb_test, brokerName=broker-a, queueId=3], queueOffset=12]
     * SendResult [sendStatus=SEND_OK, msgId=7F000001972318B4AAC25542AD6F0731, offsetMsgId=C0A86CB3000078BE000000000000676E, messageQueue=MessageQueue [topic=topic_fhb_test, brokerName=broker-a, queueId=4], queueOffset=12]
     * SendResult [sendStatus=SEND_OK, msgId=7F000001972318B4AAC25542B15F0733, offsetMsgId=C0A86CB3000078BE000000000000685F, messageQueue=MessageQueue [topic=topic_fhb_test, brokerName=broker-a, queueId=5], queueOffset=12]
     * SendResult [sendStatus=SEND_OK, msgId=7F000001972318B4AAC25542B5500735, offsetMsgId=C0A86CB3000078BE0000000000006950, messageQueue=MessageQueue [topic=topic_fhb_test, brokerName=broker-a, queueId=6], queueOffset=12]
     * SendResult [sendStatus=SEND_OK, msgId=7F000001972318B4AAC25542B9430737, offsetMsgId=C0A86CB3000078BE0000000000006A41, messageQueue=MessageQueue [topic=topic_fhb_test, brokerName=broker-a, queueId=7], queueOffset=12]
     * SendResult [sendStatus=SEND_OK, msgId=7F000001972318B4AAC25542BD350739, offsetMsgId=C0A86CB3000078C8000000000001F092, messageQueue=MessageQueue [topic=topic_fhb_test, brokerName=broker-b, queueId=0], queueOffset=212]
     * SendResult [sendStatus=SEND_OK, msgId=7F000001972318B4AAC25542C128073B, offsetMsgId=C0A86CB3000078C8000000000001F183, messageQueue=MessageQueue [topic=topic_fhb_test, brokerName=broker-b, queueId=1], queueOffset=35]
     * SendResult [sendStatus=SEND_OK, msgId=7F000001972318B4AAC25542C513073D, offsetMsgId=C0A86CB3000078C8000000000001F274, messageQueue=MessageQueue [topic=topic_fhb_test, brokerName=broker-b, queueId=2], queueOffset=36]
     * SendResult [sendStatus=SEND_OK, msgId=7F000001972318B4AAC25542C902073F, offsetMsgId=C0A86CB3000078C8000000000001F365, messageQueue=MessageQueue [topic=topic_fhb_test, brokerName=broker-b, queueId=3], queueOffset=35]
     * SendResult [sendStatus=SEND_OK, msgId=7F000001972318B4AAC25542CCF30741, offsetMsgId=C0A86CB3000078C8000000000001F456, messageQueue=MessageQueue [topic=topic_fhb_test, brokerName=broker-b, queueId=4], queueOffset=35]
     * SendResult [sendStatus=SEND_OK, msgId=7F000001972318B4AAC25542D0E00743, offsetMsgId=C0A86CB3000078C8000000000001F547, messageQueue=MessageQueue [topic=topic_fhb_test, brokerName=broker-b, queueId=5], queueOffset=35]
     * SendResult [sendStatus=SEND_OK, msgId=7F000001972318B4AAC25542D4CD0745, offsetMsgId=C0A86CB3000078C8000000000001F638, messageQueue=MessageQueue [topic=topic_fhb_test, brokerName=broker-b, queueId=6], queueOffset=35]
     * SendResult [sendStatus=SEND_OK, msgId=7F000001972318B4AAC25542D8BA0747, offsetMsgId=C0A86CB3000078C8000000000001F729, messageQueue=MessageQueue [topic=topic_fhb_test, brokerName=broker-b, queueId=7], queueOffset=35]
     * SendResult [sendStatus=SEND_OK, msgId=7F000001972318B4AAC25542DCA60749, offsetMsgId=C0A86CB3000078C8000000000001F81A, messageQueue=MessageQueue [topic=topic_fhb_test, brokerName=broker-b, queueId=8], queueOffset=35]
     * SendResult [sendStatus=SEND_OK, msgId=7F000001972318B4AAC25542E090074B, offsetMsgId=C0A86CB3000078C8000000000001F90B, messageQueue=MessageQueue [topic=topic_fhb_test, brokerName=broker-b, queueId=9], queueOffset=35]
     * SendResult [sendStatus=SEND_OK, msgId=7F000001972318B4AAC25542E47A074D, offsetMsgId=C0A86CB3000078D20000000000010944, messageQueue=MessageQueue [topic=topic_fhb_test, brokerName=broker-trace, queueId=0], queueOffset=37]
     * SendResult [sendStatus=SEND_OK, msgId=7F000001972318B4AAC25542E86D074F, offsetMsgId=C0A86CB3000078D20000000000010A35, messageQueue=MessageQueue [topic=topic_fhb_test, brokerName=broker-trace, queueId=1], queueOffset=35]
     * SendResult [sendStatus=SEND_OK, msgId=7F000001972318B4AAC25542EC580751, offsetMsgId=C0A86CB3000078D20000000000010B26, messageQueue=MessageQueue [topic=topic_fhb_test, brokerName=broker-trace, queueId=2], queueOffset=35]
     * SendResult [sendStatus=SEND_OK, msgId=7F000001972318B4AAC25542F0480753, offsetMsgId=C0A86CB3000078D20000000000010C17, messageQueue=MessageQueue [topic=topic_fhb_test, brokerName=broker-trace, queueId=3], queueOffset=35]
     * SendResult [sendStatus=SEND_OK, msgId=7F000001972318B4AAC25542F4350755, offsetMsgId=C0A86CB3000078D20000000000010D08, messageQueue=MessageQueue [topic=topic_fhb_test, brokerName=broker-trace, queueId=4], queueOffset=35]
     * SendResult [sendStatus=SEND_OK, msgId=7F000001972318B4AAC25542F8240757, offsetMsgId=C0A86CB3000078D20000000000010DF9, messageQueue=MessageQueue [topic=topic_fhb_test, brokerName=broker-trace, queueId=5], queueOffset=35]
     * SendResult [sendStatus=SEND_OK, msgId=7F000001972318B4AAC25542FC110759, offsetMsgId=C0A86CB3000078D20000000000010EEA, messageQueue=MessageQueue [topic=topic_fhb_test, brokerName=broker-trace, queueId=6], queueOffset=35]
     * SendResult [sendStatus=SEND_OK, msgId=7F000001972318B4AAC255430000075B, offsetMsgId=C0A86CB3000078D20000000000010FDB, messageQueue=MessageQueue [topic=topic_fhb_test, brokerName=broker-trace, queueId=7], queueOffset=35]
     *
     * 测试二：当topic设置的writeQueueNums比readQueueNums大时，消费是否会出现消息丢失？ 待验证
     *
     * @param args
     * @throws MQClientException
     * @throws InterruptedException
     */
    public static void main(String[] args) throws MQClientException, InterruptedException {
        // 开启消息轨迹
        DefaultMQProducer producer = new DefaultMQProducer(PRODUCER_GROUP, null, true, null);
        producer.setNamesrvAddr(DEFAULT_NAMESRVADDR);
        producer.setNamespace("dev");
        /**
         * 开启broker故障延迟机制
         */
//        producer.setSendLatencyFaultEnable(true);
        /**
         * 默认关闭broker故障延迟机制
         * 单次调用时某个broker调用失败，会自动切换到另一个broker。
         */
        producer.setSendLatencyFaultEnable(false);
        producer.start();

        for (int i = 0; i < MESSAGE_COUNT; i++) {
            try {
                String key = "Hello-RocketMQ-" + i;
                Message msg = new Message(
                        TOPIC,
                        TAG,
                        key,
                        key.getBytes(RemotingHelper.DEFAULT_CHARSET)
                );
                SendResult sendResult = producer.send(msg);
                System.out.printf("%s%n", sendResult);
            } catch (Exception e) {
                e.printStackTrace();

            } finally {
                Thread.sleep(1000);
            }
        }

        /*
         * Shut down once the producer instance is not longer in use.
         */
        producer.shutdown();
    }
}
