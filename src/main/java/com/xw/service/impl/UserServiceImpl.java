package com.xw.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xw.common.Result;
import com.xw.dto.UpdatePasswordDTO;
import com.xw.dto.UserUpdateDTO;
import com.xw.entity.User;
import com.xw.entity.UserBodyRecord;
import com.xw.entity.UserPreference;
import com.xw.entity.UserTarget;
import com.xw.mapper.UserBodyRecordMapper;
import com.xw.mapper.UserMapper;
import com.xw.mapper.UserPreferenceMapper;
import com.xw.mapper.UserTargetMapper;
import com.xw.service.UserService;
import com.xw.vo.UserVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author XW
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private UserBodyRecordMapper bodyRecordMapper;
    @Autowired
    private UserTargetMapper targetMapper;
    @Autowired
    private UserPreferenceMapper preferenceMapper;

    @Override
    public Result<UserVO> getUserInfo(Long userId) {
        UserVO vo = userMapper.getUserFullInfo(userId);

        if (vo == null) {
            return Result.error("用户不存在");
        }
        return Result.success(vo);
    }

    @Override
    public Result<String> updateUserInfo(Long userId, UserUpdateDTO updateDTO) { // 🌟 接收安全 userId
        // 1. 移除 updateDTO.getId() == null 的校验，完全信任上下文 userId

        // 2. 检查数据库中是否有该用户
        User existingUser = userMapper.selectById(userId); // 🌟 使用参数 userId
        if (existingUser == null) {
            return Result.error("用户不存在");
        }

        // 3. 动态更新的核心魔法
        User userToUpdate = new User();
        org.springframework.beans.BeanUtils.copyProperties(updateDTO, userToUpdate);

        // 🌟 核心防线：不管 BeanUtils 刚刚有没有把黑客伪造的 id 拷过来，
        // 我们都在这里强行用上下文真实的 userId 进行覆盖！杜绝越权修改。
        userToUpdate.setId(userId);

        // 4. 调用 MyBatis-Plus 的 updateById
        int rows = userMapper.updateById(userToUpdate);

        return rows > 0 ? Result.success("个人资料保存成功") : Result.error("保存失败");
    }

    @Override
    public Result<String> updatePassword(Long userId, UpdatePasswordDTO dto) { // 🌟 接收安全 userId
        // 1. 移除对 dto.getId() 的强依赖
        if (dto.getOldPassword() == null || dto.getNewPassword() == null) {
            return Result.error("密码不能为空");
        }
        if (dto.getOldPassword().equals(dto.getNewPassword())) {
            return Result.error("新密码不能与旧密码相同");
        }

        // 2. 查询当前用户
        User user = userMapper.selectById(userId); // 🌟 使用参数 userId
        if (user == null) {
            return Result.error("用户不存在");
        }

        // 3. 校验旧密码是否正确
        if (!user.getPassword().equals(dto.getOldPassword())) {
            return Result.error("旧密码错误");
        }

        // 4. 更新为新密码
        User updateUser = new User();
        updateUser.setId(userId); // 🌟 强制使用真实身份 ID 更新
        updateUser.setPassword(dto.getNewPassword());

        int rows = userMapper.updateById(updateUser);

        return rows > 0 ? Result.success("密码修改成功，请重新登录") : Result.error("密码修改失败");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<String> deleteAccount(Long userId) {
        // 1. 检查用户是否存在 (原本逻辑已经很安全)
        User user = userMapper.selectById(userId);
        if (user == null) {
            return Result.error("用户不存在");
        }

        // 2. 删除子表数据
        bodyRecordMapper.delete(new LambdaQueryWrapper<UserBodyRecord>().eq(UserBodyRecord::getUserId, userId));
        targetMapper.delete(new LambdaQueryWrapper<UserTarget>().eq(UserTarget::getUserId, userId));
        preferenceMapper.delete(new LambdaQueryWrapper<UserPreference>().eq(UserPreference::getUserId, userId));

        // 3. 最后删除主表数据
        userMapper.deleteById(userId);

        return Result.success("账户已成功注销，所有数据已彻底清除");
    }
}