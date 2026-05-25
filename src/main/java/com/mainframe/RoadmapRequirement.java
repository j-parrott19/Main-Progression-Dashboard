package com.mainframe;

import java.util.Objects;
import net.runelite.api.Quest;
import net.runelite.api.Skill;

final class RoadmapRequirement
{
	private final RequirementType type;
	private final Skill skill;
	private final int level;
	private final Quest quest;
	private final String manualKey;
	private final String label;

	private RoadmapRequirement(RequirementType type, Skill skill, int level, Quest quest, String manualKey, String label)
	{
		this.type = type;
		this.skill = skill;
		this.level = level;
		this.quest = quest;
		this.manualKey = manualKey;
		this.label = Objects.requireNonNull(label, "label");
	}

	static RoadmapRequirement skill(Skill skill, int level)
	{
		return new RoadmapRequirement(RequirementType.SKILL_LEVEL, Objects.requireNonNull(skill, "skill"), level, null, null,
			level + " " + displaySkill(skill));
	}

	static RoadmapRequirement quest(Quest quest)
	{
		return new RoadmapRequirement(RequirementType.QUEST_COMPLETE, null, 0, Objects.requireNonNull(quest, "quest"), null,
			quest.getName());
	}

	static RoadmapRequirement manual(String manualKey, String label)
	{
		return new RoadmapRequirement(RequirementType.MANUAL_UNLOCK, null, 0, null, Objects.requireNonNull(manualKey, "manualKey"), label);
	}

	static RoadmapRequirement text(String label)
	{
		return new RoadmapRequirement(RequirementType.TEXT_ONLY, null, 0, null, null, label);
	}

	RequirementType getType()
	{
		return type;
	}

	Skill getSkill()
	{
		return skill;
	}

	int getLevel()
	{
		return level;
	}

	Quest getQuest()
	{
		return quest;
	}

	String getManualKey()
	{
		return manualKey;
	}

	String getLabel()
	{
		return label;
	}

	boolean isComplete(ProgressContext context)
	{
		switch (type)
		{
			case SKILL_LEVEL:
				return context.getRealSkillLevel(skill) >= level;
			case QUEST_COMPLETE:
				return context.isQuestComplete(quest);
			case MANUAL_UNLOCK:
				return context.isManualComplete(manualKey);
			case TEXT_ONLY:
			default:
				return false;
		}
	}

	private static String displaySkill(Skill skill)
	{
		String lower = skill.name().toLowerCase().replace('_', ' ');
		StringBuilder result = new StringBuilder(lower.length());
		boolean capitalize = true;
		for (int i = 0; i < lower.length(); i++)
		{
			char c = lower.charAt(i);
			if (capitalize && Character.isLetter(c))
			{
				result.append(Character.toUpperCase(c));
				capitalize = false;
			}
			else
			{
				result.append(c);
			}
			if (c == ' ')
			{
				capitalize = true;
			}
		}
		return result.toString();
	}
}

