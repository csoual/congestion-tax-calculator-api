package org.example.infra.api.model;

import javax.validation.constraints.NotNull;
import java.time.OffsetDateTime;
import java.util.List;

public record ComputeTaxRequest(@NotNull String vehicleType, @NotNull List<OffsetDateTime> dates) {

}
