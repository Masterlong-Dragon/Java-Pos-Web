package router;

import pos.auth.Permission;
import pos.auth.PermissionImpl;
import pos.enduser.Cashier;
import pos.user.User;
import spark.template.freemarker.FreeMarkerEngine;

import java.util.HashMap;

import static spark.Spark.*;

public class LoginPage {

    private FreeMarkerEngine freemarkerEngine;

    public LoginPage(FreeMarkerEngine freemarkerEngine) {
        this.freemarkerEngine = freemarkerEngine;
        processLogin();
    }

    void processLogin() {

        get("/", (req, res) -> {
            if (req.session().attribute("user") == null) {
                res.redirect("/login");
            } else {
                res.redirect("/index");
            }
            return null;
        });

        // 登录页面路由
        get("/login", (req, res) -> {
            return modelAndView(new HashMap<>(), "login.html");
        }, freemarkerEngine);

        // 登录处理路由
        post("/login", (req, res) -> {
            String username = req.queryParams("username");
            String password = req.queryParams("password");

            // 进行身份验证逻辑，验证成功则创建会话
            User user = authenticate(username, password);
            if (user != null) {
                req.session(true); // 创建新的会话
                req.session().attribute("user", user); // 将用户对象存入会话
//                Cashier cashier = new Cashier(user);
//                req.session().attribute("cashier", cashier); // 将收银台对象存入会话
//
//                req.session().attribute("employeeId", "");

                res.redirect("/index"); // 重定向到POS页面
            } else {
                // 身份验证失败，返回登录页面或错误信息
                return "<script>alert('用户名或密码错误');window.location.href='/login'</script>";
            }
            return null;
        });
    }

    // 身份验证逻辑
    private static User authenticate(String username, String password) {
        // 在这里实现您的身份验证逻辑
        // 验证成功返回 true，验证失败返回 false
        Permission permission = PermissionImpl.Instance();
        try {
            return permission.login(username, password);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
