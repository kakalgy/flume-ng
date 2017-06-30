package com.flume.configuration.conf;

import java.util.Locale;

import com.flume.configuration.conf.ComponentConfiguration.ComponentType;

/**
 * 
 * @author
 *
 */
public class ComponentConfigurationFactory {

	/**
	 * 
	 * @param componentName
	 * @param type
	 * @param componentType
	 * @return
	 * @throws ConfigurationException
	 */
	public static ComponentConfiguration create(String componentName, String type, ComponentType componentType) throws ConfigurationException {
		Class<? extends ComponentConfiguration> confType = null;

		if (type == null) {
			throw new ConfigurationException("Cannot create component without knowing its type!");
		}

		try {
			confType = (Class<? extends ComponentConfiguration>) Class.forName(type);
			return confType.getConstructor(String.class).newInstance(type);
		} catch (Exception ignored) {
			try {
				type = type.toUpperCase(Locale.ENGLISH);
				switch (componentName) {
				case SOURCE:
return SourceConfigurationType
					break;

				default:
					break;
				}
			} catch (ConfigurationException e) {
				throw e;
			} catch (Exception e) {
				throw new ConfigurationException(
						"Could not create configuration! " + " Due to " + e.getClass().getSimpleName() + ": " + e.getMessage(), e);
			}
		}
	}
}
