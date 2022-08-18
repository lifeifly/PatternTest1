package com.lifly.pattern.chainofresponsibility;

public interface Filter {
    boolean doFilter(Request request, Response response,FilterChain chain);
}
