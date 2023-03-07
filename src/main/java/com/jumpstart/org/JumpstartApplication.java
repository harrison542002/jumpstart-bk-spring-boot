package com.jumpstart.org;

import com.cloudinary.Cloudinary;
import com.cloudinary.SingletonManager;
import com.cloudinary.utils.ObjectUtils;
import com.jumpstart.org.models.Role;
import com.jumpstart.org.repositories.RoleRepository;
import com.jumpstart.org.status.AppConstants;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.List;

@SpringBootApplication
public class JumpstartApplication implements CommandLineRunner {

	@Autowired
	public RoleRepository roleRepository;
	@Bean
	public ModelMapper modelMapper() {
		return new ModelMapper();
	}
	public static void main(String[] args) {
		String cloudName = System.getenv("cloudName");// insert here you cloud name
		String secrete = System.getenv("secrete");// insert here your api code
		String key = System.getenv("key");// insert here your api secret
		// Set Cloudinary instance
		Cloudinary cloudinary = new Cloudinary(ObjectUtils.asMap(
				"cloud_name", cloudName,
				"api_key", key,
				"api_secret", secrete));
		SingletonManager manager = new SingletonManager();
		manager.setCloudinary(cloudinary);
		manager.init();
		SpringApplication.run(JumpstartApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		try {
			// admin role
			Role role_admin = new Role();
			role_admin.setRoleName("ROLE_ADMIN");
			role_admin.setRole_id(AppConstants.ROLE_ADMIN.longValue());

			// user role
			Role role_member = new Role();
			role_member.setRole_id(AppConstants.ROLE_MEMBER.longValue());
			role_member.setRoleName("ROLE_MEMBER");

			List<Role> roles = List.of(role_admin, role_member);
			this.roleRepository.saveAll(roles);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
