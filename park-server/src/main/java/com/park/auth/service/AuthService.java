package com.park.auth.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.park.auth.entity.LoginDTO;
import com.park.auth.entity.LoginVO;
import com.park.auth.entity.SysUser;
import com.park.auth.mapper.UserMapper;
import com.park.common.exception.BusinessException;
import com.park.common.result.ResultCode;
import com.park.common.util.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * 认证服务
 *
 * @author park-team
 */
@Slf4j
@Service
public class AuthService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private JwtUtil jwtUtil;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    /**
     * 用户登录
     *
     * @param loginDTO 登录请求参数
     * @return 登录响应结果
     */
    public LoginVO login(LoginDTO loginDTO) {
        // 1. 根据用户名查询用户（包含password字段用于验证）
        LambdaQueryWrapper<SysUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysUser::getUsername, loginDTO.getUsername());
        // 显式包含password字段，因为SysUser实体中password设置了select=false
        queryWrapper.select(SysUser.class, info -> true);
        SysUser user = userMapper.selectOne(queryWrapper);

        if (user == null) {
            log.warn("登录失败：用户不存在 - {}", loginDTO.getUsername());
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }

        // 2. 验证密码（使用BCrypt验证）
        if (user.getPassword() == null || !passwordEncoder.matches(loginDTO.getPassword(), user.getPassword())) {
            log.warn("登录失败：密码错误 - {}", loginDTO.getUsername());
            throw new BusinessException(ResultCode.USER_PASSWORD_ERROR);
        }

        // 3. 验证账号状态
        if (user.getStatus() != null && user.getStatus() == 0) {
            log.warn("登录失败：账号已禁用 - {}", loginDTO.getUsername());
            throw new BusinessException(ResultCode.USER_DISABLED);
        }

        // 4. 验证角色是否匹配
        if (!loginDTO.getRoleType().equals(user.getRoleType())) {
            log.warn("登录失败：角色不匹配 - 请求角色: {}, 用户角色: {}", loginDTO.getRoleType(), user.getRoleType());
            throw new BusinessException(ResultCode.FORBIDDEN, "角色不匹配，无权登录");
        }

        // 5. 生成 JWT Token（携带角色信息）
        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), String.valueOf(user.getRoleType()));
        log.info("用户登录成功：{} (ID={}, roleType={})", user.getUsername(), user.getId(), user.getRoleType());

        // 6. 构建登录响应
        return LoginVO.builder()
                .token(token)
                .username(user.getUsername())
                .realName(user.getRealName())
                .roleType(user.getRoleType())
                .districtId(user.getDistrictId())
                .parkId(user.getParkId())
                .build();
    }

    /**
     * 根据用户ID获取用户信息
     *
     * @param userId 用户ID
     * @return 用户实体
     */
    public SysUser getUserById(Long userId) {
        SysUser user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }
        return user;
    }

    /**
     * 根据用户名获取用户信息
     *
     * @param username 用户名
     * @return 用户实体
     */
    public SysUser getUserByUsername(String username) {
        LambdaQueryWrapper<SysUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysUser::getUsername, username);
        return userMapper.selectOne(queryWrapper);
    }
}
