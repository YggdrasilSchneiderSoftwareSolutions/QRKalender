package de.qr.calendar.service;

import de.qr.calendar.model.User;
import de.qr.calendar.model.dto.UserDto;

public interface IUserService {
    User registerNewUserAccount(UserDto userDto);
}