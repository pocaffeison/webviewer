package com.rsupport.webviewer.core.repository;

import com.rsupport.webviewer.core.domain.Server;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ServerRepository extends JpaRepository<Server, String> {
}
