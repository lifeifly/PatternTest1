package com.lifly.pattern.calculator.牛牛;

import android.content.Intent;
import android.icu.text.IDNA;
import android.os.Build;

import androidx.annotation.RequiresApi;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Stack;
import java.util.TreeMap;

public class Sweep {

    /**
     * 有n个打包机器从左到右一字排开，上方有一个自动装置会抓取一批放物品到每个打包机上，放到每个机器上的这些物品数量有多有少，由于物品数量不相同，需要工人将每个机器上的
     * 物品进行移动从而到大物品相等才能打包。每个物品重量太大、每次只能搬一个物品进行移动，为了省力，只在相邻的机器上移动，请计算再搬动最小轮数前提下，使每个机器上的
     * 物品数量相等，如果不能使每个机器的物品数量相等，返回-1
     * 例如【1，0，5】表示3个机器，每个机器上分别有1.0.5个物品，经过这些轮后
     * 第一轮：1 0<-5 1 1 4 第二轮:1<-1<-4 2 1 3 第三轮:2 1<-3 2 2 2
     * 移动了3轮，每个物品数量相等，所以返回3
     *
     * @return
     */
    public static int loop(int[] arr) {
        if (arr == null || arr.length <= 1) {
            return 0;
        }
        int size = arr.length;
        int total = 0;
        for (int i = 0; i < arr.length; i++) {
            total += arr[i];
        }
        if (total % size != 0) {
            return -1;
        }

        int avg = total / size;
        int leftSum = 0;
        int ans = 0;
        for (int i = 0; i < size; i++) {
            //负数表示输入  整数表示输出
            int leftRest = leftSum - avg * i;
            int rightRest = (total - leftSum - arr[i]) - avg * (size - 1 - i);
            if (leftRest < 0 && rightRest < 0) {
                ans = Math.max(ans, Math.abs(leftRest) + Math.abs(rightRest));
            } else {
                ans = Math.max(ans, Math.max(Math.abs(leftRest), Math.abs(rightRest)));
            }
            leftSum += arr[i];
        }
        return ans;
    }

    /**
     * 矩阵转圈螺旋打印
     *
     * @return
     */
    public static void zigzigPrint(int[][] matrix) {
        if (matrix == null) return;
        //记录对角点
        int a = 0;
        int b = 0;
        int c = matrix.length - 1;
        int d = matrix[0].length - 1;
        while (a <= c && b <= d) {
            print(matrix, a++, b++, c--, d--);
        }
    }

    private static void print(int[][] matrix, int a, int b, int c, int d) {
        if (a == c) {
            //同行
            for (int i = b; i <= d; i++) {
                System.out.println(matrix[a][i]);
            }
        } else if (b == d) {
            //同列
            for (int i = a; i <= c; i++) {
                System.out.println(matrix[i][b]);
            }
        } else {
            //不同行不同列
            //先打印上部
            for (int i = b; i < d; i++) {
                System.out.println(matrix[a][i]);
            }
            //再打印右侧
            for (int i = a; i < c; i++) {
                System.out.println(matrix[i][d]);
            }
            //再打印下侧
            for (int i = d; i > b; i--) {
                System.out.println(matrix[c][i]);
            }
            //最后打印左侧
            for (int i = c; i > a; i--) {
                System.out.println(matrix[i][b]);
            }
        }
    }

    /**
     * 顺时针旋转正方形矩阵90度
     *
     * @param matrix
     */
    public static void rotateMatrix(int[][] matrix) {
        int a = 0;
        int b = 0;
        int c = matrix.length - 1;
        int d = matrix[0].length - 1;

        while (a < c) {
            rotate(matrix, a++, b++, c--, d--);
        }
    }

    /**
     * 对角点一圈一圈旋转
     *
     * @param matrix
     * @param a
     * @param b
     * @param c
     * @param d
     */
    private static void rotate(int[][] matrix, int a, int b, int c, int d) {
        for (int i = 0; i < c - a; i++) {
            System.out.println(i);
            int temp = matrix[a + i][d];
            //上侧到右侧
            matrix[a + i][d] = matrix[a][b + i];
            //左侧到上侧
            matrix[a][b + i] = matrix[c - i][b];
            //下侧到左侧
            matrix[c - i][b] = matrix[c][d - i];
            //右侧到下侧
            matrix[c][d - i] = temp;
        }
    }

    /**
     * 假设s和m初始化，s=“a”；m=s；
     * 再定义两种操作，第一种操作：
     * m=s；
     * s=s+s；
     * 第二种操作
     * s=s+m
     * 求最小的操作步骤数，可以将s拼接到长度等于n
     *
     * @return
     */
    public static int minOps(int n) {
        if (n < 2) return 0;
        if (isPrim(n)) {
            return n - 1;
        }
        //n不是质数,分解成质数串，每个质数都是k-1步，因此只需知道几个质数和质数和
        int[] divSumAndCount = divSumAndCount(n);
        return divSumAndCount[0] - divSumAndCount[1];
    }

