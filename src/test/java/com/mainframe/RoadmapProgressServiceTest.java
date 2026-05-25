package com.mainframe;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import net.runelite.api.Quest;
import net.runelite.api.Skill;
import org.junit.Test;

public class RoadmapProgressServiceTest
{
	@Test
	public void skillRequirementCompletesGoal()
	{
		RoadmapGoal goal = new RoadmapGoal("prayer-43", "43 Prayer", GoalCategory.SKILL_TARGETS, GoalTier.EARLY, 1,
			"", Arrays.asList(RoadmapRequirement.skill(Skill.PRAYER, 43)), false);
		List<GoalProgress> progress = new RoadmapProgressService(Arrays.asList(goal))
			.evaluate(new FakeProgressContext().skill(Skill.PRAYER, 43));

		assertTrue(progress.get(0).isComplete());
		assertEquals("Done", progress.get(0).getStatusText());
	}

	@Test
	public void questRequirementCompletesGoal()
	{
		RoadmapGoal goal = new RoadmapGoal("lost-city", "Lost City", GoalCategory.QUEST_CLUSTERS, GoalTier.EARLY, 1,
			"", Arrays.asList(RoadmapRequirement.quest(Quest.LOST_CITY)), false);
		List<GoalProgress> progress = new RoadmapProgressService(Arrays.asList(goal))
			.evaluate(new FakeProgressContext().quest(Quest.LOST_CITY));

		assertTrue(progress.get(0).isComplete());
	}

	@Test
	public void manualGoalRequiresManualCompletion()
	{
		RoadmapGoal goal = new RoadmapGoal("whip", "Abyssal Whip", GoalCategory.GEAR_GOALS, GoalTier.MID, 1,
			"", Arrays.asList(RoadmapRequirement.manual("whip", "Own abyssal whip")), true);

		assertFalse(new RoadmapProgressService(Arrays.asList(goal)).evaluate(new FakeProgressContext()).get(0).isComplete());
		assertTrue(new RoadmapProgressService(Arrays.asList(goal)).evaluate(new FakeProgressContext().manual("whip")).get(0).isComplete());
	}

	@Test
	public void textOnlyRequirementsExplainWithoutBlockingCompletion()
	{
		RoadmapGoal goal = new RoadmapGoal("fairy-rings", "Fairy Rings", GoalCategory.ACCOUNT_UNLOCKS, GoalTier.EARLY, 1,
			"", Arrays.asList(RoadmapRequirement.manual("fairy-rings", "Fairy rings usable"),
			RoadmapRequirement.text("Mark manually once access is granted")), true);

		GoalProgress progress = new RoadmapProgressService(Arrays.asList(goal))
			.evaluate(new FakeProgressContext().manual("fairy-rings"))
			.get(0);

		assertTrue(progress.isComplete());
		assertEquals("Done", progress.getStatusText());
	}

	@Test
	public void skillTotalRequirementCompletesFromCombinedLevels()
	{
		RoadmapRequirement requirement = RoadmapRequirement.skillTotal("130 combined Attack + Strength", 130,
			Skill.ATTACK, Skill.STRENGTH);

		assertFalse(requirement.isComplete(new FakeProgressContext().skill(Skill.ATTACK, 64).skill(Skill.STRENGTH, 65)));
		assertTrue(requirement.isComplete(new FakeProgressContext().skill(Skill.ATTACK, 65).skill(Skill.STRENGTH, 65)));
	}

	@Test
	public void anyRequirementCompletesFromOneAlternative()
	{
		RoadmapRequirement requirement = warriorsGuildRequirement();

		assertFalse(requirement.isComplete(new FakeProgressContext().skill(Skill.ATTACK, 60).skill(Skill.STRENGTH, 60)));
		assertTrue(requirement.isComplete(new FakeProgressContext().skill(Skill.ATTACK, 65).skill(Skill.STRENGTH, 65)));
		assertTrue(requirement.isComplete(new FakeProgressContext().skill(Skill.ATTACK, 99)));
		assertTrue(requirement.isComplete(new FakeProgressContext().skill(Skill.STRENGTH, 99)));
	}

