package com.jumpstart.org.security;

import com.jumpstart.org.models.Brand;
import com.jumpstart.org.models.User;
import com.jumpstart.org.repositories.BrandRepository;
import com.jumpstart.org.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class CustomUserService implements UserDetailsService{

	@Autowired
	private UserRepository userRepository;
	@Autowired
	private BrandRepository brandRepository;
	
	@Transactional
	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		Optional<User> optionalUser = userRepository.findByEmail(email);
		if(optionalUser.isPresent()){
			return UserPrincipal.createUser(optionalUser.get());
		}
		Optional<Brand> optionalBrand = brandRepository.findByBrandEmail(email);
		System.out.println("Hello World " + optionalBrand.get().getBrandEmail());
		if(optionalBrand.isPresent()){
			System.out.println("Hello World");
			return UserPrincipal.createBrand(optionalBrand.get());
		}
		throw new UsernameNotFoundException("Email: " + email + " does not exist in database");
	}
}