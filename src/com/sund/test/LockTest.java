package com.sund.test;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.junit.Test;

/**
 * ReentryLock和Synchronized效率对比
 * 
 * synchronized使用的内置锁和ReentrantLock这种显式锁在java6以后性能没多大差异，在更新的版本中内置锁只会比显式锁性能更好。
 * 这两种锁都是独占锁，java5以前内置锁性能低的原因是它没做任何优化，直接使用系统的互斥体来获取锁。显式锁除了CAS的时候利用的是本地代码以外，
 * 其它的部分都是Java代码实现的，在后续版本的Java中，显式锁不太可能会比内置锁好，只会更差。使用显式锁的唯一理由是要利用它更多的功能。
 * 
 * @author hh
 * 
 */
public class LockTest {
	class AtomicIntegerLock {
		private final Lock lock = new ReentrantLock();
		private int v;

		public AtomicIntegerLock() {
			super();
		}

		public AtomicIntegerLock(int v) {
			this.v = v;
		}

		public Integer get() {
			lock.lock();
			try {
				return v;
			} finally {
				lock.unlock();
			}
		}

		public Integer getAndSet(int v) {
			lock.lock();
			try {
				this.v = v;
				return v;
			} finally {
				lock.unlock();
			}
		}

		public void getAndIncrement() {
			lock.lock();
			try {
				v++;
			} finally {
				lock.unlock();
			}
		}

		public int getV() {
			return v;
		}

		public void setV(int v) {
			this.v = v;
		}

	}

	@Test
	public void TestReentryLock() {
		final int times = 1000000;
		final int length = 100;
		long sAi = System.nanoTime();
		final AtomicIntegerLock ai = new AtomicIntegerLock(0);
		Runnable[] ths = new Runnable[length];
		for (int i = 0; i < ths.length; i++) {
			ths[i] = new Runnable() {
				@Override
				public void run() {
					for (int i = 0; i < times; i++) {
						ai.getAndIncrement();
					}
				}
			};
		}
		for (Runnable th : ths) {
			Thread t = new Thread(th);
			t.start();
			try {
				t.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		long eAi = System.nanoTime();
		System.out.println("ai = " + ai.get());
		System.out.println("ai  time spent : " + (eAi - sAi));

		final AtomicIntegerLock ais = new AtomicIntegerLock(0);
		long sAis = System.nanoTime();
		final Object lock = new Object();
		for (int i = 0; i < ths.length; i++) {
			ths[i] = new Runnable() {
				@Override
				public void run() {
					for (int i = 0; i < times; i++) {
						synchronized (lock) {
							ais.setV(ais.getV() + 1);
						}
					}
				}
			};
		}
		for (Runnable th : ths) {
			Thread t = new Thread(th);
			t.start();
			try {
				t.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		long eAis = System.nanoTime();
		System.out.println("ais = " + ais.get());
		System.out.println("ais time spent : " + (eAis - sAis));
	}
}
