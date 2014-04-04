package com.sund.test;

import java.util.concurrent.atomic.AtomicInteger;

import junit.framework.TestCase;

import org.junit.Test;

public class AtomicIntegerTest extends TestCase{
	private final Object lock = new Object();
	@Test
	public void testAll() {
		final AtomicInteger ai = new AtomicInteger();
		// = -> 0
		ai.get();
		// ++i -> 1
		ai.incrementAndGet();
		// --i -> 0
		ai.decrementAndGet();
		// i++ -> 1
		ai.getAndIncrement();
		// i-- -> 0
		ai.getAndDecrement();
		// i+=n -> 5
		ai.getAndAdd(5);
		ai.compareAndSet(5, 8);
		assertEquals(8, ai.get());
		
		final int thLen = 10;
		Thread[] ths = new Thread[thLen];
		for(int i=0; i<thLen; i++) {
			ths[i] = new Thread(new Runnable() {
				@Override
				public void run() {
					synchronized(lock) {
						System.out.println("#1--ai = "+ai.get());
						ai.incrementAndGet();
						System.out.println("#2--ai = "+ai.get());
					}
				}
			});
		}
		
		for(Thread th : ths) {
			th.start();
		}
		for(Thread th: ths) {
			try {
				th.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		assertEquals(18, ai.get());
	}
}
