package fu.quannt.room.service;

import fu.quannt.room.dto.ApiResponseDTO;
import fu.quannt.room.dto.RoomDTO;
import fu.quannt.room.entity.Room;
import fu.quannt.room.entity.RoomStatus;
import fu.quannt.room.entity.RoomType;
import fu.quannt.room.repository.RoomRepository;
import fu.quannt.room.service.impl.RoomServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class RoomServiceImplTest {
    private RoomRepository repository;
    private RoomServiceImpl service;

    @BeforeEach
    void setUp() {
        repository = mock(RoomRepository.class);
        service = new RoomServiceImpl(repository);
    }

    @Test
    void createRoomDefaultsToAvailable() {
        RoomDTO dto = validRoomDTO();
        when(repository.save(any(Room.class))).thenAnswer(invocation -> {
            Room room = invocation.getArgument(0);
            room.setRoomId(1L);
            return room;
        });

        ApiResponseDTO response = service.createRoom(dto);

        assertThat(response.getStatus()).isEqualTo(1);
        RoomDTO created = (RoomDTO) response.getData();
        assertThat(created.getStatus()).isEqualTo(RoomStatus.AVAILABLE);
        assertThat(created.getRoomId()).isEqualTo(1L);
    }

    @Test
    void createRoomRejectsDuplicateNumber() {
        RoomDTO dto = validRoomDTO();
        when(repository.existsByRoomNumber("101")).thenReturn(true);

        ApiResponseDTO response = service.createRoom(dto);

        assertThat(response.getStatus()).isEqualTo(3);
        verify(repository, never()).save(any());
    }

    @Test
    void deleteOperationSetsMaintenanceWithoutDeleting() {
        Room room = roomEntity();
        when(repository.findById(1L)).thenReturn(Optional.of(room));

        ApiResponseDTO response = service.setToMaintenance(1L);

        assertThat(response.getStatus()).isEqualTo(1);
        assertThat(room.getStatus()).isEqualTo(RoomStatus.MAINTENANCE);
        verify(repository).save(room);
        verify(repository, never()).delete(any());
    }

    @Test
    void listRejectsOversizedPage() {
        ApiResponseDTO response = service.getRooms(0, 101, null, null);
        assertThat(response.getStatus()).isEqualTo(2);
    }

    @Test
    void getUnknownRoomReturnsNotFoundStatus() {
        when(repository.findById(99L)).thenReturn(Optional.empty());
        assertThat(service.getRoom(99L).getStatus()).isEqualTo(4);
    }

    private RoomDTO validRoomDTO() {
        RoomDTO dto = new RoomDTO();
        dto.setRoomNumber("101");
        dto.setRoomType(RoomType.SINGLE);
        dto.setPricePerNight(new BigDecimal("500000"));
        dto.setCapacity(1);
        dto.setFloor(1);
        return dto;
    }

    private Room roomEntity() {
        Room room = new Room();
        room.setRoomId(1L);
        room.setRoomNumber("101");
        room.setRoomType(RoomType.SINGLE);
        room.setPricePerNight(new BigDecimal("500000"));
        room.setCapacity(1);
        room.setFloor(1);
        room.setStatus(RoomStatus.AVAILABLE);
        return room;
    }
}
