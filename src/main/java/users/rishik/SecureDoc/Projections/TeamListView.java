package users.rishik.SecureDoc.Projections;

@SuppressWarnings("unused")
public interface TeamListView {
    long getId();
    String getName();
    LeadView getLead();

    interface LeadView {
        String getUsername();
        Long getId();
    }
}
