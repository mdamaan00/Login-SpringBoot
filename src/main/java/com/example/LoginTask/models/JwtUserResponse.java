package com.example.LoginTask.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JwtUserResponse {
    private int id;
    private String name;
    private String jwtToken;
}
