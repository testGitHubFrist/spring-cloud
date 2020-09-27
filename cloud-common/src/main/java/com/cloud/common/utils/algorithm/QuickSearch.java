package com.cloud.common.utils.algorithm;

public class QuickSearch {

    public static void main(String[] args) {
        int[] array = new int[]{10,29,1,232,3,1,23,43,0};
        print(sort(array,0,array.length-1));
    }

    public static int[] sort(int[] array,int left,int right) {
        if(left<right){
            //或者中轴位置
            int mid = partition(array,left,right);
            array = sort(array,left,mid -1);
            array = sort(array,mid+1,right);

        }
        return array;
    }
    public static int  partition(int[] array,int left,int right) {
        //基准值
        int pivot =array[left];
        int i =left +1;
        int j = right;
        while (true){
            //前面找大于基准值的第一个元素
            while (i<= j && array[i]<= pivot){
                i++;
            }
            //从后面找小于基准值的第一个元素
            while (i<=j&&array[j]>=pivot){
                j--;
            }
            if(i >= j){
                break;
            }
            //前后进行交换
            int temp = array[i];
            array[i] =array[j];
            array[j] = temp;
        }

        array[left] = array[j];
        // 使中轴元素处于有序的位置
        array[j] = pivot;
        return j;
    }
    public static void print(int[] array) {
        for (int i :array){
            System.out.print(i+";");
        }
    }
}
