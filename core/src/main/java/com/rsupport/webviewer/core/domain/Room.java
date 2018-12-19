package com.rsupport.webviewer.core.domain;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@Table(name = "rt_room", uniqueConstraints = @UniqueConstraint(name = "access_code", columnNames = {"access_code"}))
public class Room {

    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid2")
    @Column(name = "room_id", length = 50)
    private String id;

    @Column(name = "access_code", length = 6)
    private String accessCode;

    @OneToMany(mappedBy = "room", cascade = {CascadeType.MERGE, CascadeType.REMOVE}, orphanRemoval = true)
    private Set<ViewerSession> viewerSessions = new HashSet<>();

    @ManyToOne
    @JoinColumn(name = "server_id")
    private Server server;
}
