package fu.quannt.room.service.impl;

import fu.quannt.room.dto.ApiResponseDTO;
import fu.quannt.room.dto.PageDTO;
import fu.quannt.room.dto.RoomDTO;
import fu.quannt.room.entity.Room;
import fu.quannt.room.entity.RoomStatus;
import fu.quannt.room.entity.RoomType;
import fu.quannt.room.repository.RoomRepository;
import fu.quannt.room.service.RoomService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class RoomServiceImpl implements RoomService {
    private final RoomRepository roomRepository;

    public RoomServiceImpl(RoomRepository roomRepository) {
        this.roomRepository = roomRepository;
    }

    @Override
    @Transactional
    public ApiResponseDTO createRoom(RoomDTO dto) {
        if (roomRepository.existsByRoomNumber(dto.getRoomNumber())) {
            return ApiResponseDTO.error(3, "Room number already exists");
        }
        Room room = new Room();
        room.setRoomNumber(dto.getRoomNumber());
        room.setRoomType(dto.getRoomType());
        room.setPricePerNight(dto.getPricePerNight());
        room.setCapacity(dto.getCapacity());
        room.setFloor(dto.getFloor());
        room.setStatus(RoomStatus.AVAILABLE);
        return ApiResponseDTO.success(
                "Room created successfully", toDTO(roomRepository.save(room)));
    }

    @Override
    @Transactional
    public ApiResponseDTO updateRoom(Long roomId, RoomDTO dto) {
        Room room = roomRepository.findById(roomId).orElse(null);
        if (room == null) {
            return ApiResponseDTO.error(4, "Room is not found");
        }
        if (dto.getRoomNumber() != null
                && roomRepository.existsByRoomNumberAndRoomIdNot(dto.getRoomNumber(), roomId)) {
            return ApiResponseDTO.error(3, "Room number already exists");
        }
        if (dto.getRoomNumber() != null) room.setRoomNumber(dto.getRoomNumber());
        if (dto.getRoomType() != null) room.setRoomType(dto.getRoomType());
        if (dto.getPricePerNight() != null) room.setPricePerNight(dto.getPricePerNight());
        if (dto.getCapacity() != null) room.setCapacity(dto.getCapacity());
        if (dto.getFloor() != null) room.setFloor(dto.getFloor());
        if (dto.getStatus() != null) room.setStatus(dto.getStatus());
        return ApiResponseDTO.success(
                "Room updated successfully", toDTO(roomRepository.save(room)));
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponseDTO getRoom(Long roomId) {
        return roomRepository.findById(roomId)
                .map(room -> ApiResponseDTO.success("Successful", toDTO(room)))
                .orElseGet(() -> ApiResponseDTO.error(4, "Room is not found"));
    }

    @Override
    @Transactional
    public ApiResponseDTO setToMaintenance(Long roomId) {
        Room room = roomRepository.findById(roomId).orElse(null);
        if (room == null) {
            return ApiResponseDTO.error(4, "Room is not found");
        }
        room.setStatus(RoomStatus.MAINTENANCE);
        roomRepository.save(room);
        return ApiResponseDTO.success("Room set to MAINTENANCE successfully", null);
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponseDTO getRooms(int page, int size, String roomType, String status) {
        if (page < 0) return ApiResponseDTO.error(2, "page must be greater than or equal to 0");
        if (size < 1 || size > 100) return ApiResponseDTO.error(2, "size must be between 1 and 100");

        RoomType typeFilter;
        RoomStatus statusFilter;
        try {
            typeFilter = roomType == null || roomType.isBlank() ? null : RoomType.valueOf(roomType);
            statusFilter = status == null || status.isBlank() ? null : RoomStatus.valueOf(status);
        } catch (IllegalArgumentException exception) {
            return ApiResponseDTO.error(2, "roomType or status is invalid");
        }

        Page<Room> result = roomRepository.search(typeFilter, statusFilter,
                PageRequest.of(page, size, Sort.by("roomId").ascending()));
        List<RoomDTO> rooms = result.getContent().stream().map(this::toDTO).toList();
        return ApiResponseDTO.success("Successful", new PageDTO(
                result.getSize(), result.getNumber(), result.getTotalPages(),
                result.getTotalElements(), result.isFirst(), result.isLast(), rooms));
    }

    private RoomDTO toDTO(Room room) {
        RoomDTO dto = new RoomDTO();
        dto.setRoomId(room.getRoomId());
        dto.setRoomNumber(room.getRoomNumber());
        dto.setRoomType(room.getRoomType());
        dto.setPricePerNight(room.getPricePerNight());
        dto.setCapacity(room.getCapacity());
        dto.setFloor(room.getFloor());
        dto.setStatus(room.getStatus());
        return dto;
    }
}
