package com.xw.service;

import com.xw.common.Result;
import com.xw.dto.AdminLoginDTO;
import com.xw.dto.AdminUpdatePasswordDTO;
import com.xw.vo.AdminVO;

public interface AdminService {
    Result<String> login(AdminLoginDTO dto);
    Result<AdminVO> getInfo(Long id);
    Result<String> updatePassword(AdminUpdatePasswordDTO dto);
}