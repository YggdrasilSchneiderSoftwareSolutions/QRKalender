package de.qr.calendar.service;

import de.qr.calendar.exception.UserAlreadyExistsException;
import de.qr.calendar.model.User;
import de.qr.calendar.model.dto.UserDto;
import de.qr.calendar.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UserService implements IUserService {
    @Autowired
    private UserRepository repository;

    @Override
    public User registerNewUserAccount(UserDto userDto) throws UserAlreadyExistsException {
        if (emailExists(userDto.getEmail())) {
            throw new UserAlreadyExistsException("There is an account with that email address: "
                    + userDto.getEmail());
        }

        User user = new User();
        user.setUsername(userDto.getEmail().trim());
        user.setPassword(userDto.getPassword());
        user.setUsertype("STD");
        user.setEnabled(true);
        user.setPasswordExpired(false);
        return repository.save(user);
    }

    private boolean emailExists(String username) {
        return repository.findByUsername(username) != null;
    }
}