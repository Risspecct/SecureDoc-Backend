package users.rishik.SecureDoc.Controllers;

import io.swagger.v3.oas.annotations.Operation;
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

    // FILE RELATED ENDPOINTS

    @Operation( summary = "Create a team", description = "This endpoint is used to create a team")
    @PostMapping("/teams/create")
    public ResponseEntity<?> createTeam(@RequestParam String teamName){
        teamService.createTeam(teamName);
        return ResponseEntity.ok("Team created successfully");
    }

    @Operation( summary = "Get all teams", description = "This endpoint is used to fetch list of all teams")
    @GetMapping("/teams/all")
    public ResponseEntity<?> getAllTeams(){
        return ResponseEntity.ok(teamService.getAllTeams());
    }

    @Operation( summary = "Assign Team Leader", description = "This endpoint is used to assign team leaders to teams")
    @PatchMapping("/teams/{teamId}/leader")
    public ResponseEntity<?> assignLeader(@PathVariable long teamId, @RequestParam long userId){
        teamService.assignLeader(userId, teamId);
        return ResponseEntity.ok("Team leader successfully assigned");
    }

    @Operation( summary = "Delete a Team", description = "This endpoint is used to delete a team")
    @DeleteMapping("/teams/{teamId}")
    public ResponseEntity<?> deleteTeam(@PathVariable long teamId){
        teamService.deleteTeam(teamId);
        return ResponseEntity.ok("Team deleted successfully");
    }

    // USER RELATED ENDPOINTS

    @Operation( summary = "Get user info", description = "This endpoint is used to get information about single user")
    @GetMapping("/users/{userId}")
    public ResponseEntity<?> getUser(@PathVariable long userId){
        return ResponseEntity.ok(adminService.getUser(userId));
    }

    @Operation( summary = "Get all users", description = "This endpoint is used to fetch details of all the users in the database")
    @GetMapping("/users/all")
    public ResponseEntity<?> getAllUsers(){
        return ResponseEntity.ok(adminService.getAllUsers());
    }

    @Operation( summary = "Change user roles", description = "This endpoint is used to change role of a user")
    @PatchMapping("/users/{userId}/roles")
    public ResponseEntity<?> changeRole(@PathVariable long userId, @RequestParam Roles role){
        adminService.changeRole(userId, role);
        return ResponseEntity.ok("User role updated successfully");
    }

    @Operation( summary = "Delete a user", description = "This endpoint is used to delete a user using their id")
    @DeleteMapping("/users/{userId}")
    public ResponseEntity<?> deleteUser(@PathVariable long userId){
        adminService.deleteUser(userId);
        return ResponseEntity.ok("User deleted successfully");
    }
}
