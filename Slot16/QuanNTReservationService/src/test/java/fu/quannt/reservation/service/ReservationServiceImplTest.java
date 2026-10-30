package fu.quannt.reservation.service;

import fu.quannt.reservation.client.RoomClient;
import fu.quannt.reservation.dto.ApiResponseDTO;
import fu.quannt.reservation.dto.ReservationDTO;
import fu.quannt.reservation.dto.ReservationDetailDTO;
import fu.quannt.reservation.dto.RoomApiResponseDTO;
import fu.quannt.reservation.dto.RoomDTO;
import fu.quannt.reservation.entity.Reservation;
import fu.quannt.reservation.entity.ReservationStatus;
import fu.quannt.reservation.repository.ReservationRepository;
import fu.quannt.reservation.service.impl.ReservationServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ReservationServiceImplTest {
    private ReservationRepository repository;
    private RoomClient roomClient;
    private ReservationServiceImpl service;

    @BeforeEach
    void setUp() {
        repository = mock(ReservationRepository.class);
        roomClient = mock(RoomClient.class);
        service = new ReservationServiceImpl(repository, roomClient);
    }

    @Test
    void createChecksRoomAndComputesAmountByNight() {
        ReservationDTO dto = validReservationDTO();
        when(roomClient.getRoom(1L)).thenReturn(roomResponse("AVAILABLE", 2));
        when(repository.save(any(Reservation.class))).thenAnswer(invocation -> {
            Reservation reservation = invocation.getArgument(0);
            reservation.setReservationId(10L);
            return reservation;
        });

        ApiResponseDTO response = service.createReservation(dto);

        assertThat(response.getStatus()).isEqualTo(1);
        ReservationDTO created = (ReservationDTO) response.getData();
        assertThat(created.getStatus()).isEqualTo(ReservationStatus.CONFIRMED);
        assertThat(created.getTotalAmount()).isEqualByComparingTo("1500000");
    }

    @Test
    void createRejectsUnavailableRoom() {
        when(roomClient.getRoom(1L)).thenReturn(roomResponse("MAINTENANCE", 2));
        ApiResponseDTO response = service.createReservation(validReservationDTO());
        assertThat(response.getStatus()).isEqualTo(5);
        verify(repository, never()).save(any());
    }

    @Test
    void createRejectsGuestCountAboveCapacity() {
        ReservationDTO dto = validReservationDTO();
        dto.setNumberOfGuests(2);
        when(roomClient.getRoom(1L)).thenReturn(roomResponse("AVAILABLE", 1));
        assertThat(service.createReservation(dto).getStatus()).isEqualTo(2);
    }

    @Test
    void createReturnsNotFoundWhenRoomServiceReportsMissingRoom() {
        RoomApiResponseDTO response = new RoomApiResponseDTO();
        response.setStatus(4);
        when(roomClient.getRoom(1L)).thenReturn(response);
        assertThat(service.createReservation(validReservationDTO()).getStatus()).isEqualTo(4);
    }

    @Test
    void detailContainsRoomFromRoomService() {
        Reservation reservation = reservationEntity();
        when(repository.findById(10L)).thenReturn(Optional.of(reservation));
        when(roomClient.getRoom(1L)).thenReturn(roomResponse("AVAILABLE", 2));

        ReservationDetailDTO detail = (ReservationDetailDTO) service.getReservation(10L).getData();

        assertThat(detail.getRoom().getRoomNumber()).isEqualTo("101");
        assertThat(detail.getReservationId()).isEqualTo(10L);
    }

    @Test
    void cancelChangesStatusWithoutDeletingRecord() {
        Reservation reservation = reservationEntity();
        when(repository.findById(10L)).thenReturn(Optional.of(reservation));

        ApiResponseDTO response = service.cancelReservation(10L);

        assertThat(response.getStatus()).isEqualTo(1);
        assertThat(reservation.getStatus()).isEqualTo(ReservationStatus.CANCELLED);
        verify(repository).save(reservation);
        verify(repository, never()).delete(any());
    }

    private ReservationDTO validReservationDTO() {
        ReservationDTO dto = new ReservationDTO();
        dto.setGuestName("Nguyen Van A");
        dto.setGuestEmail("a@example.com");
        dto.setGuestPhone("0900000000");
        dto.setCheckInDate(date(2026, 11, 1));
        dto.setCheckOutDate(date(2026, 11, 4));
        dto.setNumberOfGuests(1);
        dto.setRoomId(1L);
        return dto;
    }

    private Reservation reservationEntity() {
        Reservation reservation = new Reservation();
        reservation.setReservationId(10L);
        reservation.setGuestName("Nguyen Van A");
        reservation.setGuestEmail("a@example.com");
        reservation.setGuestPhone("0900000000");
        reservation.setCheckInDate(date(2026, 11, 1));
        reservation.setCheckOutDate(date(2026, 11, 4));
        reservation.setNumberOfGuests(1);
        reservation.setTotalAmount(new BigDecimal("1500000"));
        reservation.setStatus(ReservationStatus.CONFIRMED);
        reservation.setRoomId(1L);
        return reservation;
    }

    private RoomApiResponseDTO roomResponse(String status, int capacity) {
        RoomDTO room = new RoomDTO();
        room.setRoomId(1L);
        room.setRoomNumber("101");
        room.setRoomType("SINGLE");
        room.setPricePerNight(new BigDecimal("500000"));
        room.setCapacity(capacity);
        room.setFloor(1);
        room.setStatus(status);
        RoomApiResponseDTO response = new RoomApiResponseDTO();
        response.setStatus(1);
        response.setData(room);
        return response;
    }

    private Date date(int year, int month, int day) {
        return Date.from(LocalDate.of(year, month, day)
                .atStartOfDay(ZoneOffset.UTC).toInstant());
    }
}
