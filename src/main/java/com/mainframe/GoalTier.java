package com.mainframe;

enum GoalTier
{
	EARLY("Early", 0),
	MID("Mid", 1),
	LATE("Late", 2);

	private final String displayName;
	private final int sortOrder;

	GoalTier(String displayName, int sortOrder)
	{
		this.displayName = displayName;
		this.sortOrder = sortOrder;
	}

	String getDisplayName()
	{
		return displayName;
	}

	int getSortOrder()
	{
		return sortOrder;
	}
}

