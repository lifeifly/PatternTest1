package com.lifly.pattern.calculator.node;

/**
 * 前缀树,将字符串每个字符放到多叉树上
 */
public class TrieTree {
    public Node root;

    public TrieTree() {
        this.root = new Node();
    }

    public void insert(String str) {
        if (str == null) {
            return;
        }
        char[] cha = str.toCharArray();
        Node node = root;
        node.pass++;
        int position = 0;
        for (int i = 0; i < cha.length; i++) {
            position = cha[i] - 'a';
            if (node.nexts[position] == null) {
                //没有通向下一个的路，新建
                node.nexts[position] = new Node();
            }
            //下一个子节点pass++
            node.nexts[position].pass++;
            //跳转到下一个
            node = node.nexts[position];
        }
        //结尾
        node.end++;
    }

    /**
     * 查找word查入过几次
     *
     * @param word
     * @return
     */
    public int search(String word) {
        if (word == null) {
            return 0;
        }
        char[] chs = word.toCharArray();
        Node node = root;
        int position = 0;
        for (int i = 0; i < chs.length; i++) {
            position = chs[i] - 'a';
            if (node.nexts[position] == null) {
                return 0;
            }
            //跳转到下一个字符节点
            node = node.nexts[position];
        }
        return node.end;
    }

    /**
     * 查找插入的字符串右几个以prefix作为前缀的
     *
     * @param prefix
     * @return
     */
    public int searchPrefix(String prefix) {
        if (prefix == null) {
            return 0;
        }
        char[] chs = prefix.toCharArray();
        Node node = root;
        int position = 0;
        int result = 0;
        for (int i = 0; i < chs.length; i++) {
            position = chs[i] - 'a';
            if (node.nexts[position] == null) {
                return 0;
            }
            node = node.nexts[position];
        }
        //最后一个字符被通过几次，就是以前缀添加了几次
        return node.pass;
    }

    /**
     * 删除一次word
     * @param word
     */
    public void delete(String word){
        if (search(word)>0){
            char[] chs=word.toCharArray();
            Node node=root;
            node.pass--;
            int position=0;
            for (int i = 0; i < chs.length; i++) {
                position=chs[i]-'a';
                //沿途pass--
                if (--node.nexts[position].pass==0){
                    //pass为0，后续节点也不用存在了
                    node.nexts[position]=null;
                    return;
                }
                node=node.nexts[position];
            }
            node.end--;
        }
    }

    public static class Node {
        public int pass;
        public int end;
        public Node[] nexts;

        public Node() {
            pass = 0;
            end = 0;
            nexts = new Node[26];
        }
    }
}
