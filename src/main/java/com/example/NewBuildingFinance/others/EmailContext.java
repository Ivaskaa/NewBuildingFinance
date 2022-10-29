package com.example.NewBuildingFinance.others;

import com.example.NewBuildingFinance.entities.auth.User;
import com.example.NewBuildingFinance.others.AbstractEmailContext;
import org.springframework.web.util.UriComponentsBuilder;

public class EmailContext extends AbstractEmailContext {

    private String token;

    @Override
    public <T> void init(T context){
        //we can do any common configuration setup here
        // like setting up some base URL and context
        User customer = (User) context; // we pass the customer informati
        put("name", customer.getName());
        setTemplateLocation("email/email-registration");
        setSubject("Complete your registration");
        setFrom("no-reply@javadevjournal.com");
        setTo(customer.getUsername());
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
}