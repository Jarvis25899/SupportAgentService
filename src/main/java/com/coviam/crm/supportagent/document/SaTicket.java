package com.coviam.crm.supportagent.document;


import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;



@Getter
@Setter
@Document(collection = "SaTicket")
public class SaTicket {

    @Id
    private String supportAgentTicketId;
    private String supportAgentId;//from common infra
    private String ticketId;//postId

}
