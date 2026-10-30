package fu.quannt.reservation.client;

import fu.quannt.reservation.dto.RoomApiResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "room-service", url = "${room.service.url}")
public interface RoomClient {
    @GetMapping("/api/rooms/{roomId}")
    RoomApiResponseDTO getRoom(@PathVariable("roomId") Long roomId);
}
