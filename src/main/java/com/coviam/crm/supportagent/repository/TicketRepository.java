package com.coviam.crm.supportagent.repository;

import com.coviam.crm.supportagent.document.Ticket;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TicketRepository extends MongoRepository<Ticket,String> {
}
