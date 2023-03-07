package com.jumpstart.org.repositories;

import com.jumpstart.org.models.Brand;
import com.jumpstart.org.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
}
