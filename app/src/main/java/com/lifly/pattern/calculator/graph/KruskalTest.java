package com.lifly.pattern.calculator.graph;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.Stack;

public class KruskalTest {

    public static class WeightComparator implements Comparator<Edge>{
        @Override
        public int compare(Edge o1, Edge o2) {
            return o1.weight- o2.weight;
        }
    }

    public static Set<Edge> kruskalMST(Graph graph){
        //将所有节点变成集合
        UnionFind unionFind=new UnionFind();
        unionFind.makeSets(graph.nodes.values());

        //小根堆
        PriorityQueue<Edge> edgePriorityQueue=new PriorityQueue<>();
        //将所有边加入小根堆
        for (Edge edge:graph.edges){
            edgePriorityQueue.add(edge);
        }
        Set<Edge> result=new HashSet<>();
        while (!edgePriorityQueue.isEmpty()){
            Edge edge=edgePriorityQueue.poll();
            if (!unionFind.isSameSet(edge.from,edge.to)){
                //合并两个节点
                unionFind.union(edge.from,edge.to);
                result.add(edge);
            }
        }
        return result;
    }


    /**
     * prim算法最小生成树
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    public static Set<Edge> primMST(Graph graph){
        //解锁的边放入小根堆
        PriorityQueue<Edge> edgePriorityQueue=new PriorityQueue<>(new WeightComparator());
        //解锁的点
        HashSet<Node> set=new HashSet<>();
        //已经放入的边
        HashSet<Edge> edges=new HashSet<>();
        Set<Edge> result=new HashSet<>();
        for (Node node:graph.nodes.values()){//随便挑一个点
            //node是开始点
            if (!set.contains(node)){//没被解锁，可以解锁该点
                set.add(node);
                for (Edge edge:node.edges){
                    if (!edges.contains(edge)){
                        //解锁所有的边
                        edgePriorityQueue.add(edge);
                        edges.add(edge);
                    }
                }
                while (!edgePriorityQueue.isEmpty()){
                    Edge edge=edgePriorityQueue.poll();//弹出最小的那个边
                    Node toNode=edge.to;//最小边的终点
                    if (!set.contains(toNode)){//如果该点没被解锁，进行解锁,路径成立
                        set.add(toNode);
                        result.add(edge);
                        for (Edge nextEdge: toNode.edges){
                            if (!edges.contains(edge)){
                                //解锁所有的边
                                edgePriorityQueue.add(nextEdge);
                                edges.add(nextEdge);
                            }
                        }
                    }
                }
            }
        }
        return result;
    }

    /**
     * 求给点A到其它点距离最小，要求每个边权重不为负
     * @return 返回A到每个点的最短距离
     */
    public static HashMap<Node,Integer> dijtsl(Node from){
        //从from出发，到大所有的点
        //记录from到每个点的距离，会不断更新
        HashMap<Node,Integer> disMap=new HashMap<>();
        //自己到自己距离是0
        disMap.put(from,0);
        //已经求出距离的点,即已经作为跳转点了
        HashSet<Node> jumpSet=new HashSet<>();
        //找出distanceMap中记录的距离最小且没做过跳转点的跳转点，当前就一个from点
        Node minNode=getMinJumpNodeByDistance(disMap,jumpSet);
        //有跳转点才能继续
        while (minNode!=null){
            //遍历所有从minNode为跳转点的边
            for (Edge edge:minNode.edges){
                //获取当前边的终点
                Node toNode=edge.to;
                if (!disMap.containsKey(toNode)){
                    //说明当前from到该点没有连通，进行连接
                    disMap.put(toNode,disMap.get(minNode)+edge.weight);
                }else {
                    //说明当前之前存在from到该店的距离，如果此时距离更小就更新
                    disMap.put(toNode,Math.min(disMap.get(minNode)+edge.weight,disMap.get(toNode)));
                }
            }
            //minNode做了跳转点后，进行记录
            jumpSet.add(minNode);
            //看看还有没有可以作为跳转点的点
            minNode=getMinJumpNodeByDistance(disMap,jumpSet);
        }
        return disMap;
    }

    /**
     * 找出distanceMap中记录的距离最小且没做过跳转点的跳转点，当前就一个from点
     * @param disMap
     * @param jumpSet
     * @return
     */
    private static Node getMinJumpNodeByDistance(HashMap<Node, Integer> disMap, HashSet<Node> jumpSet) {
        Node min=null;
        //遍历出发点到达所有点的距离点
        for(Node node:disMap.keySet()){
            if (!jumpSet.contains(node)){//说明当前点还没做过跳转点
                if (min==null){
                    min=node;
                }else {
                    min=min.value<=node.value?min:node;
                }
            }
        }
        return min;
    }

    /**
     * 改进的缔结特斯拉算法
     * @return
     */
    public static HashMap<Node,Integer> dijkst2(Node from,int size){
        NodeHeap nodeHeap=new NodeHeap(size);
        nodeHeap.addOrUpdateOrIgnore(from,0);
        HashMap<Node,Integer> result=new HashMap<>();
        while (!nodeHeap.isEmpty()){
            NodeRecord record=nodeHeap.pop();
            Node cur=record.node;
            int dis=record.distance;
            for (Edge edge: cur.edges){
                nodeHeap.addOrUpdateOrIgnore(edge.to,dis+edge.weight);
            }
            result.put(cur,dis);
        }
        return result;
    }

