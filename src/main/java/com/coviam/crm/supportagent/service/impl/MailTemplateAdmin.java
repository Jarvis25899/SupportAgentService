package com.coviam.crm.supportagent.service.impl;

import com.coviam.crm.supportagent.document.Ticket;
import com.coviam.crm.supportagent.dto.MailDTO;

public class MailTemplateAdmin {

    public static MailDTO mail(Ticket ticket){
        MailDTO mailDTO = new MailDTO();
        mailDTO.setUserEmail("jainilpatel807@gmail.com");
        long mailNumber = ticket.getMailCount();
        switch ((int) mailNumber){
            case 0:
            case 1:
                mailDTO.setContent("Hey Jainil,\n" +
                        "\n" +
                        "The ticket, "+ticket.getTicketId()+", has been generated since "+ticket.getCreatedTime()+". Please assign it to an agent.\n" +
                        "\n" +
                        "Thank you.");
                break;
            case 2:
            case 3:
                mailDTO.setContent("Hey Jainil,\n" +
                        "\n" +
                        "The ticket, "+ticket.getTicketId()+", has been generated since "+ticket.getCreatedTime()+". Please assign it to an agent as soon as possible.\n" +
                        "\n" +
                        "Thank you.");
                break;
            case 4:
                mailDTO.setContent("Jainil,\n" +
                        "\n" +
                        "The ticket, "+ticket.getTicketId()+", has been generated since "+ticket.getCreatedTime()+". Assign it to an agent now!!!");
                break;
        }
        return mailDTO;
    }
}
