package com.elcptn.mgmtsvc.dto;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;

import java.util.List;

/* @author: kc, created on 3/9/23 */
@Data
public class DashboardMetricsDto {
    private List<StatusMetricDto> inbound;

    private List<StatusMetricDto> outbound;

    private JsonNode entities;
}
