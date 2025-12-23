package com.mtsaas.auth_service.repository;

import com.mtsaas.auth_service.entity.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepo extends JpaRepository<User,Long> {

    @EntityGraph(attributePaths = {"roles"})
    User findByUsername(String userName);

    boolean existsByUsername(String username);

}
