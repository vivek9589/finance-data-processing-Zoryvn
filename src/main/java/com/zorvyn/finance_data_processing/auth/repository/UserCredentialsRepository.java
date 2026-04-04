package com.zorvyn.finance_data_processing.auth.repository;

import com.zorvyn.finance_data_processing.auth.entity.UserCredentials;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface UserCredentialsRepository extends JpaRepository<UserCredentials, UUID> {
}