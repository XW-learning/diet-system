package com.xw.service.admin;

import com.xw.common.PageResult;
import com.xw.dto.admin.AdminLoginDTO;
import com.xw.dto.admin.AdminUpdatePasswordDTO;
import com.xw.dto.admin.AdminUserQueryDTO;
import com.xw.vo.admin.AdminUserVO;
import com.xw.vo.admin.AdminVO;

public interface AdminService {
    String login(AdminLoginDTO dto);
    AdminVO getInfo(Long id);
    String updatePassword(AdminUpdatePasswordDTO dto);

    PageResult<AdminUserVO> getUserList(AdminUserQueryDTO queryDTO);
    String updateUserStatus(Long userId, Integer status);
    String deleteUser(Long userId);
    AdminUserVO getUserDetail(Long userId);
}
