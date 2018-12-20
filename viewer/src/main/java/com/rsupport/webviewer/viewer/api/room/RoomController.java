package com.rsupport.webviewer.viewer.api.room;


import com.rsupport.webviewer.core.domain.*;
import com.rsupport.webviewer.core.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class RoomController {

    private final RoomRepository roomRepository;
    private final RsnetRepository rsnetRepository;
    private final PnsRepository pnsRepository;
    private final ServerRepository serverRepository;
    private final RoomSessionRepository roomSessionRepository;
    private final ViewerRepository viewerRepository;

    @PostMapping("/room/{viewerId}")
    public ResponseEntity<Room> createRoom(@PathVariable String viewerId) {
        String serverId = "JAPAN";
        Server server = serverRepository.findById(serverId).orElseThrow(RuntimeException::new);
        Room room = new Room();
        room.setAccessCode("545451");
        room.setServer(server);
        roomRepository.save(room);
        return ResponseEntity.status(HttpStatus.OK).body(room);
    }

    @GetMapping("/room/{roomId}")
    public ResponseEntity<Room> getRoom(@PathVariable String roomId) {
        Room room = roomRepository.findById(roomId).orElseThrow(RuntimeException::new);
        return ResponseEntity.status(HttpStatus.OK).body(room);
    }

    @PostMapping("/room/{roomId}/session/{viewerId}")
    public Resource<RoomSession> getRoomSession(@PathVariable String roomId, @PathVariable String viewerId) {
        Room room = roomRepository.findById(roomId).orElseThrow(RuntimeException::new);
        Viewer viewer = viewerRepository.findById(viewerId).orElseThrow(RuntimeException::new);
        RoomSession roomSession = new RoomSession();
        viewer.getRoomSessions().add(roomSession);
        viewerRepository.save(viewer);
        roomSession.setRoom(room);
        roomSessionRepository.save(roomSession);

        Resource<RoomSession> roomSessionResource = new Resource<>(roomSession);
        Link self = ControllerLinkBuilder.linkTo(RoomController.class).withSelfRel();
        Link info = ControllerLinkBuilder.linkTo(RoomController.class).slash(viewer.getId()).withRel("info");
        roomSessionResource.add(self);
        roomSessionResource.add(info);
        return roomSessionResource;
    }

    @GetMapping("/room/session/{sessionId}")
    public ResponseEntity<RoomSession> getRoomSession(@PathVariable String sessionId) {
        RoomSession roomSession = roomSessionRepository.findById(sessionId).orElseThrow(RuntimeException::new);
        return ResponseEntity.status(HttpStatus.OK).body(roomSession);
    }

    @PutMapping("/room/session/{sessionId}/server/{pnsId}/{rsnetId}")
    public ResponseEntity<RoomSession> updateRoomSession(@PathVariable String sessionId, @PathVariable String pnsId, @PathVariable String rsnetId) {
        RoomSession roomSession = roomSessionRepository.findById(sessionId).orElseThrow(RuntimeException::new);
        Pns pns = pnsRepository.findById(pnsId).orElseThrow(RuntimeException::new);
        Rsnet rsnet = rsnetRepository.findById(rsnetId).orElseThrow(RuntimeException::new);
        roomSession.setPns(pns);
        roomSession.setRsnet(rsnet);
        roomSessionRepository.save(roomSession);
        return ResponseEntity.status(HttpStatus.OK).body(roomSession);
    }
}
