package com.xw.service;

import com.xw.common.PageResult;
import com.xw.dto.AdminLoginDTO;
import com.xw.dto.AdminUpdatePasswordDTO;
import com.xw.dto.AdminUserQueryDTO;
import com.xw.vo.AdminUserVO;
import com.xw.vo.AdminVO;

public interface AdminService {
    String login(AdminLoginDTO dto);
    AdminVO getInfo(Long id);
    String updatePassword(AdminUpdatePasswordDTO dto);

    PageResult<AdminUserVO> getUserList(AdminUserQueryDTO queryDTO);
    String updateUserStatus(Long userId, Integer status);
    String deleteUser(Long userId);
    AdminUserVO getUserDetail(Long userId);
}
