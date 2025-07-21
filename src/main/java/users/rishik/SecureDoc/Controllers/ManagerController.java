package users.rishik.SecureDoc.Controllers;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import users.rishik.SecureDoc.Services.ManagerService;

@SuppressWarnings("unused")
@PreAuthorize("hasRole('MANAGER')")
@RestController
public class ManagerController {
    private final ManagerService managerService;

    ManagerController(ManagerService managerService){
        this.managerService = managerService;
    }

    @Operation( summary = "Get team members", description = "This endpoint is used to get information about team members")
    @GetMapping("/team/members")
    public ResponseEntity<?> getTeamMembers(){
        return ResponseEntity.ok(managerService.getTeam());
    }
}
