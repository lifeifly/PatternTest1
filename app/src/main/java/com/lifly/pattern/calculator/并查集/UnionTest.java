package com.lifly.pattern.calculator.并查集;

import java.util.HashMap;
import java.util.List;
import java.util.Stack;

public class UnionTest {
    public static class Node<T> {
        public T value;

        public Node(T value) {
            this.value = value;
        }
    }

    public static class UnionSet<V> {
        public HashMap<V, Node<V>> nodes;
        public HashMap<Node<V>, Node<V>> parents;

        //只有代表点才有记录
        public HashMap<Node<V>, Integer> sizeMap;

        public UnionSet(List<V> values) {
            for (int i = 0; i < values.size(); i++) {
                Node<V> node = new Node<>(values.get(i));
                nodes.put(values.get(i), node);
                parents.put(node, node);
                sizeMap.put(node, 1);
            }
        }

        //从child开始一直往上找代表点
        public Node<V> findFather(Node<V> child) {
            Stack<Node<V>> path = new Stack<>();
            while (child != parents.get(child)) {
                path.push(child);
                child = parents.get(child);
            }
            //将每个子节点都直接指向代表点
            while (!path.isEmpty()) {
                parents.put(path.pop(), child);
            }
            return child;
        }

        /**
         * 查询a、b是否属于同一集合
         */
        public boolean isSameSet(V a, V b) {
            if (!nodes.containsKey(a) || !nodes.containsKey(b)) {
                return false;
            }
            return findFather(nodes.get(a)) == findFather(nodes.get(b));
        }

        /**
         * 将a、b所属的集合合并在一起，少的挂在多的上
         *
         * @param a
         * @param b
         */
        public void union(V a, V b) {
            if (!nodes.containsKey(a) || !nodes.containsKey(b)) {
                return;
            }
            Node<V> aHead = findFather(nodes.get(a));
            Node<V> bHead = findFather(nodes.get(b));
            if (aHead != bHead) {
                //两个不是一个集合，可以合并
                int aSize = sizeMap.get(aHead);
                int bSize = sizeMap.get(bHead);
                //小的挂在大的上，更新父节点和新代表点数量
                if (aSize >= bSize) {
                    parents.put(bHead, aHead);
                    sizeMap.put(aHead, aSize + bSize);
                    sizeMap.remove(bHead);
                } else {
                    parents.put(aHead, bHead);
                    sizeMap.put(bHead, aSize + bSize);
                    sizeMap.remove(aHead);
                }
            }
        }

        /**
         * 返回集合方法
         */
        public int getIndependentListCount(){
            return sizeMap.size();
        }
    }
}
