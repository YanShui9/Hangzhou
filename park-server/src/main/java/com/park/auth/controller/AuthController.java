package com.park.auth.controller;

import com.park.auth.entity.LoginDTO;
import com.park.auth.entity.LoginVO;
import com.park.auth.entity.SysUser;
import com.park.auth.service.AuthService;
import com.park.common.result.R;
import com.park.common.util.JwtUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

/**
 * 认证控制器
 *
 * @author park-team
 */
@Slf4j
@RestController
@RequestMapping("/api/auth")
@Api(tags = "认证管理")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * 获取登录验证码
     */
    @GetMapping("/captcha")
    @ApiOperation(value = "获取验证码", notes = "生成图形验证码，返回base64图片和唯一key")
    public R<Map<String, String>> getCaptcha() {
        return R.ok(authService.generateCaptcha());
    }

    /**
     * 用户登录
     *
     * @param loginDTO 登录请求参数
     * @return 登录响应结果（包含 token、用户信息）
     */
    @PostMapping("/login")
    @ApiOperation(value = "用户登录", notes = "用户名密码登录，返回Token和用户信息")
    public R<LoginVO> login(@Valid @RequestBody LoginDTO loginDTO) {
        log.info("用户登录请求: username={}, roleType={}", loginDTO.getUsername(), loginDTO.getRoleType());
        LoginVO loginVO = authService.login(loginDTO);
        return R.ok(loginVO);
    }

    /**
     * 获取当前登录用户信息
     * 从 JWT Token 中解析用户ID，查询数据库返回完整用户信息
     *
     * @param request HTTP 请求（携带 Token）
     * @return 当前用户信息
     */
    @GetMapping("/info")
    @ApiOperation(value = "获取当前用户信息", notes = "从Token解析当前登录用户信息")
    public R<Map<String, Object>> getUserInfo(HttpServletRequest request) {
        // 从请求属性中获取由 JwtAuthenticationFilter 解析出的用户信息
        Object userIdObj = request.getAttribute("userId");
        Long userId = null;
        if (userIdObj instanceof Integer) {
            userId = ((Integer) userIdObj).longValue();
        } else if (userIdObj instanceof Long) {
            userId = (Long) userIdObj;
        }

        if (userId == null) {
            return R.fail("无法获取用户信息");
        }

        // 查询用户完整信息
        SysUser user = authService.getUserById(userId);
        if (user == null) {
            return R.fail("用户不存在");
        }

        // 构建返回数据（排除密码等敏感字段）
        Map<String, Object> userInfo = new HashMap<>(8);
        userInfo.put("userId", user.getId());
        userInfo.put("username", user.getUsername());
        userInfo.put("realName", user.getRealName());
        userInfo.put("phone", user.getPhone());
        userInfo.put("roleType", user.getRoleType());
        userInfo.put("districtId", user.getDistrictId());
        userInfo.put("parkId", user.getParkId());

        return R.ok(userInfo);
    }

    /**
     * 用户登出
     *
     * @return 操作结果
     */
    @PostMapping("/logout")
    @ApiOperation(value = "用户登出", notes = "退出登录（客户端清除Token即可）")
    public R<Void> logout() {
        // JWT 是无状态的，登出由客户端清除 Token 实现
        // 如需服务端控制，可将 Token 加入黑名单（此处暂不实现）
        log.info("用户登出");
        return R.ok();
    }

    /**
     * 修改密码
     * 校验原密码后更新为新密码（BCrypt 加密）
     *
     * @param body    请求体 { oldPassword, newPassword }
     * @param request HTTP 请求（携带 Token）
     * @return 操作结果
     */
    @PostMapping("/change-password")
    @ApiOperation(value = "修改密码", notes = "校验原密码后更新为新密码")
    public R<Void> changePassword(@RequestBody Map<String, String> body, HttpServletRequest request) {
        Object userIdObj = request.getAttribute("userId");
        Long userId = null;
        if (userIdObj instanceof Integer) {
            userId = ((Integer) userIdObj).longValue();
        } else if (userIdObj instanceof Long) {
            userId = (Long) userIdObj;
        }
        if (userId == null) {
            return R.fail("无法获取用户信息");
        }

        String oldPassword = body.get("oldPassword");
        String newPassword = body.get("newPassword");
        if (oldPassword == null || oldPassword.isEmpty() || newPassword == null || newPassword.isEmpty()) {
            return R.fail("原密码和新密码不能为空");
        }

        authService.changePassword(userId, oldPassword, newPassword);
        return R.ok();
    }
}
