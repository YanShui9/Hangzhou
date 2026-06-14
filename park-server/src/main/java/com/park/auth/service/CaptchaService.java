package com.park.auth.service;

import com.park.common.util.CaptchaUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 验证码服务
 *
 * @author park-team
 */
@Slf4j
@Service
public class CaptchaService {

    // 验证码缓存（生产环境建议使用Redis）
    private static final ConcurrentHashMap<String, String> captchaCache = new ConcurrentHashMap<>();
    
    // 验证码有效期（毫秒）
    private static final long CAPTCHA_EXPIRE_TIME = 5 * 60 * 1000; // 5分钟
    
    // 验证码创建时间缓存
    private static final ConcurrentHashMap<String, Long> captchaTimeCache = new ConcurrentHashMap<>();

    /**
     * 生成验证码
     *
     * @return 包含验证码图片和key的Map
     */
    public Map<String, String> generateCaptcha() {
        // 生成唯一key
        String codeKey = UUID.randomUUID().toString().replace("-", "");
        
        // 生成验证码文本
        String code = CaptchaUtil.generateCode();
        
        // 生成验证码图片
        String image = CaptchaUtil.generateCaptchaImage(code);
        
        // 存储验证码
        captchaCache.put(codeKey, code.toLowerCase());
        captchaTimeCache.put(codeKey, System.currentTimeMillis());
        
        // 清理过期验证码
        cleanExpiredCaptcha();
        
        log.debug("生成验证码: key={}, code={}", codeKey, code);
        
        Map<String, String> result = new HashMap<>();
        result.put("codeKey", codeKey);
        result.put("image", image);
        return result;
    }

    /**
     * 验证验证码
     *
     * @param codeKey 验证码key
     * @param code    用户输入的验证码
     * @return 是否验证通过
     */
    public boolean verifyCaptcha(String codeKey, String code) {
        if (codeKey == null || code == null) {
            return false;
        }
        
        String cachedCode = captchaCache.get(codeKey);
        Long createTime = captchaTimeCache.get(codeKey);
        
        // 验证码不存在
        if (cachedCode == null || createTime == null) {
            log.warn("验证码不存在: codeKey={}", codeKey);
            return false;
        }
        
        // 验证码已过期
        if (System.currentTimeMillis() - createTime > CAPTCHA_EXPIRE_TIME) {
            captchaCache.remove(codeKey);
            captchaTimeCache.remove(codeKey);
            log.warn("验证码已过期: codeKey={}", codeKey);
            return false;
        }
        
        // 验证成功后删除验证码（一次性使用）
        captchaCache.remove(codeKey);
        captchaTimeCache.remove(codeKey);
        
        boolean result = cachedCode.equalsIgnoreCase(code.trim());
        log.debug("验证码验证: codeKey={}, input={}, cached={}, result={}", codeKey, code, cachedCode, result);
        
        return result;
    }

    /**
     * 清理过期验证码
     */
    private void cleanExpiredCaptcha() {
        long now = System.currentTimeMillis();
        captchaTimeCache.forEach((key, time) -> {
            if (now - time > CAPTCHA_EXPIRE_TIME) {
                captchaCache.remove(key);
                captchaTimeCache.remove(key);
            }
        });
    }
}