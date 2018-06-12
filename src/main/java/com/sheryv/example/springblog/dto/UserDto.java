package com.sheryv.example.springblog.dto;

import com.sheryv.example.springblog.Constants;
import com.sheryv.example.springblog.validation.PasswordMatches;
import com.sheryv.example.springblog.validation.ValidEmail;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;


@PasswordMatches(message = "Hasła nie pasują do siebie")
public class UserDto {

    @NotNull
    @Size(min = 3, max = 40, message = Constants.INCORRECT_LENGTH_MSG)
    private String username;

    @NotNull
    @Size(min = 1, max=255, message = Constants.INCORRECT_LENGTH_MSG)
    private String firstName;

    @NotNull
    @Size(min = 1, max=255, message = Constants.INCORRECT_LENGTH_MSG)
    private String lastName;

    @Size(min = 5, max=255, message = Constants.INCORRECT_LENGTH_MSG)
    private String password;

    private String matchingPassword;

    @ValidEmail(message = "Niepoprawny format adresu email")
    @NotNull
    @Size(min = 3,  max=255, message = Constants.INCORRECT_LENGTH_MSG)
    private String email;


    public String getEmail() {
        return email;
    }

    public void setEmail(final String email) {
        this.email = email;
    }

    private Integer role;

    public Integer getRole() {
        return role;
    }

    public void setRole(final Integer role) {
        this.role = role;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(final String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(final String lastName) {
        this.lastName = lastName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(final String password) {
        this.password = password;
    }

    public String getMatchingPassword() {
        return matchingPassword;
    }

    public void setMatchingPassword(final String matchingPassword) {
        this.matchingPassword = matchingPassword;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("UserDto [firstName=").append(firstName).append(", lastName=").append(lastName)
                .append(", password=").append(password).append(", matchingPassword=")
                .append(matchingPassword).append(", email=").append(email)
                .append(", role=").append(role).append("]");
        return builder.toString();
    }

}
