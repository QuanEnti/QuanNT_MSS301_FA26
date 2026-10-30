package fu.quannt.room.service;

import fu.quannt.room.dto.ApiResponseDTO;
import fu.quannt.room.dto.RoomDTO;

public interface RoomService {
    ApiResponseDTO createRoom(RoomDTO dto);
    ApiResponseDTO updateRoom(Long roomId, RoomDTO dto);
    ApiResponseDTO getRoom(Long roomId);
    ApiResponseDTO setToMaintenance(Long roomId);
    ApiResponseDTO getRooms(int page, int size, String roomType, String status);
}
