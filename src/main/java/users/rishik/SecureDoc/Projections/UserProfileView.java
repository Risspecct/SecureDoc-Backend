package users.rishik.SecureDoc.Projections;

public interface UserProfileView{
    long getId();
    String getUsername();
    String getEmail();
    String getRole();
    String getOrganization();
}
