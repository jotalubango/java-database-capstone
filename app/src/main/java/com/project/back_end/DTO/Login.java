package com.project.back_end.DTO;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class Login {

    // 1. 'email' field:
    //    - Type: private String
    //    - Description:
    //      - Represents the email address used for logging into the system.
    //      - The email field is expected to contain a valid email address for user authentication purposes.
    // identifier: String – The unique identifier of the user attempting to log in (email for Doctor/Patient, username for Admin)
    @NotNull(message = "Username/email cannot be null.")
    @Size(min = 5, max = 100, message = "Username/email must be at least 5 and at most 100 characters long")
    private String username;

    // 2. 'password' field:
    //    - Type: private String
    //    - Description:
    //      - Represents the password associated with the email address.
    //      - The password field is used for verifying the user's identity during login.
    //      - It is generally hashed before being stored and compared during authentication.
    @NotNull(message = "Username/email cannot be null.")
    @Size(min = 5, max = 100, message = "Username/email must be at least 5 and at most 100 characters long")
    private String password;

    // 3. Constructor:
    //    - No explicit constructor is defined for this class, as it relies on the default constructor provided by Java.
    //    - This class can be initialized with setters or directly via reflection, as per the application's needs.
    public Login() {
    }

    // 4. Getters and Setters:
    //    - Standard getter and setter methods are provided for both 'email' and 'password' fields.
    //    - The 'getEmail()' method allows access to the email value.
    //    - The 'setEmail(String email)' method sets the email value.
    //    - The 'getPassword()' method allows access to the password value.
    //    - The 'setPassword(String password)' method sets the password value.
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