	@Test
	public void dragonDefenderRequiresDefenceForPracticalUse()
	{
		GoalProgress progress = dragonDefenderProgress(new FakeProgressContext()
			.skill(Skill.ATTACK, 99)
			.skill(Skill.DEFENCE, 59)
			.manual("dragon-defender"));

		assertFalse(progress.isComplete());
		assertTrue(progress.getMissingRequirements().contains("60 Defence"));
	}

	@Test
	public void dragonDefenderShowsWarriorsGuildAccessBlocker()
	{
		GoalProgress progress = dragonDefenderProgress(new FakeProgressContext()
			.skill(Skill.ATTACK, 60)
			.skill(Skill.STRENGTH, 60)
			.skill(Skill.DEFENCE, 60)
			.manual("dragon-defender"));

		assertFalse(progress.isComplete());
		assertEquals(Collections.singletonList("Warriors' Guild access"), progress.getMissingRequirements());
	}

	@Test
	public void dragonDefenderReadyAfterCombinedAttackAndStrength()
	{
		GoalProgress progress = dragonDefenderProgress(new FakeProgressContext()
			.skill(Skill.ATTACK, 65)
			.skill(Skill.STRENGTH, 65)
			.skill(Skill.DEFENCE, 60));

		assertFalse(progress.isComplete());
		assertEquals(Collections.singletonList("Earn dragon defender"), progress.getMissingRequirements());
	}

	@Test
	public void dragonDefenderReadyAfterNinetyNineAttackOrStrength()
	{
		GoalProgress attackProgress = dragonDefenderProgress(new FakeProgressContext()
			.skill(Skill.ATTACK, 99)
			.skill(Skill.DEFENCE, 60));
		GoalProgress strengthProgress = dragonDefenderProgress(new FakeProgressContext()
			.skill(Skill.ATTACK, 60)
			.skill(Skill.STRENGTH, 99)
			.skill(Skill.DEFENCE, 60));

		assertEquals(Collections.singletonList("Earn dragon defender"), attackProgress.getMissingRequirements());
		assertEquals(Collections.singletonList("Earn dragon defender"), strengthProgress.getMissingRequirements());
	}

	@Test
	public void dragonDefenderCatalogIncludesHowToSteps()
	{
		RoadmapGoal goal = dragonDefenderGoal();

		assertFalse(goal.getHowToSteps().isEmpty());
		assertTrue(goal.getHowToSteps().get(0).contains("basement cyclopes"));
	}

	@Test
	public void defaultCatalogGoalsAllIncludeHowToSteps()
	{
		for (RoadmapGoal goal : RoadmapCatalog.createDefaultGoals())
		{
			assertFalse(goal.getId() + " should include how-to guidance", goal.getHowToSteps().isEmpty());
		}
	}

	@Test
	public void whipRequiresSeventyAttackBeforeOwnershipIsReady()
	{
		RoadmapGoal goal = RoadmapCatalog.createDefaultGoals().stream()
			.filter(item -> item.getId().equals("whip"))
			.findFirst()
			.get();

		GoalProgress blocked = new RoadmapProgressService(Arrays.asList(goal))
			.evaluate(new FakeProgressContext().manual("whip").skill(Skill.ATTACK, 69))
			.get(0);
		GoalProgress ready = new RoadmapProgressService(Arrays.asList(goal))
			.evaluate(new FakeProgressContext().skill(Skill.ATTACK, 70))
			.get(0);

		assertTrue(blocked.getMissingRequirements().contains("70 Attack"));
		assertEquals(Collections.singletonList("Own abyssal whip"), ready.getMissingRequirements());
	}

