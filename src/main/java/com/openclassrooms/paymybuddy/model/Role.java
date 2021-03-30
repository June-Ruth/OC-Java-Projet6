package com.openclassrooms.paymybuddy.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

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

    /**
     * Public constructor.
     * @param pName .
     */
    public Role(final String pName) {
        name = pName;
    }

    /**
     * private constructor.
     */
    private Role() { }

    /**
     * Getter ID.
     * @return ID
     */
    public int getId() {
        return id;
    }

    /**
     * Getter Name.
     * @return name
     */
    public String getName() {
        return name;
    }
    /**
     * Setter Name.
     * @param pName .
     */
    public void setName(final String pName) {
        name = pName;
    }

    /**
     * To String.
     * @return string information
     */
    @Override
    public String toString() {
        return "Role{"
                + "id=" + id
                + ", name='" + name + '\''
                + '}';
    }
}
