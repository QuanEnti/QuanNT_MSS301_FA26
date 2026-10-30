package fu.quannt.room.repository;

import fu.quannt.room.entity.Room;
import fu.quannt.room.entity.RoomStatus;
import fu.quannt.room.entity.RoomType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RoomRepository extends JpaRepository<Room, Long> {
    boolean existsByRoomNumber(String roomNumber);
    boolean existsByRoomNumberAndRoomIdNot(String roomNumber, Long roomId);

    @Query("""
            select r from Room r
            where (:roomType is null or r.roomType = :roomType)
              and (:status is null or r.status = :status)
            """)
    Page<Room> search(@Param("roomType") RoomType roomType,
                      @Param("status") RoomStatus status,
                      Pageable pageable);
}
