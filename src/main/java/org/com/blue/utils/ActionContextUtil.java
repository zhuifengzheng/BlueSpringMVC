package org.com.blue.utils;

import org.com.blue.annotation.*;
import org.com.blue.context.ActionContext;
import org.com.blue.factory.SingletonBeanFactory;
import org.com.blue.factory.SingletonBeanRegistry;
import org.com.blue.meta.ActionMeta;
import org.com.blue.meta.BeanDefinitionMeta;
import org.com.blue.meta.RequestContext;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 上下文工具类
 *
 * @Author by yuanpeng
 * @Date 2020/10/28
 */
public class ActionContextUtil {
    private static List<Class<?>> classList = new ArrayList<>();

    private static Map<String, ActionMeta> methodMap = new HashMap<>();

    private static SingletonBeanRegistry singletonBeanRegistry = new SingletonBeanRegistry();

    public static List<Class<?>> loadBeanDefinations() {
        return classList;
    }

    public static Map<String, ActionMeta> loadActionMeta() {
        return methodMap;
    }

    static {
        singletonBeanRegistry.registerSingleton("SingletonBeanRegistry",singletonBeanRegistry);
        singletonBeanRegistry.registerSingleton("SingletonBeanFactory",new SingletonBeanFactory());
    }

    /**
     * 这里得到bean注册的容器，按照spring的做法，是将bean和容器一些基本信息封装到applicationContext中的
     * 只需要实现相应的接口就可以拿到applicationContext，进而得到bean；还可以通过@Autowired等注解注入，在反射
     * 时将bean实例从容器中拿出，这里只是模拟从容器中获取bean，而没有去设计在容器启动时解析@Autowired等注解拿到bean
     * 也没有实现ApplicationContextAware接口的方法，而是直接从工具类中获取，想说明spring容器中的单例bean也是从缓存
     * map里面解析出的
     * @return
     */
    public static SingletonBeanRegistry getBeanRegistry(){
        return singletonBeanRegistry;
    }

    /**
     * 递归加载文件
     * @param file
     * @throws Exception
     */
    public static void recursionFile(File file) throws Exception {
        String fileName = file.getName();
        if (file.isDirectory()) {
            File[] childFiles = file.listFiles();
            for (File childFile : childFiles) {
                recursionFile(childFile);
            }
        } else {
            if (fileName.endsWith(".class")) {
                fileName = fileName.substring(0,fileName.indexOf(".class"));
                String realPath = file.getCanonicalPath();
                realPath = realPath.substring(realPath.indexOf("classes") + "classes".length() + 1, realPath.indexOf(".class")).replace(File.separator, ".");
                // 通过反射拿到该类
                Class<?> clazz = Class.forName(realPath);
                Bean[] beans = clazz.getAnnotationsByType(Bean.class);
                Controller[] controllers = clazz.getAnnotationsByType(Controller.class);
                RequestMapping[] requestMappings = clazz.getAnnotationsByType(RequestMapping.class);
                Service[] services = clazz.getAnnotationsByType(Service.class);
                if (beans.length != 0) {
                    // 处理@Bean的类
                    classList.add(clazz);
                    // 注册bean
                    singletonBeanRegistry.registerSingleton(fileName,clazz.newInstance());
                } else if (controllers.length != 0) {
                    // 处理@Controller的类上到@RequestMapping注解
                    if (requestMappings.length != 0) {
                        // 得到controller类上的requestmapping注解上的值
                        String controllerMapping = requestMappings[0].value()[0];
                        controllerMapping = controllerMapping == null ? "" : controllerMapping;
                        Method[] methods = clazz.getMethods();
                        if (methods.length == 0) {
                            return;
                        }
                        for (Method method : methods) {
                            RequestMapping[] mappings = method.getAnnotationsByType(RequestMapping.class);
                            if (mappings.length != 0) {
                                String methodMapping = mappings[0].value()[0];
                                methodMapping = mappings[0].value()[0] == null ? "" : methodMapping;
                                // 拼接controllerMapping methodMapping作为key
                                // 判断开头结尾是否有 / 有就去除
                                controllerMapping = getRequestPath(controllerMapping);
                                methodMapping = getRequestPath(methodMapping);
                                String key = controllerMapping + File.separator + methodMapping;
                                ActionMeta meta = new ActionMeta();
                                meta.setClazz(clazz);
                                meta.setMethod(method);
                                methodMap.put(key, meta);
                            }
                        }
                    }
                    singletonBeanRegistry.registerSingleton(fileName,clazz.newInstance());
                }else if(services.length!=0){
                    singletonBeanRegistry.registerSingleton(fileName,clazz.newInstance());
                }
            }
        }
    }

    /**
     * 格式化请求路径
     * @param path
     * @return
     */
    public static String getRequestPath(String path) {
        if (path.startsWith(File.separator)) {
            path = path.substring(1);
        }
        if (path.endsWith(File.separator)) {
            path = path.substring(0, path.length() - 1);
        }
        return path;
    }

    /**
     * 处理请求方法参数，映射到对应到实体
     *
     * @param parameterTypes
     * @return
     */
    public static Object[] parameterMappingObject(Class<?>[] parameterTypes) {
        if (parameterTypes.length == 0) {
            return new Object[]{};
        }
        int i = 0;
        Object[] args = new Object[parameterTypes.length];
        RequestContext requestContext = ActionContext.getRequestContext();

        try {
            for (Class<?> parameterType : parameterTypes) {
                if (parameterType == HttpServletRequest.class) {
                    args[i++] = requestContext.getHttpServletRequest();
                } else if (parameterType == HttpServletResponse.class) {
                    args[i++] = requestContext.getHttpServletResponse();
                } else {
                    for (Class<?> clazz : classList) {
                        if (parameterType == clazz) {
                            // 判断parameterType是否是clazz的实例 是就注入属性
                            HttpServletRequest request = requestContext.getHttpServletRequest();
                            //request.getParameter(clazz.getName());
                            Field[] fields = clazz.getDeclaredFields();
                            Object object = clazz.newInstance();
                            for (Field field : fields) {
                                // 解封
                                field.setAccessible(true);
                                // 将参数设置到实体中
                                if ("serialVersionUID".equalsIgnoreCase(field.getName())){
                                    continue;
                                }
                                field.set(object, request.getParameter(field.getName()));
                            }
                            args[i++] = object;
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return args;
    }
}
