package users.rishik.SecureDoc.Controllers;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import users.rishik.SecureDoc.Services.TeamService;

@PreAuthorize("hasRole('MANAGER')")
@SuppressWarnings("unused")
@RequestMapping("/team")
@RestController
public class TeamController {
    private final TeamService teamService;

    TeamController(TeamService teamService){
        this.teamService = teamService;
    }

    @Operation( summary = "View Team Members", description = "This endpoint used to view your team members")
    @GetMapping("/members")
    public ResponseEntity<?> getMembers(){
        return ResponseEntity.ok(teamService.getMembers());
    }

    @Operation( summary = "Add team members", description = "This endpoint is used to add members to your team")
    @PostMapping("/members/add")
    public ResponseEntity<?> addMember(@RequestParam long userId){
        teamService.addMember(userId);
        return ResponseEntity.ok("User added to the team successfully");
    }

    @Operation( summary = "Remove team members", description = "This endpoint is used to remove members from your team")
    @DeleteMapping("/members/remove")
    public ResponseEntity<?> removeMember(@RequestParam long userId){
        teamService.removeMember(userId);
        return ResponseEntity.ok("User removed from the team successfully");
    }
}
