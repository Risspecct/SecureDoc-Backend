package users.rishik.SecureDoc.Security.Service;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import users.rishik.SecureDoc.Security.Principals.UserPrincipal;

@Service
public class SecurityService {
    public UserPrincipal getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof UserPrincipal) {
            return (UserPrincipal) auth.getPrincipal();
        }
        throw new IllegalStateException("User not authenticated");
    }
}
