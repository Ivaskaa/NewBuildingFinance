package com.example.NewBuildingFinance.config;

import com.example.NewBuildingFinance.entities.auth.Permission;
import com.example.NewBuildingFinance.entities.auth.Role;
import com.example.NewBuildingFinance.entities.auth.User;
import com.example.NewBuildingFinance.entities.currency.Currency;
import com.example.NewBuildingFinance.entities.currency.InternalCurrency;
import com.example.NewBuildingFinance.repository.InternalCurrencyRepository;
import com.example.NewBuildingFinance.repository.auth.PermissionRepository;
import com.example.NewBuildingFinance.repository.auth.RoleRepository;
import com.example.NewBuildingFinance.repository.auth.UserRepository;
import com.example.NewBuildingFinance.service.currency.CurrencyServiceImpl;
import com.example.NewBuildingFinance.service.restTemplate.RestTemplateServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@Component
@Log4j2
@AllArgsConstructor
class AppInitialization {
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final UserRepository userRepository;
    private final RestTemplateServiceImpl restTemplateService;
    private final CurrencyServiceImpl currencyService;
    private final InternalCurrencyRepository internalCurrencyRepository;

    @PostConstruct
    private void initialization() throws JsonProcessingException {
        log.info("start app initialization");
        List<Permission> permissions = permissionRepository.findAll();
        if(permissions.isEmpty()){
            log.info("permission initialization");
            permissions.add(new Permission(1L,"STATISTICS"));
            permissions.add(new Permission(2L,"FLATS"));
            permissions.add(new Permission(3L,"CASH_REGISTER"));
            permissions.add(new Permission(4L,"BUYERS"));
            permissions.add(new Permission(5L,"AGENCIES"));
            permissions.add(new Permission(6L,"OBJECTS"));
            permissions.add(new Permission(7L,"CONTRACTS"));
            permissions.add(new Permission(8L,"SETTINGS"));
            permissions.add(new Permission(9L,"API"));
            permissionRepository.saveAll(permissions);
            log.info("success permission initialization");
        }
        Role role = roleRepository.findByMainRoleTrue();
        if(role == null){
            log.info("main admin role initialization");
            role = new Role();
            role.setId(11L);
            role.setMainRole(true);
            role.setName("Main Admin");
            role.setPermissions(permissions);
            roleRepository.save(role);
            log.info("success main admin role initialization");
        }
        User user = userRepository.findByMainAdminTrue();
        if(user == null){
            log.info("admin initialization");
            user = new User();
            user.setId(12L);
            user.setActive(true);
            user.setMainAdmin(true);
            user.setName("Admin");
            user.setSurname("Admin");
            user.setUsername("admin@gmail.com");
            BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
            user.setPassword(bCryptPasswordEncoder.encode("admin"));
            user.setRole(role);
            userRepository.save(user);
            log.info("success admin initialization");
        }
        List<Currency> currencies = currencyService.getAll();
        if(currencies.isEmpty()){
            log.info("currency initialization");
            currencies = currencyService.saveCurrency(restTemplateService.getCurrency());
            log.info("success currency initialization");
        }
        List<InternalCurrency> internalCurrencies = internalCurrencyRepository.findAll();
        if(internalCurrencies.isEmpty()){
            log.info("internal currency initialization");
            internalCurrencies = new ArrayList<>();
            for(Currency currency : currencies){
                InternalCurrency internalCurrency = new InternalCurrency();
                internalCurrency.setName(currency.getName());
                internalCurrency.setPrice(currency.getPrice());
                internalCurrency.setCashRegister("Internal currency name");
                internalCurrencies.add(internalCurrency);
            }
            internalCurrencyRepository.saveAll(internalCurrencies);
            log.info("success internal currency initialization");
        }
        log.info("success app initialization");
    }

}
