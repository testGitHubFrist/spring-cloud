package com.cloud.common.utils.algorithm;

/**
 * 二分查找
 */
public class BinarySearch {


    public static void main(String[] args) {

        int[] array = new int[]{1,2,3,4,5,6,7,8,9,10,11};
        int target = 11;
        sort(array,target);
    }

    public static void sort(int[] array,int target){
        if(array.length == 0){
            System.out.println("not found!");
            return;
        }
        int left = 0;
        int right = array.length-1;
        while (left<=right){
            int mid = (left+right)/2;
            if(target<array[mid]){
               right = mid-1;
            }else  if(target>array[mid]){
                left = mid+1;
            }else {
                System.out.println("found index:"+mid);
                return;
            }
        }
        System.out.println("not found!");
    }
}
