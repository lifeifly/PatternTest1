package com.lifly.pattern.calculator.打表;

public class Example {

    /**
     * 两种袋子1个可以装6个苹果，1个可以装8个，给一个正整数N，要求在使用两个袋子最少的情况下，还可以给袋子装满，可以返回袋子的数量，不能返回-1
     */

    public static int minBagAwesome(int apple) {
        //找规律
        if ((apple & 1) != 0) {//如果奇数返回-1
            return -1;
        }
        if (apple < 18) {//小于18就这几种情况
            return apple == 0 ? 0 : (apple == 6 || apple == 8) ? 1 : (apple == 12 || apple == 14 || apple == 16) ? 2 : -1;
        }
        //后面的规律统一
        return (apple - 18) / 8 + 3;
    }

    /**
     * 给定一个正整数N，表示N份青草，一只牛和一只羊，牛先吃，羊后吃，每一轮吃的草量必须是1，4，16，64（4的某次方）
     * 谁最先吃完谁获胜，返回谁回赢
     */
    //暴力
    public static String winner1(int n) {
        if (n < 5) {
            return (n == 0 || n == 2) ? "后手" : "先手";
        }
        int base = 1;
        //当前是先手先选
        while (base < n) {
            //一共n份，先吃掉base份，n-base留给后手
            if (winner1(n - base).equals("后手")) {
                return "先手";
            }
            if (base > n / 4) {
                //防止base*4溢出
                break;
            }
            base *= 4;
        }
        return "后手";
    }

    //规律  后先后先先
    public static String winner2(int n) {
        if (n % 5 == 0 || n % 5 == 2) {
            return "后手";
        } else {
            return "先手";
        }
    }

    /**
     * 定义一个数可以表示成若干连续正数和（数量>1）的数
     * 比如
     * 2+3=5
     * 3+4+5=12
     * 1不是，数量等于1
     * 2不是不是连续的
     * 给定N，返回是不是可以表示成若干连续正数和的数
     */
    //暴力
    public static boolean isContinuous(int n) {
        for (int i = 1; i <= n; i++) {//开头的数
            int sum = i;//总和
            for (int j = i + 1; j <= n; j++) {//后面的连续数
                if (sum + j > n) {//如果此时大于代表后面也一直大于，因此换头
                    break;
                }
                if (sum + j == n) {
                    return true;
                }
                //不相等且小于，累加
                sum += j;
            }
        }
        return false;
    }

    //规律
    public static boolean isContinuous1(int n) {
        if (n < 3) {
            return false;
        }
        return (n & (n - 1)) != 0;//n不是2的几次方
    }



    /**
     * 小虎去附近的商店买苹果，奸诈的商贩使用了捆绑交易，只提供6个每袋和8个每袋的
     * 包装包装不可拆分，可是小虎现在只想买恰好n个苹果，小虎想购买尽量少的袋数方便携带，如果不能
     * 购买恰好n个苹果，小虎将不会购买，输入一个正数n，表示小虎想购买的苹果，返回最小使用多少个袋子，如果无论如何都不能正好装下，返回-1
     */
    public static int minPackage(int n) {
        //贪心：先用最多的8个袋子尽可能装苹果
        int bag8 = n / 8;
        int bag6 = -1;
        //剩余
        int rest = n - bag8 * 8;
        while (bag8 >= 0 && rest < 24) {
            int restUse6=useBagBase6(rest);
            if (restUse6!=-1) {
                bag6=restUse6;
                break;
            }
            rest=n-(--bag8)*8;
        }
        return bag6==-1?-1:bag6+bag8;
    }
    public static int useBagBase6(int rest){
        return rest%6==0?rest/6:-1;
    }

    /**
     * 打表法
     * @param n
     * @return
     */
    public static int minPackage1(int n){
        if ((n&1)!=0)return -1;
        if (n<18){
            return n==0?0:(n==6||n==8)?1:(n==12||n==14||n==16)?2:-1;
        }
        return (n-18)/8+3;
    }

    /**
     * 两头牛吃草
     * 每次只能吃4的某次方的草，给定N，谁最先吃完谁赢
     * 两头牛都绝顶聪明
     */


}
