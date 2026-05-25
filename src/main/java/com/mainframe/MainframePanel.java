package com.mainframe;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.util.Comparator;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.Scrollable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import net.runelite.client.ui.PluginPanel;

final class MainframePanel extends PluginPanel
{
	private static final Color BACKGROUND = new Color(35, 35, 35);
	private static final Color PANEL = new Color(43, 43, 43);
	private static final Color TEXT = new Color(220, 220, 220);
	private static final Color MUTED = new Color(155, 155, 155);
	private static final Color ACCENT = new Color(230, 126, 34);
	private static final Color DONE = new Color(88, 214, 141);
	private static final Color NEEDED = new Color(255, 85, 85);
	private static final int TITLE_SIZE = 20;
	private static final int META_SIZE = 12;
	private static final int SECTION_SIZE = 16;
	private static final int GOAL_TITLE_SIZE = 15;
	private static final int GOAL_META_SIZE = 12;
	private static final int DETAIL_SIZE = 12;
	private static final int CONTROL_HEIGHT = 30;
	private static final int DETAIL_WRAP_INSET = 70;

	private final MainframeStateStore stateStore;
	private final Consumer<Boolean> manualRefresh;
	private final Runnable refreshRequest;
	private final Consumer<ProgressionPath> progressionPathChange;
	private final boolean showCompletedGoals;
	private String scope;
	private ProgressSnapshot snapshot;
	private final JPanel content = new ScrollableContent();
	private final Set<String> expandedGoalIds = new HashSet<>();

	MainframePanel(
		MainframeStateStore stateStore,
		Consumer<Boolean> manualRefresh,
		Runnable refreshRequest,
		Consumer<ProgressionPath> progressionPathChange,
		boolean showCompletedGoals)
	{
		super(false);
		this.stateStore = stateStore;
		this.manualRefresh = manualRefresh;
		this.refreshRequest = refreshRequest;
		this.progressionPathChange = progressionPathChange;
		this.showCompletedGoals = showCompletedGoals;
		setLayout(new BorderLayout());
		setBackground(BACKGROUND);
		content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
		content.setBackground(BACKGROUND);
		JScrollPane scrollPane = new JScrollPane(content);
		scrollPane.setBorder(BorderFactory.createEmptyBorder());
		scrollPane.getVerticalScrollBar().setUnitIncrement(24);
		add(scrollPane, BorderLayout.CENTER);
	}

	void update(String scope, ProgressSnapshot snapshot)
	{
		this.scope = scope;
		this.snapshot = snapshot;
		SwingUtilities.invokeLater(this::rebuild);
	}

	private void rebuild()
	{
		content.removeAll();
		content.add(header());

		if (snapshot == null)
		{
			content.add(emptyLabel("Log in or refresh to load Mainframe."));
			finish();
			return;
		}

		if (!snapshot.isProgressionPathChosen())
		{
			content.add(pathChoiceCard());
		}

		content.add(section("Next Unlocks", nextUnlockCards()));
		content.add(section(GoalCategory.ACCOUNT_UNLOCKS.getDisplayName(), goalCards(GoalCategory.ACCOUNT_UNLOCKS)));
		content.add(section(GoalCategory.SIDE_QUEST_UNLOCKS.getDisplayName(), goalCards(GoalCategory.SIDE_QUEST_UNLOCKS)));
		content.add(section(GoalCategory.SKILL_TARGETS.getDisplayName(), goalCards(GoalCategory.SKILL_TARGETS)));
		content.add(section(GoalCategory.GEAR_GOALS.getDisplayName(), goalCards(GoalCategory.GEAR_GOALS)));
		content.add(section(GoalCategory.QUEST_CLUSTERS.getDisplayName(), goalCards(GoalCategory.QUEST_CLUSTERS)));
		content.add(section(GoalCategory.CUSTOM_GOALS.getDisplayName(), customGoalCards()));
		finish();
	}

