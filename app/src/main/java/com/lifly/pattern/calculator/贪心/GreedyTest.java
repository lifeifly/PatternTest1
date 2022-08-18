package com.lifly.pattern.calculator.贪心;

import android.os.Build;
import android.widget.ProgressBar;

import androidx.annotation.RequiresApi;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Random;

public class GreedyTest {

    /**
     * 一些项目要占用一个会议室宣讲，会议室不能同时容纳两个项目宣讲
     * 给你每个项目开始和结束时间
     * 要求会议室要进行的宣讲场次最多
     * 返回最多的宣讲场次
     */
    //暴力
    public static int maxProgram(Program[] programs) {
        if (programs == null || programs.length == 0) {
            return 0;
        }
        return process1(programs, 0, 0);
    }

    //还剩的会议放到programs中,done之前安排了多少，timeLine目前的时间点是多少
    private static int process1(Program[] programs, int done, int timeLine) {
        if (programs.length == 0) return done;
        int max = done;
        //还有会议可以安排
        for (int i = 0; i < programs.length; i++) {
            if (programs[i].start >= timeLine) {
                //这个会议的开始时间在当前时间点之后，可以安排
                //生成新数组继续往下安排
                Program[] n = copyButExcept(programs, i);
                //不同类型的最大值取最大
                max = Math.max(max, process1(n, done + 1, programs[i].end));
            }
        }
        return max;
    }

    /**
     * 删除数组中的元素并返回
     */
    public static Program[] copyButExcept(Program[] programs, int i) {
        Program[] ans = new Program[programs.length - 1];
        int index = 0;
        for (int j = 0; j < programs.length; j++) {
            if (j != i) {
                ans[index++] = programs[j];
            }
        }
        return ans;
    }

    //贪心：先安排结束时间最短的
    public static int bestGreedyProgram(Program[] programs) {
        //先定义一个比较器以结束时间早的排前面，进行排序
        Arrays.sort(programs, new ProgramComparator());
        //记录当前的时间点
        int timeLine = 0;
        //记录会议个数
        int count = 0;
        //从前往后安排会议
        for (int i = 0; i < programs.length; i++) {
            if (programs[i].start >= timeLine) {
                //可以安排
                count++;
                //时间点后推
                timeLine = programs[i].end;
            }
        }
        return count;
    }

    public static class ProgramComparator implements Comparator<Program> {

        @Override
        public int compare(Program o1, Program o2) {
            return o1.end - o2.end;
        }
    }

    public static class Program {
        public int start;
        public int end;

        public Program(int start, int end) {
            this.start = start;
            this.end = end;
        }
    }


    /**
     * 一个字符串由X和.组成，X表示墙，.代表住户，只能选择在.的位置放灯，一个灯可以照亮前一个、当前、后一个三个位置
     * 怎么放置灯最少，且将所有的住户照亮
     */
    //暴力
    public static int voilent1(String str) {
        if (str == null) {
            return 0;
        }
        return process2(str.toCharArray(), 0, new HashSet<Integer>());
    }

    /**
     * 递归暴力尝试获取最优解
     *
     * @param load   路
     * @param index  当前来到那个位置
     * @param lights 存放每种情况的灯的位置
     * @return
     */
    private static int process2(char[] load, int index, HashSet<Integer> lights) {
        if (index == load.length) {//来到最后一个节点时
            //校验结果的正确性,一一检查每个住户有没有照亮他的灯
            for (int i = 0; i < load.length; i++) {
                //前一个位置、当前位置、后一个位置都没有灯，此解无效
                if (!lights.contains(i - 1) && !lights.contains(i) && !lights.contains(i + 1)) {
                    return Integer.MAX_VALUE;
                }
            }
            //说明全部照亮
            return lights.size();
        } else {
            //不管是不是墙，当前没放灯时的解的灯个数
            int no = process2(load, index + 1, lights);
            //当前如果放灯的数量，先置为最大值，为了放置当前是墙，导致结果不符
            int yes = Integer.MAX_VALUE;
            if (load[index] == '.') {
                //说明是住户可以放灯
                //灯放进去
                lights.add(index);
                //交给下一位置去处理
                yes = process2(load, index + 1, lights);
                //处理完后，恢复集合,防止递归到另一种可能性时影响其它的结果
                lights.remove(index);
            }
            return Math.min(no, yes);
        }
    }

    //贪心
    public static int greedy2(String load) {
        char[] chs = load.toCharArray();
        //记录当前位置
        int index = 0;
        //记录数量
        int lights = 0;
        while (index < chs.length) {
            if (chs[index] == 'X') {
                //说明当前是墙，不需要放灯，直接去下一个位置
                index++;
            } else {
                //当前是.住户，必须放一个灯
                lights++;
                //根据下一个节点判断灯放哪
                if (chs[index + 1] == 'X') {
                    //下一个位置是墙，直接越过，不用考虑，灯就放在当前位置
                    index = index + 2;
                } else {
                    //当前位置是住户，把灯放在这个位置，即中间位置，则下一个位置即使是住户也可以照亮，直接越过
                    index = index + 3;
                }
            }
        }
        return lights;
    }

