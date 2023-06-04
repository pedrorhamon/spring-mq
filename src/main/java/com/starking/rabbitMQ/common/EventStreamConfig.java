package com.starking.rabbitMQ.common;

import java.util.concurrent.TimeUnit;

import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.ExchangeBuilder;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.listener.RabbitListenerContainerFactory;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.amqp.RabbitProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.rabbit.stream.config.StreamRabbitListenerContainerFactory;
import org.springframework.rabbit.stream.listener.StreamListenerContainer;
import org.springframework.rabbit.stream.producer.RabbitStreamTemplate;
import org.springframework.rabbit.stream.retry.StreamRetryOperationsInterceptorFactoryBean;
import org.springframework.retry.support.RetryTemplate;

import com.rabbitmq.stream.Environment;
import com.rabbitmq.stream.OffsetSpecification;
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
	
	@Bean
	RabbitStreamTemplate rabbitStreamTemplate(Environment env, Exchange superStream,
			Jackson2JsonMessageConverter jackson2JsonMessageConverter) {
		var template = new RabbitStreamTemplate(env, superStream.getName());
		template.setMessageConverter(jackson2JsonMessageConverter);
		template.setProducerCustomizer((s, builder) -> builder.routing(Object::toString).producerBuilder());
		return template;
	}

	@Bean
	RabbitListenerContainerFactory<StreamListenerContainer> streamContainerFactory(Environment env) {
		var factory = new StreamRabbitListenerContainerFactory(env);
		factory.setNativeListener(true);
		factory.setConsumerCustomizer((id, builder) -> builder.name(applicationName).offset(OffsetSpecification.first())
				.manualTrackingStrategy());
		return factory;
	}

	@Bean
	RetryTemplate basicStreamRetryTemplate() {
		return RetryTemplate.builder().infiniteRetry()
				.exponentialBackoff(TimeUnit.SECONDS.toMillis(10), 1.5, 
						TimeUnit.MINUTES.toMillis(5))
				.build();
	}

	@Bean
	StreamRetryOperationsInterceptorFactoryBean streamRetryOperationsInterceptorFactoryBean(
			RetryTemplate basicStreamRetryTemplate) {
		var factoryBean = new StreamRetryOperationsInterceptorFactoryBean();
		factoryBean.setRetryOperations(basicStreamRetryTemplate);
		return factoryBean;
	}

	@Bean
	Exchange superStream() {
		return ExchangeBuilder.directExchange(applicationName).build();
	}
//
//	    @Bean
//	    Queue streamPartition() {
//	        return QueueBuilder
//	                .durable("partition-1")
//	                .stream()
//	                .withArgument("x-max-age", "7D")
//	                .withArgument("x-max-length-bytes", DataSize
//	                        .ofGigabytes(10).toBytes())
//	                .build();
//	    }
//
//	    @Bean
//	    Binding streamPartitionBind(Exchange superStream, Queue streamPartition) {
//	        return BindingBuilder
//	                .bind(streamPartition)
//	                .to(superStream)
//	                .with("")
//	                .noargs();
//	    }

}
