package fu.quannt.reservation.service.impl;

import feign.FeignException;
import fu.quannt.reservation.client.RoomClient;
import fu.quannt.reservation.dto.ApiResponseDTO;
import fu.quannt.reservation.dto.PageDTO;
import fu.quannt.reservation.dto.ReservationDTO;
import fu.quannt.reservation.dto.ReservationDetailDTO;
import fu.quannt.reservation.dto.RoomApiResponseDTO;
import fu.quannt.reservation.dto.RoomDTO;
import fu.quannt.reservation.entity.Reservation;
import fu.quannt.reservation.entity.ReservationStatus;
import fu.quannt.reservation.repository.ReservationRepository;
import fu.quannt.reservation.service.ReservationService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.ArrayList;
import java.util.List;

@Service
public class ReservationServiceImpl implements ReservationService {
    private final ReservationRepository reservationRepository;
    private final RoomClient roomClient;

    public ReservationServiceImpl(ReservationRepository reservationRepository,
                                  RoomClient roomClient) {
        this.reservationRepository = reservationRepository;
        this.roomClient = roomClient;
    }

    @Override
    @Transactional
    public ApiResponseDTO createReservation(ReservationDTO dto) {
        if (!hasValidDates(dto.getCheckInDate(), dto.getCheckOutDate())) {
            return ApiResponseDTO.error(2, "checkOutDate must be after checkInDate");
        }
        RoomResult roomResult = findRoom(dto.getRoomId());
        if (roomResult.notFound()) return ApiResponseDTO.error(4, "Room ID is not found");
        if (roomResult.room() == null) return ApiResponseDTO.error(0, "Failed to get room");
        RoomDTO room = roomResult.room();
        if (!"AVAILABLE".equals(room.getStatus())) {
            return ApiResponseDTO.error(5, "Room is not AVAILABLE for reservation");
        }
        if (dto.getNumberOfGuests() > room.getCapacity()) {
            return ApiResponseDTO.error(2, "numberOfGuests must not exceed room capacity");
        }

        Reservation reservation = new Reservation();
        applyGuestData(reservation, dto);
        reservation.setCheckInDate(dto.getCheckInDate());
        reservation.setCheckOutDate(dto.getCheckOutDate());
        reservation.setNumberOfGuests(dto.getNumberOfGuests());
        reservation.setRoomId(dto.getRoomId());
        reservation.setTotalAmount(calculateTotal(
                room.getPricePerNight(), dto.getCheckInDate(), dto.getCheckOutDate()));
        reservation.setStatus(ReservationStatus.CONFIRMED);
        return ApiResponseDTO.success("Reservation created successfully",
                toDTO(reservationRepository.save(reservation)));
    }

    @Override
    @Transactional
    public ApiResponseDTO updateReservation(Long reservationId, ReservationDTO dto) {
        Reservation reservation = reservationRepository.findById(reservationId).orElse(null);
        if (reservation == null) return ApiResponseDTO.error(4, "Reservation ID is not found");

        Date checkIn = dto.getCheckInDate() == null ? reservation.getCheckInDate() : dto.getCheckInDate();
        Date checkOut = dto.getCheckOutDate() == null ? reservation.getCheckOutDate() : dto.getCheckOutDate();
        int guests = dto.getNumberOfGuests() == null
                ? reservation.getNumberOfGuests() : dto.getNumberOfGuests();
        long roomId = dto.getRoomId() == null ? reservation.getRoomId() : dto.getRoomId();
        if (!hasValidDates(checkIn, checkOut)) {
            return ApiResponseDTO.error(2, "checkOutDate must be after checkInDate");
        }

        boolean bookingChanged = dto.getCheckInDate() != null || dto.getCheckOutDate() != null
                || dto.getNumberOfGuests() != null || dto.getRoomId() != null;
        if (bookingChanged) {
            RoomResult roomResult = findRoom(roomId);
            if (roomResult.notFound()) return ApiResponseDTO.error(4, "Room ID is not found");
            if (roomResult.room() == null) return ApiResponseDTO.error(0, "Failed to get room");
            RoomDTO room = roomResult.room();
            if (dto.getRoomId() != null && !"AVAILABLE".equals(room.getStatus())) {
                return ApiResponseDTO.error(5, "Room is not AVAILABLE for reservation");
            }
            if (guests > room.getCapacity()) {
                return ApiResponseDTO.error(2, "numberOfGuests must not exceed room capacity");
            }
            reservation.setTotalAmount(calculateTotal(room.getPricePerNight(), checkIn, checkOut));
        }

        applyGuestData(reservation, dto);
        reservation.setCheckInDate(checkIn);
        reservation.setCheckOutDate(checkOut);
        reservation.setNumberOfGuests(guests);
        reservation.setRoomId(roomId);
        if (dto.getStatus() != null) reservation.setStatus(dto.getStatus());
        return ApiResponseDTO.success("Reservation updated successfully",
                toDTO(reservationRepository.save(reservation)));
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponseDTO getReservation(Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId).orElse(null);
        if (reservation == null) return ApiResponseDTO.error(4, "Reservation is not found");
        RoomResult roomResult = findRoom(reservation.getRoomId());
        if (roomResult.room() == null) {
            return ApiResponseDTO.error(0, "Failed to get room from RoomService");
        }
        return ApiResponseDTO.success("Successful", toDetailDTO(reservation, roomResult.room()));
    }

