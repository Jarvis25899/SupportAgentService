package com.coviam.crm.supportagent.document;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;


@Getter
@Setter
@Document(collection = "SupportAgent")
public class SupportAgent {

    @Id
    private String supportAgentId;//from common infra
    private String supportAgentName;
    private String supportAgentEmail;
    private long ticketsResolved;
    private long ticketsPending;

}
