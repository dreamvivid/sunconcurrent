package com.sund.test;

import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;

import junit.framework.TestCase;

import org.junit.Test;

public class AtomicIntegerFieldUpdaterTest extends TestCase {
	class TestClass {
	    volatile int id;
		volatile Integer wrap;
	}
	
	public AtomicIntegerFieldUpdater<TestClass> getUpdater(String fieldName) {
		return AtomicIntegerFieldUpdater.newUpdater(TestClass.class, fieldName);
	}
	
	public AtomicReferenceFieldUpdater<TestClass, Integer> getIntegerUpdater(String fieldName) {
		return AtomicReferenceFieldUpdater.newUpdater(TestClass.class, Integer.class, fieldName);
	}
	
	@Test
	public void testAtomicField() {
		TestClass data = new TestClass();
		getUpdater("id").getAndAdd(data, 10);
		assertEquals(10, getUpdater("id").get(data));
	}
	
	@Test
	public void testAtomicIntegerField() {
		TestClass data = new TestClass();
		getIntegerUpdater("wrap").getAndSet(data, new Integer(11));
		assertEquals(new Integer(11), getIntegerUpdater("wrap").get(data));
	}
}