    @Override
    @Transactional
    public ApiResponseDTO cancelReservation(Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId).orElse(null);
        if (reservation == null) return ApiResponseDTO.error(4, "Reservation is not found");
        reservation.setStatus(ReservationStatus.CANCELLED);
        reservationRepository.save(reservation);
        return ApiResponseDTO.success("Reservation cancelled successfully", null);
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponseDTO getReservations(int page, int size, String guestName, String status) {
        if (page < 0) return ApiResponseDTO.error(2, "page must be greater than or equal to 0");
        if (size < 1 || size > 100) return ApiResponseDTO.error(2, "size must be between 1 and 100");

        ReservationStatus statusFilter;
        try {
            statusFilter = status == null || status.isBlank() ? null : ReservationStatus.valueOf(status);
        } catch (IllegalArgumentException exception) {
            return ApiResponseDTO.error(2, "status is invalid");
        }
        String nameFilter = guestName == null || guestName.isBlank() ? null : guestName;
        Page<Reservation> result = reservationRepository.search(nameFilter, statusFilter,
                PageRequest.of(page, size, Sort.by("reservationId").ascending()));
        List<ReservationDetailDTO> reservations = new ArrayList<>();
        for (Reservation reservation : result.getContent()) {
            RoomResult roomResult = findRoom(reservation.getRoomId());
            if (roomResult.room() == null) {
                return ApiResponseDTO.error(0, "Failed to get room from RoomService");
            }
            reservations.add(toDetailDTO(reservation, roomResult.room()));
        }
        return ApiResponseDTO.success("Successful", new PageDTO(
                result.getSize(), result.getNumber(), result.getTotalPages(),
                result.getTotalElements(), result.isFirst(), result.isLast(), reservations));
    }

    private void applyGuestData(Reservation reservation, ReservationDTO dto) {
        if (dto.getGuestName() != null) reservation.setGuestName(dto.getGuestName());
        if (dto.getGuestEmail() != null) reservation.setGuestEmail(dto.getGuestEmail());
        if (dto.getGuestPhone() != null) reservation.setGuestPhone(dto.getGuestPhone());
    }

    private RoomResult findRoom(Long roomId) {
        try {
            RoomApiResponseDTO response = roomClient.getRoom(roomId);
            return new RoomResult(response.getStatus() == 4, response.getData());
        } catch (FeignException.NotFound exception) {
            return new RoomResult(true, null);
        } catch (FeignException exception) {
            return new RoomResult(false, null);
        }
    }

    private ReservationDetailDTO toDetailDTO(Reservation reservation, RoomDTO room) {
        ReservationDetailDTO dto = new ReservationDetailDTO();
        copyReservation(reservation, dto);
        dto.setRoom(room);
        return dto;
    }

    private ReservationDTO toDTO(Reservation reservation) {
        ReservationDTO dto = new ReservationDTO();
        copyReservation(reservation, dto);
        return dto;
    }

    private void copyReservation(Reservation reservation, ReservationDTO dto) {
        dto.setReservationId(reservation.getReservationId());
        dto.setGuestName(reservation.getGuestName());
        dto.setGuestEmail(reservation.getGuestEmail());
        dto.setGuestPhone(reservation.getGuestPhone());
        dto.setCheckInDate(reservation.getCheckInDate());
        dto.setCheckOutDate(reservation.getCheckOutDate());
        dto.setNumberOfGuests(reservation.getNumberOfGuests());
        dto.setTotalAmount(reservation.getTotalAmount());
        dto.setStatus(reservation.getStatus());
        dto.setRoomId(reservation.getRoomId());
    }

    private boolean hasValidDates(Date checkIn, Date checkOut) {
        return checkIn != null && checkOut != null && checkOut.after(checkIn);
    }

    private BigDecimal calculateTotal(BigDecimal price, Date checkIn, Date checkOut) {
        long nights = ChronoUnit.DAYS.between(toLocalDate(checkIn), toLocalDate(checkOut));
        return price.multiply(BigDecimal.valueOf(nights));
    }

    private LocalDate toLocalDate(Date date) {
        return Instant.ofEpochMilli(date.getTime()).atZone(ZoneOffset.UTC).toLocalDate();
    }

    private record RoomResult(boolean notFound, RoomDTO room) {
    }
}
