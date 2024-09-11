drop table if exists event;

drop table if exists booking;

drop table if exists image;

drop table if exists payment;

drop table if exists price_night_interval;

drop table if exists review;

drop table if exists rental_property;

drop table if exists state;

CREATE TABLE IF NOT EXISTS rental_property
(
    id                   varchar(255) NOT NULL
    PRIMARY KEY,
    price_night_default  float        NOT NULL,
    property_owner_id    varchar(255) NOT NULL,
    property_name        varchar(255) NOT NULL,
    lat                  double       NOT NULL,
    lon                  double       NOT NULL,
    max_guests           int          NOT NULL,
    num_bedrooms         int          NOT NULL,
    num_bathrooms        int          NOT NULL,
    property_description varchar(255) NOT NULL,
    is_active            tinyint(1)   NOT NULL
    );

CREATE TABLE IF NOT EXISTS price_night_interval
(
    id                 varchar(255) NOT NULL
    PRIMARY KEY,
    rental_property_id varchar(255) NOT NULL,
    price              float        NOT NULL,
    from_date          date         NOT NULL,
    to_date            date         NOT NULL,
    CONSTRAINT PriceNightInterval_RentalProperty_id_fk
    FOREIGN KEY (rental_property_id) REFERENCES rental_property (id)
    );

CREATE TABLE IF NOT EXISTS state
(
    id    int          NOT NULL
    PRIMARY KEY,
    value varchar(255) NOT NULL
    );

INSERT INTO state (id, value) VALUES (1, 'BOOKED');
INSERT INTO state (id, value) VALUES (2, 'COMPLETED');
INSERT INTO state (id, value) VALUES (3, 'CANCELLED');
INSERT INTO state (id, value) VALUES (4, 'REFUNDED');

CREATE TABLE IF NOT EXISTS user
(
    id              varchar(255)         NOT NULL
    PRIMARY KEY,
    name            varchar(255)         NOT NULL,
    email           varchar(255)         NOT NULL,
    password        varchar(255)         NOT NULL,
    phone_number    varchar(255)         NOT NULL,
    role            varchar(255)         NOT NULL,
    is_banned tinyint(1) DEFAULT 0 NOT NULL
    );

CREATE TABLE IF NOT EXISTS booking
(
    id          varchar(255)                       NOT NULL
    PRIMARY KEY,
    account_id  varchar(255)                       NOT NULL,
    property_id varchar(255)                       NOT NULL,
    from_date   date                               NOT NULL,
    to_date     date                               NOT NULL,
    created_at  datetime DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT Booking_User_id_fk
    FOREIGN KEY (account_id) REFERENCES user (id),
    CONSTRAINT booking_property_id
    FOREIGN KEY (property_id) REFERENCES rental_property (id)
    );

CREATE TABLE IF NOT EXISTS event
(
    id         varchar(255)             NOT NULL
    PRIMARY KEY,
    booking_id varchar(255)             NOT NULL,
    date_time  datetime DEFAULT (NOW()) NOT NULL,
    state_id   varchar(255)             NOT NULL,
    CONSTRAINT Event_Booking_id_fk
    FOREIGN KEY (booking_id) REFERENCES booking (id)
    );

CREATE TABLE IF NOT EXISTS payment
(
    id                 varchar(255)                       NOT NULL
    PRIMARY KEY,
    booking_id         varchar(255)                       NOT NULL,
    total              float                              NOT NULL,
    credit_card_number varchar(255)                       NOT NULL,
    cvc                varchar(255)                       NOT NULL,
    expiration_date    datetime                           NOT NULL,
    email              varchar(255)                       NOT NULL,
    name_on_card       varchar(255)                       NOT NULL,
    created_at         datetime DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT payment_booking_fk
    FOREIGN KEY (booking_id) REFERENCES booking (id)
    );

CREATE TABLE IF NOT EXISTS review
(
    id         varchar(255)         NOT NULL
    PRIMARY KEY,
    user_id    varchar(255)         NOT NULL,
    booking_id varchar(255)         NOT NULL,
    text       varchar(255)         NOT NULL,
    stars      int                  NOT NULL,
    state  varchar(255)             NOT NULL,
    CONSTRAINT Review_BookingId_id_fk
    FOREIGN KEY (booking_id) REFERENCES booking (id),
    CONSTRAINT Review_User_id_fk
    FOREIGN KEY (user_id) REFERENCES user (id),
    CONSTRAINT stars
    CHECK ((`stars` < 6) AND (`stars` > -(1)))
    );

CREATE TABLE IF NOT EXISTS image
(
    id        varchar(255) NOT NULL
    PRIMARY KEY,
    review_id varchar(255) NOT NULL,
    reference varchar(255) NOT NULL,
    CONSTRAINT Image_Review_id_fk
    FOREIGN KEY (review_id) REFERENCES review (id)
    );

