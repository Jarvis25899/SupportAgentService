package com.coviam.crm.supportagent.repository;

import com.coviam.crm.supportagent.document.SaTicket;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SaTicketRespository extends MongoRepository<SaTicket,String> {
}
