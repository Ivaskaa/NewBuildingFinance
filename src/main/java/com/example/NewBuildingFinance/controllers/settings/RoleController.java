package com.example.NewBuildingFinance.controllers.settings;

import com.example.NewBuildingFinance.dto.auth.RoleDto;
import com.example.NewBuildingFinance.entities.auth.User;
import com.example.NewBuildingFinance.service.InternalCurrencyService;
import com.example.NewBuildingFinance.service.auth.RoleService;
import com.example.NewBuildingFinance.service.auth.UserService;
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
    private final InternalCurrencyService currencyService;
    private final RoleService roleService;
    private final UserService userService;
    private final ObjectMapper mapper;

    @GetMapping( "/roles" )
    public String roles(
            Model model
    ){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.loadUserByUsername(authentication.getName());
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
        if (roleService.checkName(roleDto.build())){
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
        roleService.save(roleDto.build());
        return mapper.writeValueAsString(null);
    }

    @PostMapping("/updateRole")
    @ResponseBody
    public String updateRole(
            @RequestBody List<RoleDto> roleDtoList
    ) throws IOException {
        //action
        roleService.update(roleDtoList);
        return mapper.writeValueAsString(null);
    }

    @GetMapping("/getRoles")
    @ResponseBody
    public String getRoles() throws JsonProcessingException {
        return mapper.writeValueAsString(roleService.findAll());
    }

    @PostMapping("/deleteRoleById")
    @ResponseBody
    public String deleteRoleById(
            Long id
    ) throws JsonProcessingException {
        roleService.deleteById(id);
        return mapper.writeValueAsString("success");
    }
}
