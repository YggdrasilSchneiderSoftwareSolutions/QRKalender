package de.qr.calendar.service;

import de.qr.calendar.exception.UserAlreadyExistsException;
import de.qr.calendar.model.User;
import de.qr.calendar.repository.UserRepository;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.log.LogMessage;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsPasswordService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

@Service
public class UserManager implements UserDetailsManager, UserDetailsPasswordService {

    private final UserRepository repository;

    private AuthenticationManager authenticationManager;

    protected final Log logger = LogFactory.getLog(getClass());

    public UserManager(UserRepository repository) {
        this.repository = repository;
    }

    @Override
    public void createUser(UserDetails user) {
        User ex = repository.findByUsername(user.getUsername());
        if (ex != null) {
            throw new UserAlreadyExistsException("User with username "+user.getUsername()+" already exists!");
        }
        if (user instanceof User) {
            User u = new User(null, user.getUsername(), ((User) user).getUsertype(), user.getPassword(), user.isEnabled(), ((User) user).isPasswordExpired());
            validateUser(u);
            repository.save(u);
            return;
        }
        throw new RuntimeException("Call to updateUser with UserDetails not User");
    }

    @Override
    public void updateUser(UserDetails user) {
        if (user instanceof User) {
            User ex = repository.findByUsername(user.getUsername());
            if (ex != null && ex.getId().equals(((User) user).getId())) {
                ex.setUsertype(((User) user).getUsertype());
                ex.setEnabled(user.isEnabled());
            }
        }
        throw new RuntimeException("Call to updateUser with UserDetails not User");
    }

    @Override
    public void deleteUser(String username) {
        User ex = repository.findByUsername(username);
        if (ex != null) {
            repository.delete(ex);
        }
    }

    @Override
    public void changePassword(String oldPassword, String newPassword) throws AuthenticationException {
        Authentication currentUser = SecurityContextHolder.getContext().getAuthentication();
        if (currentUser == null) {
            // This would indicate bad coding somewhere
            throw new AccessDeniedException(
                    "Can't change password as no Authentication object found in context " + "for current user.");
        }
        String username = currentUser.getName();
        // If an authentication manager has been set, re-authenticate the user with the
        // supplied password.
        if (this.authenticationManager != null) {
            this.logger.debug(LogMessage.format("Reauthenticating user '%s' for password change request.", username));
            this.authenticationManager
                    .authenticate(UsernamePasswordAuthenticationToken.unauthenticated(username, oldPassword));
        }
        else {
            this.logger.debug("No authentication manager set. Password won't be re-checked.");
        }
        this.logger.debug("Changing password for user '" + username + "'");
        User user = (User)loadUserByUsername(username);
        user.setPassword(newPassword);
        repository.save(user);
        Authentication authentication = createNewAuthentication(currentUser, newPassword);
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);
    }

    protected Authentication createNewAuthentication(Authentication currentAuth, String newPassword) {
        UserDetails user = loadUserByUsername(currentAuth.getName());
        UsernamePasswordAuthenticationToken newAuthentication = UsernamePasswordAuthenticationToken.authenticated(user,
                null, user.getAuthorities());
        newAuthentication.setDetails(currentAuth.getDetails());
        return newAuthentication;
    }

    @Override
    public boolean userExists(String username) {
        User ex = repository.findByUsername(username);
        return ex != null;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User ex = repository.findByUsername(username);
        if (ex == null) throw new UsernameNotFoundException(username);
        return ex;
    }

    private void validateUser(User user) {
        Assert.hasText(user.getUsername(), "Username may not be empty or null");
        Assert.hasText(user.getUsertype(), "Usertype must not be empty or null");
        Assert.isTrue("ADMIN".equals(user.getUsertype()) || "USER".equals(user.getUsertype()) || "GUEST".equals(user.getUsertype()) , "Usertype is invalid");
    }

    public AuthenticationManager getAuthenticationManager() {
        return authenticationManager;
    }

    public void setAuthenticationManager(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    @Override
    public UserDetails updatePassword(UserDetails user, String newPassword) {
        User u = (User)loadUserByUsername(user.getUsername());
        u.setPassword(newPassword);
        u.setPasswordExpired(false);
        repository.save(u);

        return u;
    }
}
