package com.flume.core.channel;

import com.flume.core.ChannelException;
import com.flume.core.Transaction;
import com.flume.sdk.Event;
import com.google.common.base.Preconditions;

/**
 * <p>
 * An implementation of basic {@link Transaction} semantics designed to work in
 * concert with {@link BasicChannelSemantics} to simplify creation of robust
 * {@link Channel} implementations. This class ensures that each transaction
 * implementation method is called only while the transaction is in the correct
 * state for that method, and only by the thread that created the transaction.
 * Nested calls to <code>begin()</code> and <code>close()</code> are supported
 * as long as they are balanced.
 * </p>
 * <p>
 * Subclasses need only implement <code>doPut</code>, <code>doTake</code>,
 * <code>doCommit</code>, and <code>doRollback</code>, and the developer can
 * rest assured that those methods are called only after transaction state
 * preconditions have been properly met. <code>doBegin</code> and
 * <code>doClose</code> may also be implemented if there is work to be done at
 * those points.
 * </p>
 * <p>
 * All InterruptedException exceptions thrown from the implementations of the
 * <code>doXXX</code> methods are automatically wrapped to become
 * ChannelExceptions, but only after restoring the interrupted status of the
 * thread so that any subsequent blocking method calls will themselves throw
 * InterruptedException rather than blocking. The exception to this rule is
 * <code>doTake</code>, which simply returns null instead of wrapping and
 * propagating the InterruptedException, though it still first restores the
 * interrupted status of the thread.
 * </p>
 * 
 * @Description
 *              <p>
 *              BasicTransactionSemantics实现了Transaction的基本语法，与BasicChannelSemantics一起工作，来使得创建健壮的Channel实现更加简单。
 *              这个类确保了一个事务中的操作（如take(),put()）只有在当前的事务处于对应的状态时才被调用，并且只有创建了当前的事务的线程才能调用这些事务中的操作。
 *              对begin()和close()的嵌套调用是支持的，只要对它们的调用保持平衡（这句不怎么明白，对同一个transaction对象嵌套调用这两个方法明显不行，
 *              应该是不同的transaction对象的close和begin方法可以嵌套）。
 *              <p>
 *              <p>
 *              它的子类可以只实现doPut,doTake,doCommit和doCommit、doRollback方法，而不必再设法确保这些方法只有在正确的事务状态下才被调用。
 *              当doBegin和doClose也有特殊的操作时，也可以实现这两个方法。
 *              </p>
 *              <p>
 *              在doXXX方法中抛出的InterruptedException都被包装进ChannelException中，但这是在恢复了当前线程的interrupted状态之后，
 *              这样接下的blocking method
 *              call就会抛出InterruptedException，而不是进入阻塞状态。这个规则的的例外是doTake,当只是返回null，
 *              而不是包装、传播InterruptedException，但是doTake仍然先恢复了线程的interrupted状态。
 *              BasicTransactionSemantics实现了跟事务范围内各个操作有关方法。
 *              </p>
 * @author Administrator
 * @date 2017年8月3日 下午3:06:51
 *
 */
public abstract class BasicTransactionSemantics implements Transaction {
	private State state;
	private long initialThreadId;

	protected void doBegin() throws InterruptedException {
	}

	protected abstract void doPut(Event event) throws InterruptedException;

	protected abstract Event doTake() throws InterruptedException;

	protected abstract void doCommit() throws InterruptedException;

	protected abstract void doRollback() throws InterruptedException;

	protected void doClose() {
	}

	/**
	 * 
	 * 构造函数
	 */
	public BasicTransactionSemantics() {
		// TODO 初始化当前类属性
		this.state = State.NEW;
		this.initialThreadId = Thread.currentThread().getId();
	}

	/**
	 * <p>
	 * The method to which {@link BasicChannelSemantics} delegates calls to
	 * <code>put</code>.
	 * </p>
	 * 
	 * @Description
	 * @param event
	 */
	protected void put(Event event) {
		Preconditions.checkState(Thread.currentThread().getId() == this.initialThreadId,
				"put() called from different thread than getTransaction()!");
		Preconditions.checkState(this.state.equals(State.OPEN), "put() called when transaction is %s!", state);
		Preconditions.checkArgument(event != null, "put() called with null event!");

		try {
			this.doPut(event);
		} catch (InterruptedException e) {
			// TODO: handle exception
			Thread.currentThread().interrupt();
			throw new ChannelException(e.toString(), e);
		}
	}

