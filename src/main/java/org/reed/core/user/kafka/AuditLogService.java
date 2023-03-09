package org.reed.core.user.kafka;

import org.reed.core.user.entity.AuditLog;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.InetAddress;
import java.util.Date;

@Component
public final class AuditLogService {
    private final KafkaProducer kafkaProducer;
    @Value("${spring.kafka.producer.audit-topic}")
    private String auditTopic;

    public AuditLogService(KafkaProducer kafkaProducer) {
        this.kafkaProducer = kafkaProducer;
    }

    public void sendLog(String appCode, Long actionId, String source, Long actionUserId, String content) throws Exception {
        AuditLog log = new AuditLog();
        log.setAppCode(appCode);
        log.setSource(source);
        log.setActionId(actionId);
        log.setActionUserId(actionUserId);
        log.setContent(content);
        String hostAddress = InetAddress.getLocalHost().getHostAddress();
        log.setIp(hostAddress);
        log.setActionDate(new Date());
        sendLog(log);
    }

    public void sendLog(String appCode, String source, Long actionUserId, String content) throws Exception {
        AuditLog log = new AuditLog();
        log.setAppCode(appCode);
        log.setSource(source);
        log.setActionUserId(actionUserId);
        log.setContent(content);
        String hostAddress = InetAddress.getLocalHost().getHostAddress();
        log.setIp(hostAddress);
        log.setActionDate(new Date());
        sendLog(log);
    }

    private void sendLog(AuditLog log) throws Exception {
        kafkaProducer.sendMessageAsync(auditTopic, log);
    }
}
