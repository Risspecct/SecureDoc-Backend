package users.rishik.SecureDoc.Controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import users.rishik.SecureDoc.Enums.Roles;
import users.rishik.SecureDoc.Services.AdminService;
import users.rishik.SecureDoc.Services.TeamService;

@PreAuthorize("hasRole('ADMIN')")
@SuppressWarnings("unused")
@RequestMapping("/admin")
@RestController
public class AdminController {
    private final TeamService teamService;
    private final AdminService adminService;

    AdminController(TeamService teamService, AdminService adminService){
        this.teamService = teamService;
        this.adminService = adminService;
    }

    // TEAM RELATED ENDPOINTS

    @Operation( summary = "Create a team", description = "This endpoint is used to create a team")
    @ApiResponses({
            @ApiResponse( responseCode = "200", description = "Team created successfully"),
            @ApiResponse( responseCode = "400", description = "Team already exists"),
            @ApiResponse( responseCode = "403", description = "Not allowed to create a team"),
            @ApiResponse( responseCode = "500", description = "Internal Server Error")
    })
    @PostMapping("/teams/create")
    public ResponseEntity<?> createTeam(@RequestParam String teamName){
        teamService.createTeam(teamName);
        return ResponseEntity.ok("Team created successfully");
    }

    @Operation( summary = "Get all teams", description = "This endpoint is used to fetch list of all teams")
    @ApiResponses({
            @ApiResponse( responseCode = "200", description = "Teams fetched successfully"),
            @ApiResponse( responseCode = "404", description = "No teams found"),
            @ApiResponse( responseCode = "403", description = "Not allowed to access this endpoint"),
            @ApiResponse( responseCode = "500", description = "Internal Server Error")
    })
    @GetMapping("/teams/all")
    public ResponseEntity<?> getAllTeams(){
        return ResponseEntity.ok(teamService.getAllTeams());
    }

    @Operation( summary = "Assign Team Leader", description = "This endpoint is used to assign team leaders to teams")
    @ApiResponses({
            @ApiResponse( responseCode = "200", description = "Team leader appointed successfully"),
            @ApiResponse( responseCode = "404", description = "User/Team not found"),
            @ApiResponse( responseCode = "403", description = "Not allowed to access this endpoint"),
            @ApiResponse( responseCode = "500", description = "Internal Server Error")
    })
    @PatchMapping("/teams/{teamId}/leader")
    public ResponseEntity<?> assignLeader(@PathVariable long teamId, @RequestParam long userId){
        teamService.assignLeader(userId, teamId);
        return ResponseEntity.ok("Team leader successfully assigned");
    }

    @Operation( summary = "Delete a Team", description = "This endpoint is used to delete a team")
    @ApiResponses({
            @ApiResponse( responseCode = "200", description = "Team deleted successfully"),
            @ApiResponse( responseCode = "404", description = "Team not found"),
            @ApiResponse( responseCode = "403", description = "Not allowed to access this endpoint"),
            @ApiResponse( responseCode = "500", description = "Internal Server Error")
    })
    @DeleteMapping("/teams/{teamId}")
    public ResponseEntity<?> deleteTeam(@PathVariable long teamId){
        teamService.deleteTeam(teamId);
        return ResponseEntity.ok("Team deleted successfully");
    }

    // USER RELATED ENDPOINTS

    @Operation( summary = "Get user info", description = "This endpoint is used to get information about single user")
    @ApiResponses({
            @ApiResponse( responseCode = "200", description = "User details fetched successfully"),
            @ApiResponse( responseCode = "404", description = "User not found"),
            @ApiResponse( responseCode = "403", description = "Access denied"),
            @ApiResponse( responseCode = "401", description = "Not Authorized"),
            @ApiResponse( responseCode = "500", description = "Internal Server Error")
    })
    @GetMapping("/users/{userId}")
    public ResponseEntity<?> getUser(@PathVariable long userId){
        return ResponseEntity.ok(adminService.getUser(userId));
    }

    @Operation( summary = "Get all users", description = "This endpoint is used to fetch details of all the users in the database")
    @ApiResponses({
            @ApiResponse( responseCode = "200", description = "Users fetched successfully"),
            @ApiResponse( responseCode = "404", description = "Users not found"),
            @ApiResponse( responseCode = "403", description = "Access denied"),
            @ApiResponse( responseCode = "401", description = "Not Authorized"),
            @ApiResponse( responseCode = "500", description = "Internal Server Error")
    })
    @GetMapping("/users/all")
    public ResponseEntity<?> getAllUsers(){
        return ResponseEntity.ok(adminService.getAllUsers());
    }

    @Operation( summary = "Change user roles", description = "This endpoint is used to change role of a user")
    @ApiResponses({
            @ApiResponse( responseCode = "200", description = "User role updated successfully successfully"),
            @ApiResponse( responseCode = "404", description = "User not found"),
            @ApiResponse( responseCode = "403", description = "Access denied"),
            @ApiResponse( responseCode = "401", description = "Not Authorized"),
            @ApiResponse( responseCode = "500", description = "Internal Server Error")
    })
    @PatchMapping("/users/{userId}/roles")
    public ResponseEntity<?> changeRole(@PathVariable long userId, @RequestParam Roles role){
        adminService.changeRole(userId, role);
        return ResponseEntity.ok("User role updated successfully");
    }

    @Operation( summary = "Delete a user", description = "This endpoint is used to delete a user using their id")
    @ApiResponses({
            @ApiResponse( responseCode = "200", description = "User deleted successfully"),
            @ApiResponse( responseCode = "404", description = "User not found"),
            @ApiResponse( responseCode = "403", description = "Access denied"),
            @ApiResponse( responseCode = "401", description = "Not Authorized"),
            @ApiResponse( responseCode = "500", description = "Internal Server Error")
    })
    @DeleteMapping("/users/{userId}")
    public ResponseEntity<?> deleteUser(@PathVariable long userId){
        adminService.deleteUser(userId);
        return ResponseEntity.ok("User deleted successfully");
    }
}
