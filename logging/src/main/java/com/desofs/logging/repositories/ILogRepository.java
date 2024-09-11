package com.desofs.logging.repositories;

import com.desofs.logging.model.Log;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ILogRepository extends MongoRepository<Log, String> {
}
