package com.example.foundlost.controllers;

import com.thesis.findpet.model.ERole;
import com.thesis.findpet.model.Role;
import com.thesis.findpet.model.User;
import com.thesis.findpet.payload.request.LoginRequest;
import com.thesis.findpet.payload.request.SignUpRequest;
import com.thesis.findpet.payload.response.JwtResponse;
import com.thesis.findpet.payload.response.MessageResponse;
import com.thesis.findpet.repository.RoleRepository;
import com.thesis.findpet.repository.UserRepository;
import com.thesis.findpet.security.jwt.JwtUtils;
import com.thesis.findpet.security.services.UserDetailsImpl;
import com.thesis.findpet.services.AmazonClient;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*", maxAge = 3600)
@AllArgsConstructor
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    JwtUtils jwtUtils;

    private AmazonClient amazonClient;

    private final static String ROLE_NOT_FOUND = "Error: Role is not found.";

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
        return ResponseEntity.ok(new JwtResponse(jwt,
                userDetails.getId(),
                userDetails.getUsername(),
                userDetails.getEmail(),
                roles,
                userDetails.getUserPhoto()
        ));
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @ModelAttribute SignUpRequest signUpRequest) {
        System.out.println(signUpRequest.getUsername());
        System.out.println(signUpRequest.getEmail());
        System.out.println(signUpRequest.getPassword());
        System.out.println(signUpRequest.getRole());
        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Username is already taken!"));
        }

        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Email is already in use!"));
        }

        User user = new User(signUpRequest.getUsername(),
                signUpRequest.getEmail(),
                encoder.encode(signUpRequest.getPassword()));

        Set<String> strRoles = signUpRequest.getRole();
        Role role = new Role();
        if (strRoles == null) {
            Role userRole = roleRepository.findByName(ERole.ROLE_REG_USER)
                    .orElseThrow(() -> new RuntimeException(ROLE_NOT_FOUND));
            role = userRole;
        } else {
            if (signUpRequest.getRole().contains("admin")) {
                Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
                        .orElseThrow(() -> new RuntimeException(ROLE_NOT_FOUND));
                role = adminRole;
            } else {
                Role userRole = roleRepository.findByName(ERole.ROLE_REG_USER)
                        .orElseThrow(() -> new RuntimeException(ROLE_NOT_FOUND));
                role = userRole;
            }
        }
        if (signUpRequest.getUserPhoto() != null) {
            String filePath = this.amazonClient.uploadFile(signUpRequest.getUserPhoto(), amazonClient.getUsersPhotoBucket());
            user.setUserPhoto(filePath);
        }
        user.setFirstName(signUpRequest.getFirstName());
        user.setLastName(signUpRequest.getLastName());
        user.setAddress(signUpRequest.getAddress());
        user.setContacts(signUpRequest.getContacts());

        user.setRole(role);
        user.setRoleId(role.getId());
        System.out.println(signUpRequest.getUsername());
        System.out.println(signUpRequest.getEmail());
        System.out.println(signUpRequest.getPassword());
        System.out.println(signUpRequest.getRole());
        userRepository.save(user);

        return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
    }
}
