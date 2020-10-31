package org.com.blue.test;

import org.com.blue.annotation.Bean;
import org.com.blue.annotation.Controller;
import org.com.blue.annotation.RequestMapping;
import org.com.blue.domain.User;
import org.com.blue.meta.ActionMeta;
import org.com.blue.meta.BeanDefinitionMeta;
import org.com.blue.utils.DbUtil;
import org.junit.Test;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.*;
import java.util.*;
import java.util.Date;

/**
 * @Author by yuanpeng
 * @Date 2020/10/28
 */
public class TestSystem {

    @Test
    public void generatePath() {
        String path = "/Users/zhuifengzheng/IdeaProjects/BlueSystem/target/BlueSystem-1.0-SNAPSHOT/WEB-INF/classes/database.properties\n";

        String realPath = path.substring(0, path.lastIndexOf(File.separator));
        System.out.println(realPath);

        String requestUri = "/user/login.action";
        requestUri = requestUri.substring(1, requestUri.indexOf(".action"));
        System.out.println(requestUri);
    }

    @Test
    public void filePath() throws Exception {
        String realPath = "/Users/zhuifengzheng/IdeaProjects/BlueSystem/target/BlueSystem-1.0-SNAPSHOT/WEB-INF/classes/org/com/blue";
        File file = new File(realPath);
        //递归变量文件，并将有@Controller @Bean...注解的相关类加载到上下文
        BeanDefinitionMeta beanDefinitionMeta = new BeanDefinitionMeta();
        List<Class<?>> classList = new ArrayList<>();

        ActionMeta actionMeta = new ActionMeta();
        Map<String, Method> methodMap = new HashMap<>();
        recursionFile(file, classList, methodMap);

        beanDefinitionMeta.setClassList(classList);
    }

    public void recursionFile(File file, List<Class<?>> classList, Map<String, Method> methodMap) throws Exception {

        String fileName = file.getName();
        if (file.isDirectory()) {
            System.out.println("dir:" + fileName);
            File[] childFiles = file.listFiles();
            for (File childFile : childFiles) {
                recursionFile(childFile, classList, methodMap);
            }
        } else {
            if (fileName.endsWith(".class")) {
                String realPath = file.getCanonicalPath();
                realPath = realPath.substring(realPath.indexOf("classes") + "classes".length() + 1, realPath.indexOf(".class")).replace(File.separator, ".");
                // 通过反射拿到该类
                System.out.println("==" + realPath);
                Class<?> clazz = Class.forName(realPath);
                Bean[] beans = clazz.getAnnotationsByType(Bean.class);
                Controller[] controllers = clazz.getAnnotationsByType(Controller.class);
                RequestMapping[] requestMappings = clazz.getAnnotationsByType(RequestMapping.class);
                if (beans.length != 0) {
                    // 处理@Bean的类
                    classList.add(clazz);
                    System.out.println("bean:" + clazz.getName());

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
                                methodMap.put(key, method);
                            }
                        }

                    }
                    System.out.println("Controller:" + clazz.getName());

                }

            }
        }

    }

    public String getRequestPath(String path) {
        if (path.startsWith(File.separator)) {
            path = path.substring(1);
        }
        if (path.endsWith(File.separator)) {
            path = path.substring(0, path.length() - 1);
        }
        return path;
    }

    @Test
    public void findUser() {
        User result = new User();
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            connection = DbUtil.getConnection();
//            PreparedStatement preparedStatement = connection.prepareStatement("select * from URP_User where UserName =?");
//            preparedStatement.setString(1,"admin");
//            preparedStatement.execute();
            statement = connection.createStatement();
            statement.execute("select * from URP_User where UserName = " + "\'admin\'");
            resultSet = statement.getResultSet();
            // 返回单结果
            query(resultSet);


        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (null != resultSet) {
                    resultSet.close();
                }
                if (null != statement) {
                    statement.close();
                }
                if (null != connection) {
                    connection.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private User query(ResultSet resultSet) throws SQLException, IllegalAccessException, InstantiationException {
        User object = User.class.newInstance();

        if (resultSet.next()) {
            // 处理结果集到对象属性的映射
            Field[] fields = User.class.getDeclaredFields();

            // 获取结果集元数据
            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();
            int[] columnToAttribute = new int[columnCount + 1];
            // 初始化数组值
            Arrays.fill(columnToAttribute, -1);
            for (int i = 0; i < fields.length; i++) {
                // 数据库返回是从第一列开始算的数据
                for (int j = 1; j < columnCount + 1; j++) {
                    // 拿到数据库列名，这里一般会将列名进行转化，为和实体字段名匹配，比如user_name=>userName
                    String columnName = metaData.getColumnName(j);
                    if (fields[i].getName().equalsIgnoreCase(columnName)) {
                        columnToAttribute[j] = i;
                    }

                }
            }
            // 获取到查询结果和对象属性对应关系后，只需要从columnToAttribute中取出数据，即可获取查询结果中对应值

            for (int j = 1; j < columnCount + 1; j++) {
                int i = columnToAttribute[j];
                if (-1 != i) {
                    Field field = fields[i];
                    field.setAccessible(true);
                    Class<?> type = field.getType();
                    // User object = User.class.newInstance();
                    if (String.class == type) {
                        field.set(object, resultSet.getString(j));
                    } else if (Integer.class == type || Integer.TYPE == type) {
                        field.set(object, resultSet.getInt(j));

                    } else if (Date.class == type) {
                        field.set(object, resultSet.getDate(j));

                    } else if (Long.class == type || Long.TYPE == type) {
                        field.set(object, resultSet.getLong(j));

                    } else if (Double.class == type || Double.TYPE == type) {
                        field.set(object, resultSet.getDouble(j));

                    } else if (Byte.class == type || Byte.TYPE == type) {
                        field.set(object, resultSet.getByte(j));

                    }
                }

            }


        }
        return object;
    }
}
