package com.mainframe;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

final class ProgressSnapshot
{
	private final List<GoalProgress> goals;
	private final List<CustomGoal> customGoals;
	private final String accountLabel;

	ProgressSnapshot(List<GoalProgress> goals, List<CustomGoal> customGoals, String accountLabel)
	{
		this.goals = Collections.unmodifiableList(new ArrayList<>(goals));
		this.customGoals = Collections.unmodifiableList(new ArrayList<>(customGoals));
		this.accountLabel = accountLabel;
	}

	List<GoalProgress> getGoals()
	{
		return goals;
	}

	List<CustomGoal> getCustomGoals()
	{
		return customGoals;
	}

	String getAccountLabel()
	{
		return accountLabel;
	}
}

