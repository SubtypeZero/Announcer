package me.subtypezero.announcer;

import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.serializer.TextSerializers;

public class ChatColorHelper {

	public static Text replaceColorCodes(String message) {
		return TextSerializers.FORMATTING_CODE.deserialize(message);
	}
}