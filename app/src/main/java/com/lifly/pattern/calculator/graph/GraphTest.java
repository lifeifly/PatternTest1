package com.lifly.pattern.calculator.graph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Stack;

public class GraphTest {

    /**
     * 图的宽度优先遍历
     */
    public void widthPriority(Node head){
        //保存将要弹出打印的队列
        Queue<Node> queue=new LinkedList<>();
        //记录添加到队列的点,防止重复
        HashSet<Node> set=new HashSet<>();
        //先加入头节点
        queue.add(head);
        set.add(head);
        while(!queue.isEmpty()){
            //先弹出打印
            Node node=queue.poll();
            System.out.println(node.value);
            //遍历下一点，进行添加
            for (int i = 0; i < node.nexts.size(); i++) {
                if (!set.contains(node.nexts.get(i))){//防止重复添加
                    //添加
                    queue.add(node.nexts.get(i));
                    set.add(node.nexts.get(i));
                }
            }
        }
    }

    /**
     * 图的深度优先遍历
     */
    public void depthPriority(Node node){
        if (node==null){
            return;
        }
        Stack<Node> stack=new Stack<>();
        //记录加入栈的对象
        HashSet<Node> set=new HashSet<>();

        stack.add(node);
        set.add(node);
        System.out.println(node.value);
        while (!stack.isEmpty()){
            Node cur=stack.pop();
            for (int i = 0; i < cur.nexts.size(); i++) {
                if (!set.contains(cur.nexts.get(i))){
                    stack.push(cur);
                    stack.push(cur.nexts.get(i));
                    set.add(cur.nexts.get(i));
                    System.out.println(cur.nexts.get(i).value);
                    break;
                }
            }
        }
    }
    /**
     * 拓补排序算法,有向无环图
     * 1.在图中找到所有入度为0的点输出
     * 2.把所有入度为0的点在图中删除，继续找所有入度为0的点输出，周而复始
     * 3.图的所有点被删掉后，依次输出的顺序就是拓补顺序
     */
    public List<Node> tuoBuPrint(Graph graph){
        HashMap<Node,Integer> inMap=new HashMap<>();
        Queue<Node> queue=new LinkedList<>();
        for (int i = 0; i < graph.nodes.size(); i++) {
            Node cur=graph.nodes.get(i);
            inMap.put(cur,cur.in);
            if (cur.in==0){
                queue.add(cur);
            }
        }
        List<Node> result=new ArrayList<>();
        while (!queue.isEmpty()){
            Node cur=queue.poll();
            result.add(cur);
            for (int i = 0; i < cur.nexts.size(); i++) {
                inMap.put(cur.nexts.get(i), inMap.get(cur.nexts.get(i))-1);
                if (inMap.get(cur.nexts.get(i))==0){
                    queue.add(cur.nexts.get(i));
                }
            }
        }
        return result;
    }

}
