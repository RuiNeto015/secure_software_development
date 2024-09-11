package com.desofs.backend.database.springRepositories;

import com.desofs.backend.database.models.BookingDB;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface BookingRepositoryJPA extends CrudRepository<BookingDB, String> {

    List<BookingDB> findByPropertyId(String propertyId);

    List<BookingDB> findAllByAccountId(String userId);

    List<BookingDB> findAllByPropertyId(String propertyId);

    @Query(value = """
              SELECT DISTINCT b.id,
                              b.account_id,
                              b.property_id,
                              b.from_date,
                              b.to_date,
                              b.created_at
              FROM booking b
                       JOIN event e ON b.id = e.booking_id
              WHERE b.to_date <= NOW()
                AND 1 = (SELECT e2.state_id
                                  FROM event e2
                                  ORDER BY e2.state_id DESC
                                  LIMIT 1);
            """, nativeQuery = true)
    List<BookingDB> findAllBookingWhereCheckoutDatePassed();
}