	private JPanel header()
	{
		JPanel header = new JPanel(new GridBagLayout());
		header.setAlignmentX(Component.LEFT_ALIGNMENT);
		header.setMaximumSize(new Dimension(Integer.MAX_VALUE, Short.MAX_VALUE));
		header.setBackground(BACKGROUND);
		header.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

		JLabel title = label("Mainframe", TITLE_SIZE, Font.BOLD, TEXT);
		JLabel account = label(snapshot == null ? "Profile" : snapshot.getAccountLabel(), META_SIZE, Font.PLAIN, MUTED);
		JLabel accountSummary = label(snapshot == null ? "Local account data" : snapshot.getAccountSummaryText(), META_SIZE, Font.PLAIN, MUTED);
		JLabel importStatus = label(snapshot == null ? "" : snapshot.getImportStatusText(), META_SIZE, Font.PLAIN, MUTED);
		JPanel stack = new JPanel();
		stack.setLayout(new BoxLayout(stack, BoxLayout.Y_AXIS));
		stack.setBackground(BACKGROUND);
		stack.add(title);
		stack.add(account);
		stack.add(accountSummary);
		stack.add(importStatus);
		if (snapshot != null)
		{
			stack.add(label("Path: " + snapshot.getProgressionPath().getDisplayName(), META_SIZE, Font.BOLD, ACCENT));
		}

		JComboBox<ProgressionPath> path = new JComboBox<>(ProgressionPath.values());
		path.setFocusable(false);
		path.setFont(path.getFont().deriveFont(Font.PLAIN, (float) META_SIZE));
		path.setMaximumSize(new Dimension(Integer.MAX_VALUE, CONTROL_HEIGHT));
		if (snapshot != null)
		{
			path.setSelectedItem(snapshot.getProgressionPath());
		}
		path.addActionListener(event ->
		{
			ProgressionPath selected = (ProgressionPath) path.getSelectedItem();
			if (selected != null && snapshot != null && selected != snapshot.getProgressionPath())
			{
				progressionPathChange.accept(selected);
			}
		});
		JButton refresh = new JButton("Refresh");
		refresh.setFocusable(false);
		refresh.setFont(refresh.getFont().deriveFont(Font.PLAIN, (float) META_SIZE));
		refresh.setMaximumSize(new Dimension(Integer.MAX_VALUE, CONTROL_HEIGHT));
		refresh.addActionListener(event -> refreshRequest.run());

		JPanel actions = new JPanel();
		actions.setOpaque(false);
		actions.setLayout(new BoxLayout(actions, BoxLayout.Y_AXIS));
		actions.add(path);
		actions.add(refresh);

		GridBagConstraints constraints = constraints();
		constraints.insets = new Insets(0, 0, 6, 0);
		constraints.weightx = 1;
		header.add(stack, constraints);
		constraints.gridy = 1;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.insets = new Insets(0, 0, 0, 0);
		header.add(actions, constraints);
		return header;
	}

	private JPanel pathChoiceCard()
	{
		JPanel card = card();
		card.setLayout(new GridBagLayout());
		JLabel title = label("First-time setup", GOAL_TITLE_SIZE, Font.BOLD, TEXT);
		JLabel detail = html("Not sure? Use Optimal Quest Completion. It prioritizes efficient quest and unlock routing.", MUTED);
		JComboBox<ProgressionPath> path = new JComboBox<>(ProgressionPath.values());
		path.setFont(path.getFont().deriveFont(Font.PLAIN, (float) META_SIZE));
		path.setSelectedItem(snapshot.getProgressionPath());
		JLabel pathDetail = html(snapshot.getProgressionPath().getDescription(), MUTED);
		path.addActionListener(event ->
		{
			ProgressionPath selected = (ProgressionPath) path.getSelectedItem();
			if (selected != null)
			{
				pathDetail.setText(htmlText(selected.getDescription(), DETAIL_SIZE, Font.PLAIN, MUTED, 34));
			}
		});
		JButton save = new JButton("Use this path");
		save.setFocusable(false);
		save.setFont(save.getFont().deriveFont(Font.PLAIN, (float) META_SIZE));
		save.addActionListener(event ->
		{
			ProgressionPath selected = (ProgressionPath) path.getSelectedItem();
			if (selected != null)
			{
				progressionPathChange.accept(selected);
			}
		});

		JPanel stack = new JPanel();
		stack.setOpaque(false);
		stack.setLayout(new BoxLayout(stack, BoxLayout.Y_AXIS));
		stack.add(title);
		stack.add(detail);
		stack.add(pathDetail);

		GridBagConstraints constraints = constraints();
		constraints.weightx = 1;
		card.add(path, constraints);
		constraints.gridy = 1;
		constraints.insets = new Insets(6, 1, 1, 1);
		card.add(stack, constraints);
		constraints.gridy = 2;
		constraints.insets = new Insets(6, 1, 1, 1);
		card.add(save, constraints);
		return card;
	}

