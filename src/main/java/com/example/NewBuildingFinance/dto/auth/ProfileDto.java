package com.example.NewBuildingFinance.dto.auth;

import com.example.NewBuildingFinance.entities.auth.User;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import java.text.ParseException;
import java.text.SimpleDateFormat;
@Data
public class ProfileDto {
    private Long id;
    @NotEmpty(message = "Must not be empty")
    private String name;
    @NotEmpty(message = "Must not be empty")
    private String surname;
    private String lastname;
    private String birthday;
    @NotEmpty(message = "Must not be empty")
    private String phone;
    private String viber;
    private String telegram;
    private String aboutMe;
    @Email(message = "Email must be valid")
    @NotEmpty(message = "Must not be empty")
    private String username; // email
    @NotEmpty(message = "Must not be empty")
    private String password;
    private String firstPassword;
    private String secondPassword;

    public User build() throws ParseException {
        User user = new User();
        user.setId(id);
        user.setName(name);
        user.setSurname(surname);
        if(!lastname.equals("")) {
            user.setLastname(lastname);
        }
        if(birthday!= null && !birthday.equals("")) {
            SimpleDateFormat format = new SimpleDateFormat();
            format.applyPattern("yyyy-MM-dd");
            user.setBirthday(format.parse(birthday));
            System.out.println(user.getBirthday());
        }
        user.setPhone(phone);
        if(!viber.equals("+38(___)___-__-__")) {
            if(!viber.contains("_")) {
                user.setViber(viber);
            }
        }
        if(!telegram.equals("")) {
            user.setTelegram(telegram);
        }
        if(!aboutMe.equals("")) {
            user.setAboutMe(aboutMe);
        }
        if(!firstPassword.equals("")) {
            user.setPassword(firstPassword);
        }
        user.setUsername(username);
        return user;
    }
}
