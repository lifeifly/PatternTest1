package com.lifly.pattern.chainofresponsibility;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FilterChain implements Filter{
    List<Filter> filters=new ArrayList<>();
    //当前执行到哪一个Filter
    int index=0;
    public FilterChain add(Filter filter){
        filters.add(filter);
        return this;
    }


    @Override
    public boolean doFilter(Request request, Response response, FilterChain chain) {
        if (index==filters.size())return false;
        Filter f=filters.get(index);
        index++;
        return f.doFilter(request, response, chain);
    }
}
