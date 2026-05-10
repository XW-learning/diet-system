package com.xw.service;

import com.xw.dto.TargetDTO;
import com.xw.entity.UserTarget;

public interface TargetService {
    UserTarget getTarget(Long userId);

    String saveTarget(Long userId, TargetDTO dto);

    String deleteTarget(Long userId);
}
