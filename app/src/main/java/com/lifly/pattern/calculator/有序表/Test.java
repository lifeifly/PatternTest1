package com.lifly.pattern.calculator.有序表;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class Test {
    /**
     * 给定一个Nx3的矩阵matrix，对于每一个长度为3的小数组arr，都表示一个大楼的三个数据
     * arr【0】表示大楼的左边界，arr【1】表示大楼的有边界，arr【2】表示大楼的高度
     * 每座大楼的地基都在x轴上，大楼之间可能会有重叠，请返回整体的轮廓数组
     * 举例matrix={
     * {2,5,6},
     * {1,7,4},
     * {4,6,7},
     * {3,6,5},
     * {10,13,2}
     * {9,11,3},
     * {12,14,4},
     * {10,12,5},
     * };
     *
     * 返回{
     *     {1，2，4}，
     *     {2，4，6}，
     *     {4，6，7}，
     *     {6，7，4}，
     *     {9，10，3}，
     *     {10，12，5}，
     *     {12，14，4}，
     * }；
     */
    public static void outline(int[][] matrix){
        Node[] nodes=new Node[2*matrix.length];
        for (int i = 0; i < matrix.length; i++) {
            int[] curBuilding=matrix[i];
            nodes[i*2]=new Node(curBuilding[0],true,curBuilding[2]);
            nodes[i*2+1]=new Node(curBuilding[1],false,curBuilding[2]);
        }
        Arrays.sort(nodes,new NodeComparator());
        //key height  value:times
        TreeMap<Integer,Integer> heightTimesMap=new TreeMap<>();
        //key x  value:maxHeight
        TreeMap<Integer,Integer> xMaxHeightsMap=new TreeMap<>();
        for (int i = 0; i < nodes.length; i++) {
            Node cur=nodes[i];
            if (cur.isAdd){
                if (heightTimesMap.containsKey(cur.height)){
                    heightTimesMap.put(cur.height,heightTimesMap.get(cur.height)+1);
                }else {
                    heightTimesMap.put(cur.height,1);
                }
            }else {
                if (heightTimesMap.get(cur.height)==1){
                    heightTimesMap.remove(cur.height);
                }else {
                    heightTimesMap.put(cur.height,1);
                }
            }

            if (heightTimesMap.isEmpty()){
                xMaxHeightsMap.put(cur.x,0);
            }else {
                xMaxHeightsMap.put(cur.x,heightTimesMap.lastKey());
            }
        }

        List<List<Integer>> res=new ArrayList<>();
        int start=0;
        int preHeight=0;
        for(Map.Entry<Integer,Integer> entry:xMaxHeightsMap.entrySet()){
            int curX=entry.getKey();
            int curHeight=entry.getValue();
            if (curHeight!=preHeight){
                if (preHeight!=0){
                    res.add(new ArrayList<>(Arrays.asList(start,curX,preHeight)));
                }
                start=curX;
                preHeight=curHeight;
            }
        }
        for (int i = 0; i < res.size(); i++) {

                List<Integer> list=res.get(i);
                System.out.println(list.get(0)+","+list.get(1)+","+list.get(2));

        }
    }
    public static class NodeComparator implements Comparator<Node>{

        @Override
        public int compare(Node o1, Node o2) {
            if (o1.x!=o2.x){
                return o1.x-o2.x;
            }
            if (o1.isAdd!=o2.isAdd){
                return o1.isAdd?-1:1;
            }
            return 0;
        }
    }
    public static class Node{
        public int x;
        public boolean isAdd;
        public int height;

        public Node(int x, boolean isAdd, int height) {
            this.x = x;
            this.isAdd = isAdd;
            this.height = height;
        }
    }


    public static void main(String[] args) {
        int[][] matrix=new int[2][3];
        matrix[0]= new int[]{1, 3, 7};
        matrix[1]= new int[]{2, 5, 8};

        outline(matrix);
    }
}
