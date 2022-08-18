package com.lifly.pattern.calculator.并查集;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class UserTest {
    public static class User {
        public String a;
        public String b;
        public String c;

        public User(String a, String b, String c) {
            this.a = a;
            this.b = b;
            this.c = c;
        }
    }


    /**
     * 如果两个user的a、b、c任一个对应相等，就认为是一个人，合并user，并返回合并后的用户数量
     */
    public static int mergeUser(List<User> users) {
        UnionTest.UnionSet<User> unionSet = new UnionTest.UnionSet<>(users);
        HashMap<String, User> aMap = new HashMap<>();
        HashMap<String, User> bMap = new HashMap<>();
        HashMap<String, User> cMap = new HashMap<>();
        for (int i = 0; i < users.size(); i++) {
            User user = users.get(i);
            if (aMap.containsKey(user.a)) {
                //说明有其它对象a字段和该对象一样，合并这两个对象
                User before = aMap.get(user.a);
                unionSet.union(user, before);
            } else {
                //说明该字段还不存在，直接放入集合
                aMap.put(user.a, user);
            }
            if (bMap.containsKey(user.b)) {
                //说明有其它对象a字段和该对象一样，合并这两个对象
                User before = aMap.get(user.b);
                unionSet.union(user, before);
            } else {
                //说明该字段还不存在，直接放入集合
                aMap.put(user.b, user);
            }
            if (cMap.containsKey(user.c)) {
                //说明有其它对象a字段和该对象一样，合并这两个对象
                User before = aMap.get(user.c);
                unionSet.union(user, before);
            } else {
                //说明该字段还不存在，直接放入集合
                aMap.put(user.c, user);
            }
        }

        return unionSet.getIndependentListCount();
    }
}
