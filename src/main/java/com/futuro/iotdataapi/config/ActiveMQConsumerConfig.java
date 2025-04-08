package com.futuro.iotdataapi.config;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class ActiveMQConsumerConfig extends RouteBuilder {
	 @Override
	 public void configure() throws Exception {
		 from("activemq:queue:p1-g6")
		 .log("Mensaje recibido: ${body}")
		 .to("bean:ConsumerService?method=proccesMessage");
	 }
}