	@Test
	public void recommendationsPreferCloserUnblockedGoals()
	{
		RoadmapGoal far = new RoadmapGoal("far", "Far", GoalCategory.SKILL_TARGETS, GoalTier.LATE, 1,
			"", Arrays.asList(RoadmapRequirement.skill(Skill.PRAYER, 70), RoadmapRequirement.quest(Quest.KINGS_RANSOM)), false);
		RoadmapGoal close = new RoadmapGoal("close", "Close", GoalCategory.SKILL_TARGETS, GoalTier.MID, 2,
			"", Arrays.asList(RoadmapRequirement.skill(Skill.PRAYER, 43), RoadmapRequirement.quest(Quest.LOST_CITY)), false);

		List<GoalProgress> progress = new RoadmapProgressService(Arrays.asList(far, close))
			.evaluate(new FakeProgressContext().skill(Skill.PRAYER, 43));

		GoalProgress closeProgress = progress.stream().filter(item -> item.getGoal().getId().equals("close")).findFirst().get();
		assertTrue(closeProgress.isNextRecommended());
	}

	@Test
	public void progressionPathsChangeRecommendationPriority()
	{
		List<RoadmapGoal> goals = Arrays.asList(
			goal("piety", "Piety", GoalCategory.ACCOUNT_UNLOCKS, RoadmapRequirement.skill(Skill.PRAYER, 70)),
			goal("whip", "Abyssal Whip", GoalCategory.GEAR_GOALS, RoadmapRequirement.manual("whip", "Own abyssal whip")),
			goal("desert-treasure", "Desert Treasure I", GoalCategory.QUEST_CLUSTERS, RoadmapRequirement.quest(Quest.DESERT_TREASURE_I)),
			goal("waterfall-quest", "Waterfall Quest", GoalCategory.QUEST_CLUSTERS, RoadmapRequirement.quest(Quest.WATERFALL_QUEST)),
			goal("quest-cape-path", "Quest Cape Path", GoalCategory.QUEST_CLUSTERS, RoadmapRequirement.manual("quest-cape-path", "Quest cape path planned")),
			goal("construction-83", "83 Construction", GoalCategory.SKILL_TARGETS, RoadmapRequirement.skill(Skill.CONSTRUCTION, 83)),
			goal("cooking-70", "70 Cooking", GoalCategory.SKILL_TARGETS, RoadmapRequirement.skill(Skill.COOKING, 70)));

		assertTrue(isRecommended(goals, ProgressionPath.OPTIMAL_QUEST_COMPLETION, "waterfall-quest"));
		assertTrue(isRecommended(goals, ProgressionPath.BOSSING, "whip"));
		assertTrue(isRecommended(goals, ProgressionPath.PVP, "desert-treasure"));
		assertTrue(isRecommended(goals, ProgressionPath.COMPLETION, "quest-cape-path"));
		assertTrue(isRecommended(goals, ProgressionPath.MAXING, "construction-83"));
		assertTrue(isRecommended(goals, ProgressionPath.BALANCED, "piety"));
	}

	@Test
	public void optimalQuestCompletionPrefersGranularQuestAndSideUnlocks()
	{
		List<RoadmapGoal> goals = Arrays.asList(
			goal("waterfall-quest", "Waterfall Quest", GoalCategory.QUEST_CLUSTERS, RoadmapRequirement.quest(Quest.WATERFALL_QUEST)),
			goal("dwarf-cannon", "Dwarf Cannon", GoalCategory.SIDE_QUEST_UNLOCKS, RoadmapRequirement.quest(Quest.DWARF_CANNON)),
			goal("whip", "Abyssal Whip", GoalCategory.GEAR_GOALS, RoadmapRequirement.manual("whip", "Own abyssal whip")),
			goal("construction-83", "83 Construction", GoalCategory.SKILL_TARGETS, RoadmapRequirement.skill(Skill.CONSTRUCTION, 83)));

		List<String> optimal = recommendedIds(goals, ProgressionPath.OPTIMAL_QUEST_COMPLETION);

		assertEquals("waterfall-quest", optimal.get(0));
		assertTrue(optimal.contains("dwarf-cannon"));
		assertTrue(optimal.indexOf("dwarf-cannon") < optimal.indexOf("whip"));
	}

