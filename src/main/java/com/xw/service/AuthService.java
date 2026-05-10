package com.xw.service;

import com.xw.dto.LoginDTO;
import com.xw.dto.RegisterDTO;
import com.xw.dto.ResetPasswordDTO;

/**
 * @author XW
 */
public interface AuthService {
    String register(RegisterDTO regRequest);

    String login(LoginDTO loginDTO);

    String resetPassword(ResetPasswordDTO resetRequest);

    String logout();
}
