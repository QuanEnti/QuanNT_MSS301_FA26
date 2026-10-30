package fu.quannt.room.controller;

import fu.quannt.room.common.OnCreate;
import fu.quannt.room.dto.ApiResponseDTO;
import fu.quannt.room.dto.RoomDTO;
import fu.quannt.room.service.RoomService;
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
@RequestMapping(value = "/api/rooms", produces = MediaType.APPLICATION_JSON_VALUE)
public class QuanNTRoomController {
    private final RoomService roomService;

    public QuanNTRoomController(RoomService roomService) {
        this.roomService = roomService;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponseDTO> createRoom(
            @Validated(OnCreate.class) @RequestBody RoomDTO dto) {
        return toResponse(roomService.createRoom(dto), HttpStatus.CREATED);
    }

    @PutMapping(value = "/{roomId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponseDTO> updateRoom(
            @PathVariable Long roomId, @Valid @RequestBody RoomDTO dto) {
        return toResponse(roomService.updateRoom(roomId, dto), HttpStatus.OK);
    }

    @GetMapping("/{roomId}")
    public ResponseEntity<ApiResponseDTO> getRoom(@PathVariable Long roomId) {
        return toResponse(roomService.getRoom(roomId), HttpStatus.OK);
    }

    @DeleteMapping("/{roomId}")
    public ResponseEntity<ApiResponseDTO> setToMaintenance(@PathVariable Long roomId) {
        return toResponse(roomService.setToMaintenance(roomId), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<ApiResponseDTO> getRooms(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String roomType,
            @RequestParam(required = false) String status) {
        return toResponse(roomService.getRooms(page, size, roomType, status), HttpStatus.OK);
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
