package com.mainframe;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.awt.Component;
import java.awt.Container;
import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicReference;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import net.runelite.api.Skill;
import org.junit.Test;

public class MainframePanelTest
{
	@Test
	public void panelBuildsOfflineAndLoggedInStates() throws Exception
	{
		InMemoryMainframeStateStore store = new InMemoryMainframeStateStore();
		MainframePanel panel = new MainframePanel(store, ignored -> { }, () -> { }, ignored -> { }, true);
		RoadmapGoal goal = new RoadmapGoal("prayer-43", "43 Prayer", GoalCategory.SKILL_TARGETS, GoalTier.EARLY, 1,
			"Protection prayers", Arrays.asList(RoadmapRequirement.skill(Skill.PRAYER, 43)), false);
		GoalProgress progress = new RoadmapProgressService(Arrays.asList(goal)).evaluate(new FakeProgressContext()).get(0);

		SwingUtilities.invokeAndWait(() -> panel.update("profile", new ProgressSnapshot(Arrays.asList(progress), Collections.emptyList(), "Profile",
			ProgressionPath.OPTIMAL_QUEST_COMPLETION, false, "Local account data", "Unknown")));
		SwingUtilities.invokeAndWait(() ->
		{
			assertTrue(panel.getComponentCount() > 0);
			assertTrue(containsText(panel, "First-time setup"));
			assertTrue(containsText(panel, "Optimal Quest Completion"));
			@SuppressWarnings("unchecked")
			JComboBox<ProgressionPath> combo = (JComboBox<ProgressionPath>) findFirst(panel, JComboBox.class);
			assertEquals(ProgressionPath.OPTIMAL_QUEST_COMPLETION, combo.getSelectedItem());
		});
	}

	@Test
	public void panelRendersSideQuestUnlockSection() throws Exception
	{
		InMemoryMainframeStateStore store = new InMemoryMainframeStateStore();
		MainframePanel panel = new MainframePanel(store, ignored -> { }, () -> { }, ignored -> { }, true);
		RoadmapGoal goal = new RoadmapGoal("dwarf-cannon", "Dwarf Cannon", GoalCategory.SIDE_QUEST_UNLOCKS, GoalTier.EARLY, 1,
			"Cannon unlock", Arrays.asList(RoadmapRequirement.skill(Skill.RANGED, 1)), false);
		GoalProgress progress = new RoadmapProgressService(Arrays.asList(goal)).evaluate(new FakeProgressContext()).get(0);

		SwingUtilities.invokeAndWait(() -> panel.update("profile", new ProgressSnapshot(Arrays.asList(progress), Collections.emptyList(), "Profile",
			ProgressionPath.OPTIMAL_QUEST_COMPLETION, true, "Local account data", "Unknown")));
		SwingUtilities.invokeAndWait(() ->
		{
			assertTrue(containsText(panel, "Side Quest Unlocks"));
			assertTrue(containsText(panel, "Dwarf Cannon"));
		});
	}

	@Test
	public void goalDetailsExpandFromCompactCards() throws Exception
	{
		InMemoryMainframeStateStore store = new InMemoryMainframeStateStore();
		MainframePanel panel = new MainframePanel(store, ignored -> { }, () -> { }, ignored -> { }, true);
		RoadmapGoal goal = new RoadmapGoal("expandable", "Expandable Goal", GoalCategory.ACCOUNT_UNLOCKS, GoalTier.EARLY, 1,
			"Expandable detail text", Arrays.asList(RoadmapRequirement.skill(Skill.PRAYER, 43)),
			Arrays.asList("First detailed step", "Second detailed step"), false);
		GoalProgress progress = new RoadmapProgressService(Arrays.asList(goal)).evaluate(new FakeProgressContext()).get(0);

		SwingUtilities.invokeAndWait(() -> panel.update("profile", new ProgressSnapshot(Arrays.asList(progress), Collections.emptyList(), "Profile",
			ProgressionPath.OPTIMAL_QUEST_COMPLETION, true, "Local account data", "Unknown")));
		SwingUtilities.invokeAndWait(() ->
		{
			assertTrue(containsText(panel, "Expandable Goal"));
			assertFalse(containsText(panel, "Expandable detail text"));
			JButton expand = findButton(panel, "+");
			expand.doClick();
			assertTrue(containsText(panel, "Expandable detail text"));
			assertTrue(containsText(panel, "First detailed step"));
			assertTrue(containsText(panel, "[*] Tip"));
		});
	}

	@Test
	public void panelBuildsAtNarrowWidthAndPathSelectionNotifies() throws Exception
	{
		InMemoryMainframeStateStore store = new InMemoryMainframeStateStore();
		AtomicReference<ProgressionPath> selected = new AtomicReference<>();
		MainframePanel panel = new MainframePanel(store, ignored -> { }, () -> { }, selected::set, true);
		RoadmapGoal goal = new RoadmapGoal("long", "A Very Long Roadmap Goal Title", GoalCategory.ACCOUNT_UNLOCKS, GoalTier.EARLY, 1,
			"This description is intentionally long so the panel has to wrap text inside a narrow RuneLite sidebar instead of clipping.",
			Arrays.asList(RoadmapRequirement.manual("long", "A very long manual requirement that should wrap in the available sidebar width")), true);
		GoalProgress progress = new RoadmapProgressService(Arrays.asList(goal)).evaluate(new FakeProgressContext()).get(0);

		SwingUtilities.invokeAndWait(() ->
		{
			panel.setSize(180, 480);
			panel.update("profile", new ProgressSnapshot(Arrays.asList(progress), Collections.emptyList(), "Profile",
				ProgressionPath.BALANCED, true, "Local account data", "Unknown"));
		});
		SwingUtilities.invokeAndWait(() ->
		{
			panel.doLayout();
			@SuppressWarnings("unchecked")
			JComboBox<ProgressionPath> combo = (JComboBox<ProgressionPath>) findFirst(panel, JComboBox.class);
			combo.setSelectedItem(ProgressionPath.BOSSING);
		});

		assertEquals(ProgressionPath.BOSSING, selected.get());
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

	private static Component findFirst(Container container, Class<?> type)
	{
		for (Component component : container.getComponents())
		{
			if (type.isInstance(component))
			{
				return component;
			}
			if (component instanceof Container)
			{
				Component nested = findFirst((Container) component, type);
				if (nested != null)
				{
					return nested;
				}
			}
		}
		return null;
	}

	private static JButton findButton(Container container, String text)
	{
		for (Component component : container.getComponents())
		{
			if (component instanceof JButton && text.equals(((JButton) component).getText()))
			{
				return (JButton) component;
			}
			if (component instanceof Container)
			{
				JButton nested = findButton((Container) component, text);
				if (nested != null)
				{
					return nested;
				}
			}
		}
		return null;
	}

	private static boolean containsText(Container container, String text)
	{
		for (Component component : container.getComponents())
		{
			if (component instanceof JLabel && ((JLabel) component).getText().contains(text))
			{
				return true;
			}
			if (component instanceof Container && containsText((Container) component, text))
			{
				return true;
			}
		}
		return false;
	}
}
