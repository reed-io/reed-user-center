package org.reed.core.user.interceptor;

import org.reed.define.Order;
import org.reed.interceptor.ReedInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Order(value = 101)
public final class UserInfoInterceptor implements ReedInterceptor {
    @Override
    public String[] pathPatterns() {
        return new String[] {"/**"};
    }


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        return ReedInterceptor.super.preHandle(request, response, handler);
    }

}
