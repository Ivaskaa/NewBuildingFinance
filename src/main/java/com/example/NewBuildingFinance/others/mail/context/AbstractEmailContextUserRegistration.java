package com.example.NewBuildingFinance.others.mail.context;

import org.springframework.web.util.UriComponentsBuilder;

public class AbstractEmailContextUserRegistration extends AbstractEmailContext {
    private String user;
    private String token;

    public void setUser(String user) {
        this.user = user;
        put("user", user);
    }

    public void setToken(String token) {
        this.token = token;
        put("token", token);
    }

    public void buildVerificationUrl(final String baseURL, final String token){
        final String url= UriComponentsBuilder.fromHttpUrl(baseURL)
                .path("/registration").queryParam("token", token).toUriString();
        put("verificationURL", url);
    }

    public AbstractEmailContextUserRegistration() {
        setContext();
    }

    public String getUser() {
        return user;
    }

    public String getToken() {
        return token;
    }
}