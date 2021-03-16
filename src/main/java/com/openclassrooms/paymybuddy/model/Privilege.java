package com.openclassrooms.paymybuddy.model;

import javax.persistence.*;

@Entity
@Table(name = "privilege")
public class Privilege {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "privilege_id")
    private int id;

    @Column(name = "privilege_name")
    private String name;

    public Privilege(String name) {
        this.name = name;
    }

    private Privilege() { }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


}
