package com.mikorpar.brbljavac_api.controllers;

import com.mikorpar.brbljavac_api.data.dtos.string.ResponseMsgDTO;
import com.mikorpar.brbljavac_api.data.dtos.users.*;
import com.mikorpar.brbljavac_api.data.models.User;
import com.mikorpar.brbljavac_api.exceptions.tokens.TokenNotFoundException;
import com.mikorpar.brbljavac_api.exceptions.users.UsernameTakenException;
import com.mikorpar.brbljavac_api.services.UserService;
import com.mikorpar.brbljavac_api.utils.DataMapper;
import com.mikorpar.brbljavac_api.utils.LoggedUserFetcher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.net.URI;
import java.util.List;

@RestController
@EnableAsync
@RequestMapping("/users")
@RequiredArgsConstructor
@Slf4j
@Validated
public class UserController {

    private final DataMapper dataMapper;
    private final UserService userService;
    private final LoggedUserFetcher userFetcher;

    @PostMapping()
    public ResponseEntity<UserRegisterResDTO> registerUser(@Valid @RequestBody UserRegisterReqDTO userReqDTO) {
        User user;
        URI uri = URI.create(ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .path("/users")
                .toUriString());
        try {
            user = userService.createUser(userReqDTO.getEmail(), userReqDTO.getUsername(), userReqDTO.getPassword());
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, ex.getMessage());
        }
        return ResponseEntity.created(uri).body(dataMapper.map(user, UserRegisterResDTO.class));
    }

    @GetMapping
    public ResponseEntity<List<UserGetResDTO>> fetchUsers() {
        return ResponseEntity.ok(dataMapper.mapList(userService.getUsers(), UserGetResDTO.class));
    }

    @GetMapping("/me")
    public ResponseEntity<UserWithoutPasswdDTO> fetchCurrentUser() {
        return ResponseEntity.ok(dataMapper.map(userFetcher.getPrincipal().getUser(), UserWithoutPasswdDTO.class));
    }

    @GetMapping("/activate")
    public ResponseEntity<ResponseMsgDTO> verifyRegistration(
            @Pattern(regexp = "[0-9A-Za-z]{8}-[0-9A-Za-z]{4}-4[0-9A-Za-z]{3}-[89ABab][0-9A-Za-z]{3}-[0-9A-Za-z]{12}")
            @RequestParam("token") String token
    ) {
        try {
            userService.verifyRegistration(token);
        } catch (TokenNotFoundException ex) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, ex.getMessage());
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage());
        }
        return ResponseEntity.ok(new ResponseMsgDTO("Verification is successful"));
    }

    @GetMapping("/passwd-reset")
    public ResponseEntity<ResponseMsgDTO> resetPasswd(@NotBlank @Email @RequestParam("email") String email) {
        userService.sendNewPassword(email);
        return ResponseEntity.ok(new ResponseMsgDTO("If a matching account was found an email with new password was sent"));
    }

    @PutMapping("/me")
    public ResponseEntity<UserUpdateResDTO> changeCredentials(@Valid @RequestBody UserUpdateReqDTO userUpdateReqDTO) {
        if (userUpdateReqDTO.getUsername() == null && userUpdateReqDTO.getPassword() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "At least one property must be not null");
        }

        UserUpdateResDTO dto;
        try {
            dto = dataMapper.map(
                    userService.updateUserCredentials(userUpdateReqDTO.getUsername(), userUpdateReqDTO.getPassword()),
                    UserUpdateResDTO.class
            );
        } catch (UsernameTakenException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage());
        }

        return ResponseEntity.ok(dto);
    }

    @DeleteMapping("/me")
    public ResponseEntity<String> deleteCurrentUser() {
        userService.deleteCurrentUser();
        return ResponseEntity.ok().build();
    }
}
