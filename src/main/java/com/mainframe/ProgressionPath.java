package com.mainframe;

enum ProgressionPath
{
	BALANCED("Balanced"),
	BOSSING("Bossing"),
	PVP("PvP"),
	COMPLETION("Completion"),
	MAXING("Maxing");

	private final String displayName;

	ProgressionPath(String displayName)
	{
		this.displayName = displayName;
	}

	String getDisplayName()
	{
		return displayName;
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
			return BALANCED;
		}

		for (ProgressionPath path : values())
		{
			if (path.name().equalsIgnoreCase(value.trim()) || path.displayName.equalsIgnoreCase(value.trim()))
			{
				return path;
			}
		}

		return BALANCED;
	}
}
