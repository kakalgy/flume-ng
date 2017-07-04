package com.flume.core.source;

import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.flume.core.CounterGroup;
import com.flume.core.SourceRunner;

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
public class PollableSourceRunner extends SourceRunner{

	//未完成
	private static final Logger LOG = LoggerFactory.getLogger(PollableSourceRunner.class);
	
	private AtomicBoolean shouldStop;
	
	private CounterGroup counterGroup;
	private 
}