    private static int[] divSumAndCount(int n) {
        int sum = 0;
        int count = 0;
        for (int i = 2; i <= n; i++) {
            while (n % i == 0) {
                sum += i;
                count++;
                n /= i;
            }
        }
        return new int[]{sum, count};
    }

    public static boolean isPrim(int n) {
        int i = 2;
        for (; i < n; i++) {
            if (n % i == 0) {
                return false;
            }
        }
        return true;
    }

    /**
     * 求出现最多的前k个字符串
     *
     * @param strs
     * @return
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    public static List<String> maxK(String[] strs, int k) {
        Map<String, Integer> appearTable = new HashMap<>();
        for (int i = 0; i < strs.length; i++) {
            if (!appearTable.containsKey(strs[i])) {
                appearTable.put(strs[i], 1);
            } else {
                appearTable.put(strs[i], appearTable.get(strs[i]));
            }
        }
        //大根堆
        PriorityQueue<K> ks = new PriorityQueue<>(new KMaxComparator());
        for (Map.Entry<String, Integer> entry : appearTable.entrySet()) {
            ks.add(new K(entry.getKey(), entry.getValue()));
        }
        List<String> res = new ArrayList<>();
        for (int i = 0; i < k; i++) {
            res.add(ks.poll().s);
        }
        return res;
    }

    public static class KMaxComparator implements Comparator<K> {

        @Override
        public int compare(K o1, K o2) {
            return o2.count - o1.count;
        }
    }

    public static class K {
        public String s;
        public int count;

        public K(String s, int count) {
            this.s = s;
            this.count = count;
        }
    }

    public static class Dynamic {
        //记录每个字符串出现的次数
        private HashMap<String, Node> appearTable;
        //数组，用于小根堆
        private Node[] heap;
        //记录字符串在堆中的位置
        private HashMap<Node, Integer> heapPosition;
        //记录当前的个数
        private int heapSize;

        public Dynamic(int size) {
            this.heapSize = 0;
            this.appearTable = new HashMap<>();
            this.heap = new Node[size];
            this.heapPosition = new HashMap<>();
        }

        public void add(String s) {
            Node curNode = null;
            int preIndex = -1;
            if (!appearTable.containsKey(s)) {
                curNode = new Node(s, 1);
                appearTable.put(s, curNode);
                heapPosition.put(curNode, -1);
            } else {
                curNode = appearTable.get(s);
                curNode.times++;
                preIndex = heapPosition.get(curNode);
            }
            if (preIndex == -1) {
                if (heapSize == heap.length) {
                    if (heap[0].times < curNode.times) {
                        heapPosition.put(heap[0], -1);
                        heapPosition.put(curNode, 0);
                        heap[0] = curNode;
                        heapify(0, heapSize);
                    }
                } else {
                    heapPosition.put(curNode, heapSize);
                    heap[heapSize] = curNode;
                    heapInsert(heapSize++);
                }
            } else {
                heapify(preIndex, heapSize);
            }
        }

        /**
         * 下沉
         *
         * @param cur
         * @param limit
         */
        private void heapify(int cur, int limit) {
            int left = cur * 2 + 1;
            while (left < limit) {
                int right = left + 1;
                int maxIndex = left;
                if (right < limit) {
                    maxIndex = heap[left].times < heap[right].times ? right : left;
                }
                if (heap[maxIndex].times < heap[cur].times) {
                    //交换位置
                    heapPosition.put(heap[maxIndex], cur);
                    heapPosition.put(heap[cur], maxIndex);
                    Node temp = heap[maxIndex];
                    heap[maxIndex] = heap[cur];
                    heap[cur] = temp;

                    cur = maxIndex;
                    left = cur * 2 + 1;
                } else {
                    break;
                }
            }
        }

        /**
         * 上浮
         */
        private void heapInsert(int cur) {
            int parent = (cur - 1) / 2;
            while (parent >= 0) {
                if (heap[parent].times > heap[cur].times) {
                    heapPosition.put(heap[parent], cur);
                    heapPosition.put(heap[cur], parent);
                    Node temp = heap[parent];
                    heap[parent] = heap[cur];
                    heap[cur] = temp;

                    cur = parent;
                    parent = (cur - 1) / 2;
                } else {
                    break;
                }
            }
        }

