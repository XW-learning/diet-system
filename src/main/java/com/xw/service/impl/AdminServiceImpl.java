package com.xw.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xw.common.Result;
import com.xw.dto.AdminLoginDTO;
import com.xw.dto.AdminUpdatePasswordDTO;
import com.xw.entity.Admin;
import com.xw.mapper.AdminMapper;
import com.xw.service.AdminService;
import com.xw.vo.AdminVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author XW
 */
@Service
public class AdminServiceImpl implements AdminService {

    @Autowired
    private AdminMapper adminMapper;

    @Override
    public Result<String> login(AdminLoginDTO dto) {
        // 1. 根据账号查询管理员
        LambdaQueryWrapper<Admin> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Admin::getUsername, dto.getUsername());
        Admin admin = adminMapper.selectOne(wrapper);

        // 2. 账号密码比对 (此处暂用明文比对，企业级实战会使用 BCrypt)
        if (admin == null || !admin.getPassword().equals(dto.getPassword())) {
            return Result.error("账号或密码错误");
        }

        // 3. 校验账号状态 (1正常 0禁用)
        if (admin.getStatus() == 0) {
            return Result.error("该管理员账号已被禁用");
        }

        // 4. 模拟生成 Token
        String token = "admin_token_" + admin.getId();
        return Result.success(token);
    }

    @Override
    public Result<AdminVO> getInfo(Long id) {
        if (id == null) return Result.error("ID不能为空");

        Admin admin = adminMapper.selectById(id);
        if (admin == null) return Result.error("管理员不存在");

        // 将 Entity 转为 VO 返回给前端，屏蔽密码
        AdminVO vo = new AdminVO();
        BeanUtils.copyProperties(admin, vo);
        return Result.success(vo);
    }

    @Override
    public Result<String> updatePassword(AdminUpdatePasswordDTO dto) {
        if (dto.getId() == null) return Result.error("管理员ID不能为空");

        Admin admin = adminMapper.selectById(dto.getId());
        if (admin == null) return Result.error("管理员不存在");

        if (!admin.getPassword().equals(dto.getOldPassword())) {
            return Result.error("原密码错误");
        }

        // 更新密码：由于 MyBatis-Plus 的特性，只会更新赋了值的字段
        Admin updateAdmin = new Admin();
        updateAdmin.setId(admin.getId());
        updateAdmin.setPassword(dto.getNewPassword());
        adminMapper.updateById(updateAdmin);

        return Result.success("密码修改成功，请重新登录");
    }
}