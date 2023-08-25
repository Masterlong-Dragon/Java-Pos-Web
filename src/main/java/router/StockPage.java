package router;

import com.google.gson.*;
import pos.entities.Product;
import pos.modules.stock.StockManager;
import pos.modules.stock.StockManagerImpl;
import pos.user.Role;
import pos.user.User;
import spark.Request;
import spark.Response;
import spark.template.freemarker.FreeMarkerEngine;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;

import static spark.Spark.*;

public class StockPage {
    private FreeMarkerEngine freemarkerEngine;

    public StockPage(FreeMarkerEngine freemarkerEngine) {
        this.freemarkerEngine = freemarkerEngine;
        processStock();
    }

    void processStock() {

        before("/stock", (req, res) -> {
            if (req.session().attribute("user") == null) {
                res.redirect("/login");
            }
            User user = req.session().attribute("user");
            if (!(user.getRole().equals(Role.Admin) || user.getRole().equals(Role.StockClerk))) {
                res.redirect("/index");
            }
        });

        get("/stock", (req, res) -> {
            return modelAndView(new HashMap<>(), "stock.html");
        }, freemarkerEngine);


        // 设置路由，处理GET请求，显示产品列表
        post("/stock/get-products", (request, response) -> getProductList(request, response));

        // 设置路由，处理POST请求，添加产品
        post("/stock/add-product", (request, response) -> addProduct(request, response));

        // 设置路由，处理POST请求，更新产品
        post("/stock/update-product", (request, response) -> updateProduct(request, response));

        // 设置路由，处理POST请求，删除产品
        post("/stock/remove-product", (request, response) -> removeProduct(request, response));

        // 设置路由，处理POST请求，搜索产品
        post("/stock/search-product", (request, response) -> searchProduct(request, response));

    }

    private Object searchProduct(Request request, Response response) {
        String name = request.queryParams("keyword");
        StockManager stockManager = StockManagerImpl.Instance();
        User user = request.session().attribute("user");
        try {
            ArrayList<Product> products = stockManager.getProducts(user, name);
            return productsToJson(products);
        } catch (Exception e) {
            // 400 Bad Request
            response.status(400);
            return renderJson(e.getMessage());
        }

    }

    private Object removeProduct(Request request, Response response) {
        int id = Integer.parseInt(request.queryParams("id"));
        StockManager stockManager = StockManagerImpl.Instance();
        User user = request.session().attribute("user");
        try {
            stockManager.removeProduct(user, id);
        } catch (Exception e) {
            // 400 Bad Request
            response.status(400);
            return renderJson(e.getMessage());
        }
        return renderJson("success");
    }

    private Object updateProduct(Request request, Response response) {
        int productId = Integer.parseInt(request.queryParams("id"));
        String productName = request.queryParams("name");
        int productQuantity = Integer.parseInt(request.queryParams("stock"));
        BigDecimal productPrice = new BigDecimal(request.queryParams("price"));
        StockManager stockManager = StockManagerImpl.Instance();
        User user = request.session().attribute("user");
        try {
            stockManager.updateProduct(user, new Product(productId, productName, productPrice, productQuantity));
        } catch (Exception e) {
            // 400 Bad Request
            response.status(400);
            return renderJson(e.getMessage());
        }
        return renderJson("success");
    }

    private Object addProduct(Request request, Response response) {
        String productName = request.queryParams("name");
        int productQuantity = Integer.parseInt(request.queryParams("stock"));
        BigDecimal productPrice = new BigDecimal(request.queryParams("price"));
        StockManager stockManager = StockManagerImpl.Instance();
        User user = request.session().attribute("user");
        try {
            stockManager.addProduct(user, new Product(0, productName, productPrice, productQuantity));
        } catch (Exception e) {
            // 400 Bad Request
            response.status(400);
            return renderJson(e.getMessage());
        }
        return renderJson("success");
    }

    private Object getProductList(Request request, Response response) {
        User user = request.session().attribute("user");
        StockManager stockManager = StockManagerImpl.Instance();
        System.out.println(request.toString());
        try {
            ArrayList<Product> products = stockManager.getProducts(user, Integer.parseInt(request.queryParams("fromIndex"))
                    , Integer.parseInt(request.queryParams("toIndex")));
            return productsToJson(products);
        }catch (Exception e){
            // 400 Bad Request
            response.status(400);
            return renderJson(e.getMessage());
        }
    }

// ...

    private static class ProductSerializer implements JsonSerializer<Product> {
        @Override
        public JsonElement serialize(Product product, Type type, JsonSerializationContext context) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("id", product.getProductID());
            jsonObject.addProperty("name", product.getName());
            jsonObject.addProperty("price", product.getPrice());
            jsonObject.addProperty("stock", product.getStock());
            return jsonObject;
        }
    }

    private String productsToJson(ArrayList<Product> products) {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Product.class, new ProductSerializer())
                .create();
        return gson.toJson(products);
    }

    private String renderJson(Object data) {
        Gson gson = new Gson();
        return gson.toJson(data);
    }
}
