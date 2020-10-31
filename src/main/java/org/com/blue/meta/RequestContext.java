package org.com.blue.meta;

import lombok.Getter;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.Serializable;

/**
 * 上下文请求
 * @Author by yuanpeng
 * @Date 2020/10/29
 */
@Getter
public class RequestContext implements Serializable {
    private static final long serialVersionUID = 3298576361140954483L;
    private HttpServletRequest httpServletRequest;
    private HttpServletResponse httpServletResponse;
    private HttpSession httpSession;
    private ServletContext servletContext;

    public RequestContext(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        this.httpServletRequest = httpServletRequest;
        this.httpServletResponse = httpServletResponse;
        this.httpSession = httpServletRequest.getSession();
        this.servletContext = httpServletRequest.getServletContext();
    }
}
