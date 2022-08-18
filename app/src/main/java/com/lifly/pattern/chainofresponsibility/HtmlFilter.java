package com.lifly.pattern.chainofresponsibility;

public class HtmlFilter implements Filter{

    @Override
    public boolean doFilter(Request request, Response response, FilterChain chain) {
        request.str=request.str.replace("<","[");
        chain.doFilter(request,response,chain);
        response.str+="HtmlFilter";
        return true;
    }
}
