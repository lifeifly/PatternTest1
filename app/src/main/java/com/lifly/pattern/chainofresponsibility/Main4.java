package com.lifly.pattern.chainofresponsibility;

import java.util.ArrayList;
import java.util.List;

public class Main4 {

    public static void main(String[] args) {
        Message msg = new Message("fly", "大家好<script>,996");
        FilterChain filterChain = new FilterChain();

        filterChain.add(new HtmlFilter())
                .add(new SensitiveFilter());

        FilterChain filterChain1 = new FilterChain();
        filterChain1.add(new HtmlFilter())
                .add(new SensitiveFilter());
        Request request=new Request();
        request.str="request";
        Response response=new Response();
        response.str="response";
        filterChain.add(filterChain1).doFilter(request,response,filterChain);
    }
}

class Message {
    String name;
    String content;

    public Message(String name, String content) {
        this.name = name;
        this.content = content;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "Message{" +
                "name='" + name + '\'' +
                ", content='" + content + '\'' +
                '}';
    }
}