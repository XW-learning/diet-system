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
 * 用户服务实现类
 *
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

    /**
     * 获取用户完整信息
     *
     * @param userId 用户ID
     * @return 用户完整信息VO
     */
    @Override
    public Result<UserVO> getUserInfo(Long userId) {
        UserVO vo = userMapper.getUserFullInfo(userId);

        if (vo == null) {
            return Result.error("用户不存在");
        }
        return Result.success(vo);
    }

    /**
     * 更新用户基础信息
     *
     * @param userId    用户ID（从上下文获取，确保安全）
     * @param updateDTO 更新信息DTO
     * @return 操作结果
     */
    @Override
    public Result<String> updateUserInfo(Long userId, UserUpdateDTO updateDTO) {
        User existingUser = userMapper.selectById(userId);
        if (existingUser == null) {
            return Result.error("用户不存在");
        }

        User userToUpdate = new User();
        org.springframework.beans.BeanUtils.copyProperties(updateDTO, userToUpdate);
        userToUpdate.setId(userId);

        int rows = userMapper.updateById(userToUpdate);

        return rows > 0 ? Result.success("个人资料保存成功") : Result.error("保存失败");
    }

    /**
     * 修改用户密码
     *
     * @param userId 用户ID（从上下文获取，确保安全）
     * @param dto    密码修改DTO
     * @return 操作结果
     */
    @Override
    public Result<String> updatePassword(Long userId, UpdatePasswordDTO dto) {
        if (dto.getOldPassword() == null || dto.getNewPassword() == null) {
            return Result.error("密码不能为空");
        }
        if (dto.getOldPassword().equals(dto.getNewPassword())) {
            return Result.error("新密码不能与旧密码相同");
        }

        User user = userMapper.selectById(userId);
        if (user == null) {
            return Result.error("用户不存在");
        }

        if (!user.getPassword().equals(dto.getOldPassword())) {
            return Result.error("旧密码错误");
        }

        User updateUser = new User();
        updateUser.setId(userId);
        updateUser.setPassword(dto.getNewPassword());

        int rows = userMapper.updateById(updateUser);

        return rows > 0 ? Result.success("密码修改成功，请重新登录") : Result.error("密码修改失败");
    }

    /**
     * 注销用户账户
     * 删除用户及其关联的所有数据
     *
     * @param userId 用户ID
     * @return 操作结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<String> deleteAccount(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            return Result.error("用户不存在");
        }

        bodyRecordMapper.delete(new LambdaQueryWrapper<UserBodyRecord>().eq(UserBodyRecord::getUserId, userId));
        targetMapper.delete(new LambdaQueryWrapper<UserTarget>().eq(UserTarget::getUserId, userId));
        preferenceMapper.delete(new LambdaQueryWrapper<UserPreference>().eq(UserPreference::getUserId, userId));

        userMapper.deleteById(userId);

        return Result.success("账户已成功注销，所有数据已彻底清除");
    }
}