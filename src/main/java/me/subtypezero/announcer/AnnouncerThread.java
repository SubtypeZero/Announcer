package me.subtypezero.announcer;

import java.util.Random;

public class AnnouncerThread extends Thread {
	private final Random random = new Random();
	private final Announcer plugin;
	private int last = 0;

	public AnnouncerThread(Announcer plugin) {
		this.plugin = plugin;
	}

	public void run() {
		if (plugin.isAnnouncerEnabled()) {
			int size = plugin.getMessages().size();

			if (plugin.isRandom()) {
				last = Math.abs(random.nextInt() % size);
			} else if (++last >= size) {
				last = 0;
			}

			if (last < size) {
				plugin.announce(last + 1);
			}
		}
	}
}