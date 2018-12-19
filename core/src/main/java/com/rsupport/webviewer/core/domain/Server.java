package com.rsupport.webviewer.core.domain;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@EqualsAndHashCode(of = {"id"})
@Table(name = "nt_server")
public class Server {

    @Id
    @Column(name = "server_id", length = 50)
    private String id;

    @Column(name = "name", length = 100)
    private String name;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "server_id")
    private Set<Pns> pns = new HashSet<>();

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "server_id")
    private Set<Rsnet> rsnet = new HashSet<>();
}
