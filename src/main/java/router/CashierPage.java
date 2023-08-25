package router;

import pos.enduser.Cashier;
import pos.entities.Order;
import pos.entities.OrderDetail;
import pos.entities.PaymentMethod;
import pos.entities.Product;
import pos.entities.individuals.Individual;
import pos.modules.individuals.CustomerManager;
import pos.modules.individuals.SalesPersonManager;
import pos.user.Role;
import pos.user.User;
import spark.Request;
import spark.template.freemarker.FreeMarkerEngine;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static spark.Spark.*;

public class CashierPage {

    private FreeMarkerEngine freemarkerEngine;

    public CashierPage(FreeMarkerEngine freemarkerEngine) {
        this.freemarkerEngine = freemarkerEngine;
        processCashierPage();
    }

    void processCashierPage() {
        // POS 页面路由
        before("/pos", (req, res) -> {
            // 检查用户是否已登录，未登录则重定向到登录页面
            if (req.session(false) == null) {
                res.redirect("/login");
            }
            User user = req.session().attribute("user");
            if (!(user.getRole().equals(Role.Admin) || user.getRole().equals(Role.SalesPerson))) {
                res.redirect("/index");
            }
        });

        get("/pos", (req, res) -> {
            // 清空订单
            Cashier cashier = req.session().attribute("cashier");
            Individual salesperson = req.session().attribute("salesperson");
            cashier.start(salesperson, new Order(Individual.DEFAULT_CUSTOMER));
            return modelAndView(posAttributes(req), "pos.html");
        }, freemarkerEngine);


        post("/pos/submit-eid", (request, response) -> {
            // 获取表单数据
            String employeeId = request.queryParams("employeeId");

            // 在这里进行后续的处理逻辑，比如保存数据到数据库等
            SalesPersonManager salesPersonManager = SalesPersonManager.Instance();
            User user = request.session().attribute("user");

            Individual salesperson = salesPersonManager.getIndividual(user, Integer.parseInt(employeeId));
            if (salesperson == null) {
                String errorMessage = "员工号不存在";
                response.status(400); // 设置响应状态码为400，表示请求失败
                return errorMessage;
            }
            request.session().attribute("salesperson", salesperson);
            request.session().attribute("employeeId", employeeId);
            Map<String, Object> attributes = new HashMap<>();
            attributes.put("employeeId", employeeId);
            Cashier cashier = request.session().attribute("cashier");
            cashier.start(salesperson, new Order(Individual.DEFAULT_CUSTOMER));
            return modelAndView(attributes, "pos.html");
        });

        post("/pos/submit-cid", (request, response) -> {
            // 获取表单数据
            String customerId = request.queryParams("customerId");
            Cashier cashier = request.session().attribute("cashier");
            User user = request.session().attribute("user");
            Individual customer = CustomerManager.Instance().getIndividual(user, Integer.parseInt(customerId));
            if (customer == null) {
                String errorMessage = "顾客号不存在";
                response.status(400); // 设置响应状态码为400，表示请求失败
                return errorMessage;
            }
            cashier.getCurrentOrder().setCustomer(customer);
            return "success";
        });

        post("/pos/get-total-amount", (request, response) -> {
            Cashier cashier = request.session().attribute("cashier");
            BigDecimal totalAmount = cashier.getCurrentOrder().getTotalAmount();
            return totalAmount.toString();
        });

        // 添加商品到订单列表的路由处理方法
        post("/pos/add-product", (request, response) -> {
            // 解析请求数据
            int productId = Integer.parseInt(request.queryParams("productId"));
            int quantity = Integer.parseInt(request.queryParams("quantity"));

            // 在这里将商品添加到订单列表
            Cashier cashier = request.session().attribute("cashier");
            cashier.addItem(productId, quantity);

            return updateOrderlist(request);
        });

        post("/pos/update-quantity", (request, response) -> {
            // 从请求参数中获取商品ID和新数量
            int productId = Integer.parseInt(request.queryParams("productId"));
            int newQuantity = Integer.parseInt(request.queryParams("quantity"));

            // 在这里处理更新商品数量的逻辑，例如更新数据库或订单对象等
            Cashier cashier = request.session().attribute("cashier");
            // 返回更新后的商品列表的 HTML
            cashier.updateItem(productId, newQuantity); // 请根据实际逻辑实现此方法
            return updateOrderlist(request);
        });

        // 设置路由，处理前端请求
        post("/pos/remove-product", (request, response) -> {
            // 从请求参数中获取商品ID
            int productId = Integer.parseInt(request.queryParams("productId"));

            // 在这里处理删除商品的逻辑，例如更新数据库或订单对象等
            Cashier cashier = request.session().attribute("cashier");
            cashier.removeItem(productId); // 请根据实际逻辑实现此方法
            // 返回更新后的商品列表的 HTML
            return updateOrderlist(request);
        });

        post("/pos/clear-checkout", (request, response) -> {
            // 在这里处理清空订单的逻辑，例如更新数据库或订单对象等
            Cashier cashier = request.session().attribute("cashier");
            cashier.getCurrentOrder().getDetail().clear(); // 请根据实际逻辑实现此方法
            // 返回更新后的商品列表的 HTML
            return updateOrderlist(request);
        });

        post("/pos/submit-order", (request, response) -> {
            // 处理提交订单请求
            Cashier cashier = request.session().attribute("cashier");

            // 校验工号是否为空
            if (cashier.getSalePerson() == null) {
                response.status(400); // 设置响应状态码为400，表示请求失败
                return "工号为空";
            }

            // 校验订单是否为空
            if (cashier.getCurrentOrder().getDetail().isEmpty()) {
                response.status(400); // 设置响应状态码为400，表示请求失败
                return "订单为空";
            }

            cashier.submitSale();
            // 返回响应
            response.type("application/json");
            return "{\"success\": true, \"orderId\": \"" + cashier.getCurrentOrder().getID() + "\"}";
        });

        post("/pos/checkout", (request, response) -> {
            // 获取请求参数
            String totalAmount = request.queryParams("amount");
            String paymentMode = request.queryParams("mode");

            Cashier cashier = request.session().attribute("cashier");
            response.type("application/json");
            int id = cashier.getCurrentOrder().getID();
            try {
                cashier.createPayment(new BigDecimal(totalAmount), PaymentMethod.valueOf(paymentMode));
                cashier.submitPayment();
                cashier.start(cashier.getSalePerson(), new Order(Individual.DEFAULT_CUSTOMER));
                return "{\"success\": true, \"orderId\": \"" + id + "\"}";
            } catch (Exception e) {
                e.printStackTrace();
                return "{\"success\": false, \"orderId\": \"" + id + "\"}";
            }
        });
    }

