package io.github.aref9571.maks.common.web;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class StatusController {

    @GetMapping("/api/status")
    public StatusResponse status() {
        return new StatusResponse("UP", "dev");
    }
}
