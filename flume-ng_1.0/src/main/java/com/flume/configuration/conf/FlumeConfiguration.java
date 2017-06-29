package com.flume.configuration.conf;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.flume.configuration.Context;
import com.flume.configuration.conf.FlumeConfigurationError.ErrorOrWarning;

/**
 * <p>
 * FlumeConfiguration is an in memory representation of the hierarchical(分层的)
 * configuration namespace required by the ConfigurationProvider. This class is
 * instantiated(实例化) with a map or properties object which is parsed to
 * construct the hierarchy in memory. Once the entire set of properties have
 * been parsed and populated, a validation routine(例行的) is run that identifies
 * and removes invalid components.
 * </p>
 *
 * @see org.apache.flume.node.ConfigurationProvider
 *
 */
public class FlumeConfiguration {

	private static final Logger logger = LoggerFactory.getLogger(FlumeConfiguration.class);
	/**
	 * 
	 */
	private final Map<String, AgentConfiguration> agentConfigMap;
	private final LinkedList<FlumeConfigurationError> errors;
	/**
	 * 换行符，首先是找系统属性中是否有line.separator，若没有的话默认返回\n
	 */
	public static final String NEWLINE = System.getProperty("line.separator", "\n");
	/**
	 * 缩进(2个空格)
	 */
	public static final String INDENTSTEP = "  ";

	/**
	 * Agent配置
	 * 
	 * @author Administrator
	 *
	 */
	public static class AgentConfiguration {
		private final String agentName;

		private String sources;
		private String sinks;
		/**
		 * channel的集合，每个channel之间是空白字符作为分隔符
		 */
		private String channels;
		private String sinkgroups;

		private final Map<String, ComponentConfiguration> sourceConfigMap;
		private final Map<String, ComponentConfiguration> sinkConfigMap;
		private final Map<String, ComponentConfiguration> channelConfigMap;
		private final Map<String, ComponentConfiguration> sinkgroupConfigMap;

		private Map<String, Context> sourceContextMap;
		private Map<String, Context> sinkContextMap;
		private Map<String, Context> channelContextMap;
		private Map<String, Context> sinkGroupContextMap;

		private Set<String> sourceSet;
		private Set<String> sinkSet;
		private Set<String> channelSet;
		private Set<String> sinkgroupSet;

		private final List<FlumeConfigurationError> errorList;

		/**
		 * 构造函数
		 * 
		 * @param agentName
		 * @param errorList
		 */
		private AgentConfiguration(String agentName, List<FlumeConfigurationError> errorList) {
			// TODO Auto-generated constructor stub
			this.agentName = agentName;
			this.errorList = errorList;

			sourceConfigMap = new HashMap<String, ComponentConfiguration>();
			sinkConfigMap = new HashMap<String, ComponentConfiguration>();
			channelConfigMap = new HashMap<String, ComponentConfiguration>();
			sinkgroupConfigMap = new HashMap<String, ComponentConfiguration>();
			sourceContextMap = new HashMap<String, Context>();
			sinkContextMap = new HashMap<String, Context>();
			channelContextMap = new HashMap<String, Context>();
			sinkGroupContextMap = new HashMap<String, Context>();
		}

		/**
		 * <p>
		 * Checks the validity of the agent configuration. This method assumes
		 * that all necessary configuration keys have been populated(填充) and are
		 * ready for validation.
		 * </p>
		 * <p>
		 * During the validation process, the components with invalid
		 * configuration will be dropped. If at the end of this process, the
		 * minimum necessary components are not available, the configuration
		 * itself will be considered invalid.
		 * </p>
		 *
		 * @return true if the configuration is valid, false otherwise
		 */
		private boolean isValid() {
			logger.debug("Starting validation of configuration for agent: {}", agentName);

			if (logger.isDebugEnabled() && LogPrivacyUtil.allowLogPrintConfig()) {
				logger.debug("Initial configuration: {}", this.getPrevalidationConfig());
			}

			// Make sure that at least one channel is specified
			if (channels == null || channels.trim().length() == 0) {
				logger.warn("Agent configuration for '" + agentName + "' does not contain any channels. Marking it as invalid.");

				errorList.add(new FlumeConfigurationError(agentName, BasicConfigurationConstants.CONFIG_CHANNELS,
						FlumeConfigurationErrorType.PROPERTY_VALUE_NULL, ErrorOrWarning.ERROR));
				return false;
			}

			channelSet = new HashSet<String>(Arrays.asList(channels.split("\\s+")));// \\s+用来匹配任意空白字符

		}

		/**
		 * 获得组装Agent的配置信息(AgentName、Sources、Channels、Sinks)，key-value的形式b
		 * 
		 * @return
		 */
		public String getPrevalidationConfig() {
			StringBuilder sb = new StringBuilder("AgentConfiguration[");
			sb.append(agentName).append("]").append(NEWLINE).append("SOURCES: ");
			sb.append(sourceContextMap).append(NEWLINE).append("CHANNELS: ");
			sb.append(channelContextMap).append(NEWLINE).append("SINKS: ");
			sb.append(sinkContextMap).append(NEWLINE);

			return sb.toString();
		}

		/**
		 * If it is a known component it will do the full validation required
		 * for that component, else it will do the validation required for that
		 * class.
		 */
		private Set<String> validateChannels(Set<String> channelSet) {
			Iterator<String> iter = channelSet.iterator();
			Map<String, Context> newContextMap = new HashMap<String, Context>();
			channelc
		}

		/**************************** Get/Set方法 ****************************************/
		public Map<String, Context> getSourceContextMap() {
			return sourceContextMap;
		}

		public Map<String, Context> getSinkContextMap() {
			return sinkContextMap;
		}

		public Map<String, Context> getChannelContextMap() {
			return channelContextMap;
		}

		public Set<String> getSourceSet() {
			return sourceSet;
		}

		public Set<String> getSinkSet() {
			return sinkSet;
		}

		public Set<String> getChannelSet() {
			return channelSet;
		}

		public Set<String> getSinkgroupSet() {
			return sinkgroupSet;
		}

		public Map<String, ComponentConfiguration> getSourceConfigMap() {
			return sourceConfigMap;
		}

		public Map<String, ComponentConfiguration> getSinkConfigMap() {
			return sinkConfigMap;
		}

		public Map<String, ComponentConfiguration> getChannelConfigMap() {
			return channelConfigMap;
		}

		public Map<String, ComponentConfiguration> getSinkgroupConfigMap() {
			return sinkgroupConfigMap;
		}
	}
}
