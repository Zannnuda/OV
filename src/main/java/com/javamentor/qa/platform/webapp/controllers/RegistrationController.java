package com.javamentor.qa.platform.webapp.controllers;

import com.javamentor.qa.platform.models.dto.UserDto;
import com.javamentor.qa.platform.models.dto.UserRegistrationDto;
import com.javamentor.qa.platform.models.entity.chat.GroupChat;
import com.javamentor.qa.platform.models.entity.user.User;
import com.javamentor.qa.platform.models.util.OnCreate;
import com.javamentor.qa.platform.security.jwt.JwtUtils;
import com.javamentor.qa.platform.service.abstracts.model.GroupChatService;
import com.javamentor.qa.platform.service.abstracts.model.UserService;
import com.javamentor.qa.platform.webapp.configs.mail.MailService;
import com.javamentor.qa.platform.webapp.converters.UserConverter;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Optional;

@RestController
@Validated
@RequestMapping("api/auth/reg")
@Api(value = "RegApi")
public class RegistrationController {
    private final UserService userService;
    private final UserConverter userConverter;
    private final JwtUtils jwtUtils;
    private final MailService mailService;
    private final GroupChatService groupChatService;
    @Value("${address.mail.confirm}")
    private String address;

    private Long chatId;

    @Autowired
    public RegistrationController(UserService userService,
                                  UserConverter userConverter,
                                  JwtUtils jwtUtils,
                                  MailService mailService,
                                  GroupChatService groupChatService) {

        this.userService = userService;
        this.userConverter = userConverter;
        this.jwtUtils = jwtUtils;
        this.mailService = mailService;
        this.groupChatService = groupChatService;
    }

    @PostMapping("/registration")
    @ApiOperation(value = "User registration",
            notes = "Provide valid UserRegistrationDto object, to register user, with email confirmation",
            response = UserDto.class)
    @ApiResponses({
            @ApiResponse(code = 200, message = "Registration successful, need confirmation", response = UserDto.class),
            @ApiResponse(code = 400, message = "Registration failed", response = String.class)
    })
    @Validated(OnCreate.class)
    public ResponseEntity<?> createUser(@Valid @RequestBody UserRegistrationDto userRegistrationDto ) {

        String email = userRegistrationDto.getEmail();
        Optional<User> optionalUser = userService.getUserByEmail(email);

        if (optionalUser.isPresent()) {

            if (optionalUser.get().isEnabled()) {
                return ResponseEntity.badRequest().body(String.format("User with email %s already exist", email));
            }

            return ResponseEntity.badRequest().body(String.format("User with email %s already exist, but not confirmed", email));
        }

        User newUser = userConverter.userDtoToUser(userRegistrationDto);
        newUser.setIsEnabled(false);
        userService.persist(newUser);

        String token = jwtUtils.generateRegJwtToken(email);

        mailService.sendSimpleMessage(newUser.getEmail(),
                                "Registration confirm",
                                String.format("For finish registration, follow to link %sregistration/confirm?token=%s", address, token));

        return ResponseEntity.ok(userConverter.userToDto(newUser));
    }

    @GetMapping("/confirm")
    @ApiOperation(value = "User registration confirmation",
            notes = "Provide String jwt, of prepared, disabled new user, to complete registration",
            response = UserDto.class)
    @ApiResponses({
            @ApiResponse(code = 200, message = "User confirmed, and enabled", response = UserDto.class),
            @ApiResponse(code = 400, message = "User not confirmed", response = String.class)
    })
    public ResponseEntity<?> confirmUser(@ApiParam(name = "token",
                                            value = "Valid jwt needed, to check user, and confirm registration",
                                            required = true) String token){

        Optional<User> user = userService.getUserByEmail(jwtUtils.getUsernameFromToken(token));

        if (user.isPresent()) {
            User newUser = user.get();
            newUser.setIsEnabled(true);
            userService.update(newUser);
            Optional<GroupChat> groupChatOptional = groupChatService.getById(chatId);
            if (groupChatOptional.isPresent()) {
                GroupChat groupChat = groupChatOptional.get();
                groupChat.getUsers().add(newUser);
                groupChatService.update(groupChat);
            }
            return ResponseEntity.ok().body(userConverter.userToDto(newUser));
        }

        return ResponseEntity.badRequest().body("User not found");
    }
}