package fu.quannt.reservation.controller;

import fu.quannt.reservation.common.OnCreate;
import fu.quannt.reservation.dto.ApiResponseDTO;
import fu.quannt.reservation.dto.ReservationDTO;
import fu.quannt.reservation.service.ReservationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/reservations", produces = MediaType.APPLICATION_JSON_VALUE)
public class QuanNTReservationController {
    private final ReservationService reservationService;

    public QuanNTReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponseDTO> createReservation(
            @Validated(OnCreate.class) @RequestBody ReservationDTO dto) {
        return toResponse(reservationService.createReservation(dto), HttpStatus.CREATED);
    }

    @PutMapping(value = "/{reservationId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponseDTO> updateReservation(
            @PathVariable Long reservationId, @Valid @RequestBody ReservationDTO dto) {
        return toResponse(reservationService.updateReservation(reservationId, dto), HttpStatus.OK);
    }

    @GetMapping("/{reservationId}")
    public ResponseEntity<ApiResponseDTO> getReservation(@PathVariable Long reservationId) {
        return toResponse(reservationService.getReservation(reservationId), HttpStatus.OK);
    }

    @DeleteMapping("/{reservationId}")
    public ResponseEntity<ApiResponseDTO> cancelReservation(@PathVariable Long reservationId) {
        return toResponse(reservationService.cancelReservation(reservationId), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<ApiResponseDTO> getReservations(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String guestName,
            @RequestParam(required = false) String status) {
        return toResponse(reservationService.getReservations(
                page, size, guestName, status), HttpStatus.OK);
    }

    private ResponseEntity<ApiResponseDTO> toResponse(
            ApiResponseDTO response, HttpStatus successStatus) {
        HttpStatus status = switch (response.getStatus()) {
            case 1 -> successStatus;
            case 2 -> HttpStatus.NOT_ACCEPTABLE;
            case 3 -> HttpStatus.IM_USED;
            case 4 -> HttpStatus.NOT_FOUND;
            case 5 -> HttpStatus.BAD_REQUEST;
            default -> HttpStatus.INTERNAL_SERVER_ERROR;
        };
        return ResponseEntity.status(status).body(response);
    }
}
