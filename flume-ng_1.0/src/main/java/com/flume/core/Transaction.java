package com.flume.core;

/**
 * <p>
 * Provides the transaction boundary while accessing a channel.
 * </p>
 * <p>
 * 当进入channel时，提供事务边界
 * </p>
 * <p>
 * A <tt>Transaction</tt> instance is used to encompass(围绕，包围) channel access
 * via the following idiom(惯用语法):
 * </p>
 * 
 * <pre>
 * <code>
 * Channel ch = ...
 * Transaction tx = ch.getTransaction();
 * try {
 *   tx.begin();
 *   ...
 *   // ch.put(event) or ch.take()
 *   ...
 *   tx.commit();
 * } catch (ChannelException ex) {
 *   tx.rollback();
 *   ...
 * } finally {
 *   tx.close();
 * }
 * </code>
 * </pre>
 * <p>
 * Depending upon(依靠) the implementation of the channel, the transaction
 * semantics may be strong, or best-effort only.
 * </p>
 *
 * <p>
 * Transactions must be thread safe. To provide a guarantee of thread safe
 * access to Transactions, see {@link BasicChannelSemantics} and
 * {@link BasicTransactionSemantics}.
 *
 * @see org.apache.flume.Channel
 */
public interface Transaction {

	enum TransactionState {
		Started, Committed, RolledBack, Closed
	}

	/**
	 * <p>
	 * Starts a transaction boundary for the current channel operation. If a
	 * transaction is already in progress, this method will join that
	 * transaction using reference counting.
	 * </p>
	 * <p>
	 * <strong>Note</strong>: For every invocation(请求) of this method there must
	 * be a corresponding invocation of {@linkplain #close()} method. Failure to
	 * ensure this can lead to dangling(悬挂的) transactions and unpredictable
	 * results.
	 * </p>
	 */
	void begin();

	/**
	 * Indicates that the transaction can be successfully committed. It is
	 * required that a transaction be in progress when this method is invoked.
	 */
	void commit();

	/**
	 * Indicates that the transaction can must be aborted. It is required that a
	 * transaction be in progress when this method is invoked.
	 */
	void rollback();

	/**
	 * <p>
	 * Ends a transaction boundary for the current channel operation. If a
	 * transaction is already in progress, this method will join that
	 * transaction using reference counting. The transaction is completed only
	 * if there are no more references left for this transaction.
	 * </p>
	 * <p>
	 * <strong>Note</strong>: For every invocation of this method there must be
	 * a corresponding invocation of {@linkplain #begin()} method. Failure to
	 * ensure this can lead to dangling transactions and unpredictable results.
	 * </p>
	 */
	void close();
}
