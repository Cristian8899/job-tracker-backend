package com.jobtracker.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.Date;

@Data
@AllArgsConstructor
public class AuthResponse {
    private String token;
    private String id;
    private String username;
    private String fullName;
    private String email;
    private Date expiry;
}
