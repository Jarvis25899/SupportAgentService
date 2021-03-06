package com.coviam.crm.supportagent.service.impl;

import com.coviam.crm.supportagent.document.SupportAgent;
import com.coviam.crm.supportagent.document.Ticket;
import com.coviam.crm.supportagent.dto.MailDTO;

public class MailTemplateAgent {
    public static MailDTO mail(Ticket ticket, SupportAgent supportAgent){
        MailDTO mailDTO = new MailDTO();
        mailDTO.setUserEmail(supportAgent.getSupportAgentEmail());
        long mailNumber = ticket.getMailCount();
        switch ((int) mailNumber){
            case 0:
            case 1:
                mailDTO.setContent("Hey "+supportAgent.getSupportAgentName()+",\n" +
                        "\n" +
                        "The ticket, "+ticket.getTicketId()+", has been assigned to you since "+ticket.getUpdatedTime()+". Please resolve the matter.\n" +
                        "\n" +
                        "Thank you.");
                break;
            case 2:
            case 3:
                mailDTO.setContent("Hey "+supportAgent.getSupportAgentName()+",\n" +
                        "\n" +
                        "The ticket, "+ticket.getTicketId()+", has been assigned to you since "+ticket.getUpdatedTime()+". Please resolve the matter as soon as possible.\n" +
                        "\n" +
                        "Thank you.");
                break;
            case 4:
                mailDTO.setContent(supportAgent.getSupportAgentName()+",\n" +
                        "\n" +
                        "The ticket, "+ticket.getTicketId()+", has been assigned to you since "+ticket.getUpdatedTime()+". Resolve the matter now or don't bother coming in tomorrow!");
                break;
        }
        return mailDTO;
    }
}
