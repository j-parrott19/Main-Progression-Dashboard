package com.mainframe;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import net.runelite.api.ItemID;
import net.runelite.api.Quest;
import net.runelite.api.Skill;
import org.junit.Test;

public class AccountProgressSnapshotTest
{
	@Test
	public void combinesLocalStateManualStateAndHiscoreSkillFallback()
	{
		Map<Skill, Integer> localSkills = new EnumMap<>(Skill.class);
		for (Skill skill : Skill.values())
		{
			localSkills.put(skill, 1);
		}
		localSkills.put(Skill.PRAYER, 43);
		Set<Quest> quests = new HashSet<>();
		quests.add(Quest.LOST_CITY);
		Set<String> manual = new HashSet<>();
		manual.add("fire-cape");
		Map<Skill, Integer> hiscoreSkills = new EnumMap<>(Skill.class);
		hiscoreSkills.put(Skill.PRAYER, 70);

		AccountProgressSnapshot snapshot = new AccountProgressSnapshot(localSkills, quests, manual, 90, 1500, "NORMAL",
			ProgressionPath.BOSSING, true, new HiscoreAccountData(true, "Hiscores loaded", hiscoreSkills, new HashMap<>()));

		assertEquals(70, snapshot.getRealSkillLevel(Skill.PRAYER));
		assertTrue(snapshot.isQuestComplete(Quest.LOST_CITY));
		assertTrue(snapshot.isManualComplete("fire-cape"));
		assertEquals(ProgressionPath.BOSSING, snapshot.getProgressionPath());
		assertEquals(90, snapshot.getCombatLevel());
	}

	@Test
	public void stateStoreDefaultsPathUntilUserChoosesOne()
	{
		InMemoryMainframeStateStore store = new InMemoryMainframeStateStore();

		assertEquals(ProgressionPath.BALANCED, store.getProgressionPath("profile"));
		assertFalse(store.hasProgressionPath("profile"));

		store.setProgressionPath("profile", ProgressionPath.MAXING);

		assertEquals(ProgressionPath.MAXING, store.getProgressionPath("profile"));
		assertTrue(store.hasProgressionPath("profile"));
	}

	@Test
	public void itemObservationCompletesOwnershipButNotAccessOnlyGoals()
	{
		Set<Integer> itemIds = new HashSet<>();
		itemIds.add(ItemID.DRAGON_SCIMITAR);

		Set<String> keys = ClientAccountSnapshotFactory.observedUnlockKeys(itemIds);

		assertTrue(keys.contains("dragon-scimitar"));
		assertFalse(keys.contains("dragon-scimitar-access"));
	}

	@Test
	public void questCompletionCompletesAccessButNotOwnershipGoals()
	{
		Set<Quest> quests = new HashSet<>();
		quests.add(Quest.MONKEY_MADNESS_I);

		Set<String> keys = ClientAccountSnapshotFactory.questAccessKeys(quests);

		assertTrue(keys.contains("dragon-scimitar-access"));
		assertFalse(keys.contains("dragon-scimitar"));
	}

	@Test
	public void completedFairytaleTwoIsReliableFairyRingAccessEvidence()
	{
		Set<Quest> quests = new HashSet<>();
		quests.add(Quest.FAIRYTALE_II__CURE_A_QUEEN);

		assertTrue(ClientAccountSnapshotFactory.questAccessKeys(quests).contains("fairy-rings"));
	}
}
