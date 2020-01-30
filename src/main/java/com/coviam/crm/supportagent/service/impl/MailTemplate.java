package com.coviam.crm.supportagent.service.impl;

import com.coviam.crm.supportagent.document.Ticket;
import com.coviam.crm.supportagent.dto.MailDTO;

public class MailTemplate  {

    public static MailDTO mail(Ticket ticket){
        MailDTO mailDTO = new MailDTO();
        mailDTO.setUserEmail("Admin Email");
        long mailNumber = ticket.getMailCount();
        switch ((int) mailNumber){
            case 0:
            case 1:
                mailDTO.setContent("Hey {$name},\n" +
                        "\n" +
                        "The ticket, {$ticketId}, has been generated since {$Time} minutes. Please assign it to an agent.\n" +
                        "\n" +
                        "Thank you.");
                break;
            case 2:
            case 3:
                mailDTO.setContent("Hey {$name},\n" +
                        "\n" +
                        "The ticket, {$ticketId}, has been generated since {$Time} minutes. Please assign it to an agent as soon as possible.\n" +
                        "\n" +
                        "Thank you.");
                break;
            case 4:
                mailDTO.setContent("{$name},\n" +
                        "\n" +
                        "The ticket, {$ticketId}, has been generated since {$Time} minutes. Assign it to an agent now!!!");
                break;
        }
        return mailDTO;
    }
}
