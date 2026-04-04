package com.zorvyn.finance_data_processing.auth.repository;

import com.zorvyn.finance_data_processing.auth.entity.ResetToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ResetTokenRepository extends JpaRepository<ResetToken, UUID> {
    Optional<ResetToken> findByResetToken(String resetToken);
}