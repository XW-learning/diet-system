package com.xw.service;

import com.xw.common.PageResult;
import com.xw.common.Result;
import com.xw.dto.AdminLoginDTO;
import com.xw.dto.AdminUpdatePasswordDTO;
import com.xw.dto.AdminUserQueryDTO;
import com.xw.vo.AdminUserVO;
import com.xw.vo.AdminVO;

public interface AdminService {
    Result<String> login(AdminLoginDTO dto);
    Result<AdminVO> getInfo(Long id);
    Result<String> updatePassword(AdminUpdatePasswordDTO dto);
    
    Result<PageResult<AdminUserVO>> getUserList(AdminUserQueryDTO queryDTO);
    Result<String> updateUserStatus(Long userId, Integer status);
    Result<String> deleteUser(Long userId);
    Result<AdminUserVO> getUserDetail(Long userId);
}