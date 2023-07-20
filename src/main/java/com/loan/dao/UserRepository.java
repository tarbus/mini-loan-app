package com.loan.dao;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.loan.models.User;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

	Page<User> findAll(Pageable pageable);

	@Query("select c from User c where c.email=?1")
	Optional<User> findUserByEmail(String email);

	@Query("select c.id from User c where c.email=?1")
	Integer findUserIdByEmail(String email);

}
