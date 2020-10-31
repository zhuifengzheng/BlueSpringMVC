package org.com.blue.handler;

import java.sql.ResultSet;

/**
 * @Author by yuanpeng
 * @Date 2020/10/29
 */
public interface Executor<T> {
    /**
     * 封装结果集，将查询结果封装到实体中
     * @param resultSet
     * @return
     */
    T handler(ResultSet resultSet);
}
