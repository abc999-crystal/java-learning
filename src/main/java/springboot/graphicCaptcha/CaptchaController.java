package springboot.graphicCaptcha;

import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.LineCaptcha;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Date;

@RestController
@RequestMapping("/captcha")
public class CaptchaController {
    /**
     * 生成验证码
     *
     * @param session
     * @param response
     */
    @GetMapping("/getCaptcha")
    public void getCaptcha(HttpSession session, HttpServletResponse response) {
        // 定义图形验证码的长和宽
        LineCaptcha lineCaptcha = CaptchaUtil.createLineCaptcha(Constants.CAPTCHA_WIDTH, Constants.CAPTCHA_HEIGHT);
        // 设置返回数据类型
        response.setContentType("image/jpeg");
        // 禁止使用缓存
        response.setHeader("Pragma", "No-cache");
        try {
            // 输出到页面
            lineCaptcha.write(response.getOutputStream());
            // 将 生成的验证码 和 验证码生成时间 存储到session中
            session.setAttribute(Constants.CAPTCHA_KEY, lineCaptcha.getCode());
            session.setAttribute(Constants.CAPTCHA_DATE, new Date());
            // 关闭流
            response.getOutputStream().close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 验证码校验
     *
     * @param captcha
     * @param session
     * @return
     */
    @PostMapping("/check")
    public boolean checkCaptcha(String captcha, HttpSession session) {
        System.out.println("接收到验证码: {}" + captcha);
        // 参数校验
        if (!StringUtils.hasLength(captcha)) {
            return false;
        }
        // 获取存储的验证码和生成时间
        String code = (String) session.getAttribute(Constants.CAPTCHA_KEY);
        Date createTime = (Date) session.getAttribute(Constants.CAPTCHA_DATE);
        // 判断验证码是否正确(验证码一般忽略大小写)
        if (captcha.equalsIgnoreCase(code)) {
            // 判断验证码是否过时
            if (createTime == null || System.currentTimeMillis() - createTime.getTime() < Constants.EXPIRATION_TIME) {
                return true;
            }
            return false;
        }
        return false;
    }
}
