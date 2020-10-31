package org.com.blue.controller;

import com.alibaba.fastjson.JSON;
import org.com.blue.annotation.Controller;
import org.com.blue.annotation.RequestMapping;
import org.com.blue.annotation.ResponseBody;
import org.com.blue.domain.User;
import org.com.blue.factory.SingletonBeanRegistry;
import org.com.blue.handler.ListExecutor;
import org.com.blue.meta.ModelAndView;
import org.com.blue.utils.ActionContextUtil;
import org.com.blue.utils.DbUtil;

import java.util.LinkedHashMap;
import java.util.List;

/**
 * 用户领域
 *
 * @Author by yuanpeng
 * @Date 2020/10/28
 */
@Controller
@RequestMapping("user")
public class UserController {

    @RequestMapping("login")
    @ResponseBody
    public ModelAndView login(User user) {
        ModelAndView modelAndView = new ModelAndView();
        SingletonBeanRegistry beanRegistry = ActionContextUtil.getBeanRegistry();
        // Object object = beanRegistry.getSingleton("User");
        List<User> query = DbUtil.query("select * from user where user_name=? and password=?", new ListExecutor<>(User.class), new Object[]{user.getUserName(), user.getPassword()});
        System.out.println(query);
        modelAndView.setView("/index.jsp");
        LinkedHashMap<String, Object> map = new LinkedHashMap<>();
        map.put("user", JSON.toJSON(query));
        modelAndView.setModel(map);
        return modelAndView;
    }

    @RequestMapping("loginOut")
    public void loginOut(User user) {

    }

}
