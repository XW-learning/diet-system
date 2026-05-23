package com.xw.service.impl.admin;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xw.common.PageResult;
import com.xw.dto.admin.AdminLoginDTO;
import com.xw.dto.admin.AdminUpdatePasswordDTO;
import com.xw.dto.admin.AdminUserQueryDTO;
import com.xw.entity.admin.Admin;
import com.xw.entity.user.User;
import com.xw.exception.BusinessException;
import com.xw.mapper.admin.AdminMapper;
import com.xw.mapper.user.UserMapper;
import com.xw.service.admin.AdminService;
import com.xw.utils.JwtUtil;
import com.xw.vo.admin.AdminUserVO;
import com.xw.vo.admin.AdminVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdminServiceImpl implements AdminService {

    @Autowired
    private AdminMapper adminMapper;

    @Autowired
    private UserMapper userMapper;

    @Override
    public String login(AdminLoginDTO dto) {
        LambdaQueryWrapper<Admin> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Admin::getUsername, dto.getUsername());
        Admin admin = adminMapper.selectOne(wrapper);

        if (admin == null || !admin.getPassword().equals(dto.getPassword())) {
            throw new BusinessException(401, "账号或密码错误");
        }

        if (admin.getStatus() == 0) {
            throw new BusinessException(403, "该管理员账号已被禁用");
        }

        return JwtUtil.generateAdminToken(admin.getId());
    }

    @Override
    public AdminVO getInfo(Long id) {
        if (id == null) throw new BusinessException("ID不能为空");

        Admin admin = adminMapper.selectById(id);
        if (admin == null) throw new BusinessException("管理员不存在");

        AdminVO vo = new AdminVO();
        BeanUtils.copyProperties(admin, vo);
        if (admin.getId() != null) {
            vo.setId(admin.getId().toString());
        }
        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String updatePassword(AdminUpdatePasswordDTO dto) {
        if (dto.getId() == null) throw new BusinessException("管理员ID不能为空");

        Admin admin = adminMapper.selectById(dto.getId());
        if (admin == null) throw new BusinessException("管理员不存在");

        if (!admin.getPassword().equals(dto.getOldPassword())) {
            throw new BusinessException("原密码错误");
        }

        Admin updateAdmin = new Admin();
        updateAdmin.setId(admin.getId());
        updateAdmin.setPassword(dto.getNewPassword());
        adminMapper.updateById(updateAdmin);

        return "密码修改成功，请重新登录";
    }

    @Override
    public PageResult<AdminUserVO> getUserList(AdminUserQueryDTO queryDTO) {
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
            vo.setId(user.getId() != null ? user.getId().toString() : null);
            vo.setPhone(user.getPhone());
            vo.setUsername(user.getUsername());
            vo.setGender(user.getGender());
            vo.setStatus(user.getStatus());
            vo.setAvatar(user.getAvatar());
            vo.setAge(user.getAge());
            vo.setEmail(user.getEmail());
            vo.setCategoryId(user.getCategoryId() != null ? user.getCategoryId().toString() : null);
            vo.setCreateTime(user.getCreateTime());
            return vo;
        }).collect(Collectors.toList());

        return new PageResult<>(voList, userPage.getTotal(), queryDTO.getPageNum(), queryDTO.getPageSize());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String updateUserStatus(Long userId, Integer status) {
        if (userId == null) {
            throw new BusinessException("用户ID不能为空");
        }

        if (status == null || (status != 0 && status != 1)) {
            throw new BusinessException("状态参数错误");
        }

        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        User updateUser = new User();
        updateUser.setId(userId);
        updateUser.setStatus(status);
        userMapper.updateById(updateUser);

        String statusText = status == 1 ? "启用" : "禁用";
        return "用户已" + statusText;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String deleteUser(Long userId) {
        if (userId == null) {
            throw new BusinessException("用户ID不能为空");
        }

        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        userMapper.deleteById(userId);
        return "用户删除成功";
    }

    @Override
    public AdminUserVO getUserDetail(Long userId) {
        if (userId == null) {
            throw new BusinessException("用户ID不能为空");
        }

        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        AdminUserVO vo = new AdminUserVO();
        BeanUtils.copyProperties(user, vo);

        if (user.getId() != null) {
            vo.setId(user.getId().toString());
        }
        if (user.getCategoryId() != null) {
            vo.setCategoryId(user.getCategoryId().toString());
        }

        return vo;
    }
}
