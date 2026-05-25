package com.mainframe;

import com.google.inject.Provides;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Set;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.StatChanged;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.ClientToolbar;
import net.runelite.client.ui.NavigationButton;

@Slf4j
@PluginDescriptor(
	name = "Mainframe",
	description = "A main-account roadmap dashboard for next OSRS unlocks.",
	tags = {"main", "progress", "roadmap", "goals", "checklist", "unlock"}
)
public class MainframePlugin extends Plugin
{
	@Inject
	private Client client;

	@Inject
	private ClientToolbar clientToolbar;

	@Inject
	private ConfigManager configManager;

	@Inject
	private MainframeConfig config;

	private NavigationButton navigationButton;
	private MainframePanel panel;
	private MainframeStateStore stateStore;
	private RoadmapProgressService progressService;

	@Override
	protected void startUp()
	{
		stateStore = new ConfigMainframeStateStore(configManager);
		progressService = new RoadmapProgressService(RoadmapCatalog.createDefaultGoals());
		panel = new MainframePanel(stateStore, ignored -> refreshPanel(), this::refreshPanel, config.showCompletedGoals());

		BufferedImage icon = MainframeIcons.createIcon();
		navigationButton = NavigationButton.builder()
			.tooltip("Mainframe")
			.icon(icon)
			.priority(7)
			.panel(panel)
			.build();
		clientToolbar.addNavigation(navigationButton);
		refreshPanel();
		log.debug("Mainframe started");
	}

	@Override
	protected void shutDown()
	{
		if (navigationButton != null)
		{
			clientToolbar.removeNavigation(navigationButton);
		}
		log.debug("Mainframe stopped");
	}

	@Subscribe
	public void onGameStateChanged(GameStateChanged event)
	{
		refreshPanel();
	}

	@Subscribe
	public void onStatChanged(StatChanged event)
	{
		refreshPanel();
	}

	private void refreshPanel()
	{
		if (panel == null || stateStore == null || progressService == null)
		{
			return;
		}

		String scope = AccountScope.fromClient(client);
		Set<String> completedManualKeys = stateStore.getCompletedManualKeys(scope);
		List<GoalProgress> goals = progressService.evaluate(new ClientProgressContext(client, completedManualKeys));
		List<CustomGoal> customGoals = stateStore.getCustomGoals(scope);
		panel.update(scope, new ProgressSnapshot(goals, customGoals, AccountScope.displayName(client)));
	}

	@Provides
	MainframeConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(MainframeConfig.class);
	}
}

