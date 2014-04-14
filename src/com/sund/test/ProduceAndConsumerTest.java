package com.sund.test;

import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 顺序队列实现生产者消费者问题
 * @author hh
 *
 */
public class ProduceAndConsumerTest {
	 private final Lock lock = new ReentrantLock();
     class Producer implements Runnable{
		private final BlockingQueue<Object> queue;
		public Producer(BlockingQueue<Object> queue) {
			this.queue = queue;
		}
		private Object produce() {
			String p = "产品-"+new Random().nextInt(1000);
			System.out.println(Thread.currentThread().getName()+" 生产产品: "+p);
			return p;
		}
		@Override
		public void run() {
			while(true) {
				lock.lock();
				try {
					if(queue.remainingCapacity()>0) {
						queue.offer(produce());
					}
				} finally {
					lock.unlock();
				}
			}
		}
	}
	
	class Consumer implements Runnable{
		private final BlockingQueue<Object> queue;
		public Consumer(BlockingQueue<Object> queue) {
			this.queue = queue;
		}
		private void consume(Object o) {
			System.out.println(Thread.currentThread().getName()+" 消费产品: "+o);
			System.out.println("########### "+queue.toString());
		}
		@Override
		public void run() {
			while(true) {
				lock.lock();
				try {
					if(!queue.isEmpty()) {
						consume(queue.poll());
					}
				} finally {
					lock.unlock();
				}
			}
		}
	}
	
	public static void main(String[] args) {
		ProduceAndConsumerTest test = new ProduceAndConsumerTest();
		ArrayBlockingQueue<Object> queue = new ArrayBlockingQueue<Object>(10);
		new Thread(test.new Producer(queue)).start();
		new Thread(test.new Consumer(queue)).start();
		new Thread(test.new Consumer(queue)).start();
	}
}
