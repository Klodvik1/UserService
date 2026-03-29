package io.github.Klodvik1.producer;

import io.github.Klodvik1.event.UserNotificationEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class UserEventProducer {
    private final KafkaTemplate<String, UserNotificationEvent> kafkaTemplate;
    private final String userNotificationTopic;

    public UserEventProducer(
            KafkaTemplate<String, UserNotificationEvent> kafkaTemplate,
            @Value("${app.kafka.topics.user-notification}") String userNotificationTopic) {
        this.kafkaTemplate = kafkaTemplate;
        this.userNotificationTopic = userNotificationTopic;
    }

    public void sendUserNotificationEvent(UserNotificationEvent event) {
        kafkaTemplate.send(userNotificationTopic, event.email(), event);
    }
}
