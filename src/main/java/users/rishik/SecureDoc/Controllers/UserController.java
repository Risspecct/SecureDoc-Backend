package users.rishik.SecureDoc.Controllers;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import users.rishik.SecureDoc.DTOs.UserRegisterDto;
import users.rishik.SecureDoc.Services.UserService;

@RestController
public class UserController {
    private final UserService userService;

    UserController(UserService userService){
        this.userService = userService;
    }

    @Operation( summary = "Add a user to the database", description = "This endpoint is used to register a new user")
    @PostMapping("/register")
    public ResponseEntity<?> addUser(@RequestBody UserRegisterDto user){
        return new ResponseEntity<>(this.userService.addUser(user), HttpStatus.CREATED);
    }
}
