package com.desofs.backend.database.repositories;

import com.desofs.backend.database.mappers.PaymentMapper;
import com.desofs.backend.database.models.PaymentDB;
import com.desofs.backend.database.springRepositories.PaymentRepositoryJPA;
import com.desofs.backend.domain.entities.PaymentEntity;
import com.desofs.backend.exceptions.DatabaseException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentRepository {

    private final PaymentRepositoryJPA paymentRepositoryJPA;
    private final PaymentMapper mapper;

    public void create(PaymentEntity paymentDomain) throws DatabaseException {
        PaymentDB paymentDB = this.paymentRepositoryJPA.findById(paymentDomain.getId().value()).orElse(null);

        if (paymentDB != null) {
            throw new DatabaseException("Duplicated ID violation.");
        }

        this.paymentRepositoryJPA.save(this.mapper.domainToDb(paymentDomain));
    }

    public PaymentEntity findById(String id) {
        try {
            PaymentDB paymentDB = this.paymentRepositoryJPA.findById(id).orElse(null);
            if (paymentDB != null) {
                return this.mapper.dbToDomain(paymentDB);
            } else {
                return null;
            }
        } catch (Exception e) {
            return null;
        }
    }
}
