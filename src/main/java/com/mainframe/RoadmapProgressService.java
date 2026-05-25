package com.mainframe;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

final class RoadmapProgressService
{
	private static final int RECOMMENDATION_LIMIT = 5;
	private final List<RoadmapGoal> goals;

	RoadmapProgressService(List<RoadmapGoal> goals)
	{
		this.goals = goals;
	}

	List<GoalProgress> evaluate(ProgressContext context)
	{
		List<GoalProgress> progress = goals.stream()
			.map(goal -> evaluateGoal(goal, context))
			.collect(Collectors.toCollection(ArrayList::new));

		progress.stream()
			.filter(goal -> !goal.isComplete())
			.sorted(recommendationComparator())
			.limit(RECOMMENDATION_LIMIT)
			.forEach(goal -> goal.setNextRecommended(true));

		progress.sort(displayComparator());
		return progress;
	}

	private GoalProgress evaluateGoal(RoadmapGoal goal, ProgressContext context)
	{
		int total = goal.getRequirements().size();
		int complete = 0;
		List<String> missing = new ArrayList<>();
		for (RoadmapRequirement requirement : goal.getRequirements())
		{
			if (requirement.isComplete(context))
			{
				complete++;
			}
			else
			{
				missing.add(requirement.getLabel());
			}
		}

		boolean manuallyDone = !goal.isManualCompletion() || context.isManualComplete(goal.getId());
		boolean completeGoal = complete == total && manuallyDone;
		if (goal.isManualCompletion() && !context.isManualComplete(goal.getId()) && missing.stream().noneMatch(goal.getTitle()::equals))
		{
			if (!containsManualRequirementForGoal(goal))
			{
				missing.add("Mark " + goal.getTitle() + " complete");
				total++;
			}
		}

		boolean blocked = !completeGoal && total > 0 && complete == 0 && goal.getTier() != GoalTier.EARLY;
		String status = completeGoal ? "Done" : complete + "/" + total + " ready";
		return new GoalProgress(goal, completeGoal, blocked, complete, total, missing, status);
	}

	private static boolean containsManualRequirementForGoal(RoadmapGoal goal)
	{
		for (RoadmapRequirement requirement : goal.getRequirements())
		{
			if (requirement.getType() == RequirementType.MANUAL_UNLOCK && goal.getId().equals(requirement.getManualKey()))
			{
				return true;
			}
		}
		return false;
	}

	private static Comparator<GoalProgress> recommendationComparator()
	{
		return Comparator
			.comparing(GoalProgress::isBlocked)
			.thenComparing(GoalProgress::getCompletionRatio, Comparator.reverseOrder())
			.thenComparing(progress -> progress.getGoal().getTier().getSortOrder())
			.thenComparing(progress -> progress.getGoal().getPriority());
	}

	private static Comparator<GoalProgress> displayComparator()
	{
		return Comparator
			.comparing((GoalProgress progress) -> progress.getGoal().getCategory().ordinal())
			.thenComparing(progress -> progress.getGoal().getTier().getSortOrder())
			.thenComparing(progress -> progress.getGoal().getPriority());
	}
}

