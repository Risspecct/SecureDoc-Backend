package users.rishik.SecureDoc.Controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import users.rishik.SecureDoc.DTOs.UserUpdateDto;
import users.rishik.SecureDoc.Services.UserService;

@SuppressWarnings("unused")
@PreAuthorize("hasRole('USER')")
@RequestMapping("user")
@RestController
public class UserController {
    private final UserService userService;

    UserController(UserService userService){
        this.userService = userService;
    }

    @Operation( summary = "Get user Details", description = "This endpoint is used to get user details using their id")
    @ApiResponses({
            @ApiResponse( responseCode = "200", description = "User details fetched successfully"),
            @ApiResponse( responseCode = "404", description = "User not found"),
            @ApiResponse( responseCode = "401", description = "Not Authorized"),
            @ApiResponse( responseCode = "500", description = "Internal Server Error")
    })
    @GetMapping("/me")
    public ResponseEntity<?> getUser(){
        return ResponseEntity.ok(this.userService.getCurrentUser());
    }

    @Operation( summary = "Update existing user", description = "This endpoint is used to update information of a user")
    @ApiResponses({
            @ApiResponse( responseCode = "200", description = "User details updated successfully"),
            @ApiResponse( responseCode = "404", description = "File not found"),
            @ApiResponse( responseCode = "401", description = "Not Authorized"),
            @ApiResponse( responseCode = "500", description = "Internal Server Error")
    })
    @PutMapping("/me")
    public ResponseEntity<?> updateUser(@RequestBody UserUpdateDto userUpdateDto){
        return ResponseEntity.ok(this.userService.updateUser(userUpdateDto));
    }

    @Operation( summary = "Delete user by id", description = "This endpoint is used to delete a user using their id")
    @ApiResponses({
            @ApiResponse( responseCode = "200", description = "User deleted successfully"),
            @ApiResponse( responseCode = "404", description = "File not found"),
            @ApiResponse( responseCode = "401", description = "Not Authorized"),
            @ApiResponse( responseCode = "500", description = "Internal Server Error")
    })
    @DeleteMapping("/me")
    public ResponseEntity<?> deleteCurrentUser(){
        this.userService.deleteCurrentUser();
        return ResponseEntity.ok("User deleted successfully");
    }
}