    private static HashMap posAttributes(Request request) {
        HashMap<String, Object> attributes = new HashMap<>();
        attributes.put("employeeId", request.session().attribute("employeeId"));
        return attributes;
    }

    private static String updateOrderlist(Request request) {
        // 获取当前订单的详细内容
        Cashier cashier = request.session().attribute("cashier");
        Order order = cashier.getCurrentOrder();

        // 生成订单中每个商品的HTML行
        StringBuilder productListHtml = new StringBuilder();
        List<OrderDetail> orderItems = order.getDetail();
        for (OrderDetail orderItem : orderItems) {
            Product product = orderItem.getProduct();
            String productId = String.valueOf(product.getProductID());
            String productName = product.getName();
            BigDecimal price = product.getPrice();
            int quantity = orderItem.getQuantity();
            BigDecimal totalPrice = orderItem.getUnitPrice();

            // 生成HTML行并追加到商品列表HTML中
            productListHtml.append("<tr>");
            productListHtml.append("<td>").append(productId).append("</td>");
            productListHtml.append("<td>").append(productName).append("</td>");
            productListHtml.append("<td>").append(price).append("</td>");
            productListHtml.append("<td>");
            productListHtml.append("<div class=\"input-group\">");
            productListHtml.append("<button class=\"btn btn-outline-primary\" onclick=\"decreaseQuantity(").append(productId).append(")\">-</button>");
            productListHtml.append("<input type=\"number\" class=\"form-control\" id=\"quantity_").append(productId).append("\" value=\"").append(quantity).append("\">");
            productListHtml.append("<button class=\"btn btn-outline-primary\" onclick=\"increaseQuantity(").append(productId).append(")\">+</button>");
            productListHtml.append("</div>");
            productListHtml.append("</td>");
            productListHtml.append("<td>").append(totalPrice).append("</td>");
            productListHtml.append("<td>");
            productListHtml.append("<button class=\"btn btn-danger\" onclick=\"removeProduct(").append(productId).append(")\">删除</button>");
            productListHtml.append("</td>");
            productListHtml.append("</tr>");
        }

        return productListHtml.toString();
    }
}
