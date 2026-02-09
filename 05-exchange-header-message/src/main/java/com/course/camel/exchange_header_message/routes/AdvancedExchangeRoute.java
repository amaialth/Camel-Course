package com.course.camel.exchange_header_message.routes;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;

@Component
public class AdvancedExchangeRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {

        from("file:data/input?noop=true&idempotent=false")
                .routeId("AdvancedExchangeDemo")

                // 1. Log the arrival
                .log(">>> Incoming Exchange ID: ${exchangeId}")

                // 2. Custom Processor to manipulate the Exchange
                .process(exchange -> {
                    // Accessing the 'In' Message
                    var inMessage = exchange.getIn();
                    String originalBody = inMessage.getBody(String.class);

                    // Reading a standard Camel Header
                    String fileName = inMessage.getHeader("CamelFileName", String.class);

                    // SETTING a Custom Header (useful for downstream routing)
                    inMessage.setHeader("ProcessingTime", LocalDateTime.now().toString());
                    inMessage.setHeader("FileExtension", fileName.substring(fileName.lastIndexOf(".")));

                    // TRANSFORMING the Body
                    String uppercaseBody = (originalBody != null) ? originalBody.toUpperCase() : "EMPTY BODY";
                    inMessage.setBody("PROCESSED AT " + LocalDateTime.now() + "\nCONTENT: " + uppercaseBody);

                    // SETTING an Exchange Property (Internal memory)
                    exchange.setProperty("InternalAuditID", "AUDIT-" + Math.random());
                })

                // 3. Inspecting our changes using Simple Expression Language
                .log("--- Metadata Inspection ---")
                .log("Custom Header (Time): ${header.ProcessingTime}")
                .log("Custom Header (Ext):  ${header.FileExtension}")
                .log("Internal Property:    ${exchangeProperty.InternalAuditID}")

                // 4. Send to output with a dynamic filename using a header
                .to("file:data/output?fileName=processed-${header.CamelFileName}");
    }
}
