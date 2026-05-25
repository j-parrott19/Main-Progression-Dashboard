package com.mainframe;

enum ProgressionPath
{
	OPTIMAL_QUEST_COMPLETION("Optimal Quest Completion", "Prioritizes efficient quest and unlock routing for players who are not sure what to pick."),
	BALANCED("Balanced"),
	BOSSING("Bossing"),
	PVP("PvP"),
	COMPLETION("Completion"),
	MAXING("Maxing");

	private final String displayName;
	private final String description;

	ProgressionPath(String displayName)
	{
		this(displayName, displayName + " priorities.");
	}

	ProgressionPath(String displayName, String description)
	{
		this.displayName = displayName;
		this.description = description;
	}

	String getDisplayName()
	{
		return displayName;
	}

	String getDescription()
	{
		return description;
	}

	@Override
	public String toString()
	{
		return displayName;
	}

	static ProgressionPath fromConfigValue(String value)
	{
		if (value == null || value.trim().isEmpty())
		{
			return OPTIMAL_QUEST_COMPLETION;
		}

		for (ProgressionPath path : values())
		{
			if (path.name().equalsIgnoreCase(value.trim()) || path.displayName.equalsIgnoreCase(value.trim()))
			{
				return path;
			}
		}

		return OPTIMAL_QUEST_COMPLETION;
	}
}
