package org.com.blue;

import org.com.blue.annotation.ComponentScan;
import org.com.blue.utils.ActionContextUtil;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.io.File;
import java.net.URL;

/**
 * tomcat启动时加载
 * @Author by yuanpeng
 * @Date 2020/10/28
 */
@ComponentScan({"org.com.blue"})
public class BootStraptServlet implements ServletContextListener {
    public BootStraptServlet(){
        System.out.println("初始化容器...");
    }

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ComponentScan[] annotationsByType = this.getClass().getAnnotationsByType(ComponentScan.class);
        if (annotationsByType.length != 0) {
            // 这里简单来，取第一个即可
            ComponentScan componentScan = annotationsByType[0];
            String[] pathValue = componentScan.value();
            String scanPath = pathValue[0];
            scanPath = scanPath.replace(".", File.separator);
            // 通过pathValue扫描文件，解析类上到注解 这里可以默认写个文件用来定位路径
            URL resource = this.getClass().getClassLoader().getResource("database.properties");
            if (null == resource) {
                System.err.println("资源加载失败");
            }
            String path = resource.getPath();
            // 这里linux 与 window系统路径因该区分处理，可以判断当前系统分别处理，这里只处理linux系统下路径
            String realPath = path.substring(0, path.lastIndexOf(File.separator) + 1);
            realPath = realPath + scanPath;
            File file = new File(realPath);
            try {
                //递归变量文件，并将有@Controller @Bean...注解的相关类加载到上下文
                ActionContextUtil.recursionFile(file);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
