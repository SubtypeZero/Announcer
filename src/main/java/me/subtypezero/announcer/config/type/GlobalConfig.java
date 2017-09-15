package me.subtypezero.announcer.config.type;

import me.subtypezero.announcer.config.ConfigManager;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
public class GlobalConfig {
	@Setting(value = "Config-Version")
	private int version;
	@Setting(value = "Announcement")
	private AnnouncementConfig announcementConfig;

	public GlobalConfig() {
		version = ConfigManager.CONFIG_VERSION;
		announcementConfig = new AnnouncementConfig();
	}

	public int getVersion() {
		return version;
	}

	public AnnouncementConfig getAnnouncementConfig() {
		return announcementConfig;
	}
}
