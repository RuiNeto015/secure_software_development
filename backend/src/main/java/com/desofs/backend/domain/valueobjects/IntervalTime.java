package com.desofs.backend.domain.valueobjects;

import java.util.Date;

import static org.apache.commons.lang3.Validate.isTrue;
import static org.apache.commons.lang3.Validate.notNull;

public class IntervalTime {

    private final Date from;
    private final Date to;

    private IntervalTime(Date from, Date to) {
        this.from = from;
        this.to = to;
    }

    public static IntervalTime create(Date from, Date to) {
        notNull(from,
                "From date must not be null.");
        notNull(to,
                "To date must not be null.");
        isTrue(from.before(to),
                "The from date must be before the to date.");

        return new IntervalTime(new Date(from.getTime()), new Date(to.getTime()));
    }

    public IntervalTime copy() {
        return new IntervalTime(new Date(from.getTime()), new Date(to.getTime()));
    }

    public Date getFrom() {
        return new Date(from.getTime());
    }

    public Date getTo() {
        return new Date(to.getTime());
    }
}
