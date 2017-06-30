package com.flume.configuration.conf.sink;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.flume.configuration.Context;
import com.flume.configuration.conf.BasicConfigurationConstants;
import com.flume.configuration.conf.ComponentConfiguration;
import com.flume.configuration.conf.ConfigurationException;

public class SinkGroupConfiguration extends ComponentConfiguration {

	private Context processorContext;
	private List<String> sinks;
	private SinkProcessorConfiguration processorConf;

	public SinkGroupConfiguration(String componentName) {
		// TODO Auto-generated constructor stub
		super(componentName);
		this.setType(ComponentType.SINKGROUP.getComponentType());
	}

	@Override
	public void configure(Context context) throws ConfigurationException {
		// TODO Auto-generated method stub
		super.configure(context);

		sinks = Arrays.asList(context.getString(BasicConfigurationConstants.CONFIG_SINKS).split("\\s+"));
		// 得到 参数的副本
		Map<String, String> params = context.getSubProperties(BasicConfigurationConstants.CONFIG_SINK_PROCESSOR_PREFIX);
		
		this.processorContext = new Context();
		this.processorContext.putAll(params);
		
		SinkProcessorType spType = this.getKnownSinkProcessor(processorContext.getString(BasicConfigurationConstants.CONFIG_TYPE));
		
		if(spType != null){
			processorConf = (SinkProcessorConfiguration)com
		}
	}

	/**
	 * 返回已在SinkProcessorType中定义的类型，若没有找到与参数对应的，则返回null
	 * 
	 * @param type
	 *            SinkProcessorType枚举类型的值 或者值所代表的类全名字符串
	 * @return
	 */
	private SinkProcessorType getKnownSinkProcessor(String type) {
		SinkProcessorType[] values = SinkProcessorType.values();
		for (SinkProcessorType value : values) {
			if (value.toString().equalsIgnoreCase(type)) {
				return value;
			}
			String sinkProcessorClassName = value.getProcessorClassName();
			if (sinkProcessorClassName != null && sinkProcessorClassName.equalsIgnoreCase(type)) {
				return value;
			}
		}
		return null;
	}

	/******************************** Get/Set方法 **************************************/
	public List<String> getSinks() {
		return sinks;
	}

	public void setSinks(List<String> sinks) {
		this.sinks = sinks;
	}

	public Context getProcessorContext() {
		return processorContext;
	}

	public void setProcessorContext(Context processorContext) {
		this.processorContext = processorContext;
	}

	public SinkProcessorConfiguration getSinkProcessorConfiguration() {
		return this.processorConf;
	}

	public void setSinkProcessorConfiguration(SinkProcessorConfiguration conf) {
		this.processorConf = conf;
	}
}
