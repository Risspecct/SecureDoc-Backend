package users.rishik.SecureDoc.Projections;

@SuppressWarnings("unused")
public interface UserView {
    Long getId();
    String getUsername();
    TeamListView getTeam();
}
