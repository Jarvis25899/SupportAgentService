package com.coviam.crm.supportagent.repository;

import com.coviam.crm.supportagent.document.SupportAgent;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SupportAgentRepository extends MongoRepository<SupportAgent,String> {
}
