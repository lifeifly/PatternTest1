package com.lifly.pattern.calculator.lfunode;

import java.util.HashMap;
import java.util.Map;

public class Test {
    /**
     * 一个缓存结构需要实现以下功能
     * void set（int key,int value）加入或修改key对应的value
     * int get(int key) 查询key对应的value值
     * 但是缓存中最多放k条记录，如果新的k+1条记录加入，在缓存结构中哪一个key被调用set或get的次数最少，就删除这个key的记录，如果有多个，就删除上次调用发生最早的哪个key的记录
     * 二维链表
     */
    public static class LFUCache {
        //key：key  value：封装的node节点，Node是双向链表
        public Map<Integer, Node> positionMap = new HashMap<>();
        //key:改动次数  value：对应的桶
        public Map<Integer, Bucket> timesMap = new HashMap<>();
        public Bucket headBucket=new Bucket(1);
        public int size;

        public void set(int key, int value) {
            if (positionMap.containsKey(key)) {
                //之前有过当前节点，将当前节点断开，两边再次相连，将当前节点放到次数+1的桶中
                //相连
                Node node = positionMap.get(key);
                node.value=value;
                Node last = node.last;
                Node next = node.next;
                if (last!=null){
                    last.next=next;
                    next.last=last;
                }
                //换桶
                Bucket parentBucket=node.parent;
                //新目标桶的次数
                int newTimes=parentBucket.times+1;
                if (timesMap.containsKey(newTimes)){
                    //将当前节点加入到存在的新桶中
                    Bucket newParentBucket=timesMap.get(newTimes);
                    newParentBucket.addNode(node);
                }else {
                    //创建新桶插在中间
                    Bucket newParentBucket=new Bucket(newTimes);
                    newParentBucket.addNode(node);

                    Bucket originNextBucket=parentBucket.next;
                    parentBucket.next=newParentBucket;
                    newParentBucket.last=parentBucket;
                    newParentBucket.next=originNextBucket;
                    originNextBucket.last=newParentBucket;

                    timesMap.put(newTimes,newParentBucket);
                }

            }else {
                //之前不存在该节点

            }
        }
    }

    public static class Node {
        public Node last;
        public Node next;
        public int key;
        public int value;
        public Bucket parent;

        public Node(Node last, Node next) {
            this.last = last;
            this.next = next;
        }

    }

    public static class Bucket {
        public int times;
        public Bucket last;
        public Bucket next;
        public Node value;
        public Node cur;

        public Bucket(int times) {
            this.times = times;
        }

        public void addNode(Node node) {
            node.parent = this;
            if (value == null) {
                value = node;
                cur = value;
            } else {
                cur.next = node;
                node.last = cur;
            }

        }
    }

    /**
     * Nge加油站组成一个环形，给定两个长度都是N的非负数组oil和dis，oil【i】代表第i个加油站村的油可以跑多少千米
     * dis【i】代表第i个加油站到环中的下一个加油站相隔多少千米
     * 假设你的车油箱足够大，初始车里没有有，如果车从第i个加油站出发，最终可以回到这个加油站，那这个加油站就是良好出发点
     * 返回boolean数组，每个加油站是不是良好出发点
     *
     * @return
     */
    public static boolean[] isGoodGo(int[] oil, int[] dis) {
        return null;
    }
}