	private JPanel section(String title, List<JPanel> cards)
	{
		JPanel section = new JPanel();
		section.setAlignmentX(Component.LEFT_ALIGNMENT);
		section.setMaximumSize(new Dimension(Integer.MAX_VALUE, Short.MAX_VALUE));
		section.setLayout(new BoxLayout(section, BoxLayout.Y_AXIS));
		section.setBackground(BACKGROUND);
		section.setBorder(BorderFactory.createEmptyBorder(5, 8, 8, 8));
		section.add(label(title, SECTION_SIZE, Font.BOLD, ACCENT));
		if (cards.isEmpty())
		{
			section.add(emptyLabel("Nothing here yet."));
		}
		else
		{
			for (JPanel card : cards)
			{
				section.add(card);
			}
		}
		return section;
	}

	private List<JPanel> nextUnlockCards()
	{
		return snapshot.getGoals().stream()
			.filter(GoalProgress::isNextRecommended)
			.sorted(Comparator.comparingInt(GoalProgress::getRecommendationRank))
			.map(this::goalCard)
			.collect(Collectors.toList());
	}

	private List<JPanel> goalCards(GoalCategory category)
	{
		return snapshot.getGoals().stream()
			.filter(progress -> progress.getGoal().getCategory() == category)
			.filter(progress -> showCompletedGoals || !progress.isComplete())
			.map(this::goalCard)
			.collect(Collectors.toList());
	}

