package com.mainframe;

import java.util.Set;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.Quest;
import net.runelite.api.QuestState;
import net.runelite.api.Skill;

final class ClientProgressContext implements ProgressContext
{
	private final Client client;
	private final Set<String> completedManualKeys;

	ClientProgressContext(Client client, Set<String> completedManualKeys)
	{
		this.client = client;
		this.completedManualKeys = completedManualKeys;
	}

	@Override
	public int getRealSkillLevel(Skill skill)
	{
		if (client == null || client.getGameState() != GameState.LOGGED_IN)
		{
			return 1;
		}
		return client.getRealSkillLevel(skill);
	}

	@Override
	public boolean isQuestComplete(Quest quest)
	{
		if (client == null || client.getGameState() != GameState.LOGGED_IN)
		{
			return false;
		}
		return quest.getState(client) == QuestState.FINISHED;
	}

	@Override
	public boolean isManualComplete(String manualKey)
	{
		return completedManualKeys.contains(manualKey);
	}
}

