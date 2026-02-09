# Module 5: Exchange, Message, Header, and Body

## Overview
Welcome to Module 5 of the Apache Camel Course! In this module, we dive deep into the internal structure of Camel messages. You'll learn about the **Exchange**, **Message**, **Headers**, and **Body**—the core components that power Camel's routing engine. We will inspect how data flows through a route and visualize these concepts using practical examples.

---

## 🧠 Core Concepts Explained

Every message in Camel travels inside an **Exchange**. Think of it as a mail parcel 📦:

1.  **Exchange** (The Parcel): The container holding the entire interaction, including the In message, Out message (if any), Exception details, and Properties.
2.  **Message** (The Letter): The actual payload being transported.
3.  **Headers** (The Label): Metadata describing the message (e.g., filename, content-type, correlation ID).
4.  **Body** (The Content): The main data payload (e.g., file content, JSON, XML).

### Analogy
*   ✉️ **Exchange** = The courier package
*   📄 **Message** = The document inside
*   🏷️ **Headers** = Address and tracking info
*   📦 **Body** = The actual message content

---

## 💻 Project: Advanced Exchange Route

In this project, we create a more advanced route (`AdvancedExchangeRoute`) that demonstrates how to manipulate headers, process the message body, and utilize exchange properties.

### Component to Build: `AdvancedExchangeRoute`

This route consumes files from `data/input`, processes them by adding custom headers and modifying the body, and writes the output to `data/output`.

**File:** `src/main/java/com/course/camel/exchange_header_message/routes/AdvancedExchangeRoute.java`

```java
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
```

### 🧩 Key Takeaways from the Code
-   **Exchange Properties**: `exchange.setProperty()` allows storing internal state for the route lifecycle.
-   **Custom Headers**: `inMessage.setHeader()` helps add metadata for downstream logic.
-   **Body Transformation**: `inMessage.setBody()` allows us to modify the payload directly.
-   **Simple Language**: `${header.Key}` and `${exchangeProperty.Key}` provide easy access to data in log statements.

---

## ⚙️ How to Run
1.  Ensure you have the definition for `AdvancedExchangeRoute` in your project.
2.  Place a file (e.g., `test.txt`) inside the `data/input` folder.
3.  Run the application (Spring Boot main class).
4.  Observe the console logs to see the Exchange details and custom headers.
5.  Check the `data/output` folder for the processed file.

---