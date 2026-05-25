package com.mainframe;

import com.google.inject.Provides;
import java.awt.image.BufferedImage;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.StatChanged;
import net.runelite.api.vars.AccountType;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.hiscore.HiscoreClient;
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

	@Inject
	private ClientThread clientThread;

	@Inject
	private HiscoreClient hiscoreClient;

	private NavigationButton navigationButton;
	private MainframePanel panel;
	private MainframeStateStore stateStore;
	private RoadmapProgressService progressService;
	private HiscoreProgressImporter hiscoreProgressImporter;
	private final Map<String, HiscoreAccountData> hiscoreCache = new ConcurrentHashMap<>();
	private final Set<String> hiscoreLookupsInFlight = Collections.newSetFromMap(new ConcurrentHashMap<>());

	@Override
	protected void startUp()
	{
		stateStore = new ConfigMainframeStateStore(configManager);
		progressService = new RoadmapProgressService(RoadmapCatalog.createDefaultGoals());
		hiscoreProgressImporter = new HiscoreProgressImporter(hiscoreClient);
		panel = new MainframePanel(stateStore, ignored -> refreshPanel(), this::refreshPanel, this::setProgressionPath, config.showCompletedGoals());

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
		requestHiscoreLookup(scope);
		Set<String> completedManualKeys = stateStore.getCompletedManualKeys(scope);
		ProgressionPath progressionPath = stateStore.getProgressionPath(scope);
		boolean progressionPathChosen = stateStore.hasProgressionPath(scope);
		HiscoreAccountData hiscoreData = hiscoreData(scope);
		AccountProgressSnapshot accountSnapshot = ClientAccountSnapshotFactory.create(client, completedManualKeys, progressionPath, progressionPathChosen, hiscoreData);
		List<GoalProgress> goals = progressService.evaluate(accountSnapshot);
		List<CustomGoal> customGoals = stateStore.getCustomGoals(scope);
		panel.update(scope, new ProgressSnapshot(goals, customGoals, AccountScope.displayName(client), progressionPath, progressionPathChosen,
			accountSnapshot.getImportStatusText(), accountSummary(accountSnapshot)));
	}

	private void setProgressionPath(ProgressionPath progressionPath)
	{
		if (stateStore == null)
		{
			return;
		}
		stateStore.setProgressionPath(AccountScope.fromClient(client), progressionPath);
		refreshPanel();
	}

	private HiscoreAccountData hiscoreData(String scope)
	{
		if (!config.enableHiscoreLookup())
		{
			return HiscoreAccountData.NOT_REQUESTED;
		}
		if (hiscoreLookupsInFlight.contains(scope))
		{
			return new HiscoreAccountData(false, "Loading public hiscores", java.util.Collections.emptyMap(), java.util.Collections.emptyMap());
		}
		return hiscoreCache.getOrDefault(scope, HiscoreAccountData.NOT_REQUESTED);
	}

	private void requestHiscoreLookup(String scope)
	{
		if (!config.enableHiscoreLookup() || client == null || client.getLocalPlayer() == null || client.getLocalPlayer().getName() == null)
		{
			return;
		}
		if (hiscoreCache.containsKey(scope) || hiscoreLookupsInFlight.contains(scope))
		{
			return;
		}

		String playerName = client.getLocalPlayer().getName();
		AccountType accountType = client.getAccountType();
		hiscoreLookupsInFlight.add(scope);
		CompletableFuture<HiscoreAccountData> future = hiscoreProgressImporter.lookupAsync(playerName, accountType, client.getWorldType());
		future.whenComplete((data, error) ->
		{
			hiscoreLookupsInFlight.remove(scope);
			hiscoreCache.put(scope, error == null && data != null ? data : HiscoreAccountData.UNAVAILABLE);
			clientThread.invoke(this::refreshPanel);
		});
	}

	private static String accountSummary(AccountProgressSnapshot snapshot)
	{
		StringBuilder summary = new StringBuilder();
		if (snapshot.getCombatLevel() > 0)
		{
			summary.append("CB ").append(snapshot.getCombatLevel()).append(" - ");
		}
		if (snapshot.getTotalLevel() > 0)
		{
			summary.append("Total ").append(snapshot.getTotalLevel()).append(" - ");
		}
		summary.append(snapshot.getAccountType());
		return summary.toString();
	}

	@Provides
	MainframeConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(MainframeConfig.class);
	}
}
