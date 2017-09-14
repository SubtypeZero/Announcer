package me.subtypezero.announcer.config.type;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@ConfigSerializable
public class AnnouncementConfig {
	@Setting(value = "enabled", comment = "Whether announcements should be enabled or disabled.")
	private boolean enabled = true;
	@Setting(value = "random", comment = "Whether announcements should be random or sequential.")
	private boolean random = false;
	@Setting(value = "prefix", comment = "The prefix to display while broadcasting announcements.")
	private String prefix = "&c[Announcement]";
	@Setting(value = "interval", comment = "The time interval (in seconds) between announcements.")
	private int interval = 60;
	@Setting(value = "Messages", comment = "The list of announcements to broadcast")
	private List<String> messages = new ArrayList<>(Arrays.asList(
			"Use /announce for information about configuring this plugin.",
			"Created by Deckerz",
			"Maintained by ThunderGemios10",
			"Ported to Sponge by SubtypeZero"));

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public boolean isRandom() {
		return random;
	}

	public void setRandom(boolean random) {
		this.random = random;
	}

	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public int getInterval() {
		return (interval < 1) ? 60 : interval;
	}

	public void setInterval(int interval) {
		if (interval > 0) {
			this.interval = interval;
		}
	}

	public List<String> getMessages() {
		return messages;
	}

	public void setMessages(List<String> messages) {
		if (messages != null) {
			this.messages = messages;
		}
	}
}
