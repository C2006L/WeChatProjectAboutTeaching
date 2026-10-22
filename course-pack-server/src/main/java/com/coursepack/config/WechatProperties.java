package com.coursepack.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 微信小程序配置（application-local.yml 或环境变量注入，不进版本库）：
 *   wechat.appid / wechat.secret
 * 未配置时 /wx-login 直接报"微信登录未配置"，其余功能不受影响。
 */
@Data
@Component
@ConfigurationProperties(prefix = "wechat")
public class WechatProperties {

    /** 小程序 AppID */
    private String appid = "";

    /** 小程序 AppSecret */
    private String secret = "";

    public boolean configured() {
        return appid != null && !appid.isBlank() && secret != null && !secret.isBlank();
    }
}
