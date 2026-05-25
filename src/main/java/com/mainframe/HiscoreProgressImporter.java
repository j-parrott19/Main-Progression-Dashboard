package com.mainframe;

import java.io.IOException;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import net.runelite.api.Skill;
import net.runelite.api.WorldType;
import net.runelite.api.vars.AccountType;
import net.runelite.client.hiscore.HiscoreClient;
import net.runelite.client.hiscore.HiscoreEndpoint;
import net.runelite.client.hiscore.HiscoreResult;
import net.runelite.client.hiscore.HiscoreSkill;

final class HiscoreProgressImporter
{
	private final HiscoreLookup hiscoreLookup;

	HiscoreProgressImporter(HiscoreClient hiscoreClient)
	{
		this.hiscoreLookup = hiscoreClient::lookup;
	}

	HiscoreProgressImporter(HiscoreLookup hiscoreLookup)
	{
		this.hiscoreLookup = hiscoreLookup;
	}

	CompletableFuture<HiscoreAccountData> lookupAsync(String playerName, AccountType accountType, Set<WorldType> worldTypes)
	{
		if (playerName == null || playerName.trim().isEmpty())
		{
			return CompletableFuture.completedFuture(HiscoreAccountData.NOT_REQUESTED);
		}

		HiscoreEndpoint endpoint = endpointFor(accountType, worldTypes);
		return CompletableFuture.supplyAsync(() ->
		{
			try
			{
				HiscoreResult result = hiscoreLookup.lookup(playerName, endpoint);
				if (result == null)
				{
					return HiscoreAccountData.UNAVAILABLE;
				}
				return convert(result, endpoint);
			}
			catch (IOException | RuntimeException ex)
			{
				return HiscoreAccountData.UNAVAILABLE;
			}
		});
	}

	interface HiscoreLookup
	{
		HiscoreResult lookup(String username, HiscoreEndpoint endpoint) throws IOException;
	}

	private static HiscoreEndpoint endpointFor(AccountType accountType, Set<WorldType> worldTypes)
	{
		HiscoreEndpoint worldEndpoint = worldTypes == null || worldTypes.isEmpty() ? HiscoreEndpoint.NORMAL : HiscoreEndpoint.fromWorldTypes(worldTypes);
		if (worldEndpoint == null)
		{
			worldEndpoint = HiscoreEndpoint.NORMAL;
		}
		if (worldEndpoint != HiscoreEndpoint.NORMAL)
		{
			return worldEndpoint;
		}
		if (accountType == null)
		{
			return HiscoreEndpoint.NORMAL;
		}
		switch (accountType)
		{
			case HARDCORE_IRONMAN:
				return HiscoreEndpoint.HARDCORE_IRONMAN;
			case ULTIMATE_IRONMAN:
				return HiscoreEndpoint.ULTIMATE_IRONMAN;
			case IRONMAN:
			case GROUP_IRONMAN:
			case HARDCORE_GROUP_IRONMAN:
				return HiscoreEndpoint.IRONMAN;
			case NORMAL:
			default:
				return HiscoreEndpoint.NORMAL;
		}
	}

	private static HiscoreAccountData convert(HiscoreResult result, HiscoreEndpoint endpoint)
	{
		Map<Skill, Integer> levels = new EnumMap<>(Skill.class);
		for (Map.Entry<Skill, HiscoreSkill> entry : skillMapping().entrySet())
		{
			net.runelite.client.hiscore.Skill hiscoreSkill = result.getSkill(entry.getValue());
			if (hiscoreSkill != null && hiscoreSkill.getLevel() > 0)
			{
				levels.put(entry.getKey(), hiscoreSkill.getLevel());
			}
		}

		Map<String, Integer> metrics = new HashMap<>();
		for (HiscoreSkill hiscoreSkill : HiscoreSkill.values())
		{
			net.runelite.client.hiscore.Skill value = result.getSkill(hiscoreSkill);
			if (value != null && value.getLevel() > 0)
			{
				metrics.put(hiscoreSkill.name(), value.getLevel());
			}
		}

		return new HiscoreAccountData(true, "Hiscores loaded: " + endpoint.getName(), levels, metrics);
	}

	private static Map<Skill, HiscoreSkill> skillMapping()
	{
		Map<Skill, HiscoreSkill> mapping = new EnumMap<>(Skill.class);
		mapping.put(Skill.ATTACK, HiscoreSkill.ATTACK);
		mapping.put(Skill.DEFENCE, HiscoreSkill.DEFENCE);
		mapping.put(Skill.STRENGTH, HiscoreSkill.STRENGTH);
		mapping.put(Skill.HITPOINTS, HiscoreSkill.HITPOINTS);
		mapping.put(Skill.RANGED, HiscoreSkill.RANGED);
		mapping.put(Skill.PRAYER, HiscoreSkill.PRAYER);
		mapping.put(Skill.MAGIC, HiscoreSkill.MAGIC);
		mapping.put(Skill.COOKING, HiscoreSkill.COOKING);
		mapping.put(Skill.WOODCUTTING, HiscoreSkill.WOODCUTTING);
		mapping.put(Skill.FLETCHING, HiscoreSkill.FLETCHING);
		mapping.put(Skill.FISHING, HiscoreSkill.FISHING);
		mapping.put(Skill.FIREMAKING, HiscoreSkill.FIREMAKING);
		mapping.put(Skill.CRAFTING, HiscoreSkill.CRAFTING);
		mapping.put(Skill.SMITHING, HiscoreSkill.SMITHING);
		mapping.put(Skill.MINING, HiscoreSkill.MINING);
		mapping.put(Skill.HERBLORE, HiscoreSkill.HERBLORE);
		mapping.put(Skill.AGILITY, HiscoreSkill.AGILITY);
		mapping.put(Skill.THIEVING, HiscoreSkill.THIEVING);
		mapping.put(Skill.SLAYER, HiscoreSkill.SLAYER);
		mapping.put(Skill.FARMING, HiscoreSkill.FARMING);
		mapping.put(Skill.RUNECRAFT, HiscoreSkill.RUNECRAFT);
		mapping.put(Skill.HUNTER, HiscoreSkill.HUNTER);
		mapping.put(Skill.CONSTRUCTION, HiscoreSkill.CONSTRUCTION);
		return Collections.unmodifiableMap(mapping);
	}
}
