package com.example.NewBuildingFinance.service.secureToken;

import com.example.NewBuildingFinance.entities.auth.SecureToken;

public interface SecureTokenService {

    /**
     * create secure token for user
     * @return secure token
     */
    SecureToken createSecureToken();

    /**
     * save secure token
     * @param token secure token
     */
    void saveSecureToken(SecureToken token);

    /**
     * find secure token by token(string)
     * @param token token(string)
     * @return secure token
     */
    SecureToken findByToken(String token);

    /**
     * delete secure token by token(string)
     * @param secureToken token
     */
    void delete(SecureToken secureToken);
}
