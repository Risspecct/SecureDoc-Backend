package users.rishik.SecureDoc.Projections;

import users.rishik.SecureDoc.Enums.Roles;

@SuppressWarnings("unused")
public interface FileView {
    String getOriginalName();
    Long getSize();
    String getContentType();
    String getOwner();
    Roles getAccessLevel();
}
