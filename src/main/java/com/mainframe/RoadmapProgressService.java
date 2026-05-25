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
		return evaluate(AccountProgressSnapshot.fromContext(context, ProgressionPath.BALANCED));
	}

	List<GoalProgress> evaluate(AccountProgressSnapshot context)
	{
		List<GoalProgress> progress = goals.stream()
			.map(goal -> evaluateGoal(goal, context))
			.collect(Collectors.toCollection(ArrayList::new));

		progress.stream()
			.filter(goal -> !goal.isComplete())
			.sorted(recommendationComparator(context))
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

	private static Comparator<GoalProgress> recommendationComparator(AccountProgressSnapshot context)
	{
		return Comparator
			.comparingDouble((GoalProgress progress) -> recommendationScore(progress, context)).reversed()
			.thenComparing(GoalProgress::isBlocked)
			.thenComparing(GoalProgress::getCompletionRatio, Comparator.reverseOrder())
			.thenComparing(progress -> progress.getGoal().getTier().getSortOrder())
			.thenComparing(progress -> progress.getGoal().getPriority());
	}

	private static double recommendationScore(GoalProgress progress, AccountProgressSnapshot context)
	{
		RoadmapGoal goal = progress.getGoal();
		double score = progress.getCompletionRatio() * 100.0d;
		score -= progress.isBlocked() ? 35.0d : 0.0d;
		score -= goal.getTier().getSortOrder() * 4.0d;
		score -= goal.getPriority() / 1000.0d;

		switch (context.getProgressionPath())
		{
			case BOSSING:
				score += bossingWeight(goal);
				score += Math.min(context.getHiscoreMetric("TZTOK_JAD"), 1) * 4.0d;
				break;
			case PVP:
				score += pvpWeight(goal);
				break;
			case COMPLETION:
				score += completionWeight(goal);
				score += Math.min(context.getHiscoreMetric("COLLECTIONS_LOGGED"), 100) / 25.0d;
				break;
			case MAXING:
				score += maxingWeight(goal);
				score += context.getTotalLevel() >= 1500 ? 4.0d : 0.0d;
				break;
			case BALANCED:
			default:
				score += balancedWeight(goal, context);
				break;
		}

		return score;
	}

	private static double balancedWeight(RoadmapGoal goal, AccountProgressSnapshot context)
	{
		double score = 0.0d;
		if (goal.getCategory() == GoalCategory.ACCOUNT_UNLOCKS)
		{
			score += 8.0d;
		}
		if (goal.getCategory() == GoalCategory.QUEST_CLUSTERS)
		{
			score += 4.0d;
		}
		if (context.getCombatLevel() >= 70 && goal.getCategory() == GoalCategory.GEAR_GOALS)
		{
			score += 4.0d;
		}
		return score;
	}

	private static double bossingWeight(RoadmapGoal goal)
	{
		double score = 0.0d;
		if (goal.getCategory() == GoalCategory.GEAR_GOALS)
		{
			score += 26.0d;
		}
		if (goal.getCategory() == GoalCategory.ACCOUNT_UNLOCKS)
		{
			score += 22.0d;
		}
		if (goal.getCategory() == GoalCategory.QUEST_CLUSTERS)
		{
			score += 10.0d;
		}
		if (hasCombatRequirement(goal))
		{
			score += 12.0d;
		}
		if (matchesAny(goal, "fire", "piety", "rigour", "augury", "dragon-slayer", "vorkath", "blowpipe", "trident"))
		{
			score += 12.0d;
		}
		return score;
	}

	private static double pvpWeight(RoadmapGoal goal)
	{
		double score = 0.0d;
		if (goal.getCategory() == GoalCategory.ACCOUNT_UNLOCKS)
		{
			score += 24.0d;
		}
		if (goal.getCategory() == GoalCategory.QUEST_CLUSTERS)
		{
			score += 18.0d;
		}
		if (hasCombatRequirement(goal))
		{
			score += 16.0d;
		}
		if (matchesAny(goal, "desert-treasure", "lunar", "dream-mentor", "piety", "ranged", "prayer", "dragon-scimitar"))
		{
			score += 14.0d;
		}
		return score;
	}

	private static double completionWeight(RoadmapGoal goal)
	{
		double score = 0.0d;
		if (goal.getCategory() == GoalCategory.QUEST_CLUSTERS)
		{
			score += 32.0d;
		}
		if (goal.getCategory() == GoalCategory.ACCOUNT_UNLOCKS)
		{
			score += 10.0d;
		}
		if (matchesAny(goal, "quest", "recipe", "barrows-gloves", "song-of-the-elves", "dragon-slayer"))
		{
			score += 16.0d;
		}
		return score;
	}

	private static double maxingWeight(RoadmapGoal goal)
	{
		double score = 0.0d;
		if (goal.getCategory() == GoalCategory.SKILL_TARGETS)
		{
			score += 34.0d;
		}
		if (goal.getCategory() == GoalCategory.ACCOUNT_UNLOCKS)
		{
			score += 8.0d;
		}
		if (matchesAny(goal, "construction", "fairy", "lunar"))
		{
			score += 12.0d;
		}
		return score;
	}

	private static boolean hasCombatRequirement(RoadmapGoal goal)
	{
		for (RoadmapRequirement requirement : goal.getRequirements())
		{
			if (requirement.getType() == RequirementType.SKILL_LEVEL && isCombatSkill(requirement.getSkill()))
			{
				return true;
			}
		}
		return false;
	}

	private static boolean isCombatSkill(net.runelite.api.Skill skill)
	{
		switch (skill)
		{
			case ATTACK:
			case STRENGTH:
			case DEFENCE:
			case HITPOINTS:
			case RANGED:
			case PRAYER:
			case MAGIC:
			case SLAYER:
				return true;
			default:
				return false;
		}
	}

	private static boolean matchesAny(RoadmapGoal goal, String... tokens)
	{
		String value = (goal.getId() + " " + goal.getTitle()).toLowerCase();
		for (String token : tokens)
		{
			if (value.contains(token))
			{
				return true;
			}
		}
		return false;
	}

	private static Comparator<GoalProgress> displayComparator()
	{
		return Comparator
			.comparing((GoalProgress progress) -> progress.getGoal().getCategory().ordinal())
			.thenComparing(progress -> progress.getGoal().getTier().getSortOrder())
			.thenComparing(progress -> progress.getGoal().getPriority());
	}
}
