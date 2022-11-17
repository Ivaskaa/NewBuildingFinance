package com.example.NewBuildingFinance.controllers.settings;

import com.example.NewBuildingFinance.dto.auth.RoleDto;
import com.example.NewBuildingFinance.entities.auth.User;
import com.example.NewBuildingFinance.service.internalCurrency.InternalCurrencyServiceImpl;
import com.example.NewBuildingFinance.service.auth.role.RoleServiceImpl;
import com.example.NewBuildingFinance.service.auth.user.UserServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@AllArgsConstructor
@RequestMapping("/settings")
public class RoleController {
    private final InternalCurrencyServiceImpl currencyService;
    private final RoleServiceImpl roleServiceImpl;
    private final UserServiceImpl userServiceImpl;
    private final ObjectMapper mapper;

    @GetMapping( "/roles" )
    public String roles(
            Model model
    ){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userServiceImpl.loadUserByUsername(authentication.getName());
        model.addAttribute("currencies", currencyService.findAll());
        model.addAttribute("user", user);
        return "roles";
    }

    @PostMapping("/addRole")
    @ResponseBody
    public String addRole(
            @Valid @RequestBody RoleDto roleDto,
            BindingResult bindingResult
    ) throws IOException {
        //validation
        if (roleServiceImpl.checkName(roleDto.getName())){
            bindingResult.addError(new FieldError("roleDto", "name", "Role has already been created"));
        }
        if(bindingResult.hasErrors()){
            Map<String, String> errors = new HashMap<>();
            for (FieldError error : bindingResult.getFieldErrors()) {
                errors.put(error.getField(), error.getDefaultMessage());
            }
            return mapper.writeValueAsString(errors);
        }

        //action
        roleServiceImpl.save(roleDto.build());
        return mapper.writeValueAsString(null);
    }

    @PostMapping("/updateRole")
    @ResponseBody
    public String updateRole(
            @RequestBody List<RoleDto> roleDtoList
    ) throws IOException {
        //action
        roleServiceImpl.updateRoles(roleDtoList);
        return mapper.writeValueAsString(null);
    }

    @GetMapping("/getRoles")
    @ResponseBody
    public String getRoles() throws JsonProcessingException {
        return mapper.writeValueAsString(roleServiceImpl.findAll());
    }

    @PostMapping("/deleteRoleById")
    @ResponseBody
    public String deleteRoleById(
            Long id
    ) throws JsonProcessingException {
        roleServiceImpl.deleteById(id);
        return mapper.writeValueAsString("success");
    }
}
