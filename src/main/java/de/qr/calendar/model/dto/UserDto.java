package de.qr.calendar.model.dto;

import lombok.Data;

import javax.validation.constraints.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

@Data
public class UserDto {

    private static final String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@" + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
    private static final Pattern PATTERN = Pattern.compile(EMAIL_PATTERN);

    @NotNull
    @NotEmpty
    private String email;

    @NotNull
    @NotEmpty
    private String password;
    @NotNull
    @NotEmpty
    private String matchingPassword;

    public List<Error> validate() {
        List<Error> errors = new ArrayList<>();
        if (!validateEmail(email)) errors.add(new Error("Invalid E-Mail"));
        if (password.length() < 8) errors.add(new Error("Password must have a length between 8 and 30 characters"));
        if (password.length() > 30) errors.add(new Error("Password must have a length between 8 and 30 characters"));
        if (!password.equals(matchingPassword)) {
            errors.add(new Error("Passwords do not match"));
        }
        return errors;
    }

    private boolean validateEmail(final String email) {
        Matcher matcher = PATTERN.matcher(email);
        return matcher.matches();
    }
}
