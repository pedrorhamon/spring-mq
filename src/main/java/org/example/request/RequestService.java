package org.example.request;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.rabbit.stream.producer.RabbitStreamTemplate;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

/**
 * @author pedroRhamon
 */
@Slf4j
@Service
public class RequestService {
	
	@Autowired
    private  RabbitStreamTemplate rabbitStreamTemplate;

    public void publisher(Request request) {
        log.info("Publisher new request: {}", request);
        rabbitStreamTemplate.convertAndSend(request);
    }
}