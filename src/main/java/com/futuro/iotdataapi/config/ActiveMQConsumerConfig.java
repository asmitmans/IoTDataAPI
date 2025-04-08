package com.futuro.iotdataapi.config;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ActiveMQConsumerConfig extends RouteBuilder {
	
	@Value("${iot.queue.name}")
	private String queue;
	
	 @Override
	 public void configure() throws Exception {
		 from("activemq:queue:" + queue)
		 .log("Mensaje recibido: ${body}")
		 .to("bean:ConsumerService?method=proccesMessage");
	 }
}
