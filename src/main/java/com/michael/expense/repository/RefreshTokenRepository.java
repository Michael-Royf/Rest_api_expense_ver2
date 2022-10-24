package com.michael.expense.repository;

import com.michael.expense.entity.RefreshToken;
import com.michael.expense.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findRefreshTokenById(Long id);

    Optional<RefreshToken> findByToken(String token);

    @Modifying
    int deleteByUser(User user);
}
