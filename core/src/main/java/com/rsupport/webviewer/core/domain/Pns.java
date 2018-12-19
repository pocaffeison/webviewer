package com.rsupport.webviewer.core.domain;

import lombok.Data;
import lombok.ToString;

import javax.persistence.*;

@Data
@Entity
@Table(name = "nt_pns")
@ToString(exclude = {"server"})
public class Pns {

    @Id
    @Column(name = "pns_id", length = 50)
    private String id;

    @Column(name = "name", length = 100)
    private String name;

    @Column(name = "public_host", length = 100)
    private String publicHost;

    @Column(name = "public_port", length = 100)
    private String publicPort;

//    @ManyToOne(optional = false, cascade = CascadeType.MERGE, fetch = FetchType.LAZY)
//    @JoinColumn(name = "server_id")
//    private Server server;
}
