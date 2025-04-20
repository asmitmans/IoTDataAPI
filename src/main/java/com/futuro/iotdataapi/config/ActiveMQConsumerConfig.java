package com.futuro.iotdataapi.config;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ActiveMQConsumerConfig extends RouteBuilder {
	
	@Value("${iot.topic.name}")
	private String topic;
	
	 @Override
	 public void configure() throws Exception {
		 from("activemq:topic:" + topic)
		 .log("Received message: ${body}")
		 .to("bean:ConsumerService?method=proccesMessage");
	 }
}
