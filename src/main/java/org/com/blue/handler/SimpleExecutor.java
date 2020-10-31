package org.com.blue.handler;

import org.com.blue.utils.DbUtil;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @Author by yuanpeng
 * @Date 2020/10/29
 */
public class SimpleExecutor<T> implements Executor<T> {

    private Class<T> clazz;

    public SimpleExecutor(Class<T> clazz) {
        this.clazz = clazz;
    }

    @Override
    public T handler(ResultSet resultSet) {
        T result = null;
        try {
            if (resultSet.next()) {
                result = DbUtil.resultToAttribute(resultSet, clazz);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return result;
    }
}
