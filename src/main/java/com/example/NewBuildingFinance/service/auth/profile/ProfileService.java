package com.example.NewBuildingFinance.service.auth.profile;

import com.example.NewBuildingFinance.entities.auth.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface ProfileService {
    /**
     * update user profile
     * @param userForm update old user
     * @param file photo
     * @return user after update
     * @throws IOException delete photo save photo
     */
    User update(User userForm, MultipartFile file) throws IOException;

    /**
     * load user by username from database
     * @param username user email
     * @return user from database
     * @throws UsernameNotFoundException fi user not found
     */
    User loadUserByUsername(String username) throws UsernameNotFoundException;

    /**
     * check unique email
     * @param email email
     * @return if email is exist return true
     */
    boolean checkEmail(String email);

    /**
     * check equals password
     * @param realPassword password from database
     * @param checkedPassword password for check
     * @return if passwords is equals return false
     */
    boolean checkRightPassword(String realPassword, String checkedPassword);

    /**
     * check equals password and repeat password
     * @param password password
     * @param repeatPassword repeatPassword
     * @return if password is equals return false
     */
    boolean checkRepeatPassword(String password, String repeatPassword);

    /**
     * phone validation
     * @param phone phone number
     * @return if validation is passed return false
     */
    boolean checkPhone(String phone);

    /**
     * viber phone validation
     * @param viber viber number
     * @return if validation is passed return false
     */
    boolean checkViber(String viber);
}
