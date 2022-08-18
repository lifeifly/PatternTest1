package com.lifly.pattern.calculator.正则表达式;

public class Regular {
    //字符匹配符

    /**
     * []表示可接受的字符列表中的任意一个
     * [^]表示不可接收的字符列表中的任意一个
     * -连字符，A-Z任意单个大写字母
     * . 匹配除\n以外的任何单个字符
     * \\d匹配单个数字
     * \\D匹配单个非数字字符相当于[^0-9]
     * \\w匹配单个数字、大小写字母字符和下划线
     * \\W匹配单个非数字、下划线、大小写字母字符相当于[^0-9a-zA-Z]
     * \\s匹配任何空白字符（空格，制表符等）
     * \\S匹配任何非空白字符
     * \\.匹配.
     */
    public static String test1(String txt) {
        String regex = "[a-z]";
        return "";
    }

    /**
     * (?i)abc表示abc都不区分大小写
     * a(?i)bc表示bc都不区分大小写
     * a((?i)b)c表示b都不区分大小写
     *
     * Pattern.compile(Pattern.CASE_INSENSITIVE);
     */

    //选择匹配符
    /**
     * |匹配“|”之前或之后 的表达式，查找顺序不受影响，一直从前往后匹配
     */
    //限定符，限定出现多少次
    /**
     * *指定字符重复0次或n次
     * +指定字符出现1次或n次
     * ？指定字符重复0次或1次，如果前面跟随其它限定符，那么就是非贪婪匹配
     * {n}指定字符出现n次
     * {n,}指定至少出现n次
     * {n，m}指定至少n个但不多余m个匹配，默认贪婪匹配
     */

    //定位符
    /**
     * ^指定字符串起始字符
     * $指定字符串结束字符
     * \\b匹配目标字符串所在边界的字符串，边界指子串间有空格或者目标字符串的结束位置
     * \B匹配目标的非边界字符串
     */

    //分组
    /**
     * 将整个匹配串再次加工为各组
     * （pattern）非命名捕获，捕获匹配的字符串，编号为零的第一个捕获是右整个正则表达式模式匹配的文本，其它捕获结果则根据左括号的顺序从1开始自动编号
     * （?<name>pattern）命名捕获，将匹配的子字符串捕获到一个组名称或编号名称中，用于name的字符串不能包含任何表达符号，并且不能以数组开头，开始使用单引号替代尖括号，例如（？‘name’）
     */

    public static String test2(String txt){
        String content="hanshunping s7789 nn1189han";
        String regStr="(\\d\\d)(\\d\\d)";
        String regStr1="(?<g1>\\d\\d)(\\d\\d)";
        return "";
    }
    //特别分组，非捕获分组不会存储分组
    /**
     * （？：pattern）匹配pattern但不捕获该匹配的子表达式，但不捕获该匹配的子表达式，即它是一个非捕获匹配，“industr（？：y|ies）”匹配industry或industries
     * （？=pattern）他是一个非捕获匹配，例如“Windows（？=95|98|NT|2000）”匹配“Windows2000”中的Windows
     * （？！pattern）他是一个非捕获匹配，例如“Windows（？！95|98|NT|2000）”匹配“Windows3.1”中的Windows
     */
}
