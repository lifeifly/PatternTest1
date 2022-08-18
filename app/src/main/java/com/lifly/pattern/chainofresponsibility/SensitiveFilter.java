package com.lifly.pattern.chainofresponsibility;

public class SensitiveFilter implements Filter{


    @Override
    public boolean doFilter(Request request, Response response, FilterChain chain) {
        request.str=request.str.replace("","");
        chain.doFilter(request, response, chain);
        response.str=response.str.replace("","");
        return true;
    }
}
