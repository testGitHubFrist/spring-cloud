package com.cloud.common.utils.algorithm;

/**
 * 递归函数计算阶乘
 */
public class Recursion {

    public static void main(String[] args) {
        System.out.print("10的阶乘："+computer(10));
    }

    public static int computer(int target) {
        int result= 1;
        if(target == 1 ){
            //基线条件
            return 1;
        }else {
            //递归条件
            return  target*computer(target -1);
        }
    }
}
