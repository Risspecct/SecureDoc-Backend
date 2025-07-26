package users.rishik.SecureDoc.Projections;

@SuppressWarnings("unused")
public interface UserView {
    Long getId();
    String getUsername();
    TeamView getTeam();

    interface TeamView {
        Long getId();
        String getName();
    }
}
