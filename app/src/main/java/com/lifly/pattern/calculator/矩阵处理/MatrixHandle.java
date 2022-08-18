package com.lifly.pattern.calculator.矩阵处理;

public class MatrixHandle {

    /**
     * 矩阵绕圈打印
     */

    public static void printMatrixZigZig(int[][] matrix){
        //先水平移后垂直移的点A
        int aX=0;//A的行号
        int aY=0;//A的列号
        //先垂直后水平移的点B
        int bX=0;//B的行号
        int bY=0;//B的列号
        //结尾点的位置
        int endX=matrix.length-1;//最大行数的下标
        int endY=matrix[0].length-1;//最大列数的下标
        boolean fromUp=false;//标记从上往下打印还是从下往上打印
        while (aX!=endX+1){
            //打印当前A、B点所在的斜线
            printLevel(matrix,aX,aY,bX,bY,fromUp);
            //移动A,先水平后垂直
            aX=aY==endY?aX+1:aX;
            aY=aY==endY?aY:aY+1;
            //移动B，先垂直后水平
            bY=bX==endX?bY+1:bY;
            bX=bX==endX?bX:bX+1;

            //逆转打印方向
            fromUp=!fromUp;
        }
    }

    private static void printLevel(int[][] matrix, int aX, int aY, int bX, int bY, boolean fromUp) {
        if (fromUp){
            //从上往下
            while (aX!=bX+1){
                System.out.println(matrix[aX++][aY--]);
            }
        }else {
            //从下往上
            while (bX!=aX-1){
                System.out.println(matrix[bX--][bY++]);
            }
        }
    }

    /**
     * 转圈打印二维数组
     * 123
     * 456
     * 789
     * 输出：123698745
     */
    public static void printRing(int[][] matrix){
        int aR=0;
        int aC=0;
        int bR=matrix.length-1;
        int bC=matrix[0].length-1;
        while (aR<=bR&&aC<=bC){
            printEdge(matrix,aR++,aC++,bR--,bC--);
        }
    }

    /**
     * 知道一个圈的左上角A的位置aR，aC和右下角点bR，bC，打印这个圈
     * @param m
     * @param aR
     * @param aC
     * @param bR
     * @param bC
     */
    public static void printEdge(int[][] m,int aR,int aC,int bR,int bC){
        if (aR==bR){//只剩一个横线
            for (int i = aC; i <= bC; i++) {
                System.out.println(m[aR][i]+" ");
            }
        }else if (aC==bC){//只剩一个竖线了
            for (int i = aR; i <= bR; i++) {
                System.out.println(m[i][aC]+" ");
            }
        }else {
            //不同行不同列
            //左上角开始打印
            int curC=aC;
            int curR=aR;
            while (curC!=bC){//规定第一次只打印一行的前n-1个
                System.out.println(m[curR][curC++]+" ");
            }
            while (curR!=bR){//规定第二次只打印一列的前n-1个
                System.out.println(m[curR++][curC]+" ");
            }
            while (curC!=aC){//规定第三次只打印一行的后n-1个
                System.out.println(m[curR][curC--]+" ");
            }
            while (curR!=aR){//规定第四次只打印一列的后n-1个
                System.out.println(m[curR--][curC]+" ");
            }
        }
    }

    /**
     * 正方形矩阵旋转
     * 一层一层处理
     */
    public static void rotateRing(int[][] matrix){
        int aR=0;int aC=0;
        int bR=matrix.length-1;
        int bC=matrix[0].length-1;
        while (aC<bC){
            rotateEdge(matrix,aR++,aC++,bR--,bC--);
        }
    }
    /**
     * 旋转一层矩阵
     * @param m
     * @param aR
     * @param aC
     * @param bR
     * @param bC
     */
    public static void rotateEdge(int[][] m,int aR,int aC,int bR,int bC){
        int temp=0;
        //外层循环第几组，将一个正方形外层分成n行-1个组
        for (int i = 0; i <= bC - aC; i++) {
            temp=m[aR][aC+i];
            m[aR][aC+i]=m[bR-i][aC];
            m[bR-i][aC]=m[bR][bC-i];
            m[bR][bC-i]=m[aR+i][bC];
            m[aR+i][bC]=temp;
        }
    }

    public static void main(String[] args) {
        int[][] m={{1,2,3},{4,5,6},{7,8,9},{10,11,12}};
        printRing(m);
    }
}
