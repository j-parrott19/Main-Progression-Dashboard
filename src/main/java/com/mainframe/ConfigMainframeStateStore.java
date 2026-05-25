package com.mainframe;

import java.util.Arrays;
import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;
import net.runelite.client.config.ConfigManager;

final class ConfigMainframeStateStore implements MainframeStateStore
{
	static final String GROUP = "mainframe";
	private static final String MANUAL_PREFIX = "manual.";
	private static final String CUSTOM_PREFIX = "custom.";
	private final ConfigManager configManager;

	ConfigMainframeStateStore(ConfigManager configManager)
	{
		this.configManager = configManager;
	}

	@Override
	public Set<String> getCompletedManualKeys(String scope)
	{
		String value = configManager.getConfiguration(GROUP, MANUAL_PREFIX + scope);
		if (value == null || value.trim().isEmpty())
		{
			return new TreeSet<>();
		}
		return Arrays.stream(value.split(","))
			.map(String::trim)
			.filter(token -> !token.isEmpty())
			.collect(Collectors.toCollection(TreeSet::new));
	}

	@Override
	public void setManualComplete(String scope, String manualKey, boolean complete)
	{
		Set<String> keys = getCompletedManualKeys(scope);
		if (complete)
		{
			keys.add(manualKey);
		}
		else
		{
			keys.remove(manualKey);
		}
		configManager.setConfiguration(GROUP, MANUAL_PREFIX + scope, String.join(",", keys));
	}

	@Override
	public java.util.List<CustomGoal> getCustomGoals(String scope)
	{
		String value = configManager.getConfiguration(GROUP, CUSTOM_PREFIX + scope);
		if (value == null || value.trim().isEmpty())
		{
			return Collections.emptyList();
		}
		return CustomGoalCodec.decode(value);
	}

	@Override
	public void saveCustomGoals(String scope, java.util.List<CustomGoal> goals)
	{
		configManager.setConfiguration(GROUP, CUSTOM_PREFIX + scope, CustomGoalCodec.encode(goals));
	}
}

