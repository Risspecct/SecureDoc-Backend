package users.rishik.SecureDoc.Controllers;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import users.rishik.SecureDoc.DTOs.UserUpdateDto;
import users.rishik.SecureDoc.Services.UserService;

@RequestMapping("user")
@RestController
public class UserController {
    private final UserService userService;

    UserController(UserService userService){
        this.userService = userService;
    }

    @Operation( summary = "Get user Details", description = "This endpoint is used to get user details using their id")
    @GetMapping("/")
    public ResponseEntity<?> getUser(){
        return ResponseEntity.ok(this.userService.getUser(this.userService.getUserId()));
    }

    @Operation( summary = "Update existing user", description = "This endpoint is used to update information of a user")
    @PutMapping("/")
    public ResponseEntity<?> updateUser(@RequestBody UserUpdateDto userUpdateDto){
        return ResponseEntity.ok(this.userService.updateUser(this.userService.getUserId(), userUpdateDto));
    }

    @Operation( summary = "Delete user by id", description = "This endpoint is used to delete a user using their id")
    @DeleteMapping("/")
    public ResponseEntity<?> deleteUser(){
        this.userService.deleteUser(this.userService.getUserId());
        return ResponseEntity.ok("User deleted successfully");
    }
}
