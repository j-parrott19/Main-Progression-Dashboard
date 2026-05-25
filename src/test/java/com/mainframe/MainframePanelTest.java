package com.mainframe;

import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collections;
import javax.swing.SwingUtilities;
import net.runelite.api.Skill;
import org.junit.Test;

public class MainframePanelTest
{
	@Test
	public void panelBuildsOfflineAndLoggedInStates() throws Exception
	{
		InMemoryMainframeStateStore store = new InMemoryMainframeStateStore();
		MainframePanel panel = new MainframePanel(store, ignored -> { }, () -> { }, true);
		RoadmapGoal goal = new RoadmapGoal("prayer-43", "43 Prayer", GoalCategory.SKILL_TARGETS, GoalTier.EARLY, 1,
			"Protection prayers", Arrays.asList(RoadmapRequirement.skill(Skill.PRAYER, 43)), false);
		GoalProgress progress = new RoadmapProgressService(Arrays.asList(goal)).evaluate(new FakeProgressContext()).get(0);

		SwingUtilities.invokeAndWait(() -> panel.update("profile", new ProgressSnapshot(Arrays.asList(progress), Collections.emptyList(), "Profile")));
		SwingUtilities.invokeAndWait(() -> assertTrue(panel.getComponentCount() > 0));
	}

	@Test
	public void customGoalCrudPersistsThroughStore()
	{
		InMemoryMainframeStateStore store = new InMemoryMainframeStateStore();
		CustomGoal goal = CustomGoal.create("Make a vorkath tab", GoalCategory.CUSTOM_GOALS);
		store.saveCustomGoals("profile", Arrays.asList(goal));

		assertTrue(store.getCustomGoals("profile").get(0).getTitle().contains("vorkath"));
		store.saveCustomGoals("profile", Arrays.asList(goal.withComplete(true)));
		assertTrue(store.getCustomGoals("profile").get(0).isComplete());
	}
}

