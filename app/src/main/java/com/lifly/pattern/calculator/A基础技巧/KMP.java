package com.lifly.pattern.calculator.A基础技巧;

public class KMP {
    /**
     * 给定一个字符串ss和匹配串pp，求第一个子串可以和pp相同的子串位置
     * @param ss
     * @param pp
     * @return
     */
    public static int matchStr(String ss,String pp){
        int[] nexts=new int[pp.length()];
        getNexts(nexts,pp);
        //模式串pp位置
        int j=0;
        for (int i = 0; i < ss.length(); i++) {
            while(j>0&&ss.charAt(i)!=pp.charAt(j)){
                j=nexts[j-1];
            }
            if (ss.charAt(i)==pp.charAt(j)){
                j++;
            }
            if (j==pp.length()){
                return i-pp.length()+1;
            }
        }
        return -1;
    }

    /**
     * 获取next数组，next[i]代表str[0...i]子串的最长相等前后缀的长度
     */
    public static void getNexts(int[] nexts,String str){
        //前缀结尾位置
        int j=0;
        nexts[0]=0;
        //枚举后缀结尾位置
        for (int i = 1; i < str.length(); i++) {
            while(j>0&&str.charAt(i)!=str.charAt(j)){
                j=nexts[j-1];
            }
            if(str.charAt(i)==str.charAt(j)){
                j++;
            }
            nexts[i]=j;
        }
    }

    public static void main(String[] args) {
        String ss="aabaabaaf";
        String pp="aabaaf";

        System.out.println(matchStr(ss,pp));
    }
}
