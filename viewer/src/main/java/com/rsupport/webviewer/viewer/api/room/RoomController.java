package com.rsupport.webviewer.viewer.api.room;


import com.rsupport.webviewer.core.domain.Room;
import com.rsupport.webviewer.core.domain.Server;
import com.rsupport.webviewer.core.domain.ViewerSession;
import com.rsupport.webviewer.core.repository.RoomRepository;
import com.rsupport.webviewer.core.repository.ServerRepository;
import com.rsupport.webviewer.core.repository.ViewerSessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class RoomController {

    private final RoomRepository roomRepository;
    private final ServerRepository serverRepository;
    private final ViewerSessionRepository viewerSessionRepository;

    @PostMapping("/room/{sessionId}")
    public ResponseEntity<Room> createRoom(@PathVariable String sessionId) {
        String serverId = "JAPAN";
        Server server = serverRepository.findById(serverId).orElseThrow(RuntimeException::new);
        Room room = new Room();
        room.setAccessCode("545451");
        room.setServer(server);
        roomRepository.save(room);
        ViewerSession viewerSession = viewerSessionRepository.findById(sessionId).orElseThrow(RuntimeException::new);
        viewerSession.setRoom(room);
        viewerSessionRepository.save(viewerSession);
        return ResponseEntity.status(HttpStatus.OK).body(room);
    }

    @GetMapping("/room/{roomId}")
    public ResponseEntity<Room> getRoom(@PathVariable String roomId) {
        Room room = roomRepository.findById(roomId).orElseThrow(RuntimeException::new);
        return ResponseEntity.status(HttpStatus.OK).body(room);
    }
}
