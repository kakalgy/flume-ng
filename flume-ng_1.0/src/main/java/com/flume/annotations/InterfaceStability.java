package com.flume.annotations;

import java.lang.annotation.Documented;

/**
 * Annotation to inform users of how much to rely on a particular package, class
 * or method not changing over time. Currently the stability can be
 * {@link Stable}, {@link Evolving} or {@link Unstable}. <br>
 *
 * <ul>
 * <li>All classes that are annotated with {@link Public} or
 * {@link LimitedPrivate} must have InterfaceStability annotation.</li>
 * <li>Classes that are {@link Private} are to be considered unstable unless a
 * different InterfaceStability annotation states otherwise.</li>
 * <li>Incompatible changes must not be made to classes marked as stable.</li>
 * </ul>
 * 
 * @author
 *
 */
@InterfaceAudience.Public
@InterfaceStability.Evolving
public class InterfaceStability {
	/**
	 * Can evolve(发展,进化) while retaining(保持) compatibility(通用性) for minor
	 * release boundaries.; can break compatibility only at major release (ie.
	 * at m.0).
	 */
	@Documented
	public @interface Stable {
	};

	/**
	 * Evolving, but can break compatibility at minor release (i.e. m.x)
	 */
	@Documented
	public @interface Evolving {
	};

	/**
	 * No guarantee is provided as to reliability or stability across any level
	 * of release granularity.
	 */
	@Documented
	public @interface Unstable {
	};
}
