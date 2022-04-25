package com.mikorpar.brbljavac_api.data.repositories;

import com.mikorpar.brbljavac_api.data.dtos.users.UserWithoutPasswdDTO;
import com.mikorpar.brbljavac_api.data.models.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends MongoRepository<User, String> {

    Optional<User> findByEmail(String email);

    User findByUsername(String username);

    List<UserWithoutPasswdDTO> findAllByUsernameNot(String username);

    @Query(value="{ 'confTokens.token' : ?0 }")
    User findByConfToken(String token);
}