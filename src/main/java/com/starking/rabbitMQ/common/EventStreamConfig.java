package com.starking.rabbitMQ.common;

import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.amqp.RabbitProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.rabbitmq.stream.Environment;
import com.rabbitmq.stream.impl.StreamEnvironmentBuilder;

import lombok.RequiredArgsConstructor;

/**
 * @author pedroRhamon
 */

@Configuration
@RequiredArgsConstructor
public class EventStreamConfig {
	
	@Value("${spring.application.name}")
	private String applicationName;
	
	@Bean
	Environment env(RabbitProperties properties) {
		return new StreamEnvironmentBuilder()
				.host(properties.getHost())
				.port(properties.getStream().getPort())
				.username(properties.getUsername())
				.password(properties.getPassword())
				.virtualHost(properties.getVirtualHost())
				.build();
	}
	
	@Bean
	RabbitAdmin rabbitAdmin(ConnectionFactory factory) {
		return new RabbitAdmin(factory);
	}

}
