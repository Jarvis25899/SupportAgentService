package com.coviam.crm.supportagent.service;

import com.coviam.crm.supportagent.document.SaTicket;
import com.coviam.crm.supportagent.document.SupportAgent;
import com.coviam.crm.supportagent.document.Ticket;
import com.coviam.crm.supportagent.dto.CommentDTO;
import com.coviam.crm.supportagent.dto.PostDTO;

import java.util.List;

public interface SupportAgentService {

    SupportAgent addSupportAgent(SupportAgent supportAgent);
    void deleteSupportAgent(String sId);
    List<Ticket> getTicketList();
    SaTicket assignTicket(SaTicket saTicket);
    List<SupportAgent> getSAList();
    Ticket getTicketById(String ticketId);
    Ticket uploadComments(CommentDTO commentDTO);
    String closeTicket(String ticketId);
    List<Ticket> getTicketsBySAId(String supportAgentId);
    String createTicket(PostDTO postDTO);

}
