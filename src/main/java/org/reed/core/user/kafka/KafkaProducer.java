package org.reed.core.user.kafka;

import org.reed.log.ReedLogger;
import org.reed.utils.EnderUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Component
public class KafkaProducer {
    private final KafkaTemplate<String, Object> kafkaTemplate;
    protected static final Logger logger = LoggerFactory.getLogger(KafkaProducer.class);

    public KafkaProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendMessageSync(String topic, Object message) throws InterruptedException, ExecutionException, TimeoutException {
        kafkaTemplate.send(topic, message).get(10L, TimeUnit.SECONDS);
    }

    public void sendMessageAsync(final String topic, Object content) {
        ReedLogger.info(EnderUtil.devInfo() + "准备往主题" + topic + "发送消息内容: " + content);
        ListenableFuture<SendResult<String, Object>> send = kafkaTemplate.send(topic, content);
        send.addCallback(new ListenableFutureCallback<SendResult<String, Object>>() {
            @Override
            public void onSuccess(SendResult result) {
                ReedLogger.info(EnderUtil.devInfo() + topic + " 生产者 发送消息成功");
            }

            public void onFailure(Throwable throwable) {
                ReedLogger.error(EnderUtil.devInfo() + " 生产者 发送消息失败", throwable);
            }

        });
    }
}
