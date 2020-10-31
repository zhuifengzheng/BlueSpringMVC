package org.com.blue.domain;

import lombok.Data;
import org.com.blue.annotation.Bean;

import java.io.Serializable;
import java.util.Date;

/**
 * @Author by yuanpeng
 * @Date 2020/10/28
 */
@Data
@Bean
public class User implements Serializable {
    private static final long serialVersionUID = -7773793980878643761L;
    private Integer id;
    private String userName;
    private String password;
    private String mobile;
    private Date createTime;
}
