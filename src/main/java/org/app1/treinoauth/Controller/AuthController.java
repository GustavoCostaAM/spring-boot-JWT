package org.app1.treinoauth.Controller;

import org.app1.treinoauth.DTO.AuthRequest;
import org.app1.treinoauth.Model.Role;
import org.app1.treinoauth.Model.UserModel;
import org.app1.treinoauth.Model.UserPrincipal;
import org.app1.treinoauth.Repository.UserRepository;
import org.app1.treinoauth.Security.JwtService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthController(AuthenticationManager authenticationManager, JwtService jwtService, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/login")
    public ResponseEntity<String> pegarToken(@RequestBody AuthRequest authRequest){
        //verifica se os credenciais estão corretos
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authRequest.getEmail(), authRequest.getSenha())
        );

        //procura o usuario
        UserModel userModel = userRepository.findUserByEmail(authRequest.getEmail()).get();
        String token = jwtService.generateToken(new UserPrincipal(userModel));
        return ResponseEntity.status(HttpStatus.OK).body(token);
    }

    @PostMapping("/cadastro")
    public ResponseEntity<AuthRequest> cadastro(@RequestBody AuthRequest authRequest){
        UserModel usuarioAdicionado = new UserModel();
        usuarioAdicionado.setEmail(authRequest.getEmail());
        usuarioAdicionado.setSenha(passwordEncoder.encode(authRequest.getSenha()));
        userRepository.save(usuarioAdicionado);
        return ResponseEntity.status(HttpStatus.OK).body(authRequest);
    }

    @GetMapping("/testeADM")
    public ResponseEntity<String> testeADM(){
        return ResponseEntity.status(HttpStatus.OK).body("req feita com sucesso");
    }




}
