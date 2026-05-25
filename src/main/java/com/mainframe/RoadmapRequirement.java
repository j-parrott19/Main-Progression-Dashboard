package com.mainframe;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import net.runelite.api.Quest;
import net.runelite.api.Skill;

final class RoadmapRequirement
{
	private final RequirementType type;
	private final Skill skill;
	private final List<Skill> skills;
	private final int level;
	private final Quest quest;
	private final String manualKey;
	private final String label;
	private final List<RoadmapRequirement> options;

	private RoadmapRequirement(
		RequirementType type,
		Skill skill,
		List<Skill> skills,
		int level,
		Quest quest,
		String manualKey,
		String label,
		List<RoadmapRequirement> options)
	{
		this.type = type;
		this.skill = skill;
		this.skills = Collections.unmodifiableList(new ArrayList<>(skills));
		this.level = level;
		this.quest = quest;
		this.manualKey = manualKey;
		this.label = Objects.requireNonNull(label, "label");
		this.options = Collections.unmodifiableList(new ArrayList<>(options));
	}

	static RoadmapRequirement skill(Skill skill, int level)
	{
		return new RoadmapRequirement(RequirementType.SKILL_LEVEL, Objects.requireNonNull(skill, "skill"), Collections.emptyList(),
			level, null, null, level + " " + displaySkill(skill), Collections.emptyList());
	}

	static RoadmapRequirement skillTotal(String label, int level, Skill... skills)
	{
		if (skills.length == 0)
		{
			throw new IllegalArgumentException("skills must not be empty");
		}
		return new RoadmapRequirement(RequirementType.SKILL_TOTAL, null, Arrays.asList(skills), level, null, null,
			Objects.requireNonNull(label, "label"), Collections.emptyList());
	}

	static RoadmapRequirement any(String label, RoadmapRequirement... options)
	{
		if (options.length == 0)
		{
			throw new IllegalArgumentException("options must not be empty");
		}
		return new RoadmapRequirement(RequirementType.ANY, null, Collections.emptyList(), 0, null, null,
			Objects.requireNonNull(label, "label"), Arrays.asList(options));
	}

	static RoadmapRequirement quest(Quest quest)
	{
		return new RoadmapRequirement(RequirementType.QUEST_COMPLETE, null, Collections.emptyList(), 0,
			Objects.requireNonNull(quest, "quest"), null, quest.getName(), Collections.emptyList());
	}

	static RoadmapRequirement manual(String manualKey, String label)
	{
		return new RoadmapRequirement(RequirementType.MANUAL_UNLOCK, null, Collections.emptyList(), 0, null,
			Objects.requireNonNull(manualKey, "manualKey"), label, Collections.emptyList());
	}

	static RoadmapRequirement text(String label)
	{
		return new RoadmapRequirement(RequirementType.TEXT_ONLY, null, Collections.emptyList(), 0, null, null, label, Collections.emptyList());
	}

	RequirementType getType()
	{
		return type;
	}

	Skill getSkill()
	{
		return skill;
	}

	List<Skill> getSkills()
	{
		return skills;
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

	List<RoadmapRequirement> getOptions()
	{
		return options;
	}

	boolean isComplete(ProgressContext context)
	{
		switch (type)
		{
			case SKILL_LEVEL:
				return context.getRealSkillLevel(skill) >= level;
			case SKILL_TOTAL:
				return skills.stream()
					.mapToInt(context::getRealSkillLevel)
					.sum() >= level;
			case ANY:
				return options.stream().anyMatch(option -> option.isComplete(context));
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
