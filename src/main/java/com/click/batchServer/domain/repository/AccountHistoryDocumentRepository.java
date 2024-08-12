package com.click.batchServer.domain.repository;

import com.click.batchServer.domain.mongo.AccountHistoryDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface AccountHistoryDocumentRepository extends
    MongoRepository<AccountHistoryDocument, String> {

}
