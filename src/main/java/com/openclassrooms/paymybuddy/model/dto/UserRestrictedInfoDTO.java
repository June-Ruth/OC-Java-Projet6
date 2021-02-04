package com.openclassrooms.paymybuddy.model.dto;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class UserRestrictedInfoDTO {
    /**
     * First name.
     */
    @NotNull(message = "First name cannot be null")
    @Size(max = 15, message = "First name must be less than 15 characters")
    private String firstName;

    /**
     * Last name.
     */
    @NotNull(message = "Last name cannot be null")
    @Size(max = 15, message = "Last name must be less than 15 characters")
    private String lastName;

    /**
     * Email.
     * Must be unique.
     */
    @Email(message = "Email should be valid")
    @Size(max = 60, message = "Email must be less than 60 characters")
    private String email;

    /**
     * Getter first name.
     * @return first name
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Setter first name.
     * @param pFirstName to set.
     */
    public void setFirstName(final String pFirstName) {
        firstName = pFirstName;
    }

    /**
     * Getter last name.
     * @return last name
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Setter last name.
     * @param pLastName to set
     */
    public void setLastName(final String pLastName) {
        lastName = pLastName;
    }

    /**
     * Getter email.
     * @return email
     */
    public String getEmail() {
        return email;
    }

    /**
     * Setter email.
     * @param pEmail to set
     */
    public void setEmail(final String pEmail) {
        email = pEmail;
    }

}