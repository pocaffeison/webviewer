package com.rsupport.webviewer.core.repository;

import com.rsupport.webviewer.core.domain.Viewer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ViewerRepository extends JpaRepository<Viewer, String> {
}
