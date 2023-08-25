import static spark.Spark.*;

import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapperBuilder;
import freemarker.template.TemplateExceptionHandler;
import router.CashierPage;
import router.IndexPage;
import router.LoginPage;
import router.StockPage;
import spark.template.freemarker.FreeMarkerEngine;

import java.io.File;
import java.io.IOException;

public class Main {

    public static void main(String[] args) {


        // 设置静态文件目录
        staticFiles.externalLocation("/public");

        // 创建FreeMarker配置对象
        Configuration configuration = new Configuration(Configuration.getVersion());
        // 设置模板加载路径
        String currentWorkingDirectory = System.getProperty("user.dir");
        File currentDirectory = new File(currentWorkingDirectory);
        String parentDirectory = currentDirectory.getParent();
        File externalTemplateFolder = new File(parentDirectory + "/frontend/templates");
        try {
            configuration.setDirectoryForTemplateLoading(externalTemplateFolder);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        // 其他配置选项
        configuration.setDefaultEncoding("UTF-8");
        configuration.setObjectWrapper(new DefaultObjectWrapperBuilder(Configuration.getVersion()).build());
        configuration.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);

        // 创建FreeMarker引擎
        FreeMarkerEngine freemarkerEngine = new FreeMarkerEngine(configuration);

        exception(IllegalArgumentException.class, (e, request, response) -> {
            response.status(400);
            response.body("Bad request");
        });

        // 设置登录页面路由
        new LoginPage(freemarkerEngine);
        // 设置功能选择页面路由
        new IndexPage(freemarkerEngine);
        // 设置收银页面路由
        new CashierPage(freemarkerEngine);
        // 设置库存页面路由
        new StockPage(freemarkerEngine);
    }
}
