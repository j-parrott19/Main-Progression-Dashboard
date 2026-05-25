package com.mainframe;

import java.util.EnumMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.InventoryID;
import net.runelite.api.Item;
import net.runelite.api.ItemContainer;
import net.runelite.api.ItemID;
import net.runelite.api.Player;
import net.runelite.api.Quest;
import net.runelite.api.QuestState;
import net.runelite.api.Skill;
import net.runelite.api.vars.AccountType;

final class ClientAccountSnapshotFactory
{
	private ClientAccountSnapshotFactory()
	{
	}

	static AccountProgressSnapshot create(
		Client client,
		Set<String> completedManualKeys,
		ProgressionPath progressionPath,
		boolean progressionPathChosen,
		HiscoreAccountData hiscoreData)
	{
		Map<Skill, Integer> skillLevels = new EnumMap<>(Skill.class);
		Set<Quest> completedQuests = new HashSet<>();
		Set<String> effectiveManualKeys = new HashSet<>(completedManualKeys);
		int combatLevel = 0;
		int totalLevel = 0;
		String accountType = "Unknown";

		if (client != null && client.getGameState() == GameState.LOGGED_IN)
		{
			for (Skill skill : Skill.values())
			{
				skillLevels.put(skill, client.getRealSkillLevel(skill));
			}
			for (Quest quest : Quest.values())
			{
				if (quest.getState(client) == QuestState.FINISHED)
				{
					completedQuests.add(quest);
				}
			}
			Player player = client.getLocalPlayer();
			if (player != null)
			{
				combatLevel = player.getCombatLevel();
			}
			totalLevel = client.getTotalLevel();
			AccountType clientAccountType = client.getAccountType();
			if (clientAccountType != null)
			{
				accountType = clientAccountType.name().replace('_', ' ');
			}
			effectiveManualKeys.addAll(visibleUnlockKeys(client));
		}
		else
		{
			for (Skill skill : Skill.values())
			{
				skillLevels.put(skill, 1);
			}
		}

		return new AccountProgressSnapshot(skillLevels, completedQuests, effectiveManualKeys, combatLevel, totalLevel, accountType,
			progressionPath, progressionPathChosen, hiscoreData);
	}

	private static Set<String> visibleUnlockKeys(Client client)
	{
		Set<Integer> visibleItemIds = new HashSet<>();
		addContainerItems(client, visibleItemIds, InventoryID.INVENTORY);
		addContainerItems(client, visibleItemIds, InventoryID.EQUIPMENT);

		Set<String> keys = new HashSet<>();
		addIfContains(keys, visibleItemIds, "ava-device", ItemID.AVAS_DEVICE, ItemID.AVAS_ATTRACTOR, ItemID.AVAS_ACCUMULATOR, ItemID.AVAS_ASSEMBLER);
		addIfContains(keys, visibleItemIds, "dragon-scimitar", ItemID.DRAGON_SCIMITAR, ItemID.DRAGON_SCIMITAR_OR, ItemID.DRAGON_SCIMITAR_CR);
		addIfContains(keys, visibleItemIds, "fighter-torso", ItemID.FIGHTER_TORSO, ItemID.FIGHTER_TORSO_OR, ItemID.FIGHTER_TORSO_BROKEN);
		addIfContains(keys, visibleItemIds, "dragon-defender", ItemID.DRAGON_DEFENDER, ItemID.DRAGON_DEFENDER_T, ItemID.DRAGON_DEFENDER_LT, ItemID.DRAGON_DEFENDER_BROKEN);
		addIfContains(keys, visibleItemIds, "barrows-gloves", ItemID.BARROWS_GLOVES, ItemID.BARROWS_GLOVES_WRAPPED);
		addIfContains(keys, visibleItemIds, "fire-cape", ItemID.FIRE_CAPE, ItemID.FIRE_CAPE_10566, ItemID.FIRE_CAPE_BROKEN);
		addIfContains(keys, visibleItemIds, "berserker-ring", ItemID.BERSERKER_RING, ItemID.BERSERKER_RING_I, ItemID.BERSERKER_RING_I_25264, ItemID.BERSERKER_RING_I_26770);
		addIfContains(keys, visibleItemIds, "fury", ItemID.AMULET_OF_FURY, ItemID.AMULET_OF_FURY_OR);
		addIfContains(keys, visibleItemIds, "whip", ItemID.ABYSSAL_WHIP, ItemID.VOLCANIC_ABYSSAL_WHIP, ItemID.FROZEN_ABYSSAL_WHIP, ItemID.ABYSSAL_WHIP_OR);
		addIfContains(keys, visibleItemIds, "blowpipe", ItemID.TOXIC_BLOWPIPE, ItemID.TOXIC_BLOWPIPE_EMPTY);
		addIfContains(keys, visibleItemIds, "toxic-trident", ItemID.TRIDENT_OF_THE_SWAMP, ItemID.UNCHARGED_TOXIC_TRIDENT,
			ItemID.TRIDENT_OF_THE_SWAMP_E, ItemID.UNCHARGED_TOXIC_TRIDENT_E, ItemID.TRIDENT_OF_THE_SWAMP_O);
		return keys;
	}

	private static void addContainerItems(Client client, Set<Integer> itemIds, InventoryID inventoryID)
	{
		ItemContainer container = client.getItemContainer(inventoryID);
		if (container == null)
		{
			return;
		}
		for (Item item : container.getItems())
		{
			if (item.getId() > 0)
			{
				itemIds.add(item.getId());
			}
		}
	}

	private static void addIfContains(Set<String> keys, Set<Integer> itemIds, String key, int... candidates)
	{
		for (int itemId : candidates)
		{
			if (itemIds.contains(itemId))
			{
				keys.add(key);
				return;
			}
		}
	}
}
