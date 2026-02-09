package com.course.camel.camel_starter_project.routes;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class MyFirstRoute extends RouteBuilder {
    @Override
    public void configure() throws Exception {
        from("timer:first-timer?period=5000")
                .setBody(constant("Camel is running in Spring Boot!"))
                .to("log:first-logger");

        from("file:data/input?idempotentKey=${file:name}-${file:size}-${date:now:yyyyMMddHHmmss}")
                .routeId("FileTransformationRoute") // Assigning an ID to the Route
                .log("Processing file: ${header.CamelFileName}")
                // 2. Processor: Custom logic to transform data
                .process(exchange -> {
                    String body = exchange.getIn().getBody(String.class);
                    exchange.getIn().setBody(body.toUpperCase());
                    System.out.println("Processing file content: " + body);
                })

                // 3. Endpoint (Destination): Moving to 'data/output' folder
                .to("file:data/output");
    }
}