	/**
	 * <p>
	 * The method to which {@link BasicChannelSemantics} delegates calls to
	 * <code>take</code>.
	 * </p>
	 * 
	 * @Description
	 * @return
	 */
	protected Event take() {
		Preconditions.checkState(Thread.currentThread().getId() == this.initialThreadId,
				"take() called from different thread than getTransaction()!");
		Preconditions.checkState(state.equals(State.OPEN), "take() called when transaction is %s!", state);

		try {
			return this.doTake();
		} catch (InterruptedException e) {
			// TODO: handle exception
			Thread.currentThread().interrupt();
			return null;
		}
	}

	@Override
	public void begin() {
		// TODO Auto-generated method stub
		Preconditions.checkState(Thread.currentThread().getId() == initialThreadId,
				"begin() called from different thread than getTransaction()!");
		Preconditions.checkState(state.equals(State.NEW), "begin() called when transaction is " + state + "!");

		try {
			this.doBegin();
		} catch (InterruptedException e) {
			// TODO: handle exception
			Thread.currentThread().interrupt();
			throw new ChannelException(e.toString(), e);
		}
		this.state = State.OPEN;
	}

	@Override
	public void commit() {
		// TODO Auto-generated method stub
		Preconditions.checkState(Thread.currentThread().getId() == initialThreadId,
				"commit() called from different thread than getTransaction()!");
		Preconditions.checkState(state.equals(State.OPEN), "commit() called when transaction is %s!", state);

		try {
			this.doCommit();
		} catch (InterruptedException e) {
			// TODO: handle exception
			Thread.currentThread().interrupt();
			throw new ChannelException(e.toString(), e);
		}
		this.state = State.COMPLETED;
	}

	@Override
	public void rollback() {
		// TODO Auto-generated method stub
		Preconditions.checkState(Thread.currentThread().getId() == initialThreadId,
				"rollback() called from different thread than getTransaction()!");
		Preconditions.checkState(state.equals(State.OPEN), "rollback() called when transaction is %s!", state);

		this.state = State.COMPLETED;

		try {
			this.doRollback();
		} catch (InterruptedException e) {
			// TODO: handle exception
			Thread.currentThread().interrupt();
			throw new ChannelException(e.toString(), e);
		}
	}

	@Override
	public void close() {
		// TODO Auto-generated method stub
		Preconditions.checkState(Thread.currentThread().getId() == initialThreadId,
				"close() called from different thread than getTransaction()!");
		Preconditions.checkState(state.equals(State.NEW) || state.equals(State.COMPLETED),
				"close() called when transaction is %s" + " - you must either commit or rollback first", state);

		this.state = State.CLOSED;
		this.doClose();
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		StringBuilder builder = new StringBuilder();
		builder.append("BasicTransactionSemantics: {");
		builder.append(" state:").append(this.state);
		builder.append(" initialThreadId:").append(this.initialThreadId);
		builder.append(" }");
		return builder.toString();
	}

	/**
	 * <p>
	 * The state of the {@link Transaction} to which it belongs.
	 * </p>
	 * <dl>
	 * <dt>NEW</dt>
	 * <dd>A newly created transaction that has not yet begun.</dd>
	 * <dt>OPEN</dt>
	 * <dd>A transaction that is open. It is permissible to commit or rollback.
	 * </dd>
	 * <dt>COMPLETED</dt>
	 * <dd>This transaction has been committed or rolled back. It is illegal to
	 * perform any further operations beyond closing it.</dd>
	 * <dt>CLOSED</dt>
	 * <dd>A closed transaction. No further operations are permitted.</dd>
	 * </dl>
	 * 
	 * @Description Transaction的四种事务状态
	 * @author Administrator
	 * @date 2017年8月3日 下午3:15:26
	 *
	 */
	protected static enum State {
		NEW, OPEN, COMPLETED, CLOSED
	}

	/******************************** Get/Set方法 **************************************/
	/**
	 * 
	 * @Description 返回当前BasicTransactionSemantics的事务状态
	 * @return the current state of the transaction
	 */
	public State getState() {
		return this.state;
	}
}
