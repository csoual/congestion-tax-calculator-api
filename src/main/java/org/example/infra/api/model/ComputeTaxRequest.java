package org.example.infra.api.model;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import javax.validation.constraints.NotNull;
import java.time.OffsetDateTime;
import java.util.List;

public record ComputeTaxRequest(
        @NotNull
        @Schema(required = true, example = "Car")
        String vehicleType,
        @NotNull
        @Schema(required = true, example = "[\"2013-08-20T06:54:10.374Z\"]")
        List<OffsetDateTime> dates) {

}
