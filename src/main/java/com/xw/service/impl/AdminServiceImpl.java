package com.xw.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xw.common.Result;
import com.xw.dto.AdminLoginDTO;
import com.xw.dto.AdminUpdatePasswordDTO;
import com.xw.dto.AdminUserQueryDTO;
import com.xw.entity.Admin;
import com.xw.entity.User;
import com.xw.mapper.AdminMapper;
import com.xw.mapper.UserMapper;
import com.xw.service.AdminService;
import com.xw.vo.AdminUserVO;
import com.xw.vo.AdminVO;
import com.xw.common.PageResult;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author XW
 */
@Service
public class AdminServiceImpl implements AdminService {

    @Autowired
    private AdminMapper adminMapper;

    @Autowired
    private UserMapper userMapper;

    @Override
    public Result<String> login(AdminLoginDTO dto) {
        LambdaQueryWrapper<Admin> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Admin::getUsername, dto.getUsername());
        Admin admin = adminMapper.selectOne(wrapper);

        if (admin == null || !admin.getPassword().equals(dto.getPassword())) {
            return Result.error("账号或密码错误");
        }

        if (admin.getStatus() == 0) {
            return Result.error("该管理员账号已被禁用");
        }

        String token = "admin_token_" + admin.getId();
        return Result.success(token);
    }

    @Override
    public Result<AdminVO> getInfo(Long id) {
        if (id == null) return Result.error("ID不能为空");

        Admin admin = adminMapper.selectById(id);
        if (admin == null) return Result.error("管理员不存在");

        AdminVO vo = new AdminVO();
        BeanUtils.copyProperties(admin, vo);
        // ✅ 需要手动转换
        if (admin.getId() != null) {
            vo.setId(admin.getId().toString());
        }
        return Result.success(vo);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<String> updatePassword(AdminUpdatePasswordDTO dto) {
        if (dto.getId() == null) return Result.error("管理员ID不能为空");

        Admin admin = adminMapper.selectById(dto.getId());
        if (admin == null) return Result.error("管理员不存在");

        if (!admin.getPassword().equals(dto.getOldPassword())) {
            return Result.error("原密码错误");
        }

        Admin updateAdmin = new Admin();
        updateAdmin.setId(admin.getId());
        updateAdmin.setPassword(dto.getNewPassword());
        adminMapper.updateById(updateAdmin);

        return Result.success("密码修改成功，请重新登录");
    }

    @Override
    public Result<PageResult<AdminUserVO>> getUserList(AdminUserQueryDTO queryDTO) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();

        if (StringUtils.hasText(queryDTO.getKeyword())) {
            wrapper.and(w -> w.like(User::getPhone, queryDTO.getKeyword())
                    .or()
                    .like(User::getUsername, queryDTO.getKeyword()));
        }

        if (queryDTO.getStatus() != null) {
            wrapper.eq(User::getStatus, queryDTO.getStatus());
        }

        wrapper.orderByDesc(User::getCreateTime);

        Page<User> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
        Page<User> userPage = userMapper.selectPage(page, wrapper);

        List<AdminUserVO> voList = userPage.getRecords().stream().map(user -> {
            AdminUserVO vo = new AdminUserVO();
            // ✅ 手动设置需要类型转换的字段
            vo.setId(user.getId() != null ? user.getId().toString() : null);
            vo.setPhone(user.getPhone());
            vo.setUsername(user.getUsername());
            vo.setGender(user.getGender());
            vo.setStatus(user.getStatus());
            vo.setAvatar(user.getAvatar());
            vo.setAge(user.getAge());
            vo.setEmail(user.getEmail());
            vo.setCategoryId(String.valueOf(user.getCategoryId() != null ? Long.valueOf(user.getCategoryId().toString()) : null));
            vo.setCreateTime(user.getCreateTime());
            return vo;
        }).collect(Collectors.toList());

        PageResult<AdminUserVO> pageResult = new PageResult<>(
                voList,
                userPage.getTotal(),
                queryDTO.getPageNum(),
                queryDTO.getPageSize()
        );

        return Result.success(pageResult);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<String> updateUserStatus(Long userId, Integer status) {
        if (userId == null) {
            return Result.error("用户ID不能为空");
        }
        
        if (status == null || (status != 0 && status != 1)) {
            return Result.error("状态参数错误");
        }
        
        User user = userMapper.selectById(userId);
        if (user == null) {
            return Result.error("用户不存在");
        }
        
        User updateUser = new User();
        updateUser.setId(userId);
        updateUser.setStatus(status);
        userMapper.updateById(updateUser);
        
        String statusText = status == 1 ? "启用" : "禁用";
        return Result.success("用户已" + statusText);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<String> deleteUser(Long userId) {
        if (userId == null) {
            return Result.error("用户ID不能为空");
        }
        
        User user = userMapper.selectById(userId);
        if (user == null) {
            return Result.error("用户不存在");
        }
        
        userMapper.deleteById(userId);
        return Result.success("用户删除成功");
    }

    @Override
    public Result<AdminUserVO> getUserDetail(Long userId) {
        if (userId == null) {
            return Result.error("用户ID不能为空");
        }

        User user = userMapper.selectById(userId);
        if (user == null) {
            return Result.error("用户不存在");
        }

        AdminUserVO vo = new AdminUserVO();
        // 先复制相同类型的字段
        BeanUtils.copyProperties(user, vo);

        // ✅ 手动转换 Long -> String 类型的字段
        if (user.getId() != null) {
            vo.setId(user.getId().toString());
        }
        if (user.getCategoryId() != null) {
            vo.setCategoryId(user.getCategoryId().toString());
        }

        return Result.success(vo);
    }
}