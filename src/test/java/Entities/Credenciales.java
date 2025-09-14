package Entities;

import lombok.Getter;
import lombok.Setter;

@Getter
public class Credenciales {
    private String username = "admin";
    private String password = "password123";
    @Setter
    private String Token;
}
