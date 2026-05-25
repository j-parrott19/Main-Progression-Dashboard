package com.mainframe;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

final class ProgressSnapshot
{
	private final List<GoalProgress> goals;
	private final List<CustomGoal> customGoals;
	private final String accountLabel;
	private final ProgressionPath progressionPath;
	private final boolean progressionPathChosen;
	private final String importStatusText;
	private final String accountSummaryText;

	ProgressSnapshot(
		List<GoalProgress> goals,
		List<CustomGoal> customGoals,
		String accountLabel,
		ProgressionPath progressionPath,
		boolean progressionPathChosen,
		String importStatusText,
		String accountSummaryText)
	{
		this.goals = Collections.unmodifiableList(new ArrayList<>(goals));
		this.customGoals = Collections.unmodifiableList(new ArrayList<>(customGoals));
		this.accountLabel = accountLabel;
		this.progressionPath = progressionPath;
		this.progressionPathChosen = progressionPathChosen;
		this.importStatusText = importStatusText;
		this.accountSummaryText = accountSummaryText;
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
		return importStatusText;
	}

	String getAccountSummaryText()
	{
		return accountSummaryText;
	}
}
