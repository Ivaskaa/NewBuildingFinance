package com.example.NewBuildingFinance.service.secureToken;

import com.example.NewBuildingFinance.entities.auth.SecureToken;

public interface SecureTokenService {

    SecureToken createSecureToken();

    void saveSecureToken(SecureToken token);

    SecureToken findByToken(String token);

    int getTokenValidityInSeconds();

    void save(SecureToken secureToken);

    void delete(SecureToken secureToken);
}
