package com.starking.rabbitMQ.request;

import org.springframework.rabbit.stream.producer.RabbitStreamTemplate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author pedroRhamon
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RequestService {
    private final RabbitStreamTemplate rabbitStreamTemplate;

    public void publisher(Request request) {
        log.info("Publisher new request: {}", request);
        rabbitStreamTemplate.convertAndSend(request);
    }
}