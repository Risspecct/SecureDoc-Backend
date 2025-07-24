package users.rishik.SecureDoc.Controllers;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import users.rishik.SecureDoc.Services.TeamService;

@PreAuthorize("hasRole('ADMIN')")
@SuppressWarnings("unused")
@RequestMapping("/admin")
@RestController
public class AdminController {
    private final TeamService teamService;

    AdminController(TeamService teamService){
        this.teamService = teamService;
    }

    @Operation( summary = "Create a team", description = "This endpoint is used to create a team")
    @PostMapping("/teams/create")
    public ResponseEntity<?> createTeam(@RequestParam String teamName){
        teamService.createTeam(teamName);
        return ResponseEntity.ok("Team crated successfully");
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
}
