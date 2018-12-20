package com.rsupport.webviewer.viewer.api.room;

import com.rsupport.webviewer.core.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RoomManagerImpl implements RoomManager {
    private final RoomRepository roomRepository;
}
