package com.Audible.UserService.service;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.Audible.UserService.entity.user;
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
	

	public user registerUser(user user) {
		user.setPassword(passwordEncoder.encode(user.getPassword()));
		return repo.save(user);
	}
	
	
	public List<user> getAllUsers(){
		List<user> userList=repo.findAll();
		return userList;
	}

	
	public String verify(user user) {
		Authentication authentication=authManager.authenticate(new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword()));
		if(authentication.isAuthenticated()) {
			UserDetails ud=myUserDetailsService.loadUserByUsername(user.getUsername());
			Collection<? extends GrantedAuthority> authorities=ud.getAuthorities();
			String role=authorities.iterator().next().getAuthority();
			return jwtService.generateToken(ud.getUsername(),role.substring(5));
		}
		return "fail";
	}

	// Get user by username
	
    public Optional<user> getUserByUsername(String username) {
        return Optional.of(repo.findByUsername(username));
    }

    // Delete user by ID
	
    public void deleteUserById(Integer customerId) {
        if (!repo.existsById(customerId)) {
            throw new RuntimeException("User with ID " + customerId + " does not exist.");
        }
        repo.deleteById(customerId);
    }

    // Update user's role
	
    public void updateUserRole(Integer customerId, String role) {
		user user = repo.findById(customerId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + customerId));

        user.setRole(role);
        repo.save(user);
    }

	
    public long getUserCount() {
        return repo.count();
    }
}
