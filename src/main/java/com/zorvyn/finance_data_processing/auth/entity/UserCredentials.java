package com.zorvyn.finance_data_processing.auth.entity;

import com.zorvyn.finance_data_processing.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;


@Entity
@Table(name = "user_credentials")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserCredentials {

    @Id
    private UUID userId;

    @OneToOne
    @MapsId
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false)
    private String passwordHash;

    @Column(nullable = false)
    private boolean isActive = true;
}