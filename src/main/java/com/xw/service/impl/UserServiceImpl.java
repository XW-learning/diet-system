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

        // 2. 判断用户是否存在
        if (vo == null) {
            return Result.error("用户不存在");
        }
        return Result.success(vo);
    }

    @Override
    public Result<String> updateUserInfo(UserUpdateDTO updateDTO) {
        // 1. 安全校验：检查 ID 是否为空
        if (updateDTO.getId() == null) {
            return Result.error("用户ID不能为空");
        }

        // 2. 检查数据库中是否有该用户
        User existingUser = userMapper.selectById(updateDTO.getId());
        if (existingUser == null) {
            return Result.error("用户不存在");
        }

        // 3. 动态更新的核心魔法
        User userToUpdate = new User();
        // 将 DTO 中的数据拷贝到一个全新的 User 实体中
        // 此时 userToUpdate 里，前端传了值的字段有数据，没传的字段全是 null
        org.springframework.beans.BeanUtils.copyProperties(updateDTO, userToUpdate);

        // 4. 调用 MyBatis-Plus 的 updateById
        // 它的底层策略是：自动忽略值为 null 的字段，只拼接有值的字段到 UPDATE 语句中！
        int rows = userMapper.updateById(userToUpdate);

        return rows > 0 ? Result.success("个人资料保存成功") : Result.error("保存失败");
    }

    @Override
    public Result<String> updatePassword(UpdatePasswordDTO dto) {
        // 1. 校验参数
        if (dto.getId() == null) {
            return Result.error("用户ID不能为空");
        }
        if (dto.getOldPassword().equals(dto.getNewPassword())) {
            return Result.error("新密码不能与旧密码相同");
        }

        // 2. 查询当前用户
        User user = userMapper.selectById(dto.getId());
        if (user == null) {
            return Result.error("用户不存在");
        }

        // 3. 校验旧密码是否正确
        if (!user.getPassword().equals(dto.getOldPassword())) {
            return Result.error("旧密码错误");
        }

        // 4. 更新为新密码
        User updateUser = new User();
        updateUser.setId(user.getId());
        updateUser.setPassword(dto.getNewPassword());

        // 使用 updateById，只会更新我们 set 了值的 password 字段
        int rows = userMapper.updateById(updateUser);

        return rows > 0 ? Result.success("密码修改成功，请重新登录") : Result.error("密码修改失败");
    }


    @Override
    @Transactional(rollbackFor = Exception.class) // 🌟 开启事务：只要中间报错，前面的删除全部回滚撤销！
    public Result<String> deleteAccount(Long userId) {
        // 1. 检查用户是否存在
        User user = userMapper.selectById(userId);
        if (user == null) {
            return Result.error("用户不存在");
        }

        // 2. 删除子表数据 (Java 层面的级联删除)
        // 2.1 删除身材记录
        bodyRecordMapper.delete(new LambdaQueryWrapper<UserBodyRecord>().eq(UserBodyRecord::getUserId, userId));

        // 2.2 删除目标数据
        targetMapper.delete(new LambdaQueryWrapper<UserTarget>().eq(UserTarget::getUserId, userId));

        // 2.3 删除饮食偏好
        preferenceMapper.delete(new LambdaQueryWrapper<UserPreference>().eq(UserPreference::getUserId, userId));

        // 3. 最后删除主表数据 (t_user)
        userMapper.deleteById(userId);

        return Result.success("账户已成功注销，所有数据已彻底清除");
    }
}