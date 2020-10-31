package org.com.blue.meta;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @Author by yuanpeng
 * @Date 2020/10/28
 */
@Data
public class BeanDefinitionMeta implements Serializable {
    private static final long serialVersionUID = 3089582897448044405L;
    private List<Class<?>> classList;

}
