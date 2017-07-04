package com.flume.core.annotations;

import java.lang.annotation.Documented;

/**
 * <p>
 * Java注解是附加在代码中的一些元信息，用于一些工具在编译、运行时进行解析和使用，起到说明、配置的功能。注解不会也不能影响代码的实际逻辑，仅仅起到辅助性的作用。
 * </p>
 * <p>
 * Documented 注解表明这个注解应该被 javadoc工具记录. 默认情况下,javadoc是不包括注解的.
 * 但如果声明注解时指定了 @Documented,则它会被 javadoc 之类的工具处理, 所以注解类型信息也会被包括在生成的文档中.
 * </p>
 * Annotation to inform(通知) users of a package, class or method's intended（有意的）
 * audience. Currently the audience can be {@link Public},
 * {@link LimitedPrivate} or {@link Private}. <br>
 * All public classes must have InterfaceAudience annotation. <br>
 * <ul>
 * <li>Public classes that are not marked with this annotation must be
 * considered by default as {@link Private}.</li>
 *
 * <li>External applications must only use classes that are marked
 * {@link Public}. Avoid using non public classes as these classes could be
 * removed or change in incompatible ways.</li>
 *
 * <li>Flume projects must only use classes that are marked
 * {@link LimitedPrivate} or {@link Public}</li>
 *
 * <li>Methods may have a different annotation that it is more restrictive
 * compared to the audience classification of the class. Example: A class might
 * be {@link Public}, but a method may be {@link LimitedPrivate}</li>
 * </ul>
 */
@InterfaceAudience.Public
@InterfaceStability.Evolving
public class InterfaceAudience {

	private InterfaceAudience() {
	} // Audience can't exist on its own

	/**
	 * Intended for use by any project or application.
	 */
	@Documented
	public @interface Public {
	};

	/**
	 * Intended only for the project(s) specified in the annotation. For
	 * example, "Common", "HDFS", "MapReduce", "ZooKeeper", "HBase".
	 */
	@Documented
	public @interface LimitedPrivate {
		String[] value();
	};

	/**
	 * Intended for use only within Flume
	 */
	@Documented
	public @interface Private {
	};

}
