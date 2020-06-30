package com.gitee.util.locks;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

public class ReenterSelfLock implements Lock{

	
	private static class Sync extends AbstractQueuedSynchronizer{
		private static final long serialVersionUID = 1L;
		//是否处于被占用状态
		protected boolean isHeldExclusively() {
			return getState() > 0;
		}
		//当前状态为0 是获取锁
		@Override
		protected boolean tryAcquire(int arg) {
			if(compareAndSetState(0, 1)) {
				setExclusiveOwnerThread(Thread.currentThread());
				return true;
			}else if(getExclusiveOwnerThread() == Thread.currentThread()) {
				setState(getState() +1);
				return true;
			}
			return false;
		}
		
		@Override
		protected boolean tryRelease(int arg) {
			if(getExclusiveOwnerThread() != Thread.currentThread()) {
				throw new IllegalMonitorStateException();
			}
			if(getState() == 0 ) {
				throw new IllegalMonitorStateException();
			}
			setState(getState() - 1); 
			if(getState() == 0 ) {
				setExclusiveOwnerThread(null);
			}
			return true;
		}
		// 返回一个Condition,每个condition都包含了一个condition队列
		Condition newConition(){
			return new ConditionObject();
		}
	}
	
	// 仅需要将操作代理到Sync上即可
	private final Sync sync = new Sync();
	
	
	@Override
	public void lock() {
		sync.acquire(1);
		
	}

	@Override
	public void lockInterruptibly() throws InterruptedException {
		sync.acquireInterruptibly(1);
		
	}

	@Override
	public boolean tryLock() {
		return sync.tryAcquire(1);
	}

	@Override
	public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
		return sync.tryAcquireNanos(1, unit.toNanos(time));
	}

	@Override
	public void unlock() {
		sync.release(1);
	}

	@Override
	public Condition newCondition() {
		return sync.newConition();
	}

}
