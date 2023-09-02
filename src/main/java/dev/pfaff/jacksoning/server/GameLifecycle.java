package dev.pfaff.jacksoning.server;

import net.minecraft.text.Text;

public enum GameLifecycle {
	NotStarted("not_started"),
	Running("running"),
	Ended("ended");

	public final Text text;

	private GameLifecycle(String id) {
		this.text = Text.translatable("message.jacksoning.game_lifecycle." + id).styled(s -> s.withBold(true));
	}
}
