package com.github.zregvart;

import javax.jms.ConnectionFactory;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.amqp.AMQPComponent;
import org.apache.camel.component.jms.JmsConfiguration;
import org.apache.qpid.jms.JmsConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Component;

@SpringBootApplication
public class ArtemisTry {

    @Component
    class ArtemisRoute extends RouteBuilder {

        @Autowired
        ArtemisRoute(final AMQPComponent component) {
            final JmsConfiguration configuration = new JmsConfiguration();
            final ConnectionFactory connectionFactory = new JmsConnectionFactory("amqp://localhost:5672");
            configuration.setConnectionFactory(connectionFactory);

            component.setConfiguration(configuration);
        }

        @Override
        public void configure() throws Exception {
            from("timer:generate?period=2s").log("${in.header.CamelTimerCounter} -> IN")
                .setBody(simple("in.header.CamelTimerCounter", String.class)).to("amqp:queue:IN");
            from("amqp:queue:IN").log("received: ${body}");
        }
    }

    public static void main(final String[] args) {
        SpringApplication.run(ArtemisTry.class, args);
    }
}
