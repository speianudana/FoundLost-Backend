package com.pbl.foundlost.controllers;

import com.pbl.foundlost.model.ERole;
import com.pbl.foundlost.model.Role;
import com.pbl.foundlost.model.User;
import com.pbl.foundlost.payload.request.LoginRequest;
import com.pbl.foundlost.payload.request.SignUpRequest;
import com.pbl.foundlost.payload.response.JwtResponse;
import com.pbl.foundlost.payload.response.MessageResponse;
import com.pbl.foundlost.repository.RoleRepository;
import com.pbl.foundlost.repository.UserRepository;
import com.pbl.foundlost.security.jwt.JwtUtils;
import com.pbl.foundlost.security.services.UserDetailsImpl;
import com.pbl.foundlost.services.AmazonClient;
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
import java.util.HashSet;
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
//
//    @PostMapping("/signup1")
//    public ResponseEntity<?> registerUser(@ModelAttribute SignUpRequest signUpRequest) {
//        System.out.println(signUpRequest.getUsername());
//        System.out.println(signUpRequest.getEmail());
//        System.out.println(signUpRequest.getPassword());
//        System.out.println(signUpRequest.getRole());
//        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
//            return ResponseEntity
//                    .badRequest()
//                    .body(new MessageResponse("Error: Username is already taken!"));
//        }
//
//        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
//            return ResponseEntity
//                    .badRequest()
//                    .body(new MessageResponse("Error: Email is already in use!"));
//        }
//
//        User user = new User(signUpRequest.getUsername(),
//                signUpRequest.getEmail(),
//                encoder.encode(signUpRequest.getPassword()));
//
//        Set<String> strRoles = signUpRequest.getRole();
//        Role role = new Role();
//        if (strRoles == null) {
//            Role userRole = roleRepository.findByName(ERole.ROLE_USER)
//                    .orElseThrow(() -> new RuntimeException(ROLE_NOT_FOUND));
//            role = userRole;
//        } else {
//            if (signUpRequest.getRole().contains("admin")) {
//                Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
//                        .orElseThrow(() -> new RuntimeException(ROLE_NOT_FOUND));
//                role = adminRole;
//            } else {
//                Role userRole = roleRepository.findByName(ERole.ROLE_USER)
//                        .orElseThrow(() -> new RuntimeException(ROLE_NOT_FOUND));
//                role = userRole;
//            }
//        }
//        if (signUpRequest.getUserPhoto() != null) {
//            String filePath = this.amazonClient.uploadFile(signUpRequest.getUserPhoto(), amazonClient.getUsersPhotoBucket());
//            user.setUserPhoto(filePath);
//        }
//        user.setFirstName(signUpRequest.getFirstName());
//        user.setLastName(signUpRequest.getLastName());
//        user.setAddress(signUpRequest.getAddress());
//        user.setContacts(signUpRequest.getContacts());
//
//        user.setRole(role);
//        user.setRoleId(role.getId());
//        System.out.println(signUpRequest.getUsername());
//        System.out.println(signUpRequest.getEmail());
//        System.out.println(signUpRequest.getPassword());
//        System.out.println(signUpRequest.getRole());
//        userRepository.save(user);
//
//        return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
//    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@RequestBody SignUpRequest signUpRequest) {
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
        // Create new user's account
        User user = new User(signUpRequest.getUsername(),
                signUpRequest.getEmail(),
                encoder.encode(signUpRequest.getPassword()));
        Set<String> strRoles = signUpRequest.getRole();
        Role role;
        if (strRoles == null) {
            Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
            role = userRole;
        } else {
            if (signUpRequest.getRole().contains("admin")) {
                Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
                        .orElseThrow(() -> new RuntimeException(ROLE_NOT_FOUND));
                role = adminRole;
            } else {
                Role userRole = roleRepository.findByName(ERole.ROLE_USER)
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
        userRepository.save(user);
        return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
    }
}
