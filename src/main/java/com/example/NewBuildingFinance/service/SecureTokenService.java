package com.example.NewBuildingFinance.service;

import com.example.NewBuildingFinance.entities.auth.SecureToken;
import com.example.NewBuildingFinance.repository.auth.SecureTokenRepository;
import lombok.extern.log4j.Log4j2;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.keygen.BytesKeyGenerator;
import org.springframework.security.crypto.keygen.KeyGenerators;
import org.springframework.stereotype.Service;

import java.nio.charset.Charset;
import java.time.LocalDateTime;

@Service
@Log4j2
public class SecureTokenService {

    private static final BytesKeyGenerator DEFAULT_TOKEN_GENERATOR = KeyGenerators.secureRandom(15);
    private static final Charset US_ASCII = Charset.forName("US-ASCII");

    @Value("${jdj.secure.token.validity}")
    private int tokenValidityInSeconds;

    @Autowired
    private SecureTokenRepository secureTokenRepository;

    public SecureToken createSecureToken(){
        log.info("create secure token");
        String tokenValue = new String(Base64.encodeBase64URLSafe(DEFAULT_TOKEN_GENERATOR.generateKey()), US_ASCII); // this is a sample, you can adapt as per your security need
        SecureToken secureToken = new SecureToken();
        secureToken.setToken(tokenValue);
        secureToken.setExpireAt(LocalDateTime.now().plusSeconds(getTokenValidityInSeconds()));
        this.saveSecureToken(secureToken);
        log.info("success create secure token");
        return secureToken;
    }

    public void saveSecureToken(SecureToken token) {
        secureTokenRepository.save(token);
    }

    public SecureToken findByToken(String token) {
        return secureTokenRepository.findByToken(token);
    }

    public void removeToken(SecureToken token) {
        secureTokenRepository.delete(token);
    }

    public void removeTokenByToken(String token) {
        secureTokenRepository.removeByToken(token);
    }

    public int getTokenValidityInSeconds() {
        return tokenValidityInSeconds;
    }

    public void save(SecureToken secureToken) {
        secureTokenRepository.save(secureToken);
    }

    public void delete(SecureToken secureToken) {
        secureTokenRepository.delete(secureToken);
    }
}
