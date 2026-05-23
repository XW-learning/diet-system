package com.xw.service.user;

import com.xw.dto.user.LoginDTO;
import com.xw.dto.user.RegisterDTO;
import com.xw.dto.user.ResetPasswordDTO;

/**
 * @author XW
 */
public interface AuthService {
    String register(RegisterDTO regRequest);

    String login(LoginDTO loginDTO);

    String resetPassword(ResetPasswordDTO resetRequest);

    String logout();
}
