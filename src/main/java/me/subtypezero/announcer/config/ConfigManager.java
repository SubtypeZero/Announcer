package me.subtypezero.announcer.config;

import me.subtypezero.announcer.Announcer;
import me.subtypezero.announcer.config.type.GlobalConfig;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.commented.SimpleCommentedConfigurationNode;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import ninja.leaping.configurate.objectmapping.ObjectMapper;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import org.slf4j.Logger;

import java.io.IOException;

public class ConfigManager {
	public static final int CONFIG_VERSION = 1;
	private final Announcer PLUGIN = Announcer.getInstance();
	private final Logger LOGGER = PLUGIN.getLogger();
	private ObjectMapper<GlobalConfig>.BoundInstance configMapper;
	private ConfigurationLoader<CommentedConfigurationNode> loader;

	public ConfigManager(ConfigurationLoader<CommentedConfigurationNode> loader) {
		this.loader = loader;
		try {
			this.configMapper = ObjectMapper.forObject(PLUGIN.getConfig());
		} catch (ObjectMappingException e) {
			e.printStackTrace();
		}
		this.load();
	}

	/**
	 * Saves the serialized config to file
	 */
	public void save() {
		try {
			SimpleCommentedConfigurationNode out = SimpleCommentedConfigurationNode.root();
			this.configMapper.serialize(out);
			this.loader.save(out);
		} catch (ObjectMappingException | IOException e) {
			LOGGER.error(String.format("Failed to save config.\r\n %s", e.getMessage()));
		}
	}

	/**
	 * Load the config into serialized objects, for the configMapper
	 */
	public void load() {
		try {
			this.configMapper.populate(this.loader.load());
		} catch (ObjectMappingException | IOException e) {
			LOGGER.error(String.format("Failed to load config.\r\n %s", e.getMessage()));
		}
	}
}
