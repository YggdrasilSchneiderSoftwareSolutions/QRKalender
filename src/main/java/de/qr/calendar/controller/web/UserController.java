package de.qr.calendar.controller.web;

import de.qr.calendar.exception.UserAlreadyExistsException;
import de.qr.calendar.model.User;
import de.qr.calendar.model.dto.UserDto;
import de.qr.calendar.service.UserManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

@Controller
public class UserController {

    private UserManager userManager;

    private final PasswordEncoder encoder;

    public UserController(UserManager userManager, PasswordEncoder encoder) {
        this.userManager = userManager;
        this.encoder = encoder;
    }

    @GetMapping("/user/registration")
    public String showRegistrationForm(WebRequest request, Model model) {
        UserDto userDto = new UserDto();
        model.addAttribute("user", userDto);
        return "registration";
    }

    @PostMapping("/user/registration")
    public ModelAndView registerUserAccount(
            @ModelAttribute("user") @Valid UserDto userDto,
            HttpServletRequest request,
            Errors errors) {
        ModelAndView mav = new ModelAndView("registration", "user", userDto);
        try {
            List<Error> dtoErrors = userDto.validate();
            if (dtoErrors.isEmpty()) {
                User user = new User();
                user.setUsername(userDto.getEmail().trim());
                user.setPassword(encoder.encode(userDto.getPassword()));
                user.setUsertype("USER");
                user.setEnabled(true);
                user.setPasswordExpired(false);
                userManager.createUser(user);
                return new ModelAndView("redirect:/kalender");
            } else {
                String message = "";
                for (Error error : dtoErrors) {
                    message += (message.isEmpty() ? "" : "\n") + error.getMessage();
                }
                mav.addObject("message", message);
                return mav;
            }
        } catch (UserAlreadyExistsException uaeEx) {
            mav.addObject("message", "An account for that username/email already exists.");
            return mav;
        } catch (Exception ex) {
            mav.addObject("message", ex.getMessage());
            return mav;
        }
    }
}
