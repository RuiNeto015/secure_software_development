package com.desofs.backend.database.mappers;

import com.desofs.backend.database.models.EventDB;
import com.desofs.backend.database.models.UserDB;
import com.desofs.backend.domain.aggregates.UserDomain;
import com.desofs.backend.domain.valueobjects.*;
import org.springframework.stereotype.Component;

@Component
public class EventMapper {

    public Event dbToDomain(EventDB event) {
        return Event.create(event.getDateTime(), event.getState().getValue());
    }


}