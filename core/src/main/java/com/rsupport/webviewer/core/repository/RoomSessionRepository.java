package com.rsupport.webviewer.core.repository;

import com.rsupport.webviewer.core.domain.ViewerSession;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ViewerSessionRepository extends JpaRepository<ViewerSession, String> {
}
