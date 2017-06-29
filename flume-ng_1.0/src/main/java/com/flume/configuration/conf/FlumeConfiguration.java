package com.flume.configuration.conf;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.flume.configuration.Context;

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
	
	

	public static class AgentConfiguration {
		private final String agentName;

		private String sources;
		private String sinks;
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
