package com.xw.service.user;

import com.xw.dto.user.TargetDTO;
import com.xw.entity.user.UserTarget;

public interface TargetService {
    UserTarget getTarget(Long userId);

    String saveTarget(Long userId, TargetDTO dto);

    String deleteTarget(Long userId);
}
