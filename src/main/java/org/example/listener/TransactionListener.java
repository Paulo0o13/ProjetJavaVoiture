package org.example.listener;

import org.example.configuration.RabbitConfiguration;
import org.example.controller.TransactionController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class TransactionListener {


    private static final Logger log = LoggerFactory.getLogger(TransactionController.class);

    @RabbitListener(queues = RabbitConfiguration.LISTENER_QUEUE)
    public void onMsgReceive(Map<String, Object> payload) {
        String msg = (String) payload.get("msg");

        log.info("[LISTENER] Received message {}", msg);

    }


}
