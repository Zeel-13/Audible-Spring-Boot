package com.Audible.UserService.service;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.Audible.UserService.entity.user;
import com.Audible.UserService.exception.ResourceNotFoundException;
import com.Audible.UserService.repository.userRepository;

@Service
public class ServiceImpl implements service{
	@Autowired
	private userRepository repo;
//	@Autowired
//	private BCryptPasswordEncoder encoder=new BCryptPasswordEncoder(12);
	@Autowired
	private PasswordEncoder passwordEncoder;
	@Autowired
	private AuthenticationManager authManager;
	@Autowired
	private JwtService jwtService;
	@Autowired
	private MyUserDetailsService myUserDetailsService;
	

	@Override
    public user registerUser(user user) {
        if (user == null || user.getPassword() == null) {
            throw new IllegalArgumentException("Invalid user or password");
        }
        try {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            return repo.save(user);
        } catch (DataAccessException e) {
            throw new RuntimeException("Database error while registering user", e);
        }
    }

    @Override
    public List<user> getAllUsers() {
        try {
            List<user> userList = repo.findAll();
            if (userList.isEmpty()) {
                throw new ResourceNotFoundException("No users found");
            }
            return userList;
        } catch (DataAccessException e) {
            throw new RuntimeException("Error retrieving users from database", e);
        }
    }

    @Override
    public String verify(user user) {
        try {
            Authentication authentication = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword())
            );
            if (authentication.isAuthenticated()) {
                UserDetails ud = myUserDetailsService.loadUserByUsername(user.getUsername());
                Collection<? extends GrantedAuthority> authorities = ud.getAuthorities();
                String role = authorities.iterator().next().getAuthority();
                return jwtService.generateToken(ud.getUsername(), role.substring(5));
            }
            throw new BadCredentialsException("Authentication failed");
        } catch (BadCredentialsException e) {
            throw new RuntimeException("Invalid credentials", e);
        } catch (Exception e) {
            throw new RuntimeException("Authentication error", e);
        }
    }

    @Override
    public Optional<user> getUserByUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be null or empty");
        }
        user found = repo.findByUsername(username);
        if (found == null) {
            throw new ResourceNotFoundException("User not found with username: " + username);
        }
        return Optional.of(found);
    }

    @Override
    public void deleteUserById(Integer customerId) {
        if (!repo.existsById(customerId)) {
            throw new ResourceNotFoundException("User with ID " + customerId + " does not exist");
        }
        try {
            repo.deleteById(customerId);
        } catch (DataAccessException e) {
            throw new RuntimeException("Error deleting user", e);
        }
    }

    @Override
    public void updateUserRole(Integer customerId, String role) {
        user user = repo.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + customerId));
        user.setRole(role);
        try {
            repo.save(user);
        } catch (DataAccessException e) {
            throw new RuntimeException("Failed to update user role", e);
        }
    }

    @Override
    public long getUserCount() {
        try {
            return repo.count();
        } catch (DataAccessException e) {
            throw new RuntimeException("Failed to retrieve user count", e);
        }
    }
}
