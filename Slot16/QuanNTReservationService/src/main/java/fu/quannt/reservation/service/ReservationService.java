package fu.quannt.reservation.service;

import fu.quannt.reservation.dto.ApiResponseDTO;
import fu.quannt.reservation.dto.ReservationDTO;

public interface ReservationService {
    ApiResponseDTO createReservation(ReservationDTO dto);
    ApiResponseDTO updateReservation(Long reservationId, ReservationDTO dto);
    ApiResponseDTO getReservation(Long reservationId);
    ApiResponseDTO cancelReservation(Long reservationId);
    ApiResponseDTO getReservations(int page, int size, String guestName, String status);
}
