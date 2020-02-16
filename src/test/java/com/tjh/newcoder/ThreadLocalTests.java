package com.tjh.newcoder;

import org.junit.Test;


public class ThreadLocalTests {
    //每个线程都持有自己的ThreadLocalMap
    public static ThreadLocal<String> threadLocal_1 = new ThreadLocal<>();
    public static ThreadLocal<String> threadLocal_2 = new ThreadLocal<>();

    public void threadLocalTest1(){
        threadLocal_1.set("123");
        threadLocal_2.set("lyn");

    }
    public void threadLocalTest2(){
        threadLocal_1.set("345");
        threadLocal_2.set("tjh");

    }
    @Test
    public void testMulti(){
        Thread thread_1 = new Thread(new Runnable() {
            @Override
            public void run() {
                threadLocalTest1();
                System.out.println("1--threadLocal_1  "+threadLocal_1.get());
                System.out.println("1--threadLocal_2  "+threadLocal_2.get());
            }
        },"thread_1");
        Thread thread_2 = new Thread(new Runnable() {
            @Override
            public void run() {
                threadLocalTest2();
                System.out.println("2--threadLocal_1  "+threadLocal_1.get());
                System.out.println("2--threadLocal_2  "+threadLocal_2.get());
            }
        },"thread_2");
        thread_1.start();
        thread_2.start();
    }
}
