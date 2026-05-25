package com.mainframe;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;
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
}

