package users.rishik.SecureDoc.Projections;

@SuppressWarnings("unused")
public interface UserView {
    String getUsername();
    String getRole();
    TeamListView getTeam();
    String getEmail();
}