	private JPanel goalCard(GoalProgress progress)
	{
		RoadmapGoal goal = progress.getGoal();
		JPanel card = card();
		card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));

		JCheckBox checkbox = new JCheckBox();
		checkbox.setOpaque(false);
		checkbox.setSelected(progress.isComplete());
		checkbox.setEnabled(goal.isManualCompletion());
		checkbox.setToolTipText(goal.isManualCompletion() ? "Mark manual progress" : "Auto-tracked when requirements are met");
		checkbox.addActionListener(event ->
		{
			stateStore.setManualComplete(scope, goal.getId(), checkbox.isSelected());
			for (RoadmapRequirement requirement : goal.getRequirements())
			{
				setManualRequirementComplete(requirement, checkbox.isSelected());
			}
			manualRefresh.accept(checkbox.isSelected());
		});

		JPanel summary = new JPanel(new BorderLayout(6, 0));
		summary.setAlignmentX(Component.LEFT_ALIGNMENT);
		summary.setMaximumSize(new Dimension(Integer.MAX_VALUE, Short.MAX_VALUE));
		summary.setOpaque(false);
		JPanel stack = new JPanel();
		stack.setAlignmentX(Component.LEFT_ALIGNMENT);
		stack.setMaximumSize(new Dimension(Integer.MAX_VALUE, Short.MAX_VALUE));
		stack.setOpaque(false);
		stack.setLayout(new BoxLayout(stack, BoxLayout.Y_AXIS));
		stack.add(goalHeader(progress));
		String rank = progress.isNextRecommended() ? "Recommended #" + progress.getRecommendationRank() + " - " : "";
		stack.add(label(rank + goal.getTier().getDisplayName() + " - " + progress.getStatusText(), GOAL_META_SIZE, Font.PLAIN, MUTED));

		summary.add(checkbox, BorderLayout.WEST);
		summary.add(stack, BorderLayout.CENTER);
		card.add(summary);
		if (isExpanded(goal))
		{
			card.add(goalDetails(progress));
		}
		return card;
	}

	private JPanel goalHeader(GoalProgress progress)
	{
		RoadmapGoal goal = progress.getGoal();
		JPanel header = new JPanel(new BorderLayout(6, 0));
		header.setOpaque(false);
		JButton expand = new JButton(isExpanded(goal) ? "-" : "+");
		expand.setFocusable(false);
		expand.setFont(expand.getFont().deriveFont(Font.BOLD, (float) GOAL_META_SIZE));
		expand.setMargin(new Insets(0, 4, 0, 4));
		expand.setPreferredSize(new Dimension(28, 24));
		expand.setToolTipText(isExpanded(goal) ? "Hide details" : "Show details");
		expand.addActionListener(event ->
		{
			if (isExpanded(goal))
			{
				expandedGoalIds.remove(goal.getId());
			}
			else
			{
				expandedGoalIds.clear();
				expandedGoalIds.add(goal.getId());
			}
			rebuild();
		});
		header.add(html(goal.getTitle(), GOAL_TITLE_SIZE, Font.BOLD, progress.isComplete() ? DONE : TEXT, 78), BorderLayout.CENTER);
		header.add(expand, BorderLayout.EAST);
		return header;
	}

	private JPanel goalDetails(GoalProgress progress)
	{
		RoadmapGoal goal = progress.getGoal();
		JPanel details = new JPanel();
		details.setAlignmentX(Component.LEFT_ALIGNMENT);
		details.setMaximumSize(new Dimension(Integer.MAX_VALUE, Short.MAX_VALUE));
		details.setOpaque(false);
		details.setLayout(new BoxLayout(details, BoxLayout.Y_AXIS));
		details.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
		details.add(detailBlock("[i] Why", goal.getDescription(), MUTED));
		details.add(requirementsBlock(progress));
		if (!goal.getHowToSteps().isEmpty())
		{
			details.add(detailBlock("[>] Steps", numberedSteps(goal.getHowToSteps()), MUTED));
		}
		details.add(detailBlock("[*] Tip", tipText(progress), ACCENT));
		return details;
	}

	private JPanel requirementsBlock(GoalProgress progress)
	{
		JPanel block = new JPanel();
		block.setAlignmentX(Component.LEFT_ALIGNMENT);
		block.setMaximumSize(new Dimension(Integer.MAX_VALUE, Short.MAX_VALUE));
		block.setOpaque(false);
		block.setLayout(new BoxLayout(block, BoxLayout.Y_AXIS));
		block.setBorder(BorderFactory.createEmptyBorder(5, 0, 6, 0));
		block.add(label("[?] Requirements", DETAIL_SIZE, Font.BOLD, ACCENT));

		List<RequirementProgress> met = progress.getRequirementProgresses().stream()
			.filter(item -> item.isComplete() && !item.isInformational())
			.collect(Collectors.toList());
		List<RequirementProgress> needed = progress.getRequirementProgresses().stream()
			.filter(item -> !item.isComplete() && !item.isInformational())
			.collect(Collectors.toList());
		List<RequirementProgress> notes = progress.getRequirementProgresses().stream()
			.filter(RequirementProgress::isInformational)
			.collect(Collectors.toList());

		if (!met.isEmpty())
		{
			addRequirementGroup(block, "Requirements met:", met, DONE);
		}
		if (!needed.isEmpty())
		{
			addRequirementGroup(block, "Requirements needed:", needed, NEEDED);
		}
		if (!notes.isEmpty())
		{
			addRequirementGroup(block, "Notes:", notes, MUTED);
		}
		if (met.isEmpty() && needed.isEmpty() && notes.isEmpty())
		{
			block.add(html("No tracked requirements.", MUTED));
		}
		return block;
	}

	private void addRequirementGroup(JPanel block, String title, List<RequirementProgress> requirements, Color rowColor)
	{
		JLabel groupTitle = label(title, DETAIL_SIZE, Font.BOLD, TEXT);
		groupTitle.setBorder(BorderFactory.createEmptyBorder(5, 0, 1, 0));
		block.add(groupTitle);
		for (RequirementProgress requirement : requirements)
		{
			block.add(html(requirementText(requirement), rowColor));
		}
	}

	private String requirementText(RequirementProgress requirement)
	{
		if (requirement.getOptions().isEmpty())
		{
			return requirement.getLabel();
		}

		List<String> completeOptions = requirement.getOptions().stream()
			.filter(RequirementProgress::isComplete)
			.map(RequirementProgress::getLabel)
			.collect(Collectors.toList());
		if (!completeOptions.isEmpty())
		{
			return requirement.getLabel() + "\nMet via: " + String.join(" OR ", completeOptions);
		}
		return requirement.getLabel() + "\nNeed one: " + requirement.getOptions().stream()
			.map(RequirementProgress::getLabel)
			.collect(Collectors.joining(" OR "));
	}

	private JPanel detailBlock(String title, String text, Color color)
	{
		JPanel block = new JPanel();
		block.setAlignmentX(Component.LEFT_ALIGNMENT);
		block.setMaximumSize(new Dimension(Integer.MAX_VALUE, Short.MAX_VALUE));
		block.setOpaque(false);
		block.setLayout(new BoxLayout(block, BoxLayout.Y_AXIS));
		block.setBorder(BorderFactory.createEmptyBorder(5, 0, 6, 0));
		block.add(label(title, DETAIL_SIZE, Font.BOLD, ACCENT));
		block.add(html(text, color));
		return block;
	}

	private boolean isExpanded(RoadmapGoal goal)
	{
		return expandedGoalIds.contains(goal.getId());
	}

	private String numberedSteps(List<String> steps)
	{
		List<String> numbered = new ArrayList<>();
		for (int i = 0; i < steps.size(); i++)
		{
			numbered.add((i + 1) + ". " + steps.get(i));
		}
		return String.join("\n", numbered);
	}

	private String tipText(GoalProgress progress)
	{
		RoadmapGoal goal = progress.getGoal();
		if (progress.isComplete())
		{
			return "Done. Collapse it and move to the next unlock.";
		}
		if (progress.isNextRecommended())
		{
			return "High-value for your selected path right now.";
		}
		if (goal.getCategory() == GoalCategory.SIDE_QUEST_UNLOCKS)
		{
			return "Optional unlock. Pick it up when the reward helps your current route.";
		}
		if (goal.isManualCompletion())
		{
			return "Manual check. Mark it after the reward is actually claimed.";
		}
		if (progress.isBlocked())
		{
			return "Park this until at least one requirement is close.";
		}
		return "Good filler when you want structured progress without changing paths.";
	}

	private void setManualRequirementComplete(RoadmapRequirement requirement, boolean complete)
	{
		if (requirement.getType() == RequirementType.MANUAL_UNLOCK)
		{
			stateStore.setManualComplete(scope, requirement.getManualKey(), complete);
		}
		for (RoadmapRequirement option : requirement.getOptions())
		{
			setManualRequirementComplete(option, complete);
		}
	}

	private List<JPanel> customGoalCards()
	{
		List<JPanel> cards = new ArrayList<>();
		cards.add(addCustomGoalCard());
		for (CustomGoal goal : snapshot.getCustomGoals())
		{
			cards.add(customGoalCard(goal));
		}
		return cards;
	}

	private JPanel addCustomGoalCard()
	{
		JPanel card = card();
		card.setLayout(new BorderLayout(4, 4));
		JTextField title = new JTextField();
		title.setFont(title.getFont().deriveFont(Font.PLAIN, (float) META_SIZE));
		title.setToolTipText("Custom goal title");

		Map<String, GoalCategory> categories = new LinkedHashMap<>();
		categories.put(GoalCategory.ACCOUNT_UNLOCKS.getDisplayName(), GoalCategory.ACCOUNT_UNLOCKS);
		categories.put(GoalCategory.SIDE_QUEST_UNLOCKS.getDisplayName(), GoalCategory.SIDE_QUEST_UNLOCKS);
		categories.put(GoalCategory.SKILL_TARGETS.getDisplayName(), GoalCategory.SKILL_TARGETS);
		categories.put(GoalCategory.GEAR_GOALS.getDisplayName(), GoalCategory.GEAR_GOALS);
		categories.put(GoalCategory.QUEST_CLUSTERS.getDisplayName(), GoalCategory.QUEST_CLUSTERS);
		JComboBox<String> category = new JComboBox<>(categories.keySet().toArray(new String[0]));
		category.setFont(category.getFont().deriveFont(Font.PLAIN, (float) META_SIZE));
		JButton add = new JButton("Add");
		add.setFont(add.getFont().deriveFont(Font.PLAIN, (float) META_SIZE));
		add.addActionListener(event ->
		{
			String value = title.getText().trim();
			if (value.isEmpty())
			{
				return;
			}
			List<CustomGoal> goals = new ArrayList<>(stateStore.getCustomGoals(scope));
			goals.add(CustomGoal.create(value, categories.get((String) category.getSelectedItem())));
			stateStore.saveCustomGoals(scope, goals);
			title.setText("");
			refreshRequest.run();
		});

		card.add(title, BorderLayout.CENTER);
		card.add(category, BorderLayout.NORTH);
		card.add(add, BorderLayout.EAST);
		return card;
	}

	private JPanel customGoalCard(CustomGoal goal)
	{
		JPanel card = card();
		card.setLayout(new BorderLayout(6, 0));

		JCheckBox checkbox = new JCheckBox(goal.getTitle());
		checkbox.setOpaque(false);
		checkbox.setFont(checkbox.getFont().deriveFont(Font.BOLD, (float) GOAL_TITLE_SIZE));
		checkbox.setForeground(goal.isComplete() ? DONE : TEXT);
		checkbox.setSelected(goal.isComplete());
		checkbox.addActionListener(event -> updateCustomGoal(goal.withComplete(checkbox.isSelected())));

		JButton edit = new JButton("Edit");
		edit.setFont(edit.getFont().deriveFont(Font.PLAIN, (float) META_SIZE));
		edit.addActionListener(event ->
		{
			String value = JOptionPane.showInputDialog(this, "Edit custom goal", goal.getTitle());
			if (value != null && !value.trim().isEmpty())
			{
				updateCustomGoal(goal.withTitle(value.trim()));
			}
		});

		JButton delete = new JButton("Delete");
		delete.setFont(delete.getFont().deriveFont(Font.PLAIN, (float) META_SIZE));
		delete.addActionListener(event ->
		{
			List<CustomGoal> goals = stateStore.getCustomGoals(scope).stream()
				.filter(existing -> !existing.getId().equals(goal.getId()))
				.collect(Collectors.toList());
			stateStore.saveCustomGoals(scope, goals);
			refreshRequest.run();
		});

		JPanel actions = new JPanel();
		actions.setOpaque(false);
		actions.add(edit);
		actions.add(delete);

		card.add(checkbox, BorderLayout.CENTER);
		card.add(actions, BorderLayout.EAST);
		return card;
	}

	private void updateCustomGoal(CustomGoal updated)
	{
		List<CustomGoal> goals = stateStore.getCustomGoals(scope).stream()
			.map(existing -> existing.getId().equals(updated.getId()) ? updated : existing)
			.collect(Collectors.toList());
		stateStore.saveCustomGoals(scope, goals);
		refreshRequest.run();
	}

	private JPanel card()
	{
		JPanel card = new JPanel();
		card.setAlignmentX(Component.LEFT_ALIGNMENT);
		card.setMaximumSize(new Dimension(Integer.MAX_VALUE, Short.MAX_VALUE));
		card.setBackground(PANEL);
		card.setBorder(BorderFactory.createCompoundBorder(
			BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(55, 55, 55)),
			BorderFactory.createEmptyBorder(8, 8, 8, 8)));
		return card;
	}

	private GridBagConstraints constraints()
	{
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.anchor = GridBagConstraints.WEST;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.insets = new Insets(1, 1, 1, 1);
		return constraints;
	}

	private JLabel emptyLabel(String text)
	{
		JLabel label = label(text, META_SIZE, Font.PLAIN, MUTED);
		label.setBorder(BorderFactory.createEmptyBorder(4, 0, 4, 0));
		return label;
	}

	private JLabel label(String text, int size, int style, Color color)
	{
		JLabel label = new JLabel(text);
		label.setForeground(color);
		label.setFont(label.getFont().deriveFont(style, (float) size));
		return label;
	}

	private JLabel html(String text, Color color)
	{
		return html(text, DETAIL_SIZE, Font.PLAIN, color, DETAIL_WRAP_INSET);
	}

	private JLabel html(String text, int size, int style, Color color, int wrapInset)
	{
		JLabel label = label(htmlText(text, size, style, color, wrapInset), size, style, color);
		return label;
	}

	private String htmlText(String text, int size, int style, Color color, int wrapInset)
	{
		String weight = style == Font.BOLD ? "bold" : "normal";
		return "<html><body style='width:" + wrapWidth(wrapInset) + "px; font-family:sans-serif; font-size:" + size + "pt; font-weight:" + weight + "; color:" + hex(color) + "; margin:0; padding:0; word-wrap:break-word;'>"
			+ escape(text).replace("\n", "<br>") + "</body></html>";
	}

	private int wrapWidth(int inset)
	{
		int width = Math.max(getWidth(), content.getWidth());
		if (width <= 0)
		{
			width = 240;
		}
		return Math.max(110, width - inset);
	}

	private String escape(String text)
	{
		return text.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
	}

	private String hex(Color color)
	{
		return String.format("#%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue());
	}

	private void finish()
	{
		content.revalidate();
		content.repaint();
	}

	private static final class ScrollableContent extends JPanel implements Scrollable
	{
		@Override
		public Dimension getPreferredScrollableViewportSize()
		{
			return getPreferredSize();
		}

		@Override
		public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction)
		{
			return 16;
		}

		@Override
		public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction)
		{
			return Math.max(16, visibleRect.height - 16);
		}

		@Override
		public boolean getScrollableTracksViewportWidth()
		{
			return true;
		}

		@Override
		public boolean getScrollableTracksViewportHeight()
		{
			return false;
		}
	}
}
