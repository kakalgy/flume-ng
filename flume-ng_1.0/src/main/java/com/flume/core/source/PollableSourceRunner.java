package com.flume.core.source;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.flume.core.CounterGroup;
import com.flume.core.PollableSource;
import com.flume.core.Source;
import com.flume.core.SourceRunner;
import com.flume.core.channel.ChannelProcessor;
import com.flume.core.lifecycle.LifecycleState;
import com.flume.sdk.EventDeliveryException;

/**
 * <p>
 * An implementation of {@link SourceRunner} that can drive a
 * {@link PollableSource}.
 * </p>
 * <p>
 * A {@link PollableSourceRunner} wraps a {@link PollableSource} in the required
 * run loop in order for it to operate. Internally, metrics and counters are
 * kept such that a source that returns a {@link PollableSource.Status} of
 * {@code BACKOFF} causes the run loop to do exactly that. There's a maximum
 * backoff period of 500ms. A source that returns {@code READY} is immediately
 * invoked. Note that {@code BACKOFF} is merely a hint to the runner; it need
 * not be strictly adhered to.
 * </p>
 */
public class PollableSourceRunner extends SourceRunner {

	// 未完成
	private static final Logger LOG = LoggerFactory.getLogger(PollableSourceRunner.class);

	private AtomicBoolean shouldStop;

	private CounterGroup counterGroup;
	private PollingRunner pollingRunner;
	private Thread runnerThread;
	private LifecycleState lifecycleState;

	/**
	 * 构造函数
	 */
	public PollableSourceRunner() {
		// TODO Auto-generated constructor stub
		this.shouldStop = new AtomicBoolean();// 初始化为false
		this.counterGroup = new CounterGroup();
		this.lifecycleState = LifecycleState.IDLE;
	}

	@Override
	public void start() {
		// TODO Auto-generated method stub
		PollableSource source = (PollableSource) this.getSource();
		ChannelProcessor cp = source.getChannelProcessor();
		cp.initialize();
		source.start();

		this.pollingRunner = new PollingRunner();
		this.pollingRunner.pollableSource = source;
		this.pollingRunner.countGroup = this.counterGroup;
		this.pollingRunner.shouldStop = this.shouldStop;

		this.runnerThread = new Thread(this.pollingRunner);
		this.runnerThread.setName(this.getClass().getSimpleName() + "-" + source.getClass().getSimpleName() + "-" + source.getName());
		this.runnerThread.start();

		this.lifecycleState = LifecycleState.START;
	}

	@Override
	public void stop() {
		// TODO Auto-generated method stub
		this.pollingRunner.shouldStop.set(true);

		try {
			this.runnerThread.interrupt();
			this.runnerThread.join();

		} catch (InterruptedException e) {
			// TODO: handle exception
			LOG.warn("Interrupted while waiting for polling runner to stop. Please report this.", e);
			Thread.currentThread().interrupt();
		}

		Source source = this.getSource();
		source.stop();
		ChannelProcessor cp = source.getChannelProcessor();
		cp.close();

		this.lifecycleState = LifecycleState.STOP;
	}

	@Override
	public LifecycleState getLifecycleState() {
		// TODO Auto-generated method stub
		return this.lifecycleState;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "PollableSourceRunner: { source:" + this.getSource() + " counterGroup:" + this.counterGroup + " }";
	}

	public static class PollingRunner implements Runnable {

		private PollableSource pollableSource;
		private AtomicBoolean shouldStop;
		private CounterGroup countGroup;

