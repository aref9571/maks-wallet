package io.github.aref9571.maks.common.web;

import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties(prefix = "maks.app")
@Validated
public record AppProperties(@NotBlank String version) {
}
