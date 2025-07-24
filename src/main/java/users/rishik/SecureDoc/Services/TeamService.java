package users.rishik.SecureDoc.Services;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import users.rishik.SecureDoc.Entities.Team;
import users.rishik.SecureDoc.Entities.User;
import users.rishik.SecureDoc.Enums.Roles;
import users.rishik.SecureDoc.Exceptions.AlreadyExistsException;
import users.rishik.SecureDoc.Exceptions.NotFoundException;
import users.rishik.SecureDoc.Projections.TeamListView;
import users.rishik.SecureDoc.Projections.UserView;
import users.rishik.SecureDoc.Repositories.TeamRepository;
import users.rishik.SecureDoc.Repositories.UserRepository;
import users.rishik.SecureDoc.Security.Principals.UserPrincipal;
import users.rishik.SecureDoc.Security.Service.SecurityService;

import java.util.List;

@Service
public class TeamService {

    private final TeamRepository teamRepository;
    private final UserRepository userRepository;
    private final SecurityService securityService;

    TeamService(TeamRepository teamRepository, UserRepository userRepository, SecurityService securityService) {
        this.teamRepository = teamRepository;
        this.userRepository = userRepository;
        this.securityService = securityService;
    }

    public void createTeam(String name) {
        if (teamRepository.existsByName(name)) {
            throw new AlreadyExistsException("Team already exists");
        }
        Team team = new Team();
        team.setName(name);
        teamRepository.save(team);
    }

    public List<TeamListView> getAllTeams() {
        List<TeamListView> teams = teamRepository.findAllBy();
        if (teams.isEmpty()) throw new NotFoundException("No teams found");
        return teams;
    }

    public void assignLeader(long userId, long teamId) {
        Team team = getTeam(teamId);
        User user = getUser(userId);

        if (user.getRole() != Roles.MANAGER) {
            throw new IllegalArgumentException("Promote the user to MANAGER before assigning as TL");
        }

        if (user.getTeam() != null && user.getTeam().getId() != teamId) {
            throw new IllegalStateException("User is part of a different team");
        }

        if (user.getTeam() == null) {
            user.setTeam(team);
            team.getMembers().add(user);
        }

        UserPrincipal currentUser = securityService.getCurrentUser();
        if (currentUser.getRole() != Roles.ADMIN) {
            throw new AccessDeniedException("Only ADMIN can assign team leaders");
        }

        team.setLead(user);
        userRepository.save(user);
        teamRepository.save(team);
    }

    public void addMember(long userId, long teamId) {
        User user = getUser(userId);
        Team team = getTeam(teamId);

        if (user.getTeam() != null && user.getTeam().getId() != teamId) {
            throw new IllegalStateException("User already belongs to a different team");
        }

        validateTeamLead(teamId);
        if (user.getTeam() == null) {
            user.setTeam(team);
            team.getMembers().add(user);

            userRepository.save(user);
            teamRepository.save(team);
        }
    }

    public void removeMember(long userId, long teamId) {
        User user = getUser(userId);
        Team team = getTeam(teamId);

        if (user.getTeam() == null || user.getTeam().getId() != teamId) {
            throw new IllegalStateException("User is not a member of this team");
        }

        validateTeamLead(teamId);
        team.getMembers().remove(user);
        user.setTeam(null);

        userRepository.save(user);
        teamRepository.save(team);
    }

    public void addMember(long userId) {
        long teamId = getCurrentUserTeamId();
        addMember(userId, teamId);
    }

    public void removeMember(long userId) {
        long teamId = getCurrentUserTeamId();
        removeMember(userId, teamId);
    }

    public List<UserView> getMembers(long teamId) {
        if (!teamRepository.existsById(teamId)) {
            throw new NotFoundException("No team found with id: " + teamId);
        }
        return userRepository.findAllByTeamId(teamId);
    }

    public List<UserView> getMembers() {
        long teamId = getCurrentUserTeamId();
        return getMembers(teamId);
    }

    public void deleteTeam(long teamId) {
        validateTeamLead(teamId);

        Team team = getTeam(teamId);
        for (User member : team.getMembers()) {
            member.setTeam(null);
            userRepository.save(member);
        }

        teamRepository.delete(team);
    }

    private Team getTeam(long teamId) {
        return teamRepository.findById(teamId)
                .orElseThrow(() -> new NotFoundException("No team found with id: " + teamId));
    }

    private User getUser(long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("No user found with id: " + userId));
    }

    private long getCurrentUserTeamId() {
        UserPrincipal currentUser = securityService.getCurrentUser();
        User user = getUser(currentUser.getId());
        if (user.getTeam() == null) {
            throw new NotFoundException("You are not assigned to any team");
        }
        return user.getTeam().getId();
    }

    private void validateTeamLead(long teamId) {
        UserPrincipal currentUser = securityService.getCurrentUser();
        Team team = getTeam(teamId);

        if (currentUser.getRole() != Roles.MANAGER || team.getLead() == null || team.getLead().getId() != currentUser.getId()) {
            throw new AccessDeniedException("Only the assigned team lead can modify this team");
        }
    }
}