    /**
     * 一条金条切成两半，需要花费和长度数值一样的铜板
     * 给定数组10，20，30，代表三个人按此数量分金条，总长度为60
     * 怎么确定最小的代价去切割金条
     */
    //暴力
    public static int voilent3(int[] arr) {
        if (arr == null || arr.length == 0) {
            return 0;
        }
        //暴力枚举任何两段合在一起的后续
        int a = process3(arr, 0);

        return a;
    }

    /**
     * @param arr  剩余需要切分的长度
     * @param cost 之前的花费
     * @return 总花费
     */
    private static int process3(int[] arr, int cost) {
        if (arr.length == 1) {
            return cost;
        }
        int ans = Integer.MAX_VALUE;
        for (int i = 0; i < arr.length; i++) {
            for (int j = i + 1; j < arr.length; j++) {
                ans = Math.min(ans, process3(copyButExcept3(arr, i, j), cost + arr[i] + arr[j]));
            }
        }
        return ans;
    }

    private static int[] copyButExcept3(int[] arr, int i, int k) {
        int[] res = new int[arr.length - 1];
        int index = 0;
        for (int j = 0; j < arr.length; j++) {
            if (j != i && k != j) {
                res[index++] = arr[j];
            }
        }
        res[index] = arr[i] + arr[k];
        return res;
    }

    //贪心，只先切大的那个
    public static int greedy3(int[] arr) {
        if (arr == null || arr.length <= 1) {
            return 0;
        }
        //排序
        Arrays.sort(arr);
        int cost = 0;
        for (int i = arr.length - 1; i > 0; i--) {
            //计算花费
            for (int j = 0; j <= i; j++) {
                cost += arr[j];
            }
        }
        return cost;
    }

    //贪心，哈夫曼树
    public static int greedy31(int[] arr) {
        PriorityQueue<Integer> pQ = new PriorityQueue<>();
        for (int i = 0; i < arr.length; i++) {
            pQ.add(arr[i]);
        }
        int sum = 0;
        int cur = 0;
        while (pQ.size() > 1) {
            cur = pQ.poll() + pQ.poll();
            sum += cur;
            pQ.add(cur);
        }
        return sum;
    }

    /**
     * 给你一些项目projects，初始资金为w，所做项目个数为k，只能同时做一个项目，怎么保证在K个项目中利润最大
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    public static int findMaximizedCapital(int k, int w, int[] profits, int[] capital) {
        //创建花费的小跟堆
        PriorityQueue<Project> costQueue=new PriorityQueue<>(new MinCostComparator());
        //创建利润的大根堆
        PriorityQueue<Project> profitQueue=new PriorityQueue<>(new MaxProfitComparator());
        //先将所有的项目加入小根堆
        for (int i = 0; i < profits.length; i++) {
            costQueue.add(new Project(profits[i],capital[i]));
        }
        //根据项目个数限制
        for (int i = 0; i < k; i++) {
            //依次根据自己的资金来将自己可以做的项目加入到大根堆
            while (!costQueue.isEmpty()&&costQueue.peek().c<=w){
                profitQueue.add(costQueue.poll());
            }
            if (profitQueue.isEmpty()){
                //说明自己当前一个项目也做不了了，不用继续执行了
                return w;
            }
            //然后弹出一个目前自己能做的且利润最大的项目来做
            Project project=profitQueue.poll();
            //做完后资金累加
            w+=project.p;
        }
        return w;
    }

    //根据利润的大比较器
    public static class MaxProfitComparator implements Comparator<Project> {

        @Override
        public int compare(Project o1, Project o2) {
            return o2.p - o1.p;
        }
    }

    //根据花费的小比较器
    public static class MinCostComparator implements Comparator<Project> {

        @Override
        public int compare(Project o1, Project o2) {
            return o1.c - o2.c;
        }
    }

    public static class Project {
        public int p;
        public int c;

        public Project(int p, int c) {
            this.p = p;
            this.c = c;
        }
    }

    public static void main(String[] args) {
        List<Integer> list=new ArrayList<>();

        System.out.println(list.get(0));
    }


    public static int[] generator(int max, int maxSize) {
        Random random = new Random();
        int[] arr = new int[random.nextInt(maxSize) + 1];
        for (int i = 0; i < arr.length; i++) {
            arr[i] = random.nextInt(max) + 1;
        }
        return arr;
    }
}
