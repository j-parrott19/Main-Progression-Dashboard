package com.mainframe;

import java.util.Collections;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import net.runelite.api.Quest;
import net.runelite.api.Skill;

class AccountProgressSnapshot implements ProgressContext
{
	private final Map<Skill, Integer> skillLevels;
	private final Set<Quest> completedQuests;
	private final Set<String> completedManualKeys;
	private final int combatLevel;
	private final int totalLevel;
	private final String accountType;
	private final ProgressionPath progressionPath;
	private final boolean progressionPathChosen;
	private final HiscoreAccountData hiscoreData;

	AccountProgressSnapshot(
		Map<Skill, Integer> skillLevels,
		Set<Quest> completedQuests,
		Set<String> completedManualKeys,
		int combatLevel,
		int totalLevel,
		String accountType,
		ProgressionPath progressionPath,
		boolean progressionPathChosen,
		HiscoreAccountData hiscoreData)
	{
		EnumMap<Skill, Integer> skillCopy = new EnumMap<>(Skill.class);
		skillCopy.putAll(skillLevels);
		this.skillLevels = Collections.unmodifiableMap(skillCopy);
		this.completedQuests = Collections.unmodifiableSet(new HashSet<>(completedQuests));
		this.completedManualKeys = Collections.unmodifiableSet(new HashSet<>(completedManualKeys));
		this.combatLevel = combatLevel;
		this.totalLevel = totalLevel;
		this.accountType = accountType;
		this.progressionPath = progressionPath;
		this.progressionPathChosen = progressionPathChosen;
		this.hiscoreData = hiscoreData == null ? HiscoreAccountData.NOT_REQUESTED : hiscoreData;
	}

	static AccountProgressSnapshot fromContext(ProgressContext context, ProgressionPath progressionPath)
	{
		Map<Skill, Integer> skillLevels = new EnumMap<>(Skill.class);
		for (Skill skill : Skill.values())
		{
			skillLevels.put(skill, context.getRealSkillLevel(skill));
		}
		return new AccountProgressSnapshot(skillLevels, Collections.emptySet(), Collections.emptySet(), 0, 0, "Unknown",
			progressionPath, true, HiscoreAccountData.NOT_REQUESTED)
		{
			@Override
			public boolean isQuestComplete(Quest quest)
			{
				return context.isQuestComplete(quest);
			}

			@Override
			public boolean isManualComplete(String manualKey)
			{
				return context.isManualComplete(manualKey);
			}
		};
	}

	@Override
	public int getRealSkillLevel(Skill skill)
	{
		int localLevel = skillLevels.getOrDefault(skill, 1);
		int hiscoreLevel = hiscoreData.getSkillLevels().getOrDefault(skill, 1);
		return Math.max(localLevel, hiscoreLevel);
	}

	@Override
	public boolean isQuestComplete(Quest quest)
	{
		return completedQuests.contains(quest);
	}

	@Override
	public boolean isManualComplete(String manualKey)
	{
		return completedManualKeys.contains(manualKey);
	}

	int getCombatLevel()
	{
		return combatLevel;
	}

	int getTotalLevel()
	{
		return totalLevel;
	}

	String getAccountType()
	{
		return accountType;
	}

	ProgressionPath getProgressionPath()
	{
		return progressionPath;
	}

	boolean isProgressionPathChosen()
	{
		return progressionPathChosen;
	}

	String getImportStatusText()
	{
		return hiscoreData.getStatusText();
	}

	int getHiscoreMetric(String key)
	{
		return hiscoreData.getMetric(key);
	}
}
