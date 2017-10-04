/**
 * 竞态条件的典型例子
 *      1. EvenGenerator被设计来专门生成偶数。
 *      2. 每个EvenChecker都拥有一个独立的线程。
 *         它调用一个EvenGenerator对象生成一个偶数，然后检查这个数是否真的是偶数。
 *
 * 测试场景中，我们只创建一个EvenGenerator实例，
 * 但同时有多个EvenChecker调用这个生成器获得偶数。
 *
 * 多个EvenChecker线程同时对currentEvenValue域进行读写，这就造成了竞态条件。
 *
 * 在单线程顺序执行场景下，nextEven()方法每次都可以正确返回偶数。
 * 多个EvenChecker线程同时调用同一个EvenGenerator实例是，nextEven()会错误地生成奇数。
 *
 * 原因就是当a线程调用nextEven()执行到第一行++currentEvenValue时，切换到b线程，
 * 再执行了一次++currentEvenValue，又切换回a线程，执行第二行++currentEvenValue，
 * 并返回currentEvenValue值，此时currentEvenValue自增了3次，所以是个奇数。
 */
package com.ciaoshen.howtomcatworks.ex04.concurrency;

class EvenGenerator {

    private int currentEvenValue = 0; // 竟态资源（多个EvenChecker线程同时读写这个域）

    public int nextEven() {
        ++currentEvenValue;
        Thread.yield(); // cause failure faster
        ++currentEvenValue;
        return currentEvenValue;
    }
}
