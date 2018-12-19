package com.rsupport.webviewer.viewer.api.viewer;

import com.rsupport.webviewer.core.domain.Viewer;
import com.rsupport.webviewer.core.domain.ViewerSession;
import com.rsupport.webviewer.core.repository.ViewerRepository;
import com.rsupport.webviewer.core.repository.ViewerSessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class ViewerController {

    private final ViewerRepository viewerRepository;
    private final ViewerSessionRepository viewerSessionRepository;

    @PostMapping("/viewer")
    public ResponseEntity<Viewer> createViewer() {
        Viewer viewer = new Viewer();
        viewerRepository.save(viewer);
        return ResponseEntity.status(HttpStatus.OK).body(viewer);
    }

    @GetMapping("/viewer/{viewerId}")
    public ResponseEntity<Viewer> getViewer(@PathVariable String viewerId) {
        Viewer viewer = viewerRepository.findById(viewerId).orElseThrow(RuntimeException::new);
        return ResponseEntity.status(HttpStatus.OK).body(viewer);
    }

    @PostMapping("/viewer/{viewerId}/session")
    public ResponseEntity<Viewer> createViewerSession(@PathVariable String viewerId) {
        Viewer viewer = viewerRepository.findById(viewerId).orElseThrow(RuntimeException::new);
        ViewerSession viewerSession = new ViewerSession();
        viewer.getViewerSessions().add(viewerSession);
        viewer = viewerRepository.save(viewer);
        return ResponseEntity.status(HttpStatus.OK).body(viewer);
    }

    @PutMapping("/viewer/session/{sessionId}/{serverId}")
    public ResponseEntity<Viewer> createViewerSession(@PathVariable String sessionId, @PathVariable String serverId) {

//        ViewerSession viewerSession = viewerSessionRepository.findById(sessionId).orElseThrow(RuntimeException::new);
//
//        Viewer viewer = viewerRepository.findById(viewerId).orElseThrow(RuntimeException::new);
//        ViewerSession viewerSession = new ViewerSession();
//        viewer.getViewerSessions().add(viewerSession);
//        viewer = viewerRepository.save(viewer);
        return ResponseEntity.status(HttpStatus.OK).body(new Viewer());
    }
}
