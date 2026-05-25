package com.mainframe;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

final class GoalProgress
{
	private final RoadmapGoal goal;
	private final boolean complete;
	private final boolean blocked;
	private final int completedRequirements;
	private final int totalRequirements;
	private final List<String> missingRequirements;
	private final String statusText;
	private boolean nextRecommended;

	GoalProgress(
		RoadmapGoal goal,
		boolean complete,
		boolean blocked,
		int completedRequirements,
		int totalRequirements,
		List<String> missingRequirements,
		String statusText)
	{
		this.goal = goal;
		this.complete = complete;
		this.blocked = blocked;
		this.completedRequirements = completedRequirements;
		this.totalRequirements = totalRequirements;
		this.missingRequirements = Collections.unmodifiableList(new ArrayList<>(missingRequirements));
		this.statusText = statusText;
	}

	RoadmapGoal getGoal()
	{
		return goal;
	}

	boolean isComplete()
	{
		return complete;
	}

	boolean isBlocked()
	{
		return blocked;
	}

	int getCompletedRequirements()
	{
		return completedRequirements;
	}

	int getTotalRequirements()
	{
		return totalRequirements;
	}

	List<String> getMissingRequirements()
	{
		return missingRequirements;
	}

	String getStatusText()
	{
		return statusText;
	}

	boolean isNextRecommended()
	{
		return nextRecommended;
	}

	void setNextRecommended(boolean nextRecommended)
	{
		this.nextRecommended = nextRecommended;
	}

	double getCompletionRatio()
	{
		if (totalRequirements == 0)
		{
			return complete ? 1.0d : 0.0d;
		}
		return (double) completedRequirements / (double) totalRequirements;
	}
}

