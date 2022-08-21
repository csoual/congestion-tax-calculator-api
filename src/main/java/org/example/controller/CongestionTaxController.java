package org.example.controller;

import org.example.controller.model.ComputeTaxRequest;
import org.example.service.CongestionTaxService;
import org.example.controller.model.ComputeTaxResponse;

import javax.inject.Inject;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.time.OffsetDateTime;
import java.util.Date;

@Path("/tax_calculator")
public class CongestionTaxController {

    @Inject
    CongestionTaxService congestionTaxService;

    @POST
    @Path("")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public ComputeTaxResponse computeTax(@Valid @NotNull ComputeTaxRequest request) {
        Date[] dates = new Date[request.dates().size()];
        for (int i = 0; i < request.dates().size(); i++) {
            OffsetDateTime date = request.dates().get(i);
            dates[i] = new Date(date.getYear() - 1900, date.getMonthValue() - 1, date.getDayOfMonth(), date.getHour(), date.getMinute(), date.getSecond());
        }
        int taxAmount = congestionTaxService.getTax(request::vehicleType, dates);
        return new ComputeTaxResponse(taxAmount);
    }

}
