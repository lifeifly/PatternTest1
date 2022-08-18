package com.yele.hu.upgradetools.util;

import com.yele.baseapp.utils.LogUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ClockUtils {

    private static final String TAG = "ClockUtils";
    
    /**
     * 解析星期数据
     * @param week 从查询指令获取的星期数据
     */
    public static List<Integer> analysisWeek(String week){
        String inter = hexToInter(week);
        LogUtils.i(TAG,"解析出来的二进制：" + inter);
        List<Integer> list = new ArrayList<>();
        char[] chars = inter.toCharArray();
        for (int i=0;i<chars.length;i++){
            if(chars[i]-49 == 0){
                list.add(i);
            }
        }
        return list;
    }

    /**
     * 十六进制转二进制
     * 限制长度为7位，不够前面补0
     * @param hex 十六进制
     * @return
     */
    private static String hexToInter(String hex){
        int ten = Integer.parseInt(hex, 16);
        String sixteen = Integer.toBinaryString(ten);
        return String.format(Locale.US,"%07d",Integer.parseInt(sixteen));
    }

    /**
     * 二进制转十六进制
     * @param index 二进制
     * @return
     */
    public static String interToHex(String index){
        int ten = Integer.parseInt(index, 2);
        return Integer.toHexString(ten);
    }


    /**
     * 二进制求和
     * @param a 二进制的值
     * @param b 当前的二进制值
     * @return
     */
    public static String indexToSum(String a,String b){
        int i = a.length() - 1;
        int j = b.length() - 1;

        //这个是结果： 可变的字符序列对象
        StringBuilder res = new StringBuilder();

        int curSum;

        //进位的标志位
        int carry = 0;

        while(i >=0 || j >=0){
            curSum = carry;
            // 当前位置的a的i位和 b 的j 位，都是末位进行相加
            if(i >= 0){
                curSum += a.charAt(i) - '0';
                i--;
            }
            if(j >= 0){
                curSum += b.charAt(j) - '0';
                j--;
            }
            // 判断是否需要进位
            if(curSum > 1){
                // 1+1的情况,在二进制下 需要减去2,有进位
                curSum -= 2;
                carry = 1;
            }else{
                carry = 0;
            }
            // 只写结果的值，进位作为下一轮的初始值
            res.insert(0, curSum);
        }
        if(carry == 1){
            res.insert(0, 1);
        }
        return res.toString();
    }
    
}