    /**
     * 新建小根堆
     */
    public static class NodeHeap{
        private Node[] nodes;//实际堆结构
        //key：node value：node对应的结构索引
        private HashMap<Node,Integer> heapIndexMap;
        //key:到达的Node，value：到达node的距离
        private HashMap<Node,Integer> distanceMap;
        //堆上有多少个点
        private int size;
        public NodeHeap(int size){
            nodes=new Node[size];
            heapIndexMap=new HashMap<>();
            distanceMap=new HashMap<>();
            size=0;
        }
        public boolean isEmpty(){
            return size==0;
        }
        private boolean isEntered(Node node){
            return heapIndexMap.containsKey(node);
        }
        public boolean inHeap(Node node){
            return isEntered(node)&&heapIndexMap.get(node)!=-1;
        }
        public void addOrUpdateOrIgnore(Node node,int distance){
            if (inHeap(node)){
                distanceMap.put(node,Math.min(distanceMap.get(node),distance));
                insertHeapify(node,heapIndexMap.get(node));
            }
            if (!isEntered(node)){
                nodes[size]=node;
                heapIndexMap.put(node,size);
                distanceMap.put(node,distance);
                insertHeapify(node,size++);
            }
        }

        /**
         * 上浮决定父节点是否和当前交换位置
         * @param node
         * @param index
         */
        private void insertHeapify(Node node, Integer index) {
            while(distanceMap.get(nodes[index])<distanceMap.get(nodes[(index-1)/2])){
                swap(index,(index-1)/2);
                index=(index-1)/2;
            }
        }

        /**
         * 下沉
         */
        private void heapify(int index,int size){
            while ((index*2+1)<=size){
                int left=index*2+1;
                int right=index*2+2;
                if (right<=size){
                    if (distanceMap.get(nodes[left])<=distanceMap.get(nodes[right])){
                        if (distanceMap.get(nodes[index])>distanceMap.get(nodes[left])){
                            swap(left,index);
                            index=left;
                        }
                    }else {
                        if (distanceMap.get(nodes[index])>distanceMap.get(nodes[right])){
                            swap(right,index);
                            index=right;
                        }
                    }
                }else {
                    if (distanceMap.get(nodes[index])>distanceMap.get(nodes[left])){
                        swap(left,index);
                        index=left;
                    }
                }
            }
        }
        public NodeRecord pop(){
            NodeRecord nodeRecord=new NodeRecord(nodes[0],distanceMap.get(nodes[0]));
            swap(0,size-1);
            heapIndexMap.put(nodes[size-1],-1);
            distanceMap.remove(nodes[size-1]);
            nodes[size-1]=null;
            heapify(0,--size);
            return nodeRecord;
        }

        private void swap(int index1,int index2){
            heapIndexMap.put(nodes[index1],index2);
            heapIndexMap.put(nodes[index2],index1);
            Node tmp=nodes[index1];
            nodes[index1]=nodes[index2];
            nodes[index2]=tmp;
        }
    }
    public static class NodeRecord{
        public Node node;
        public int distance;

        public NodeRecord(Node node, int distance) {
            this.node = node;
            this.distance = distance;
        }
    }

    public static class UnionFind{
        public HashMap<Node,Node> fatherMap;
        public HashMap<Node,Integer> sizeMap;

        public UnionFind() {
            fatherMap=new HashMap<>();
            sizeMap=new HashMap<>();
        }

        public void makeSets(Collection<Node> nodes){
            fatherMap.clear();
            sizeMap.clear();
            for (Node node:nodes){
                fatherMap.put(node,node);
                sizeMap.put(node,1);
            }
        }
        public Node findFather(Node node){
            Stack<Node> path=new Stack<>();
            while (fatherMap.get(node)!=node){
                path.add(node);
                node=fatherMap.get(node);
            }
            while (!path.isEmpty()){
                fatherMap.put(path.pop(),node);
            }
            return node;
        }

        public boolean isSameSet(Node one,Node two){
            if (one==null||two==null){
                return false;
            }
            return findFather(one)==findFather(two);
        }

        public void union(Node one,Node two){
            if (one==null||two==null){
                return;
            }
            if (!isSameSet(one,two)){
                Node oneF=findFather(one);
                Node twoF=findFather(two);
                int oneSize=sizeMap.get(oneF);
                int twoSize=sizeMap.get(twoF);

                if (oneSize>=twoSize){
                    fatherMap.put(twoF,oneF);
                    sizeMap.put(oneF,oneSize+twoSize);
                    sizeMap.remove(twoF);
                }else {
                    fatherMap.put(oneF,twoF);
                    sizeMap.put(twoF,oneSize+twoSize);
                    sizeMap.remove(oneF);
                }
            }
        }
    }
}
