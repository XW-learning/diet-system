package com.xw.service.impl.user;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xw.dto.user.UpdatePasswordDTO;
import com.xw.dto.user.UserUpdateDTO;
import com.xw.entity.user.User;
import com.xw.entity.user.UserBodyRecord;
import com.xw.entity.user.UserPreference;
import com.xw.entity.user.UserTarget;
import com.xw.exception.BusinessException;
import com.xw.mapper.user.UserBodyRecordMapper;
import com.xw.mapper.user.UserMapper;
import com.xw.mapper.user.UserPreferenceMapper;
import com.xw.mapper.user.UserTargetMapper;
import com.xw.service.user.UserService;
import com.xw.vo.user.UserVO;
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

    @Override
    public UserVO getUserInfo(Long userId) {
        UserVO vo = userMapper.getUserFullInfo(userId);

        if (vo == null) {
            throw new BusinessException("用户不存在");
        }
        return vo;
    }

    @Override
    public String updateUserInfo(Long userId, UserUpdateDTO updateDTO) {
        User existingUser = userMapper.selectById(userId);
        if (existingUser == null) {
            throw new BusinessException("用户不存在");
        }

        User userToUpdate = new User();
        org.springframework.beans.BeanUtils.copyProperties(updateDTO, userToUpdate);
        userToUpdate.setId(userId);

        int rows = userMapper.updateById(userToUpdate);

        if (rows <= 0) throw new BusinessException("保存失败");
        return "个人资料保存成功";
    }

    @Override
    public String updatePassword(Long userId, UpdatePasswordDTO dto) {
        if (dto.getOldPassword() == null || dto.getNewPassword() == null) {
            throw new BusinessException("密码不能为空");
        }
        if (dto.getOldPassword().equals(dto.getNewPassword())) {
            throw new BusinessException("新密码不能与旧密码相同");
        }

        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        if (!user.getPassword().equals(dto.getOldPassword())) {
            throw new BusinessException("旧密码错误");
        }

        User updateUser = new User();
        updateUser.setId(userId);
        updateUser.setPassword(dto.getNewPassword());

        int rows = userMapper.updateById(updateUser);

        if (rows <= 0) throw new BusinessException("密码修改失败");
        return "密码修改成功，请重新登录";
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String deleteAccount(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        bodyRecordMapper.delete(new LambdaQueryWrapper<UserBodyRecord>().eq(UserBodyRecord::getUserId, userId));
        targetMapper.delete(new LambdaQueryWrapper<UserTarget>().eq(UserTarget::getUserId, userId));
        preferenceMapper.delete(new LambdaQueryWrapper<UserPreference>().eq(UserPreference::getUserId, userId));

        userMapper.deleteById(userId);

        return "账户已成功注销，所有数据已彻底清除";
    }
}
