package org.com.blue.handler;

import org.com.blue.utils.DbUtil;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author by yuanpeng
 * @Date 2020/10/29
 */
public class ListExecutor<T> implements Executor<List<T>> {
    private Class<T> clazz;

    public ListExecutor(Class<T> clazz) {
        this.clazz = clazz;
    }

    @Override
    public List<T> handler(ResultSet resultSet) {
        List<T> result = new ArrayList<>();
        try {
            while (resultSet.next()) {
                T query = DbUtil.resultToAttribute(resultSet, clazz);
                result.add(query);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return result;
    }
}
