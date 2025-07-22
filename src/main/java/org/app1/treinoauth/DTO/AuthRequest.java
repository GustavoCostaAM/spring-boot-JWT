package org.app1.treinoauth.DTO;


import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Service;

@Getter
@Setter
public class AuthRequest {
    private String email;
    private String senha;
}
