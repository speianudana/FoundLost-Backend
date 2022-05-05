package com.pbl.foundlost.controllers;


import com.pbl.foundlost.model.ERole;
import com.pbl.foundlost.model.Role;
import com.pbl.foundlost.model.User;
import com.pbl.foundlost.payload.request.UserRequest;
import com.pbl.foundlost.payload.response.UserResponse;
import com.pbl.foundlost.repository.RoleRepository;
import com.pbl.foundlost.repository.UserRepository;
import com.pbl.foundlost.services.AmazonClient;
import com.pbl.foundlost.services.UsersService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "*", maxAge = 3600)
@AllArgsConstructor
@RestController
@RequestMapping("/api/users")
public class UserController {

    private UserRepository userRepository;

    private UsersService userService;

    private PasswordEncoder bcryptEncoder;

    private RoleRepository roleRepository;

    private AmazonClient amazonClient;

    /**
     * Controller contains:
     * 1. Create an admin user: /createAdminUser, permissions: admin
     * 2. Get all users:        /getAllUsers,     permissions: admin
     * 3. Get a user details:  /getUserDetails,   permissions: all users, authenticated user, admin
     * 4. Edit a user:          /editUser,        permissions: admin
     * 5. Edit profile:         /editProfile,     permissions: authenticated user, admin
     * 6. Delete user:          /deleteUser,      permissions: admin
     * 7. Delete profile:       /deleteProfile,   permissions: authenticated user, admin
     **/

    //1. done
    @PostMapping("/createAdminUser")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity createAdminUser(@Valid @RequestBody User user) {
        try {
            Role userRole = roleRepository.findByName(ERole.ROLE_ADMIN)
                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
            user.setPassword(bcryptEncoder.encode(user.getPassword()));
            user.setRole(userRole);
            userRepository.save(user);
            return ResponseEntity.ok().build();

        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    //2. done
    @GetMapping("/getAllUsers")
    @PreAuthorize("hasRole('ADMIN')")
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    //3. done
    @GetMapping("/getUserDetails")
    public ResponseEntity<UserResponse> getUser(@RequestParam String id) {
        try {
            User user = userRepository.getOne(Long.parseLong(id));
            UserResponse userResponse = new UserResponse();
            userResponse.setUserPhoto(user.getUserPhoto());
            userResponse.setAddress(user.getAddress());
            userResponse.setContacts(user.getContacts());
            userResponse.setEmail(user.getEmail());
            userResponse.setFirstName(user.getFirstName());
            userResponse.setId(user.getId());
            userResponse.setLastName(user.getLastName());
            userResponse.setUsername(user.getUsername());

            return ResponseEntity.ok(userResponse);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    //4. done
    @PutMapping("/editUser")
    @PreAuthorize("hasRole('REG_USER') or hasRole('ADMIN')")
    public ResponseEntity editUser(@Valid @ModelAttribute UserRequest userRequest) {
        try {
            Optional<User> optionalUser = userRepository.findById(userRequest.getId());
            if (optionalUser.isPresent()) {
                User user = optionalUser.get();
                user.setId(optionalUser.get().getId());
                user.setPassword(optionalUser.get().getPassword());
//                user.setPassword(bcryptEncoder.encode(user.getPassword()));
                user.setLastName(userRequest.getLastName());
                user.setFirstName(userRequest.getFirstName());
                user.setUsername(userRequest.getUsername());
                user.setAddress(userRequest.getAddress());
                user.setContacts(userRequest.getContacts());
                user.setEmail(userRequest.getEmail());
                if (userRequest.getUserPhoto() != null) {
                    String filePath = this.amazonClient.uploadFile(userRequest.getUserPhoto(), amazonClient.getUsersPhotoBucket());
                    user.setUserPhoto(filePath);
                }

                userRepository.save(user);
                return ResponseEntity.ok().build();
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    //5. problem
    @DeleteMapping("/deleteUser")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity deleteUser(@RequestParam(name = "id") String id) {
        try {

            Optional<User> optionalUser = userRepository.findById(Long.parseLong(id));
            if (optionalUser.isPresent()) {
                userRepository.delete(optionalUser.get());

                return ResponseEntity.ok().build();
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    //6. approx done
    @DeleteMapping("/deleteProfile")
    @PreAuthorize("hasRole('REG_USER') or hasRole('ADMIN')")
    public ResponseEntity deleteProfile(@RequestParam(name = "id") String id) {
        try {
            User user = userRepository.getOne(getAuthenticatedUserId());
            userRepository.delete(user);
            return ResponseEntity.ok().build();
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    private Long getAuthenticatedUserId() {
        Long userId = 0L;
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        try {
            Optional<User> optionalUser = userRepository.findByUsername(auth.getName());
            if (optionalUser.isPresent()) {
                User user = optionalUser.get();
                userId = user.getId();
            }
            return userId;
        } catch (Exception e) {
            System.out.println("Not present");
        }
        return userId;
    }
}
