package com.example;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@Slf4j
public class TestController {
    private static final String CORRELATION_ID_HEADER = "Correlation-Id";


    @GetMapping("/test-logging")
    public void testLogging(@RequestAttribute(value = CORRELATION_ID_HEADER, required = false) String correlationId) {

        if (correlationId == null || correlationId.isEmpty()) {
            correlationId = UUID.randomUUID().toString();
            String id = MDC.get(CORRELATION_ID_HEADER);
            MDC.put(CORRELATION_ID_HEADER, correlationId);
        }
        log.info("Debug logging test for correaltion Id: " + correlationId);
        log.info("Info test for correaltion Id: " + correlationId);
        log.info("Warning test for correaltion Id: " + correlationId);
        log.info("Error test for correaltion Id: " + correlationId);


    }
}