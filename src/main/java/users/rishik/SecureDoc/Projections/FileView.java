package users.rishik.SecureDoc.Projections;


@SuppressWarnings("unused")
public interface FileView {
    Long getId();
    String getOriginalName();
    Long getSize();
    String getContentType();
    String getOwner();
}
