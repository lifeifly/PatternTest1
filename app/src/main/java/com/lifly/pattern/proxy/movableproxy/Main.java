package com.lifly.pattern.proxy.movableproxy;

import android.text.method.MovementMethod;

import com.lifly.pattern.proxy.staticproxy.Movable;
import com.lifly.pattern.proxy.staticproxy.Tank;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class Main {
    public static void main(String[] args) {
        Tank t=new Tank();

        Movable m= (Movable) Proxy.newProxyInstance(Tank.class.getClassLoader(), new Class[]{Movable.class}, new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                System.out.println("method"+method.getName());
                //当代理对象m执行方法method时被监听到，实际是进行被代理对象t的该方法
                Object o=method.invoke(t,args);
                System.out.println("methgod"+method.getName());
                return o;
            }
        });
        m.move();
    }
}
