package users.rishik.SecureDoc.Controllers;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import users.rishik.SecureDoc.DTOs.UserUpdateDto;
import users.rishik.SecureDoc.Services.UserService;

@PreAuthorize("hasRole('USER')")
@RequestMapping("user")
@RestController
public class UserController {
    private final UserService userService;

    UserController(UserService userService){
        this.userService = userService;
    }

    @Operation( summary = "Get user Details", description = "This endpoint is used to get user details using their id")
    @GetMapping("/me")
    public ResponseEntity<?> getUser(){
        return ResponseEntity.ok(this.userService.getCurrentUser());
    }

    @Operation( summary = "Update existing user", description = "This endpoint is used to update information of a user")
    @PutMapping("/me")
    public ResponseEntity<?> updateUser(@RequestBody UserUpdateDto userUpdateDto){
        return ResponseEntity.ok(this.userService.updateUser(userUpdateDto));
    }

    @Operation( summary = "Delete user by id", description = "This endpoint is used to delete a user using their id")
    @DeleteMapping("/me")
    public ResponseEntity<?> deleteCurrentUser(){
        this.userService.deleteCurrentUser();
        return ResponseEntity.ok("User deleted successfully");
    }
}
