package fu.quannt.reservation.repository;

import fu.quannt.reservation.entity.Reservation;
import fu.quannt.reservation.entity.ReservationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    @Query("""
            select r from Reservation r
            where (:guestName is null or lower(r.guestName) like lower(concat('%', :guestName, '%')))
              and (:status is null or r.status = :status)
            """)
    Page<Reservation> search(@Param("guestName") String guestName,
                             @Param("status") ReservationStatus status,
                             Pageable pageable);
}
