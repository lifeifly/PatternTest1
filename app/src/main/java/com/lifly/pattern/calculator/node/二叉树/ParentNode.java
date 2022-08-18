package com.lifly.pattern.calculator.node.二叉树;
//后继节点，一颗二叉树中在中序遍历中后一个节点就是后继节点
//前驱节点：一个二叉树在中序遍历中后一个前一个节点就是前驱节点
public class ParentNode {
    int value;
    ParentNode left;
    ParentNode right;
    ParentNode parent;

    public ParentNode(int value) {
        this.value = value;
    }

    /**
     * 给与某一个节点，求该节点的后继节点，没有则返回null
     * @param node
     * @return
     */
    public static ParentNode getPostNode(ParentNode node){
        if (node==null)return null;
        //两种情况，一个有右树，一个没右树
        //1.有右树，找右树的最左节点
        if (node.right!=null){
            return getLeftMost(node.right);
        }else {
            //2.无右树,一直向上找一个节点的左子树是当前分支，那么这个节点就是后继节点
            //获取该节点的父节点
            ParentNode parent=node.parent;
            while (parent!=null&&parent.left!=node){
                //继续向上找
                node=parent;
                parent=parent.parent;
            }
            return parent;
        }
    }

    /**
     * 找这个节点最左的节点,无左节点则返回自己
     * @param right
     * @return
     */
    private static ParentNode getLeftMost(ParentNode right) {
        if (right==null)return right;
        while (right.left!=null){
            right=right.left;
        }
        return right;
    }


}
