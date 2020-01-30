package com.coviam.crm.supportagent.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SupportAgentDTO {

    private String supportAgentId;
    private String supportAgentName;
    private String supportAgentEmail;
    private long ticketsResolved;
    private long ticketsPending;

}
