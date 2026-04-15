package com.knmiet.et.data;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class AppUser {
    private String username;
    private String email;
    private String password;
    private String role;
}
