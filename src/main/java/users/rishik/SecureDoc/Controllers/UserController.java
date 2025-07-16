package users.rishik.SecureDoc.Controllers;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import users.rishik.SecureDoc.DTOs.UserRegisterDto;
import users.rishik.SecureDoc.DTOs.UserUpdateDto;
import users.rishik.SecureDoc.Services.UserService;

@RequestMapping("user")
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

    @Operation( summary = "Get user Details", description = "This endpoint is used to get user details using their id")
    @GetMapping("/")
    public ResponseEntity<?> getUser(@RequestParam long id){
        return ResponseEntity.ok(this.userService.getUser(id));
    }

    @Operation( summary = "Update existing user", description = "This endpoint is used to update information of a user")
    @PutMapping("/")
    public ResponseEntity<?> updateUser(@RequestParam long id, @RequestBody UserUpdateDto userUpdateDto){
        return ResponseEntity.ok(this.userService.updateUser(id, userUpdateDto));
    }

    @Operation( summary = "Delete user by id", description = "This endpoint is used to delete a user using their id")
    @DeleteMapping("/")
    public ResponseEntity<?> deleteUser(@RequestParam long id){
        this.userService.deleteUser(id);
        return ResponseEntity.ok("User deleted successfully");
    }
}