	@Test
	public void recommendationRanksExposePathSpecificOrder()
	{
		List<RoadmapGoal> goals = Arrays.asList(
			goal("fairy-rings", "Fairy Rings", GoalCategory.ACCOUNT_UNLOCKS, RoadmapRequirement.manual("fairy-rings", "Fairy rings usable")),
			goal("fire-cape", "Fire Cape", GoalCategory.ACCOUNT_UNLOCKS, RoadmapRequirement.skill(Skill.RANGED, 70)),
			goal("dragon-scimitar-access", "Dragon Scimitar Access", GoalCategory.ACCOUNT_UNLOCKS, RoadmapRequirement.quest(Quest.MONKEY_MADNESS_I)),
			goal("construction-83", "83 Construction", GoalCategory.SKILL_TARGETS, RoadmapRequirement.skill(Skill.CONSTRUCTION, 83)),
			goal("quest-cape-path", "Quest Cape Path", GoalCategory.QUEST_CLUSTERS, RoadmapRequirement.manual("quest-cape-path", "Quest cape path planned")),
			goal("whip", "Abyssal Whip", GoalCategory.GEAR_GOALS, RoadmapRequirement.manual("whip", "Own abyssal whip")));

		List<String> bossing = recommendedIds(goals, ProgressionPath.BOSSING);
		List<String> maxing = recommendedIds(goals, ProgressionPath.MAXING);
		List<String> completion = recommendedIds(goals, ProgressionPath.COMPLETION);

		assertEquals("fairy-rings", bossing.get(0));
		assertTrue(bossing.indexOf("fire-cape") < bossing.indexOf("whip"));
		assertTrue(maxing.indexOf("construction-83") < maxing.indexOf("fire-cape"));
		assertEquals("quest-cape-path", completion.get(0));
	}

	private static boolean isRecommended(List<RoadmapGoal> goals, ProgressionPath path, String goalId)
	{
		List<GoalProgress> progress = new RoadmapProgressService(goals).evaluate(snapshot(path));
		return progress.stream()
			.filter(item -> item.getGoal().getId().equals(goalId))
			.findFirst()
			.get()
			.isNextRecommended();
	}

	private static List<String> recommendedIds(List<RoadmapGoal> goals, ProgressionPath path)
	{
		return new RoadmapProgressService(goals).evaluate(snapshot(path)).stream()
			.filter(GoalProgress::isNextRecommended)
			.sorted(java.util.Comparator.comparingInt(GoalProgress::getRecommendationRank))
			.map(progress -> progress.getGoal().getId())
			.collect(Collectors.toList());
	}

	private static AccountProgressSnapshot snapshot(ProgressionPath path)
	{
		Map<Skill, Integer> skills = new EnumMap<>(Skill.class);
		for (Skill skill : Skill.values())
		{
			skills.put(skill, 1);
		}
		skills.put(Skill.PRAYER, 43);
		skills.put(Skill.CONSTRUCTION, 50);
		return new AccountProgressSnapshot(skills, Collections.emptySet(), Collections.emptySet(), 90, 1500, "NORMAL",
			path, true, HiscoreAccountData.NOT_REQUESTED);
	}

	private static RoadmapGoal goal(String id, String title, GoalCategory category, RoadmapRequirement requirement)
	{
		return new RoadmapGoal(id, title, category, GoalTier.MID, 100, "", Arrays.asList(requirement), requirement.getType() == RequirementType.MANUAL_UNLOCK);
	}

	private static RoadmapRequirement warriorsGuildRequirement()
	{
		return RoadmapRequirement.any("Warriors' Guild access",
			RoadmapRequirement.skillTotal("130 combined Attack + Strength", 130, Skill.ATTACK, Skill.STRENGTH),
			RoadmapRequirement.skill(Skill.ATTACK, 99),
			RoadmapRequirement.skill(Skill.STRENGTH, 99));
	}

	private static GoalProgress dragonDefenderProgress(FakeProgressContext context)
	{
		return new RoadmapProgressService(Arrays.asList(dragonDefenderGoal())).evaluate(context).get(0);
	}

	private static RoadmapGoal dragonDefenderGoal()
	{
		return RoadmapCatalog.createDefaultGoals().stream()
			.filter(goal -> goal.getId().equals("dragon-defender"))
			.findFirst()
			.get();
	}
}
