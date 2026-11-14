package io.github.aref9571.maks.common.web;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(StatusController.class)
@TestPropertySource(properties = "maks.app.version=1.2.3")
@EnableConfigurationProperties(AppProperties.class)
class StatusControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void returnsStatusUpWithVersion() throws Exception {
        mockMvc.perform(get("/api/status"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("UP"))
            .andExpect(jsonPath("$.version").value("1.2.3"));
    }
}
