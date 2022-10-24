package com.michael.expense.service;

import com.michael.expense.entity.RefreshToken;
import com.michael.expense.entity.User;

public interface RefreshTokenService {
    RefreshToken createRefreshToken(User user);

    RefreshToken getRefreshTokenByToken(String token);

    RefreshToken getRefreshTokenById(Long tokenId);

    RefreshToken verifyExpiration(String refreshToken);

    int deleteByUserId();
}
