package io.github.aref9571.maks.common.web;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class StatusController {

    private final AppProperties appProperties;

    public StatusController(AppProperties appProperties) {
        this.appProperties = appProperties;
    }

    @GetMapping("/api/status")
    public StatusResponse status() {
        return new StatusResponse("UP", appProperties.version());
    }
}
