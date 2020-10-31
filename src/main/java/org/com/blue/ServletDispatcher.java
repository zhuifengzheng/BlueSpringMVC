package org.com.blue;

import com.alibaba.fastjson.JSON;
import org.com.blue.annotation.ComponentScan;
import org.com.blue.annotation.ResponseBody;
import org.com.blue.context.ActionContext;
import org.com.blue.meta.ActionMeta;
import org.com.blue.meta.ModelAndView;
import org.com.blue.meta.RequestContext;
import org.com.blue.utils.ActionContextUtil;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

/**
 * @Author by yuanpeng
 * @Date 2020/10/28
 */
@WebServlet("/")
@ComponentScan({"org.com.blue.domain"})
public class ServletDispatcher extends HttpServlet {

    /**
     * // 1、封装请求到自己上下文环境的thradLocal中
     * <p>
     * // 2、从配置文件或注解中加载数据，封装到一个全局请求类中（包括请求url与访问的controller类和方法信息等）
     * <p>
     * // 3、从配置文件或注解中加载所有bean实体
     * <p>
     * // 4、通过访问url匹配到具体到类与方法，再通过反射调用该方法，获取方法参数实体类型calss，
     * // 调用方法前将请求参数里面的参数与实体类class字段进行匹配，匹配到设置到类实体相应字段中
     * <p>
     * // 5、反射调用方法后返回结果，判断反射的方法有没有自定义@ResponseBody，判断该结果类型，序列化返回结果
     *
     * @param req
     * @param res
     * @throws ServletException
     * @throws IOException
     */
    @Override
    public void service(ServletRequest req, ServletResponse res) throws ServletException, IOException {

        HttpServletRequest request;
        HttpServletResponse response;
        if (req instanceof HttpServletRequest && res instanceof HttpServletResponse) {
            request = (HttpServletRequest) req;
            response = (HttpServletResponse) res;
        } else {
            throw new ServletException("non-HTTP request or response");
        }
        // 设置上下文环境
        ActionContext.setRequestContext(new RequestContext(request, response));

        try {
            String requestURI = request.getRequestURI().substring(1);
            System.out.println("requestURI:" + requestURI);
            // todo 这里可以对设置的静态资源直接返回

            List<Class<?>> classList = ActionContext.classList;
            Map<String, ActionMeta> methodMap = ActionContext.methodMap;
            System.out.println(classList);
            System.out.println(methodMap);
            // 根据uri从配置中匹配到controller类
            ActionMeta actionMeta = methodMap.get(requestURI);
            // 获取对应的方法和类，通过反射执行方法
            if (null == actionMeta) {
                return;
            }
            Method method = actionMeta.getMethod();
            Class<?> clazz = actionMeta.getClazz();
            Object object = clazz.newInstance();
            Class<?>[] parameterTypes = method.getParameterTypes();
            Object[] args;
            // 将请求参数映射到对应实体类中
            args = ActionContextUtil.parameterMappingObject(parameterTypes);
            Object invoke = method.invoke(object, args);
            // 获取反射方法结果，进行序列化
            if (method.getAnnotationsByType(ResponseBody.class).length != 0) {
                if (null != invoke) {
                    if (invoke.getClass()== ModelAndView.class) {
                        ModelAndView modelAndView = (ModelAndView) invoke;
                        request.getSession().setAttribute("user", modelAndView.getModel().get("user"));

                        if (null != modelAndView.getView()) {
                            request.getRequestDispatcher(modelAndView.getView()).forward(req, response);
                        }
                        return;
                    }
                }
            }
            response.setContentType("text/html;charset=utf-8");
            // 对invoke序列化
            response.getWriter().println("待开发...");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
