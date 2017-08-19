package com.flume.core.channel;

import java.util.HashMap;

import com.flume.core.ChannelException;
import com.flume.core.Transaction;
import com.flume.core.annotations.InterfaceAudience;
import com.flume.core.annotations.InterfaceStability;
import com.flume.sdk.Event;
import com.google.common.base.Preconditions;

/**
 * <p>
 * An implementation of basic {@link Channel} semantics, including the implied
 * thread-local semantics of the {@link Transaction} class, which is required to
 * extend {@link BasicTransactionSemantics}.
 * </p>
 * 
 * @Description
 * @author Administrator
 * @date 2017年8月6日 下午4:51:18
 *
 */
@InterfaceAudience.Public
@InterfaceStability.Stable
public abstract class BasicChannelSemantics extends AbstractChannel {

	/**
	 * 每个线程都包括了一个唯一的Transaction对象，保证了事务的隔离性
	 */
	private ThreadLocal<BasicTransactionSemantics> currentTransaction = new ThreadLocal<>();

	private boolean initialized = false;

	/**
	 * <p>
	 * Called upon first getTransaction() request, while synchronized on this
	 * {@link Channel} instance. Use this method to delay the initialization
	 * resources until just before the first transaction begins.
	 * </p>
	 * 
	 * @Description 注意延迟加载，直到getTransaction方法首次调用时再加载此方法
	 */
	protected void initialize() {
	}

	/**
	 * <p>
	 * Called to create new {@link Transaction} objects, which must extend
	 * {@link BasicTransactionSemantics}. Each object is used for only one
	 * transaction, but is stored in a thread-local and retrieved by
	 * <code>getTransaction</code> for the duration of that transaction.
	 * </p>
	 * 
	 * @Description 建立一个新事务，具体的实现方法有Channel的具体实现类实现
	 * @return
	 */
	protected abstract BasicTransactionSemantics createTransaction();

	/**
	 * <p>
	 * Ensures that a transaction exists for this thread and then delegates the
	 * <code>put</code> to the thread's {@link BasicTransactionSemantics}
	 * instance.
	 * </p>
	 * 
	 * @Description 首先得到线程对应的transaction，由transaction来调用put方法
	 */
	@Override
	public void put(Event event) throws ChannelException {
		// TODO Auto-generated method stub
		BasicTransactionSemantics transaction = this.currentTransaction.get();
		Preconditions.checkState(transaction != null, "No transaction exists for this thread");
		transaction.put(event);
	}

	/**
	 * <p>
	 * Ensures that a transaction exists for this thread and then delegates the
	 * <code>take</code> to the thread's {@link BasicTransactionSemantics}
	 * instance.
	 * </p>
	 * 首先得到线程对应的transaction，由transaction来调用put方法
	 */
	@Override
	public Event take() throws ChannelException {
		// TODO Auto-generated method stub
		BasicTransactionSemantics transaction = this.currentTransaction.get();
		Preconditions.checkState(transaction != null, "No transaction exists for this thread");
		return transaction.take();  
	}

	/**
	 * <p>
	 * Initializes the channel if it is not already, then checks to see if there
	 * is an open transaction for this thread, creating a new one via
	 * <code>createTransaction</code> if not.
	 * 
	 * @return the current <code>Transaction</code> object for the calling
	 *         thread
	 *         </p>
	 * @Description 在线程中获取当前线程中的事务通过getTransaction方法，它会调用BasicChannelSemantics中定义的的抽象方法createTransaction()
	 *              来获取BasicTransactionSemantics的实例
	 */
	@Override
	public Transaction getTransaction() {
		// TODO Auto-generated method stub
		// 判断initialized是否已经被修改过（注意synchronized同步）
		if (!this.initialized) {
			synchronized (this) {
				if (!initialized) {
					this.initialize();
					this.initialized = true;
				}
			}
		}

		BasicTransactionSemantics transaction = this.currentTransaction.get();
		if (transaction == null || transaction.getState().equals(BasicTransactionSemantics.State.CLOSED)) {
			// 当此线程的transaction为null或状态为Closed时，重新建立一个新事务（具体的建立方法由Channel的具体实现类来定义），并将这个事务存入ThreadLocal中
			transaction = this.createTransaction();
			this.currentTransaction.set(transaction);
		}
		return transaction;
	}

}
