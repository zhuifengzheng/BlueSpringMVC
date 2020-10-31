package org.com.blue.utils;

import org.com.blue.handler.Executor;

import javax.servlet.ServletException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.sql.*;
import java.util.Arrays;
import java.util.Properties;

/**
 * 数据库操作工具类
 *
 * @Author by yuanpeng
 * @Date 2020/10/29
 */
public class DbUtil {
    private static Properties dbProperties;

    static {
        try {
            // 加载数据库配置文件
            InputStream resourceAsStream = DbUtil.class.getClassLoader().getResourceAsStream("database.properties");
            Properties properties = new Properties();
            properties.load(resourceAsStream);
            // 加载数据库驱动 此方法会执行加载类的static构造块
            Class.forName(properties.getProperty("jdbc.driver"));
            dbProperties = properties;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取连接对象
     *
     * @return
     */
    public static Connection getConnection() {
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(dbProperties.getProperty("jdbc.url"), dbProperties.getProperty("jdbc.username"), dbProperties.getProperty("jdbc.password"));
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return connection;
    }


    /**
     * @param sql
     * @param executor
     * @param args
     * @param <T>
     * @return
     */
    public static <T> T query(String sql, Executor<T> executor, Object... args) {
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        T result = null;
        try {
            connection = DbUtil.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            if (args.length == 0) {
                preparedStatement.execute();
                resultSet = preparedStatement.getResultSet();
            } else {
                int i = 1;
                for (Object arg : args) {
                    Class<?> clazz = arg.getClass();
                    if (clazz == String.class) {
                        preparedStatement.setString(i, (String) arg);
                    } else if (clazz == Integer.class || clazz == int.class) {
                        preparedStatement.setInt(i, (int) arg);
                    } else if (clazz == Long.class || clazz == long.class) {
                        preparedStatement.setLong(i, (long) arg);
                    } else if (clazz == Double.class || clazz == double.class) {
                        preparedStatement.setDouble(i, (double) arg);
                    } else if (clazz == byte.class || clazz == Byte.class) {
                        preparedStatement.setByte(i, (byte) arg);
                    } else if (clazz == java.util.Date.class) {
                        //java.sqlDate extends java.util.Date
                        preparedStatement.setDate(i, (Date) arg);
                    } else {
                        // 抛出异常
                        throw new ServletException("不支持的参数类型");
                    }
                    i++;
                }
                // 执行sql
                preparedStatement.execute();
                // 得到返回结果
                resultSet = preparedStatement.getResultSet();
            }
            result = executor.handler(resultSet);

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
        return result;
    }

    /**
     * 通过结果集封装数据返回
     *
     * @param resultSet
     * @return
     * @throws SQLException
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    public static <T> T resultToAttribute(ResultSet resultSet, Class<T> clazz) {
        T object = null;
        try {
            // 处理结果集到对象属性的映射
            object = clazz.newInstance();
            Field[] fields = clazz.getDeclaredFields();

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
                    columnName = generateColumnName(columnName);
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

                    } else if (java.util.Date.class == type) {
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
        } catch (Exception e) {

        }
        return object;
    }

    private static String generateColumnName(String columnName) {
        return columnName.replace("_","");
    }
}
