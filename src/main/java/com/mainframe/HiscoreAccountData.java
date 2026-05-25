package com.mainframe;

import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import net.runelite.api.Skill;

final class HiscoreAccountData
{
	static final HiscoreAccountData NOT_REQUESTED = new HiscoreAccountData(false, "Local account data", Collections.emptyMap(), Collections.emptyMap());
	static final HiscoreAccountData UNAVAILABLE = new HiscoreAccountData(false, "Hiscores unavailable", Collections.emptyMap(), Collections.emptyMap());

	private final boolean available;
	private final String statusText;
	private final Map<Skill, Integer> skillLevels;
	private final Map<String, Integer> metrics;

	HiscoreAccountData(boolean available, String statusText, Map<Skill, Integer> skillLevels, Map<String, Integer> metrics)
	{
		this.available = available;
		this.statusText = statusText;
		EnumMap<Skill, Integer> skillCopy = new EnumMap<>(Skill.class);
		skillCopy.putAll(skillLevels);
		this.skillLevels = Collections.unmodifiableMap(skillCopy);
		this.metrics = Collections.unmodifiableMap(new HashMap<>(metrics));
	}

	boolean isAvailable()
	{
		return available;
	}

	String getStatusText()
	{
		return statusText;
	}

	Map<Skill, Integer> getSkillLevels()
	{
		return skillLevels;
	}

	int getMetric(String key)
	{
		return metrics.getOrDefault(key, 0);
	}

	Map<String, Integer> getMetrics()
	{
		return metrics;
	}
}