        public Node[] getResult() {
            return heap;
        }
    }

    public static class Node {
        public String str;
        public int times;

        public Node(String str, int times) {
            this.str = str;
            this.times = times;
        }
    }

    /**
     * 实现一种猫狗队列
     * add方法将cat和dog放入队列
     * 调用pollAll方法，将进入队列顺序依次弹出
     * 调用pollDog方法，将dog按进入队列顺序依次弹出
     * 调用pollCat方法，将cat按进入队列顺序依次弹出
     * 调用isEmpty方法，检查队列是否还有猫和狗的实例
     * 调用isDogEmpty方法，检查队列是否还有狗的实例
     * 调用isCatEmpty方法，检查队列是否还有猫的实例
     * 要求以上所以方法的时间复杂度都是O（1）的
     */
    public static class CatDogQueue {

    }

    /**
     * 实现一个特殊的栈，在实现栈的基本功能的基础上，在实现返回栈中最小元素的操作
     * 要求1.pop、push、getMin操作的时间复杂度都是O（1）
     * 2.设计的栈类型可以实现现成的栈结构
     */
    public static class EnqueStack {
        private Stack<Integer> data;
        private Stack<Integer> min;
    }

    /**
     * 用队列实现栈
     */
    public static class StackByQueue {
        private Queue<Integer> queue1;
        private Queue<Integer> queue2;

        public StackByQueue() {
            queue1 = new LinkedList<>();
            queue2 = new LinkedList<>();
        }

        public void push(int i) {
            if (isEmpty()) {
                queue1.add(i);
            } else {
                if (queue1.isEmpty()) {
                    queue2.add(i);
                } else {
                    queue1.add(i);
                }
            }
        }

        public int pop() {
            if (queue1.isEmpty() && queue2.isEmpty()) {
                throw new RuntimeException("stack is empty");
            }
            return dao();
        }

        public boolean isEmpty() {
            return queue1.isEmpty() && queue2.isEmpty();
        }

        public int poll() {
            int res = dao();
            if (queue1.isEmpty()) {
                queue2.add(res);
            } else {
                queue1.add(res);
            }
            return res;
        }

        private int dao() {
            if (queue1.isEmpty()) {
                while (queue2.size() > 1) {
                    queue1.add(queue2.poll());
                }
                return queue2.poll();
            } else {
                while (queue1.size() > 1) {
                    queue2.add(queue1.poll());
                }
                return queue1.poll();
            }
        }
    }

    /**
     * 用栈实现队列
     */
    public static class QueueByStack {
        private Stack<Integer> pushStack;
        private Stack<Integer> popStack;

        public QueueByStack() {
            pushStack = new Stack<>();
            popStack = new Stack<>();
        }

        public void push(int i) {
            pushStack.push(i);
            dao();
        }

        public int poll() {
            if (popStack.isEmpty() && pushStack.isEmpty()) {
                throw new RuntimeException("queue is empty");
            }
            dao();
            return popStack.pop();
        }

        public int peek() {
            if (popStack.isEmpty() && pushStack.isEmpty()) {
                throw new RuntimeException("queue is empty");
            }
            dao();
            return popStack.peek();
        }

        private void dao() {
            if (popStack.isEmpty()) {
                while (!pushStack.isEmpty()) {
                    popStack.push(pushStack.pop());
                }
            }
        }
    }

    /**
     * 给你一个二维数组matrix
     * 其中每个数都是正数，要求从左上角走到右下角，每一步只能向右或者向下，沿途经过的数字要累加起来，最后返回最小路径和
     */
    public static int zipMatrix(int[][] matrix) {
        int[] dp = new int[matrix[0].length];
        int sum = 0;
        for (int i = 0; i < matrix[0].length; i++) {
            sum += matrix[0][i];
            dp[i] = sum;
        }
        for (int i = 1; i < matrix.length; i++) {
            for (int j = 0; j < matrix[0].length; j++) {
                dp[j] = matrix[i][j] + Math.min(dp[j], (j - 1 >= 0 ? dp[j - 1] : 0));
            }
        }
        return dp[matrix[0].length - 1];
    }

    /**
     * 给定一个数组arr，已知所有的值都是非负的，将这个数组看作容器，请返回容器能装多少水
     * arr=[3,1,2,5,2,4]
     * 可以放下5格水
     *
     * @return
     */
    public static int water(int[] arr) {
        return 0;
    }

    /**
     * 给定一个数组arr长度为N，你可以把任意长度大于0且小于N的前缀作为左部分，剩下的作为右部分，但是每种划分下都有左部分的最大值和有部分的最大值，请返回最大的
     * 左部分最大值减去右部分的最大值的绝对值
     *
     * @return
     */
    public static int maxAbs(int[] arr) {
        return 0;
    }

    /**
     * 一个字符串str，可以将自己的前任意部分挪到后面形成的字符串叫做str的旋转词，给你字符串a和b，判断a和b是否互为旋转词
     *
     * @param a
     * @param b
     * @return
     */
    public static boolean isRotateStr(String a, String b) {
        //1.a+a
        String p = a + a;
        //2.判断b是否是p的子串
        return false;
    }

    /**
     * 给定数组arr，每个元素代表每台咖啡机泡一个杯咖啡的时间,给定N代表N个人喝咖啡
     * 给定a代表一个咖啡洗杯器洗一个杯子的时间只有一台
     * 给定b代表咖啡杯挥发干净的时间
     * 返回所有杯子洗完并且都喝完咖啡的至少的总时间
     *
     * @return
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    public static int washCoffee(int[] arr, int N, int a, int b) {
        //1.获取每个人喝到咖啡的时间，即每个杯子等待被洗的时间
        int[] waitWashArr = new int[N];
        PriorityQueue<AvaiableCoffee> queue = new PriorityQueue<>(new AvaiableCoffeeComparator());
        for (int i = 0; i < arr.length; i++) {
            queue.add(new AvaiableCoffee(0, arr[i]));
        }
        for (int i = 0; i < N; i++) {
            AvaiableCoffee avaiableCoffee = queue.poll();
            waitWashArr[i] = avaiableCoffee.avaiable + avaiableCoffee.make;
            avaiableCoffee.setAvaiable(waitWashArr[i]);
            queue.add(avaiableCoffee);
        }
        //每个杯子等待被洗是一个递增的数组
        return wash(waitWashArr, N, 0, 0, a, b);
    }

    /**
     * @param wash     每个杯子等待被洗的时间
     * @param N        总杯子数量
     * @param cur      当前需要洗的位置
     * @param washLine 洗杯机的可用时间
     * @param a        洗杯机洗一个杯子的时间
     * @param b        自己挥发的时间
     * @return
     */
    public static int wash(int[] wash, int N, int cur, int washLine, int a, int b) {
        if (cur == N - 1) {//来到最后一个杯子了，取当前杯子用洗杯机洗和选择挥发的最短的时间
            return Math.min(Math.max(washLine, wash[cur]) + a, wash[cur] + b);
        }
        //用咖啡机洗
        int wash1 = Math.max(washLine, wash[cur]) + a;
        //后面洗完的时间
        int next1 = wash(wash, N, cur + 1, wash1, a, b);
        //取最大的那个
        int p1 = Math.max(wash1, next1);

        //自己挥发
        int dry1 = wash[cur] + b;
        //后面洗完的时间
        int next2 = wash(wash, N, cur + 1, washLine, a, b);
        //取最大的那个
        int p2 = Math.max(dry1, next2);

        return Math.min(p1, p2);
    }

    public static class AvaiableCoffeeComparator implements Comparator<AvaiableCoffee> {

        @Override
        public int compare(AvaiableCoffee o1, AvaiableCoffee o2) {
            return (o1.avaiable + o1.make) - (o2.avaiable + o2.make);
        }
    }

    public static class AvaiableCoffee {
        public int avaiable;
        public int make;

        public AvaiableCoffee(int avaiable, int make) {
            this.avaiable = avaiable;
            this.make = make;
        }

        public int getAvaiable() {
            return avaiable;
        }

        public void setAvaiable(int avaiable) {
            this.avaiable = avaiable;
        }

        public int getMake() {
            return make;
        }

        public void setMake(int make) {
            this.make = make;
        }
    }

    /**
     * 喝咖啡洗咖啡杯动态规划
     *
     * @param arr
     * @param N
     * @return
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    public static int washCoffee1(int[] arr, int N, int a, int b) {
        //1.获取每个人喝到咖啡的时间，即每个杯子等待被洗的时间
        int[] waitWashArr = new int[N];
        PriorityQueue<AvaiableCoffee> queue = new PriorityQueue<>(new AvaiableCoffeeComparator());
        for (int i = 0; i < arr.length; i++) {
            queue.add(new AvaiableCoffee(0, arr[i]));
        }
        for (int i = 0; i < N; i++) {
            AvaiableCoffee avaiableCoffee = queue.poll();
            waitWashArr[i] = avaiableCoffee.avaiable + avaiableCoffee.make;
            avaiableCoffee.setAvaiable(waitWashArr[i]);
            queue.add(avaiableCoffee);
        }
        //每个杯子等待被洗是一个递增的数组
        return wash1(waitWashArr, 0, a, b);
    }

    public static int wash1(int[] arr, int N, int a, int b) {
        //获取washline最大值
        int washLineMax = getMaxWashLine(0, N, 0, arr, a, b);
        int[][] dp = new int[N][washLineMax + 1];//washLine包括0
        //最后一行basecase
        for (int i = 0; i < washLineMax; i++) {
            dp[N - 1][i] = Math.min(Math.max(i, arr[N - 1]) + a, arr[N - 1] + b);
        }
        for (int i = N - 2; i >= 0; i--) {
            for (int j = 0; j < washLineMax; j++) {
                //用咖啡机洗
                int wash1 = Math.max(j, arr[i]) + a;
                //后面洗完的时间
                int next1 = dp[i + 1][wash1];
                //取最大的那个
                int p1 = Math.max(wash1, next1);

                //自己挥发
                int dry1 = arr[i] + b;
                //后面洗完的时间
                int next2 = dp[i + 1][dry1];
                //取最大的那个
                int p2 = Math.max(dry1, next2);

                dp[i][j] = Math.min(p1, p2);
            }
        }
        return dp[0][0];
    }

    private static int getMaxWashLine(int cur, int N, int washLine, int[] wash, int a, int b) {
        if (cur == N - 1) {//来到最后一个杯子了，取当前杯子用洗杯机洗和选择挥发的最短的时间
            return Math.min(Math.max(washLine, wash[cur]) + a, wash[cur] + b);
        }
        //用咖啡机洗
        int wash1 = Math.max(washLine, wash[cur]) + a;
        //后面洗完的时间
        int next1 = wash(wash, N, cur + 1, wash1, a, b);
        //取最大的那个
        int p1 = Math.max(wash1, next1);


        return p1;
    }

    /**
     * 给定一个数组arr，如果通过调整可以做到arr中任意两个相邻的数字相乘是4的倍数，返回true、如果不能返回false
     *
     * @param arr
     * @return
     */
    public static boolean dis(int[] arr) {
        int a = 0;//奇数的个数
        int b = 0;//只有一个2因子的数
        int c = 0;//包含4因子的数
        for (int i = 0; i < arr.length; i++) {
            if ((arr[i] & 1) != 0) {
                a++;
            } else if (arr[i] % 4 == 0) {
                c++;
            } else {
                b++;
            }
        }
        if (b == 0) {
            //需要包含因子4的数量最少的摆放方式是 奇4奇
            if (a == 0 && c > 0) {
                return true;
            } else if (a == 1 && c >= 1) {
                return true;
            } else if (a > 1 && c >= a - 1) {
                return true;
            } else {
                return false;
            }
        } else {
            //需要包含因子4的数量最少的摆放方式是 2222224奇4奇
            if (a == 0 && c >= 0) {
                return true;
            } else if (c >= a) {
                return true;
            } else {
                return false;
            }
        }
    }

    /**
     * 字符串由'0'和'1'两种字符构成
     * 当字符串长度为1时，所有可能的字符串为”0“和"1"
     * 当字符串长度为2时，所有可能的字符串为”00“、”01“、”10“、”11“
     * 当字符串长度为3时，所有可能的字符串为”000“、”001“、”010“、”011“、”100“、”101“、”110“、”111“
     * 如果某一个字符串中，只要是出现‘0’的位置，左边就靠着”1“，这样的字符串叫做达标字符串
     * 给定一个正数N，返回所有长度为N的字符串中，达标字符串的数量
     * 比如，N=3，返回3，因为只有”101“、”110“、”111“达标
     *
     * @return
     */
    public static int passStr() {
        return 0;
    }

    /**
     * Log N时间求斐波那契数列
     *
     * @param n
     * @return
     */
    public static int fi(int n) {
        if (n < 1) {
            return 0;
        }
        if (n == 1 || n == 2) {
            return 1;
        }
        int[][] base = {{1, 1}, {1, 0}};
        int[][] res = matrixPower(base, n - 2);
        return res[0][0] + res[1][0];
    }

    private static int[][] matrixPower(int[][] base, int n) {
        int[][] res = new int[base.length][base[0].length];
        for (int i = 0; i < res.length; i++) {
            res[i][i] = 1;
        }
        int[][] temp = base;
        for (; n != 0; n >>= 1) {
            if ((n & 1) != 0) {
                res = muliMatrix(res, temp);
            }
            temp = muliMatrix(temp, temp);
        }
        return new int[0][];
    }

    private static int[][] muliMatrix(int[][] m1, int[][] m2) {
        int[][] res = new int[m1.length][m2[0].length];
        for (int i = 0; i < m1.length; i++) {
            for (int j = 0; j < m2[0].length; j++) {
                for (int k = 0; k < m2.length; k++) {
                    res[i][j] += m1[j][k] * m2[k][j];
                }
            }
        }
        return res;
    }

    /**
     * 在迷迷糊糊的大草原上，小红捡到了n根木棍，第i根木棍的长度为i，
     * 小红现在很开心，想选出其中的三根木棍组成美丽的三角形
     * 但是小明想捉弄小红，想去掉一些木棍，使得小红任意三根都不能组成三角形
     * 请问小明最少去掉多少根木棍
     *
     * @return
     */
    public static int manySticks(int n) {
        //将每个满足i-2 + i-1 <=i的木棍留下，就是最多剩下的不能构成三角形的根数，符合斐波那契数列
        return 0;
    }

    /**
     * 牛牛准备参加学校组织的春游，出发前牛牛准备往背包里装一些零食，背包容量是W，牛牛家里一共n袋，第i袋体积为v[i],
     * 牛牛想知道在总体积不超过背包容量的情况下，他一共有多少种零食的方法（总体积为0也是一种方法）
     *
     * @return
     */
    public static int mangPut(int[] v, int w) {
        int n = v.length;
        long sum = 0;
        for (int i = 0; i < n; i++) {
            sum += v[i];
        }
        if (sum < w) {
            //总数小于w，每个元素都有要或不要两种情况
            return (int) Math.pow(2, n);
        } else {
            return process(v, w, 0);
        }
    }

    /**
     * 当前来到第cur个元素，可用空间为w
     *
     * @param v
     * @param w
     * @param cur
     * @return
     */
    private static int process(int[] v, int w, int cur) {
        if (w < 0) {
            //本次尝试无效
            return 0;
        }
        int res = 0;
        if (cur == v.length) {
            //全选完了容量还足够，生成一种方案
            res = 1;
        } else {
            //要当前元素
            res += process(v, w - v[cur], cur + 1);
            //不要当前元素
            res += process(v, w, cur + 1);
        }
        return res;
    }

    public static int manyPutDp(int[] v, int w) {
        int n = v.length;
        long sum = 0;
        for (int i = 0; i < n; i++) {
            sum += v[i];
        }
        if (sum < w) {
            //总数小于w，每个元素都有要或不要两种情况
            return (int) Math.pow(2, n);
        } else {
            //位置为行，剩余空间为列
            int[][] dp = new int[n + 1][w + 1];
            for (int i = 0; i <= w; i++) {
                dp[n][i] = 1;
            }
            for (int i = n - 1; i >= 0; i--) {
                for (int j = 0; j <= w; j++) {
                    int res = 0;
                    if (j - v[i] >= 0) {
                        res += dp[i + 1][j - v[i]];
                    }
                    res += dp[i + 1][j];
                    dp[i][j] = res;
                }
            }
            for (int i = 0; i < n; i++) {
                System.out.println(Arrays.toString(dp[i]));
            }
            return dp[0][w];
        }
    }


    /**
     * 记忆化搜索
     */
    public static int manyPut1(int[] v, int w) {
        int n = v.length;
        long sum = 0;
        for (int i = 0; i < n; i++) {
            sum += v[i];
        }
        if (sum < w) {
            //总数小于w，每个元素都有要或不要两种情况
            return (int) Math.pow(2, n);
        } else {
            //key：元素和 value：元素和为key的组合方式
            HashMap<Integer, Integer> table = new HashMap<>();
            table.put(0, 1);
            return process1(v, w, 0, table);
        }
    }

    private static int process1(int[] v, int w, int cur, HashMap<Integer, Integer> table) {
        if (w < 0) {
            //本次尝试无效
            return 0;
        }
        int res = 0;
        if (cur == v.length) {
            //全选完了容量还足够，生成一种方案
            if (!table.containsKey(w)) {
                table.put(w, 1);
            }
        } else {
            if (!table.containsKey(w)) {
                //要当前元素
                res += process1(v, w - v[cur], cur + 1, table);
                //不要当前元素
                res += process1(v, w, cur + 1, table);
                table.put(w, res);
            }
        }
        return table.get(w);
    }

    /**
     * 为了找到自己满意的工作，牛牛收集了每种工作的难度和报酬，牛牛选工作的标准是在难度不超过自身能力值的情况下，牛牛选择最高的报酬的工作。
     * 在牛牛选定自己的工作后，牛牛的小伙伴们来找牛牛帮忙选工作，牛牛依然使用自己的标准来帮助小伙伴们。牛牛的小伙伴太多了，于是把这个任务交给你。
     * <p>
     * class Job{
     * public int money;
     * public int hard;
     * <p>
     * public Job(int money,int hard){
     * this.money=money;
     * this.hard=hard;
     * }
     * }
     * 给定Job类型的数组表示所有的工作。给定一个int类型数组arr，表示每个小伙伴的能力
     * 返回int类型数组，表示每个小伙伴按照牛牛的标准选定工作后的报酬
     *
     * @return
     */
    public static int[] everyProfit(Job[] jobs, int[] hards) {
        //排序
        Arrays.sort(jobs, new JobComparator());
        //有序表
        TreeMap<Integer, Integer> map = new TreeMap<>();
        map.put(jobs[0].hard, jobs[0].money);
        Job pre = jobs[0];
        //按难度进行分组，只要难度增加报酬增加的才放入有序表，因为定义的比较器，所以一般每组的第一个就是所要的
        for (int i = 0; i < jobs.length; i++) {
            if (jobs[i].hard != pre.hard && jobs[i].money > pre.money) {
                map.put(jobs[i].hard, jobs[i].money);
                pre = jobs[i];
            }
        }
        //找到每个力所能及的工作就是最大的报酬了
        int[] profits = new int[hards.length];
        for (int i = 0; i < hards.length; i++) {
            //返回小于或等于指定key的entry的key，不存在返回null
            Integer key = map.floorKey(hards[i]);
            profits[i] = key == null ? 0 : key;
        }
        return profits;
    }

    /**
     * Job比较器先按难度从小到大再按报酬从大到小
     */
    public static class JobComparator implements Comparator<Job> {

        @Override
        public int compare(Job o1, Job o2) {
            return (o1.hard - o2.hard) != 0 ? (o1.hard - o2.hard) : (o2.money - o1.money);
        }
    }

    class Job {

        public int money;

        public int hard;


        public Job(int money, int hard) {
            this.money = money;
            this.hard = hard;
        }

    }

    /**
     * 给定字符串arr，如果符合日常人们书写的整数的形式，返回这个整数，不符合或者越界返回-1
     * 条件：1.除了数字只能包含“-”号，
     * 2.如果有“-”且只能存在于开头，开头数字不为0
     * 3.如果开头是0，后续必须无数字
     *
     * @param str
     * @return
     */
    public static int passWriteStr(String str) {
        if (str == null || str.equals("")) return 0;

        char[] chr = str.toCharArray();
        if (!isValid(chr)) {
            throw new RuntimeException("can not convert");
        }
        boolean neg = chr[0] == '-';
        int minq = Integer.MIN_VALUE / 10;
        int minr = Integer.MIN_VALUE % 10;
        int res = 0;
        int cur = 0;
        for (int i = neg ? 1 : 0; i < chr.length; i++) {
            //判断是否越界
            cur = '0' - chr[i];
            if ((res < minq) || (res == minq && (cur) < minr)) {
                throw new RuntimeException("can not convert");
            }
            res = res * 10 + cur;
        }
        //最后判断需要取负时是否越界
        if (res == Integer.MIN_VALUE && !neg) {
            throw new RuntimeException("can not convert");
        }
        return neg ? res : -res;
    }

    /**
     * 检查某一个字符，是否符合日常书写
     *
     * @return
     */
    public static boolean isValid(char[] str) {
        //之前三个判断枚举第一个位置一定合法
        if (str[0] != '-' && (str[0] < '0' || str[0] > '9')) {
            return false;
        }
        if (str[0] == '-' && (str.length == 1 || str[1] == '0')) {
            return false;
        }
        if (str[0] == '0' && str.length > 1) {
            return false;
        }

        //之后从1位置开始判断
        for (int i = 1; i < str.length; i++) {
            if (str[i] < '0' || str[i] > '9') {
                return false;
            }
        }
        return true;
    }

    /**
     * 给你一个字符串类型数组arr，
     * String[] arr={"b\\cst“，”d\\“，”a\\d\\e“,"a\\b\\c"};
     * 你把这些路径中包含的目录结构给画出来，子目录直接列在父目录下面，并比父目录向右前进两格
     * a
     * b
     * c
     * d
     * e
     * b
     * cst
     * d
     * <p>
     * 同一级需要按字母顺序排列，不能乱
     */
    public static void printArr(String[] arr) {
        if (arr == null || arr.length == 0) {
            System.out.print("");
        }
        //将字符串数组构建成有序表
        Node1 head = generatorNode1(arr);
        //图的深度优先遍历
        depthPrintln(head, 0);
    }

    private static void depthPrintln(Node1 head, int depth) {
        if (depth != 0) {
            System.out.println(getSpace(depth) + head.name);
        }
        for (Node1 node1 : head.nextMap.values()) {
            depthPrintln(node1, depth + 1);
        }
    }

    private static String getSpace(int depth) {
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i < depth; i++) {
            sb.append("  ");
        }
        return sb.toString();
    }

    private static Node1 generatorNode1(String[] arr) {
        Node1 head = new Node1("", new TreeMap<>());

        for (int i = 0; i < arr.length; i++) {
            String[] strArr = arr[i].split("\\\\");
            Node1 cur = head;
            for (int j = 0; j < strArr.length; j++) {
                if (!cur.nextMap.containsKey(strArr[j])) {
                    cur.nextMap.put(strArr[j], new Node1(strArr[j], new TreeMap<>()));
                }
                cur = cur.nextMap.get(strArr[j]);
            }
        }
        return head;
    }

    /**
     * 前缀树
     */
    public static class Node1 {
        public String name;
        //记录下一个节点，按字典序排序所有用有序表
        public TreeMap<String, Node1> nextMap;

        public Node1(String name, TreeMap<String, Node1> nextMap) {
            this.name = name;
            this.nextMap = nextMap;
        }
    }

    /**
     * 双向链表节点的结构和二叉树的结构时一样的，如果你把last认为是left，next认为是right的话
     * 给定一个搜索二叉树的头节点head，请转化成一条有序的双向链表，并返回链表的头节点
     *
     * @return
     */
    public static Node2 returnHead(Node2 head) {
        if (head == null) {
            return null;
        }
        return process2(head).start;
    }

    public static Info process2(Node2 head) {
        if (head == null) {
            return new Info(null, null);
        }
        Info leftHeadEnd = process2(head.left);
        Info rightHeadEnd = process2(head.right);

        if (leftHeadEnd.end != null) {
            leftHeadEnd.end.right = head;
        }
        head.left = leftHeadEnd.end;
        head.right = rightHeadEnd.start;
        if (rightHeadEnd != null) {
            rightHeadEnd.start.left = head;
        }

        return new Info(leftHeadEnd != null ? leftHeadEnd.start : head, rightHeadEnd != null ? rightHeadEnd.end : head);
    }


    public static class Info {
        public Node2 start;
        public Node2 end;

        public Info(Node2 start, Node2 end) {
            this.start = start;
            this.end = end;
        }
    }

    public static class Node2 {
        public int value;
        public Node2 left;
        public Node2 right;

        public Node2(int value) {
            this.value = value;
        }
    }

    /**
     * 找到一颗二叉树中，最大的搜索二叉树，返回最大搜索二叉树的头节点
     *
     * @return
     */
    public static Node2 maxSearchTreeNodeCount(Node2 head) {
        if (head == null) {
            return null;
        }
        return process3(head).head;
    }

    private static Info1 process3(Node2 head) {
        if (head == null) {
            return null;
        }
        Info1 leftInfo = process3(head.left);
        Info1 rightInfo = process3(head.right);

        boolean isSearch = false;
        int count = 0;
        int max = head.value;
        int min = head.value;
        Node2 cur = null;
        if (leftInfo != null) {
            max = Math.max(max, leftInfo.max);
            min = Math.min(min, leftInfo.min);
        }
        if (rightInfo != null) {
            min = Math.min(min, rightInfo.min);
            max = Math.max(max, rightInfo.max);
        }
        if (leftInfo != null) {
            count = leftInfo.count;
            cur = leftInfo.head;
        }
        if (rightInfo != null && rightInfo.count > count) {
            count = rightInfo.count;
            cur = rightInfo.head;
        }

        if ((leftInfo == null || leftInfo.isSearch) &&//确保左子树是搜索二叉树
                (rightInfo == null || rightInfo.isSearch//确保右子树是搜索二叉树
                )) {
            if ((leftInfo == null || leftInfo.max < head.value) && (rightInfo == null || rightInfo.min > head.value)) {
                isSearch = true;
                count = (leftInfo == null ? 0 : leftInfo.count) + 1 + (rightInfo == null ? 0 : rightInfo.count);
                cur = head;
            }
        }

        return new Info1(isSearch, max, min, count, cur);
    }

    public static class Info1 {
        public boolean isSearch;
        public int max;
        public int min;
        public int count;
        public Node2 head;

        public Info1(boolean isSearch, int max, int min, int count, Node2 head) {
            this.isSearch = isSearch;
            this.max = max;
            this.min = min;
            this.count = count;
            this.head = head;
        }

        public Info1(boolean isSearch, int max, int min, int count) {
            this.isSearch = isSearch;
            this.max = max;
            this.min = min;
            this.count = count;
        }
    }

    /**
     * XX...XX.....XXX
     * X代表墙，不能放路灯
     * .代表住户，可以放路灯，一个路灯可以照亮当前位置和左右两个位置
     * 问至少多少个路灯可以把所有的住户照亮
     *
     * @return
     */
    public static int minLight(String str) {
        if (str == null || str.length() == 0) {
            return 0;
        }
        char[] chs = str.toCharArray();
        int ans = 0;
        int i = 0;
        while (i < chs.length) {
            if (chs[i] == 'X') {
                i++;
            } else {
                if (i + 1 >= chs.length) {
                    ans++;
                    break;
                } else if (chs[i + 1] != '.') {
                    ans++;
                    i = i + 2;
                } else {
                    ans++;
                    i = i + 3;
                }
            }
        }
        return ans;
    }

    /**
     * 已知一颗二叉树没有重复节点，并且给定了这棵树的中序遍历和先序遍历数组，返回后序遍历数组
     * int[] pre={1,2,3,4,5,3,6,7};
     * int[] mid={4,2,5,1,6,3,7};
     * <p>
     * {4,5,2,6,7,3,1}
     *
     * @return
     */
    public static void afterArr(int[] pre, int[] mid) {
        int n = pre.length;
        int[] pos = new int[n];
        set(pre, 0, n - 1, mid, 0, n - 1, pos, 0, n - 1);
        System.out.println(Arrays.toString(pos));
    }

    private static void set(int[] pre, int prei, int prej, int[] mid, int midi, int midj, int[] pos, int posi, int posj) {
        if (prei > prej) {
            return;
        }
        if (prei == prej) {
            pos[posj] = pre[prei];
            return;
        }
        pos[posj] = pre[prei];
        //找到prei在中序遍历数组德位置
        int i = midi;
        for (; i <= midj; i++) {
            if (pre[prei] == mid[i]) {
                break;
            }
        }
        //分别去构建左子树和右子树
        set(pre, prei + 1, prei + i - midi, mid, midi, i - 1, pos, posi, posi + i - midi);
        set(pre, prei + i - midi + 1, prej, mid, i + 1, midj, pos, posi + i - midi + 1, posj - 1);
    }



    public static void main(String[] args) {
        String[] s = {"a\\b\\c", "a\\d\\e", "b\\c\\e"};
        printArr(s);
    }

}

