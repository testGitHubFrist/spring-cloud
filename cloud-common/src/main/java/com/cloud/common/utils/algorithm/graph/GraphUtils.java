package com.cloud.common.utils.algorithm.graph;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class GraphUtils<T> {

    private ArrayList<T> vertexList;//存储点的链表
    private int[][] edges;//邻接矩阵，用来存储边
    private int numOfEdges;//边的数目

    public GraphUtils(int n) {
        //初始化矩阵，一维数组，和边的数目
        edges = new int[n][n];
        vertexList = new ArrayList(n);
        numOfEdges = 0;
    }

    public List getVertexList() {
        return vertexList;
    }

    public int getNumOfEdges() {
        return numOfEdges;
    }

    public T getValueByIndex(int i) {
        return vertexList.get(i);
    }

    public int getWeight(int v1, int v2) {
        return edges[v1][v2];
    }

    public void insertVertex(T vertex) {
        vertexList.add(vertex);
    }

    public void insertEdge(int v1, int v2, int weight) {
        edges[v1][v2] = weight;
        numOfEdges++;
    }

    public void deletEdge(int v1, int v2) {
        edges[v1][v2] = 0;
        numOfEdges--;
    }

    //获取第一个邻接节点的下标
    public int getFristNeighbor(int index) {
        for (int j = 0; j < vertexList.size(); j++) {
            if (edges[index][j] > 0) {
                return j;
            }
        }
        return -1;
    }

    //根据前一个邻接节点的下一个邻接节点
    public int getNextNeighbor(int v1, int v2) {
        for (int i = v2 + 1; i < vertexList.size(); i++) {
            if (edges[v1][i] > 0) {
                return i;
            }
        }
        return -1;
    }


    //测试
    public static void main(String[] args) {
        int n = 4, e = 4;
        String labels[] = {"V1", "V2", "V3", "V4"};
        GraphUtils<String> graphUtils = new GraphUtils(n);
        for (String label : labels) {
            graphUtils.insertVertex(label);
        }
        graphUtils.insertEdge(0, 1, 2);
        graphUtils.insertEdge(0, 2, 5);
        graphUtils.insertEdge(2, 3, 8);
        graphUtils.insertEdge(3, 0, 7);
        System.out.println("o后的邻接结点" + graphUtils.getValueByIndex(graphUtils.getFristNeighbor(0)));
        System.out.println("0后下一个邻接结点" + graphUtils.getValueByIndex(graphUtils.getNextNeighbor(0, 1)));
        graphUtils.bfs();
    }

    public void bfs() {
        boolean[] isVisited = new boolean[this.getVertexList().size()];
        //遍历每个结点
        for (int i = 0; i < vertexList.size(); i++) {
            if (!isVisited[i]) {
                bfs(isVisited, i);
            }
        }
    }

    private void bfs(boolean[] isVisited, int i) {
        int u; //表示 队列的头节点对应下标
        int w; // 邻接点w
        LinkedList<Integer> queue = new LinkedList<>();
        //访问结点，输出结点信息
        System.out.print("BFS -> " + getValueByIndex(i) + " => ");
        isVisited[i] = true;
        //加入队列尾部
        queue.addLast(i);
        while (!queue.isEmpty()) {
            //remove头节点
            u = queue.remove(i);
            //获取第一个邻接结点
            w = getFristNeighbor(u);
            while (w != -1) {
                //如果第一个邻接结点未被访问
                if (!isVisited[w]) {
                    System.out.print(getValueByIndex(w) + "=>");
                    isVisited[w] = true;
                    //加入队尾
                    queue.addLast(w);
                }

                w = getNextNeighbor(u, w);
            }
        }
    }

    private void dfs(boolean[] isVisited, int i) {
        //首先我们访问该结点,输出
        System.out.print(getValueByIndex(i) + "->");
        //将结点设置为已经访问
        isVisited[i] = true;
        //查找结点i的第一个邻接结点w
        //System.out.println("当前节点 --> " + i);
        int w = getFristNeighbor(i);
        //System.out.println("相邻接点 --> " + w);
        while (w != -1) {//说明有
            if (!isVisited[w]) {
                dfs(isVisited, w);
            }
            //如果w结点已经被访问过
            w = getNextNeighbor(i, w);
            //System.out.println("已被访问 --> " + w);
        }

    }

    //对dfs 进行一个重载, 遍历我们所有的结点，并进行 dfs
    public void dfs() {
        boolean[] isVisited = new boolean[vertexList.size()];
        System.out.print("DFS ->");
        //遍历所有的结点，进行dfs[回溯]
        for (int i = 0; i < getVertexList().size(); i++) {
            if (!isVisited[i]) {
                dfs(isVisited, i);
            }
        }
    }


}
