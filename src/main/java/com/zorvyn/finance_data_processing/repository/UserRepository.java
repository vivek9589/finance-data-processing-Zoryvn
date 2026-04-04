package com.zorvyn.finance_data_processing.repository;


import com.zorvyn.finance_data_processing.entity.User;
import com.zorvyn.finance_data_processing.enums.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {


    // Optional<User> findById(UUID id);

    Optional<User> findByEmail(String email);

    Optional<User> findByEmailAndIsDeletedFalse(String email);
    Page<User> findByStatus(Status status, Pageable pageable);

    Page<User> findByStatusAndDeletedFalse(Status status, Pageable pageable);


    boolean existsByEmail(String email);
    boolean existsByEmailAndIsDeletedFalse(String email);


}
