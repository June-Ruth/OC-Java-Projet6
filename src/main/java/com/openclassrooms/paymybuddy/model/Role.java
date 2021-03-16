package com.openclassrooms.paymybuddy.model;

import javax.persistence.*;
import java.util.Collection;

@Entity
@Table(name = "role_profile")
public class Role {
    /**
     * ID, generated by DataBase.
     * Use as primary key in DataBase.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "role_id")
    private int id;

    /**
     * Role name defined.
     */
    @Column(name = "role_name")
    private String name;

    @ManyToMany
    @JoinTable(name = "role_privilege",joinColumns = @JoinColumn(name = "role_id", referencedColumnName = "role_id"),
    inverseJoinColumns = @JoinColumn(  name = "privilege_id", referencedColumnName = "privilege_id"))
    private Collection<Privilege> privileges;

    /**
     * Public constructor.
     * @param name .
     */
    public Role(String name, final Collection<Privilege> pPrivileges ) {
        this.name = name;
        privileges = pPrivileges;
    }

    private Role() { }

    /**
     * Getter ID.
     * @return ID
     */
    public int getId() {
        return id;
    }

    /**
     * Setter ID.
     * ID is auto-generated, should not be accessible.
     * @param pId to set
     */
    private void setId(final int pId) {
        id = pId;
    }

    public String getName() {
        return name;
    }

    public void setName(String pName) {
        name = pName;
    }

    public Collection<Privilege> getPrivileges() {
        return privileges;
    }

    public void setPrivileges(final Collection<Privilege> pPrivileges) {
        privileges = pPrivileges;
    }
}
