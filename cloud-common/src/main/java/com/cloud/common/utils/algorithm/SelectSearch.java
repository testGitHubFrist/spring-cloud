package com.cloud.common.utils.algorithm;

/**
 * 选择排序
 */
public class SelectSearch {

    public static void main(String[] args) {

        int[] array = new int[]{10,29,1,232,3,1,23,43,0};
        sort(array);
        print(array);
    }

    public static void sort(int[] array) {

        int minIndex = 0;
        int temp = 0;
        for (int i =0;i<array.length-1;i++){
            for (int j = i+1;j<array.length;j++) {
                if(array[j]<array[minIndex]){
                    minIndex = j;
                }
            }
            temp = array[minIndex];
            array[minIndex] = array[i];
            array[i]=temp;
        }
    }

    public static void print(int[] array){
        for (int i :array){
            System.out.print(i+";");
        }
    }
}
