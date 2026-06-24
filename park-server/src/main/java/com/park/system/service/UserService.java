package com.park.system.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.park.auth.entity.SysUser;
import com.park.auth.mapper.UserMapper;
import com.park.common.exception.BusinessException;
import com.park.common.result.ResultCode;
import com.park.system.dto.UserQueryDTO;
import com.park.system.dto.UserSaveDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * 用户管理服务
 *
 * @author park-team
 */
@Slf4j
@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    /** 默认密码 */
    private static final String DEFAULT_PASSWORD = "123456";

    /**
     * 分页查询用户列表
     *
     * @param queryDTO 查询条件
     * @return 分页结果
     */
    public IPage<SysUser> getUserPage(UserQueryDTO queryDTO) {
        Page<SysUser> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());

        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(queryDTO.getUsername()),
                        SysUser::getUsername, queryDTO.getUsername())
                .eq(queryDTO.getRoleType() != null,
                        SysUser::getRoleType, queryDTO.getRoleType())
                .eq(queryDTO.getDistrictId() != null,
                        SysUser::getDistrictId, queryDTO.getDistrictId())
                .eq(queryDTO.getStatus() != null,
                        SysUser::getStatus, queryDTO.getStatus())
                .orderByDesc(SysUser::getCreateTime);

        return userMapper.selectPage(page, wrapper);
    }

    /**
     * 根据ID查询用户
     *
     * @param id 用户ID
     * @return 用户实体
     */
    public SysUser getUserById(Long id) {
        SysUser user = userMapper.selectById(id);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }
        return user;
    }

    /**
     * 新增用户
     *
     * @param dto 用户信息
     */
    public void saveUser(UserSaveDTO dto) {
        // 校验用户名唯一
        checkUsernameUnique(dto.getUsername(), null);

        // 校验业务规则
        validateUserRole(dto);

        SysUser user = new SysUser();
        user.setUsername(dto.getUsername());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setRoleType(dto.getRoleType());
        user.setDistrictId(dto.getDistrictId());
        user.setParkId(dto.getParkId());
        user.setRealName(dto.getRealName());
        user.setPhone(dto.getPhone());
        user.setStatus(dto.getStatus());

        userMapper.insert(user);
        log.info("新增用户成功: username={}", dto.getUsername());
    }

    /**
     * 修改用户
     *
     * @param dto 用户信息
     */
    public void updateUser(UserSaveDTO dto) {
        if (dto.getId() == null) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "用户ID不能为空");
        }

        // 检查用户是否存在
        SysUser existUser = getUserById(dto.getId());

        // 校验用户名唯一（排除自身）
        checkUsernameUnique(dto.getUsername(), dto.getId());

        // 校验业务规则
        validateUserRole(dto);

        existUser.setUsername(dto.getUsername());
        existUser.setRoleType(dto.getRoleType());
        existUser.setDistrictId(dto.getDistrictId());
        existUser.setParkId(dto.getParkId());
        existUser.setRealName(dto.getRealName());
        existUser.setPhone(dto.getPhone());
        existUser.setStatus(dto.getStatus());

        // 如果传了密码则更新
        if (StringUtils.hasText(dto.getPassword())) {
            existUser.setPassword(passwordEncoder.encode(dto.getPassword()));
        }

        userMapper.updateById(existUser);
        log.info("修改用户成功: id={}, username={}", dto.getId(), dto.getUsername());
    }

    /**
     * 删除用户
     *
     * @param id 用户ID
     */
    public void deleteUser(Long id) {
        SysUser user = getUserById(id);
        userMapper.deleteById(id);
        log.info("删除用户成功: id={}, username={}", id, user.getUsername());
    }

    /**
     * 重置密码（重置为 123456）
     *
     * @param id 用户ID
     */
    public void resetPassword(Long id) {
        SysUser user = getUserById(id);
        user.setPassword(passwordEncoder.encode(DEFAULT_PASSWORD));
        userMapper.updateById(user);
        log.info("重置密码成功: id={}, username={}", id, user.getUsername());
    }

    /**
     * 校验用户名唯一
     *
     * @param username  用户名
     * @param excludeId 排除的用户ID（修改时传入自身ID）
     */
    private void checkUsernameUnique(String username, Long excludeId) {
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getUsername, username);
        if (excludeId != null) {
            wrapper.ne(SysUser::getId, excludeId);
        }
        if (userMapper.selectCount(wrapper) > 0) {
            throw new BusinessException(ResultCode.USER_EXISTS);
        }
    }

    /**
     * 校验用户角色相关的业务规则
     */
    private void validateUserRole(UserSaveDTO dto) {
        if (dto.getRoleType() == 2 && dto.getDistrictId() == null) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "区县管理员必须指定所属区县");
        }
        if (dto.getRoleType() == 3 && dto.getParkId() == null) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "园区管理员必须指定所属园区");
        }
    }
}
