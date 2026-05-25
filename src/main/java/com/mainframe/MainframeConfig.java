package com.mainframe;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup(ConfigMainframeStateStore.GROUP)
public interface MainframeConfig extends Config
{
	@ConfigItem(
		keyName = "showCompletedGoals",
		name = "Show completed goals",
		description = "Show completed roadmap goals in the Mainframe dashboard"
	)
	default boolean showCompletedGoals()
	{
		return true;
	}
}

