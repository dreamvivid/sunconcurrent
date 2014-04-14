package com.sund.test;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.junit.Test;

/**
 * ReentryLock��SynchronizedЧ�ʶԱ�
 * 
 * synchronizedʹ�õ���������ReentrantLock������ʽ����java6�Ժ�����û�����죬�ڸ��µİ汾��������ֻ�����ʽ�����ܸ��á�
 * �����������Ƕ�ռ����java5��ǰ���������ܵ͵�ԭ������û���κ��Ż���ֱ��ʹ��ϵͳ�Ļ���������ȡ������ʽ������CAS��ʱ�����õ��Ǳ��ش������⣬
 * �����Ĳ��ֶ���Java����ʵ�ֵģ��ں����汾��Java�У���ʽ����̫���ܻ���������ã�ֻ����ʹ����ʽ����Ψһ������Ҫ����������Ĺ��ܡ�
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
