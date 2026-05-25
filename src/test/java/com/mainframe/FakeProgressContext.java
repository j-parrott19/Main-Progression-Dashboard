package com.mainframe;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import net.runelite.api.Quest;
import net.runelite.api.Skill;

final class FakeProgressContext implements ProgressContext
{
	private final Map<Skill, Integer> skills = new HashMap<>();
	private final Set<Quest> quests = new HashSet<>();
	private final Set<String> manual = new HashSet<>();

	FakeProgressContext skill(Skill skill, int level)
	{
		skills.put(skill, level);
		return this;
	}

	FakeProgressContext quest(Quest quest)
	{
		quests.add(quest);
		return this;
	}

	FakeProgressContext manual(String key)
	{
		manual.add(key);
		return this;
	}

	@Override
	public int getRealSkillLevel(Skill skill)
	{
		return skills.getOrDefault(skill, 1);
	}

	@Override
	public boolean isQuestComplete(Quest quest)
	{
		return quests.contains(quest);
	}

	@Override
	public boolean isManualComplete(String manualKey)
	{
		return manual.contains(manualKey);
	}
}

