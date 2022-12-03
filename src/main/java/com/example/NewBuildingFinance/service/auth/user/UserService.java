package com.example.NewBuildingFinance.service.auth.user;

import com.example.NewBuildingFinance.entities.auth.User;
import com.example.NewBuildingFinance.entities.setting.Setting;
import org.springframework.data.domain.Page;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.List;

public interface UserService {
    /**
     * get users page with sorting from database
     * @param currentPage current page
     * @param size table size
     * @param sortingField sorting field
     * @param sortingDirection sorting direction
     * @return users page from database
     */
    Page<User>  findSortingPage(
            Integer currentPage,
            Integer size,
            String sortingField,
            String sortingDirection);

    /**
     * get user with id 1 from database
     * @return director
     */
    User findDirector();

    /**
     * get users with permission buyers from database
     * @return list of managers
     */
    List<User> findManagers(Long userId);

    /**
     * save user to database
     * @param user user for save
     * @return user after save
     */
    User save(User user);

    /**
     * update user in database
     * @param userForm user for update
     * @return user after updating
     */
    User update(User userForm);

    /**
     * delete from database by id
     * @param id id for deleting
     */
    void deleteById(Long id);

    /**
     * find user from database by id
     * @param id id for find
     * @return user
     */
    User findById(Long id);

    /**
     * find permissions by user id
     * @param id user id
     * @return permissions list
     */
    List<String> getUserPermissionsById(Long id);

    /**
     * find user by email(username) from database
     * @param username user email(username)
     * @return user
     * @throws UsernameNotFoundException if username not found
     */
    User loadUserByUsername(String username) throws UsernameNotFoundException;

    /**
     * change user active (if user was inactive u will active him or vice versa)
     * @param id user id
     * @return user
     */
    User changeUserActiveById(Long id);

    /**
     * in database check unique user email(username)
     * @param email user email(username)
     * @return if email(username) is exist return true
     */
    boolean checkEmail(String email);

    /**
     * in database check unique user phone
     * @param phone user phone
     * @return if phone is exist return true
     */
    boolean checkPhone(String phone);

    /**
     * send registration email to user email(username)
     * @param user user
     */
    void sendRegistrationEmail(User user);

    /**
     * find user dy after pre-registration token
     * @param token token
     * @return user or null if token not valid
     */
    User findUserByToken(String token);

    /**
     * save user password and set active true
     * @param user user
     * @param password user password
     */
    void savePassword(User user, String password);

    /**
     * find token and delete him from database
     * @param token token
     */
    void deleteToken(String token);
}
