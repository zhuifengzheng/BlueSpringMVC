package org.com.blue.meta;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.LinkedHashMap;

/**
 * @Author by yuanpeng
 * @Date 2020/10/31
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ModelAndView implements Serializable {
    private static final long serialVersionUID = 1641326100884636527L;
    /**
     * 返回的视图
     */
    private String view;

    /**
     * 返回的数据模型
     */
    private LinkedHashMap<String, Object> model;


}
