package users.rishik.SecureDoc.Projections;

@SuppressWarnings("unused")
public interface UserProfileView{
    long getId();
    String getUsername();
    String getEmail();
    String getRole();
    String getTeam();
}