		@Override
		public void run() {
			// TODO Auto-generated method stub
			LOG.debug("Polling runner starting. Source:{}", pollableSource);

			while (!this.shouldStop.get()) {
				this.countGroup.incrementAndGet("runner.polls");

				try {
					if (this.pollableSource.process().equals(PollableSource.Status.BACKOFF)) {
						this.countGroup.incrementAndGet("runner.backoffs");

						Thread.sleep(Math.min(
								this.countGroup.incrementAndGet("runner.backoffs.consecutive") * this.pollableSource.getBackOffSleepIncrement(),
								this.pollableSource.getMaxBackOffSleepInterval()));// consecutive
																					// 连续的
					} else {
						this.countGroup.set("runner.backoffs.consecutive", 0L);
					}
				} catch (InterruptedException e) {
					LOG.info("Source runner interrupted. Exiting");
					this.countGroup.incrementAndGet("runner.interruptions");
				} catch (EventDeliveryException e) {
					LOG.error("Unable to deliver event. Exception follows.", e);
					this.countGroup.incrementAndGet("runner.deliveryErrors");
				} catch (Exception e) {
					this.countGroup.incrementAndGet("runner.errors");
					LOG.error("Unhandled exception, logging and sleeping for " + this.pollableSource.getMaxBackOffSleepInterval() + "ms", e);
					try {
						Thread.sleep(this.pollableSource.getMaxBackOffSleepInterval());
					} catch (InterruptedException ex) {
						Thread.currentThread().interrupt();
					}
				}
			}

			LOG.debug("Polling runner exiting. Metrics:{}", this.countGroup);
		}
	}

	public static class Thread3 extends Thread {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			while (true) {
				if (Thread.interrupted()) {
					System.out.println("Someone interrupted me.");
				} else {
					System.out.println("Going...");
				}
				long now = System.currentTimeMillis();
				while (System.currentTimeMillis() - now < 1000) {
					// 为了避免Thread.sleep()而需要捕获InterruptedException而带来的理解上的困惑,
					// 此处用这种方法空转1秒
				}
			}
		}
	}

	public static class Thread4 extends Thread {
		private Thread parent;

		public Thread4(Thread parent) {
			this.parent = parent;
		}

		public void run() {
			while (true) {
				System.out.println("sub thread is running...");
				long now = System.currentTimeMillis();
				while (System.currentTimeMillis() - now < 2000) {
					// 为了避免Thread.sleep()而需要捕获InterruptedException而带来的理解上的困惑,
					// 此处用这种方法空转2秒
				}
				parent.interrupt();
			}
		}
	}

	public static class MyThread extends Thread {
		public void run() {
			int num = longTimeRunningNonInterruptMethod(2, 0);
			System.out.println("长时间任务运行结束,num=" + num);
			System.out.println("线程的中断状态:" + Thread.interrupted());
		}

		private static int longTimeRunningNonInterruptMethod(int count, int initNum) {
			for (int i = 0; i < count; i++) {
				for (int j = 0; j < Integer.MAX_VALUE; j++) {
					initNum++;
				}
			}
			return initNum;
		}
	}

	public static class MyThread2 extends Thread {
		public void run() {
			
			int num = -1;
			try {
//				Thread.sleep(1000);
				num = longTimeRunningInterruptMethod(2, 0);
			} catch (InterruptedException e) {
				System.out.println("线程被中断");
				throw new RuntimeException(e);
			}
			System.out.println("长时间任务运行结束,num=" + num);
			System.out.println("线程的中断状态:" + Thread.interrupted());
		}

		private static int longTimeRunningInterruptMethod(int count, int initNum) throws InterruptedException {
			for (int i = 0; i < count; i++) {
				TimeUnit.SECONDS.sleep(5);
			}
			return initNum;
		}
	}

	public static void main(String[] args) {
		// Thread3 t = new Thread3();
		// t.start();
		// try {
		// Thread.sleep(3000);
		// } catch (InterruptedException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		// t.interrupt();

		// Thread4 t = new Thread4(Thread.currentThread());
		// t.start();
		// try {
		// t.join();
		// } catch (InterruptedException e) {
		// System.out.println("Parent thread will die...");
		// }

		// Thread t = new MyThread();
		// t.start();
		// t.interrupt();
		// System.out.println("已调用线程的interrupt方法");

		Thread t = new MyThread2();
		t.start();
		t.interrupt();
		System.out.println("已调用线程的interrupt方法");
	}
}
