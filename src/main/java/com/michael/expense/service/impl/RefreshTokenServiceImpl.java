package com.michael.expense.service.impl;

import com.michael.expense.entity.RefreshToken;
import com.michael.expense.entity.User;
import com.michael.expense.exceptions.domain.TokenRefreshException;
import com.michael.expense.repository.RefreshTokenRepository;
import com.michael.expense.repository.UserRepository;
import com.michael.expense.service.RefreshTokenService;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Date;

import static com.michael.expense.constant.SecurityConstant.*;
import static com.michael.expense.constant.UserImplConstant.NO_USER_FOUND_BY_USERNAME;


@Service
public class RefreshTokenServiceImpl implements RefreshTokenService {

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;
    @Autowired
    private UserRepository userRepository;


    @Override
    public RefreshToken createRefreshToken(User user) {
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(user);
        refreshToken.setToken(generateRefreshToken());
        refreshToken.setExpiredDate(new Date(System.currentTimeMillis() + EXPIRATION_TIME_FOR_REFRESH_TOKEN));
        refreshTokenRepository.save(refreshToken);
        return refreshToken;
    }

    @Override
    public RefreshToken getRefreshTokenByToken(String token) {
        return refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new TokenRefreshException(REFRESH_TOKEN_NOT_FOUND));
    }

    @Override
    public RefreshToken getRefreshTokenById(Long tokenId) {
        return refreshTokenRepository.findRefreshTokenById(tokenId)
                .orElseThrow(() -> new TokenRefreshException(REFRESH_TOKEN_NOT_FOUND));
    }

    @Override
    public RefreshToken verifyExpiration(String refreshToken) {
        RefreshToken token = getRefreshTokenByToken(refreshToken);
        if (token.getExpiredDate().before(new Date())) {
            refreshTokenRepository.delete(token);
            throw new TokenRefreshException(REFRESH_TOKEN_EXPIRED);
        }
        return token;
    }

    @Transactional
    @Override
    public int deleteByUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(NO_USER_FOUND_BY_USERNAME + username));
        return refreshTokenRepository.deleteByUser(userRepository.findById(user.getId()).get());
    }


    private String generateRefreshToken() {
        return RandomStringUtils.randomAlphanumeric(50);
    }

}
