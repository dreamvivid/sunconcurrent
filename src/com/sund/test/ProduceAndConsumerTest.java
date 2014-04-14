package com.sund.test;

import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * ˳�����ʵ������������������
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
			String p = "��Ʒ-"+new Random().nextInt(1000);
			System.out.println(Thread.currentThread().getName()+" ������Ʒ: "+p);
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
			System.out.println(Thread.currentThread().getName()+" ���Ѳ�Ʒ: "+o);
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
