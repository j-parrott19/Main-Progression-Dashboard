package com.mainframe;

enum GoalCategory
{
	NEXT_UNLOCKS("Next Unlocks"),
	ACCOUNT_UNLOCKS("Account Unlocks"),
	SIDE_QUEST_UNLOCKS("Side Quest Unlocks"),
	SKILL_TARGETS("Skill Targets"),
	GEAR_GOALS("Gear Goals"),
	QUEST_CLUSTERS("Quest Clusters"),
	CUSTOM_GOALS("Custom Goals");

	private final String displayName;

	GoalCategory(String displayName)
	{
		this.displayName = displayName;
	}

	String getDisplayName()
	{
		return displayName;
	}
}
