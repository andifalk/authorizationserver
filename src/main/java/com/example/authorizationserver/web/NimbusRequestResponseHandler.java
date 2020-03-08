package com.example.authorizationserver.web;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.nimbusds.oauth2.sdk.http.HTTPRequest;
import com.nimbusds.oauth2.sdk.http.HTTPResponse;
import com.nimbusds.oauth2.sdk.http.ServletUtils;

import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.method.support.ModelAndViewContainer;

public class NimbusRequestResponseHandler
        implements HandlerMethodArgumentResolver, HandlerMethodReturnValueHandler {

  @Override
  public boolean supportsParameter(MethodParameter methodParameter) {
    return HTTPRequest.class.isAssignableFrom(methodParameter.getParameterType());
  }

  @Override
  public Object resolveArgument(MethodParameter methodParameter, ModelAndViewContainer modelAndViewContainer,
                                NativeWebRequest nativeWebRequest, WebDataBinderFactory webDataBinderFactory)
          throws Exception {

    HttpServletRequest request = nativeWebRequest.getNativeRequest(HttpServletRequest.class);
    if (request != null) {
      return ServletUtils.createHTTPRequest(request);
    } else {
      return null;
    }
  }

  @Override
  public boolean supportsReturnType(MethodParameter methodParameter) {
    return HTTPResponse.class.isAssignableFrom(methodParameter.getParameterType());
  }

  @Override
  public void handleReturnValue(Object o, MethodParameter methodParameter, ModelAndViewContainer modelAndViewContainer, NativeWebRequest nativeWebRequest) throws Exception {
    HTTPResponse response = (HTTPResponse) o;
    HttpServletResponse res = nativeWebRequest.getNativeResponse(HttpServletResponse.class);
    if (res != null) {
      res.setStatus(response.getStatusCode());
      res.setContentType(MediaType.APPLICATION_JSON_VALUE);
      res.getWriter().println(response.getContent());
      res.flushBuffer();
    }
    modelAndViewContainer.setRequestHandled(true);
  }
}

