package com.spring.auth.service;

import com.spring.auth.entity.User;
import com.spring.auth.entity.UserRole;
import com.spring.auth.repository.UserRepository;
import com.spring.auth.utils.AuthResponse;
import com.spring.auth.utils.LoginRequest;
import com.spring.auth.utils.RegisterRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RefreshTokenService refreshTokenService;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private AuthenticationManager authenticationManager;

    public AuthResponse register(RegisterRequest request) {
        var user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password((passwordEncoder.encode(request.getPassword())))
                .role(UserRole.USER)
                .build();

        System.out.println("User details : " + user.toString());

        User savedUser = userRepository.save(user);
        var accessToken = jwtService.generateTokenFromUsername(savedUser);
        System.out.println("accessToken : " + accessToken);

        var refreshToken = refreshTokenService.createRefreshToken(savedUser.getEmail());
        System.out.println("refreshToken : " + refreshToken);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken.getRefreshToken())
                .build();
    }

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                ));

        var user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("User not found!"));
        var accessToken = jwtService.generateTokenFromUsername(user);
        var refreshToken = refreshTokenService.createRefreshToken(request.getEmail());

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken.getRefreshToken())
                .build();
    }
}
