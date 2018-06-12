package com.iflytek.kafka;

import com.iflytek.util.HbaseDao;
import com.iflytek.util.PropertiesUtil;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.text.ParseException;
import java.util.Collections;

public class HbaseConsumer {
    public static void main(String[] args) throws ParseException {

        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(PropertiesUtil.properties);

        consumer.subscribe(Collections.singletonList("calllog"));

        HbaseDao hbaseDao = new HbaseDao();

        while (true) {
            ConsumerRecords<String, String> records = consumer.poll(500);
            for (ConsumerRecord<String, String> record : records) {
                System.out.println(record.value());

                hbaseDao.put(record.value());

            }
        }
    }
}
