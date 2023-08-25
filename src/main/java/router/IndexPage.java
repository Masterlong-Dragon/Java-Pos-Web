package router;

import pos.enduser.Cashier;
import pos.user.Role;
import pos.user.User;
import spark.template.freemarker.FreeMarkerEngine;

import java.util.HashMap;

import static spark.Spark.*;
import static spark.Spark.post;

public class IndexPage {

    private FreeMarkerEngine freemarkerEngine;

    public IndexPage(FreeMarkerEngine freemarkerEngine) {
        this.freemarkerEngine = freemarkerEngine;
        processIndexPage();
    }

    void processIndexPage() {

        before("/index", (req, res) -> {
            User user = req.session().attribute("user");
            if (user == null) {
                res.redirect("/login");
            }
        });

        // POS页面路由
        get("/index", (req, res) -> {
            return modelAndView(new HashMap<>(), "index.html");
        }, freemarkerEngine);

        // 处理权限校验请求
        post("/check-permission", (request, response) -> {
            String function = request.queryParams("function");
            User user = request.session().attribute("user");

            // 根据传入的功能名称进行权限校验逻辑
            if (function.equals("checkout")) {
                // 校验结账功能权限
                if (user.getRole().equals(Role.Admin) || user.getRole().equals(Role.SalesPerson)) {
                    Cashier cashier = new Cashier(user);
                    request.session().attribute("cashier", cashier); // 将收银台对象存入会话
                    request.session().attribute("employeeId", "");
                    response.redirect("/pos");
                } else {
                    return "<script>alert('您没有权限进行结账操作');window.location.href='/index'</script>";
                }
            } else if (function.equals("stock-management")) {
                // 校验库存管理功能权限
                if (user.getRole().equals(Role.Admin) || user.getRole().equals(Role.StockClerk)) {
                    response.redirect("/stock");
                } else {
                    return "<script>alert('您没有权限进行库存管理操作');window.location.href='/index'</script>";
                }
                // 如果有权限，进行跳转到库存管理页面
            }

            return "";
        });

        post("logout", (req, res) -> {
            // 结束会话
            req.session().invalidate();
            res.redirect("/login");
            return null;
        });
    }
}
