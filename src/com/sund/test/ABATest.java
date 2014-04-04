package com.sund.test;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicStampedReference;

import junit.framework.TestCase;

import org.junit.Test;

public class ABATest extends TestCase {
	private volatile AtomicInteger v = new AtomicInteger(100);

	class Thread1 implements Runnable {
		@Override
		public void run() {
			try {
				TimeUnit.SECONDS.sleep(2);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			v.compareAndSet(100, 101);
		}
	}

	class Thread2 implements Runnable {
		@Override
		public void run() {
			v.compareAndSet(100, 102);
			v.compareAndSet(102, 100);
		}
	}

	@Test
	public void testAtomicInteger() {
		Thread thread1 = new Thread(new Thread1());
		Thread thread2 = new Thread(new Thread2());
		thread1.start();
		thread2.start();
		try {
			thread1.join();
			thread2.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		assertEquals(101, v.get());
	}

	private AtomicStampedReference<Integer> sv = new AtomicStampedReference<Integer>(
			100, 0);

	class StampedThread1 implements Runnable {
		@Override
		public void run() {
			int currStamp = sv.getStamp();
			try {
				TimeUnit.SECONDS.sleep(1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			sv.compareAndSet(100, 101, currStamp, currStamp + 1);
		}
	}

	class StampedThread2 implements Runnable {
		@Override
		public void run() {
			sv.compareAndSet(100, 102, sv.getStamp(), sv.getStamp() + 1);
			sv.compareAndSet(102, 100, sv.getStamp(), sv.getStamp() + 1);
		}
	}

	@Test
	public void testAtomicStampedReference() {
		Thread thread1 = new Thread(new StampedThread1());
		Thread thread2 = new Thread(new StampedThread2());
		thread1.start();
		thread2.start();
		try {
			thread1.join();
			thread2.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		assertEquals(new Integer(100), sv.getReference());
	}
}
