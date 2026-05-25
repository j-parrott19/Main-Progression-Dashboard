package com.mainframe;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
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

	private final MainframeStateStore stateStore;
	private final Consumer<Boolean> manualRefresh;
	private final Runnable refreshRequest;
	private final boolean showCompletedGoals;
	private String scope;
	private ProgressSnapshot snapshot;
	private final JPanel content = new JPanel();

	MainframePanel(
		MainframeStateStore stateStore,
		Consumer<Boolean> manualRefresh,
		Runnable refreshRequest,
		boolean showCompletedGoals)
	{
		super(false);
		this.stateStore = stateStore;
		this.manualRefresh = manualRefresh;
		this.refreshRequest = refreshRequest;
		this.showCompletedGoals = showCompletedGoals;
		setLayout(new BorderLayout());
		setBackground(BACKGROUND);
		content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
		content.setBackground(BACKGROUND);
		JScrollPane scrollPane = new JScrollPane(content);
		scrollPane.setBorder(BorderFactory.createEmptyBorder());
		scrollPane.getVerticalScrollBar().setUnitIncrement(16);
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

		content.add(section("Next Unlocks", nextUnlockCards()));
		content.add(section(GoalCategory.ACCOUNT_UNLOCKS.getDisplayName(), goalCards(GoalCategory.ACCOUNT_UNLOCKS)));
		content.add(section(GoalCategory.SKILL_TARGETS.getDisplayName(), goalCards(GoalCategory.SKILL_TARGETS)));
		content.add(section(GoalCategory.GEAR_GOALS.getDisplayName(), goalCards(GoalCategory.GEAR_GOALS)));
		content.add(section(GoalCategory.QUEST_CLUSTERS.getDisplayName(), goalCards(GoalCategory.QUEST_CLUSTERS)));
		content.add(section(GoalCategory.CUSTOM_GOALS.getDisplayName(), customGoalCards()));
		finish();
	}

	private JPanel header()
	{
		JPanel header = new JPanel(new BorderLayout(6, 0));
		header.setBackground(BACKGROUND);
		header.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

		JLabel title = label("Mainframe", 18, Font.BOLD, TEXT);
		JLabel account = label(snapshot == null ? "Profile" : snapshot.getAccountLabel(), 11, Font.PLAIN, MUTED);
		JPanel stack = new JPanel();
		stack.setLayout(new BoxLayout(stack, BoxLayout.Y_AXIS));
		stack.setBackground(BACKGROUND);
		stack.add(title);
		stack.add(account);

		JButton refresh = new JButton("Refresh");
		refresh.setFocusable(false);
		refresh.addActionListener(event -> refreshRequest.run());

		header.add(stack, BorderLayout.CENTER);
		header.add(refresh, BorderLayout.EAST);
		return header;
	}

	private JPanel section(String title, List<JPanel> cards)
	{
		JPanel section = new JPanel();
		section.setLayout(new BoxLayout(section, BoxLayout.Y_AXIS));
		section.setBackground(BACKGROUND);
		section.setBorder(BorderFactory.createEmptyBorder(5, 8, 8, 8));
		section.add(label(title, 14, Font.BOLD, ACCENT));
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
		card.setLayout(new GridBagLayout());
		GridBagConstraints constraints = constraints();

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
				if (requirement.getType() == RequirementType.MANUAL_UNLOCK)
				{
					stateStore.setManualComplete(scope, requirement.getManualKey(), checkbox.isSelected());
				}
			}
			manualRefresh.accept(checkbox.isSelected());
		});

		card.add(checkbox, constraints);
		constraints.gridx = 1;
		constraints.weightx = 1;
		JLabel title = label(goal.getTitle(), 12, Font.BOLD, progress.isComplete() ? DONE : TEXT);
		card.add(title, constraints);

		constraints.gridy++;
		constraints.gridx = 1;
		JLabel meta = label(goal.getTier().getDisplayName() + " - " + progress.getStatusText(), 10, Font.PLAIN, MUTED);
		card.add(meta, constraints);

		constraints.gridy++;
		JLabel description = html(goal.getDescription(), MUTED);
		card.add(description, constraints);

		if (!progress.isComplete() && !progress.getMissingRequirements().isEmpty())
		{
			constraints.gridy++;
			JLabel missing = html("Needs: " + String.join(", ", progress.getMissingRequirements()), TEXT);
			card.add(missing, constraints);
		}

		return card;
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
		title.setToolTipText("Custom goal title");

		Map<String, GoalCategory> categories = new LinkedHashMap<>();
		categories.put(GoalCategory.ACCOUNT_UNLOCKS.getDisplayName(), GoalCategory.ACCOUNT_UNLOCKS);
		categories.put(GoalCategory.SKILL_TARGETS.getDisplayName(), GoalCategory.SKILL_TARGETS);
		categories.put(GoalCategory.GEAR_GOALS.getDisplayName(), GoalCategory.GEAR_GOALS);
		categories.put(GoalCategory.QUEST_CLUSTERS.getDisplayName(), GoalCategory.QUEST_CLUSTERS);
		JComboBox<String> category = new JComboBox<>(categories.keySet().toArray(new String[0]));
		JButton add = new JButton("Add");
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
		checkbox.setForeground(goal.isComplete() ? DONE : TEXT);
		checkbox.setSelected(goal.isComplete());
		checkbox.addActionListener(event -> updateCustomGoal(goal.withComplete(checkbox.isSelected())));

		JButton edit = new JButton("Edit");
		edit.addActionListener(event ->
		{
			String value = JOptionPane.showInputDialog(this, "Edit custom goal", goal.getTitle());
			if (value != null && !value.trim().isEmpty())
			{
				updateCustomGoal(goal.withTitle(value.trim()));
			}
		});

		JButton delete = new JButton("Delete");
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
		card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 160));
		card.setBackground(PANEL);
		card.setBorder(BorderFactory.createCompoundBorder(
			BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(55, 55, 55)),
			BorderFactory.createEmptyBorder(6, 6, 6, 6)));
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
		JLabel label = label(text, 11, Font.PLAIN, MUTED);
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
		JLabel label = label("<html><body style='width:185px'>" + escape(text) + "</body></html>", 10, Font.PLAIN, color);
		return label;
	}

	private String escape(String text)
	{
		return text.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
	}

	private void finish()
	{
		content.revalidate();
		content.repaint();
	}
}
