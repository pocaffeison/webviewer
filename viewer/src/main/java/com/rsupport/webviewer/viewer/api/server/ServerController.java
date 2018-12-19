package com.rsupport.webviewer.viewer.api.server;

import com.rsupport.webviewer.core.domain.Server;
import com.rsupport.webviewer.core.repository.ServerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ServerController {

    private final ServerRepository serverRepository;

    @GetMapping("/server")
    public ResponseEntity<Server> getServer() {
        String serverId = "JAPAN";
        Server server = serverRepository.findById(serverId).orElseThrow(RuntimeException::new);
        return ResponseEntity.status(HttpStatus.OK).body(server);
    }
}
