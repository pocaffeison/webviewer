package com.rsupport.webviewer.core.repository;

import com.rsupport.webviewer.core.domain.RoomSession;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoomSessionRepository extends JpaRepository<RoomSession, String> {
}
