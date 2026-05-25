package com.mainframe;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

final class RoadmapProgressService
{
	private static final int RECOMMENDATION_LIMIT = 5;
	private static final Set<String> WIKI_FIRST_GOALS = Collections.unmodifiableSet(new HashSet<>(Arrays.asList(
		"prayer-43",
		"druidic-ritual",
		"waterfall-quest",
		"witchs-house",
		"fight-arena",
		"tree-gnome-village",
		"grand-tree",
		"priest-in-peril",
		"ghosts-ahoy",
		"lost-city",
		"fairytale-i",
		"fairy-rings",
		"dragon-scimitar-access",
		"ava-device",
		"recipe-for-disaster-start",
		"dragon-scimitar",
		"dragon-defender",
		"barrows-gloves",
		"prayer-70",
		"kings-ransom",
		"piety",
		"fire-cape"
	)));
	private final List<RoadmapGoal> goals;

	RoadmapProgressService(List<RoadmapGoal> goals)
	{
		this.goals = goals;
	}

	List<GoalProgress> evaluate(ProgressContext context)
	{
		return evaluate(AccountProgressSnapshot.fromContext(context, ProgressionPath.OPTIMAL_QUEST_COMPLETION));
	}

	List<GoalProgress> evaluate(AccountProgressSnapshot context)
	{
		List<GoalProgress> progress = goals.stream()
			.map(goal -> evaluateGoal(goal, context))
			.collect(Collectors.toCollection(ArrayList::new));

		List<GoalProgress> recommended = progress.stream()
			.filter(goal -> !goal.isComplete())
			.sorted(recommendationComparator(context))
			.limit(RECOMMENDATION_LIMIT)
			.collect(Collectors.toList());

		for (int i = 0; i < recommended.size(); i++)
		{
			GoalProgress goal = recommended.get(i);
			goal.setNextRecommended(true);
			goal.setRecommendationRank(i + 1);
			goal.setRecommendationScore(recommendationScore(goal, context));
		}

		progress.sort(displayComparator());
		return progress;
	}

	private GoalProgress evaluateGoal(RoadmapGoal goal, ProgressContext context)
	{
		int total = 0;
		int complete = 0;
		List<String> missing = new ArrayList<>();
		List<RequirementProgress> requirementProgresses = new ArrayList<>();
		for (RoadmapRequirement requirement : goal.getRequirements())
		{
			RequirementProgress requirementProgress = evaluateRequirement(requirement, context);
			requirementProgresses.add(requirementProgress);
			if (requirement.getType() == RequirementType.TEXT_ONLY)
			{
				missing.add(requirement.getLabel());
				continue;
			}
			total++;
			if (requirementProgress.isComplete())
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
				requirementProgresses.add(new RequirementProgress("Mark " + goal.getTitle() + " complete", false, false, Collections.emptyList()));
				total++;
			}
		}

		boolean blocked = !completeGoal && total > 0 && complete == 0 && goal.getTier() != GoalTier.EARLY;
		String status = completeGoal ? "Done" : complete + "/" + total + " ready";
		return new GoalProgress(goal, completeGoal, blocked, complete, total, missing, requirementProgresses, status);
	}

	private static RequirementProgress evaluateRequirement(RoadmapRequirement requirement, ProgressContext context)
	{
		List<RequirementProgress> options = requirement.getOptions().stream()
			.map(option -> evaluateRequirement(option, context))
			.collect(Collectors.toList());
		boolean informational = requirement.getType() == RequirementType.TEXT_ONLY;
		return new RequirementProgress(requirement.getLabel(), requirement.isComplete(context), informational, options);
	}

	private static boolean containsManualRequirementForGoal(RoadmapGoal goal)
	{
		for (RoadmapRequirement requirement : goal.getRequirements())
		{
			if (containsManualRequirement(requirement, goal.getId()))
			{
				return true;
			}
		}
		return false;
	}

	private static boolean containsManualRequirement(RoadmapRequirement requirement, String manualKey)
	{
		if (requirement.getType() == RequirementType.MANUAL_UNLOCK && manualKey.equals(requirement.getManualKey()))
		{
			return true;
		}
		for (RoadmapRequirement option : requirement.getOptions())
		{
			if (containsManualRequirement(option, manualKey))
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
		score += wikiFirstWeight(goal, context);

		switch (context.getProgressionPath())
		{
			case OPTIMAL_QUEST_COMPLETION:
				score += optimalQuestCompletionWeight(goal);
				break;
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

	private static double wikiFirstWeight(RoadmapGoal goal, AccountProgressSnapshot context)
	{
		if (!WIKI_FIRST_GOALS.contains(goal.getId()))
		{
			return 0.0d;
		}

		double score = 18.0d;
		if (goal.getTier() == GoalTier.EARLY)
		{
			score += 8.0d;
		}
		if (context.getTotalLevel() < 1250 && goal.getCategory() != GoalCategory.GEAR_GOALS)
		{
			score += 6.0d;
		}
		if ("fairy-rings".equals(goal.getId()))
		{
			score += 18.0d;
		}
		if ("dragon-scimitar-access".equals(goal.getId()))
		{
			score += 10.0d;
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

	private static double optimalQuestCompletionWeight(RoadmapGoal goal)
	{
		double score = 0.0d;
		if (goal.getCategory() == GoalCategory.QUEST_CLUSTERS)
		{
			score += 30.0d;
		}
		if (goal.getCategory() == GoalCategory.ACCOUNT_UNLOCKS)
		{
			score += 22.0d;
		}
		if (goal.getCategory() == GoalCategory.SIDE_QUEST_UNLOCKS)
		{
			score += 14.0d;
		}
		if (goal.getTier() == GoalTier.EARLY)
		{
			score += 10.0d;
		}
		if (matchesAny(goal, "druidic", "waterfall", "witchs-house", "fight-arena", "tree-gnome", "grand-tree", "priest-in-peril"))
		{
			score += 24.0d;
		}
		if (matchesAny(goal, "fairy", "ghosts-ahoy", "lost-city", "animal-magnetism", "dragon-scimitar-access", "recipe", "barrows-gloves"))
		{
			score += 20.0d;
		}
		if (matchesAny(goal, "tears-of-guthix", "dwarf-cannon", "bone-voyage", "family-crest", "horror-from-the-deep"))
		{
			score += 10.0d;
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
		if (matchesAny(goal, "barrows-gloves", "dragon-scimitar-access", "fairy-rings"))
		{
			score += 10.0d;
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
			if (hasCombatRequirement(requirement))
			{
				return true;
			}
		}
		return false;
	}

	private static boolean hasCombatRequirement(RoadmapRequirement requirement)
	{
		if (requirement.getType() == RequirementType.SKILL_LEVEL)
		{
			return isCombatSkill(requirement.getSkill());
		}
		if (requirement.getType() == RequirementType.SKILL_TOTAL)
		{
			for (net.runelite.api.Skill skill : requirement.getSkills())
			{
				if (isCombatSkill(skill))
				{
					return true;
				}
			}
		}
		for (RoadmapRequirement option : requirement.getOptions())
		{
			if (hasCombatRequirement(option))
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
