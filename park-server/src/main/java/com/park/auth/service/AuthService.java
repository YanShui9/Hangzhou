package com.park.auth.service;

import cn.hutool.captcha.LineCaptcha;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.img.ImgUtil;
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

import java.util.Base64;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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

    /** 验证码缓存：key -> {code, expireTime} */
    private static final ConcurrentHashMap<String, long[]> CAPTCHA_CACHE = new ConcurrentHashMap<>();
    /** 验证码有效期：5分钟 */
    private static final long CAPTCHA_EXPIRE_MS = 5 * 60 * 1000L;

    /**
     * 生成验证码
     * @return {captchaKey, captchaImg(base64)}
     */
    public Map<String, String> generateCaptcha() {
        LineCaptcha captcha = new LineCaptcha(130, 48, 4, 30);
        String code = captcha.getCode();
        String key = IdUtil.fastSimpleUUID();

        long expireAt = System.currentTimeMillis() + CAPTCHA_EXPIRE_MS;
        CAPTCHA_CACHE.put(key, new long[]{ code.hashCode(), expireAt });

        // 定期清理过期项
        if (CAPTCHA_CACHE.size() > 200) {
            long now = System.currentTimeMillis();
            CAPTCHA_CACHE.entrySet().removeIf(e -> e.getValue()[1] < now);
        }

        String base64 = ImgUtil.toBase64(captcha.getImage(), "png");
        Map<String, String> result = new LinkedHashMap<>(4);
        result.put("captchaKey", key);
        result.put("captchaImg", "data:image/png;base64," + base64);
        return result;
    }

    /**
     * 校验验证码（校验后立即删除，一次性使用）
     */
    private void validateCaptcha(String captchaKey, String captchaCode) {
        long[] entry = CAPTCHA_CACHE.remove(captchaKey);
        if (entry == null) {
            throw new BusinessException(ResultCode.CAPTCHA_ERROR);
        }
        if (System.currentTimeMillis() > entry[1]) {
            throw new BusinessException(ResultCode.CAPTCHA_ERROR);
        }
        if (captchaCode == null || captchaCode.hashCode() != entry[0]) {
            throw new BusinessException(ResultCode.CAPTCHA_ERROR);
        }
    }

    /**
     * 用户登录
     *
     * @param loginDTO 登录请求参数
     * @return 登录响应结果
     */
    public LoginVO login(LoginDTO loginDTO) {
        // 0. 校验验证码
        validateCaptcha(loginDTO.getCaptchaKey(), loginDTO.getCaptchaCode());

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

        // 2. 验证密码（BCrypt）
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

    /**
     * 修改密码
     *
     * @param userId      用户ID
     * @param oldPassword 原密码（明文）
     * @param newPassword 新密码（明文）
     */
    public void changePassword(Long userId, String oldPassword, String newPassword) {
        // 1. 查询用户（显式包含 password 字段）
        LambdaQueryWrapper<SysUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysUser::getId, userId);
        queryWrapper.select(SysUser.class, info -> true);
        SysUser user = userMapper.selectOne(queryWrapper);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }

        // 2. 校验原密码
        if (user.getPassword() == null || !passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new BusinessException(ResultCode.USER_PASSWORD_ERROR);
        }

        // 3. 新密码不能与原密码相同
        if (passwordEncoder.matches(newPassword, user.getPassword())) {
            throw new BusinessException(ResultCode.FAILURE, "新密码不能与原密码相同");
        }

        // 4. 加密新密码并更新
        String encodedNewPassword = passwordEncoder.encode(newPassword);
        user.setPassword(encodedNewPassword);
        userMapper.updateById(user);
        log.info("用户修改密码成功：userId={}, username={}", userId, user.getUsername());
    }
}
