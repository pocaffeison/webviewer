package com.rsupport.webviewer.viewer.api.viewer;

import com.rsupport.webviewer.core.domain.Viewer;
import com.rsupport.webviewer.core.repository.ViewerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ViewerController {

    private final ViewerRepository viewerRepository;

    //Id, Password 필요.
    @PostMapping(value = "/viewer", produces = MediaType.APPLICATION_JSON_VALUE)
    public Resource<Viewer> createViewer() {
        Viewer viewer = new Viewer();
        viewerRepository.save(viewer);
        Resource<Viewer> viewerResource = new Resource<>(viewer);
        Link self = ControllerLinkBuilder.linkTo(ViewerController.class).withSelfRel();
        Link info = ControllerLinkBuilder.linkTo(ViewerController.class).slash(viewer.getId()).withRel("info");
        viewerResource.add(self);
        viewerResource.add(info);
        return viewerResource;
    }

    @GetMapping(value = "/viewer/{viewerId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Viewer> getViewer(@PathVariable String viewerId) {
        Viewer viewer = viewerRepository.findById(viewerId).orElseThrow(RuntimeException::new);
        return ResponseEntity.status(HttpStatus.OK).body(viewer);
    }
}
