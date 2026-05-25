package com.mainframe;

import static com.mainframe.RoadmapRequirement.manual;
import static com.mainframe.RoadmapRequirement.quest;
import static com.mainframe.RoadmapRequirement.skill;
import static com.mainframe.RoadmapRequirement.text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import net.runelite.api.Quest;
import net.runelite.api.Skill;

final class RoadmapCatalog
{
	private RoadmapCatalog()
	{
	}

	static List<RoadmapGoal> createDefaultGoals()
	{
		List<RoadmapGoal> goals = new ArrayList<>();

		goals.add(goal("prayer-43", "43 Prayer", GoalCategory.SKILL_TARGETS, GoalTier.EARLY, 10,
			"Protection prayers make questing and early bosses much smoother.", false, skill(Skill.PRAYER, 43)));
		goals.add(goal("ava-device", "Ava's Device", GoalCategory.ACCOUNT_UNLOCKS, GoalTier.EARLY, 20,
			"A ranged quality-of-life unlock from Animal Magnetism.", true, quest(Quest.ANIMAL_MAGNETISM), manual("ava-device", "Claim Ava's device")));
		goals.add(goal("dragon-scimitar-access", "Dragon Scimitar Access", GoalCategory.ACCOUNT_UNLOCKS, GoalTier.EARLY, 30,
			"Monkey Madness I unlocks the ability to buy and use the classic early melee weapon.", true,
			quest(Quest.MONKEY_MADNESS_I), manual("dragon-scimitar-access", "Dragon scimitar access unlocked")));
		goals.add(goal("dragon-scimitar", "Own Dragon Scimitar", GoalCategory.GEAR_GOALS, GoalTier.EARLY, 35,
			"Core early melee weapon after Monkey Madness I. Auto-checks when seen in inventory, equipment, or a loaded bank.",
			true, quest(Quest.MONKEY_MADNESS_I), manual("dragon-scimitar", "Own dragon scimitar")));
		goals.add(goal("fairy-rings", "Fairy Rings", GoalCategory.ACCOUNT_UNLOCKS, GoalTier.EARLY, 40,
			"Fast travel network unlocked after starting Fairytale II. Auto-checks after a confirmed fairy-ring use or completed Fairytale II.",
			true, manual("fairy-rings", "Fairy rings usable"), text("If this is not checked automatically, mark it once the Fairy Godfather has granted access.")));
		goals.add(goal("fighter-torso", "Fighter Torso", GoalCategory.GEAR_GOALS, GoalTier.EARLY, 50,
			"Strong melee body slot before high-cost upgrades.", true, manual("fighter-torso", "Earn fighter torso")));
		goals.add(goal("dragon-defender", "Dragon Defender", GoalCategory.ACCOUNT_UNLOCKS, GoalTier.EARLY, 60,
			"Staple off-hand for melee progression.", true, manual("dragon-defender", "Earn dragon defender")));
		goals.add(goal("recipe-for-disaster-start", "Recipe for Disaster Started", GoalCategory.QUEST_CLUSTERS, GoalTier.EARLY, 70,
			"Open the long Barrows gloves chain early so subquests can fit around other goals.", false,
			quest(Quest.RECIPE_FOR_DISASTER__ANOTHER_COOKS_QUEST)));
		goals.add(goal("lost-city", "Lost City", GoalCategory.QUEST_CLUSTERS, GoalTier.EARLY, 80,
			"Unlocks Zanaris and dragon dagger access.", false, quest(Quest.LOST_CITY)));
		goals.add(goal("prayer-70", "70 Prayer", GoalCategory.SKILL_TARGETS, GoalTier.MID, 90,
			"Supports Piety and tougher PvM prayers.", false, skill(Skill.PRAYER, 70)));
		goals.add(goal("ranged-75", "75 Ranged", GoalCategory.SKILL_TARGETS, GoalTier.MID, 100,
			"Unlocks blowpipe and stronger ranged setups.", false, skill(Skill.RANGED, 75)));
		goals.add(goal("construction-50", "50 Construction", GoalCategory.SKILL_TARGETS, GoalTier.MID, 110,
			"Gets the house moving before the ornate pool push.", false, skill(Skill.CONSTRUCTION, 50)));
		goals.add(goal("construction-83", "83 Construction", GoalCategory.SKILL_TARGETS, GoalTier.LATE, 120,
			"Boostable account milestone for ornate pool and strong house utility.", false, skill(Skill.CONSTRUCTION, 83)));
		goals.add(goal("lunar-diplomacy", "Lunar Diplomacy", GoalCategory.QUEST_CLUSTERS, GoalTier.MID, 130,
			"Unlocks Lunar spells and sets up Vengeance/Dream Mentor progression.", false, quest(Quest.LUNAR_DIPLOMACY)));
		goals.add(goal("dream-mentor", "Dream Mentor", GoalCategory.QUEST_CLUSTERS, GoalTier.MID, 140,
			"Expands Lunar spellbook utility.", false, quest(Quest.LUNAR_DIPLOMACY), quest(Quest.DREAM_MENTOR)));
		goals.add(goal("desert-treasure", "Desert Treasure I", GoalCategory.QUEST_CLUSTERS, GoalTier.MID, 150,
			"Ancient Magicks unlock for combat and utility.", false, quest(Quest.DESERT_TREASURE_I)));
		goals.add(goal("kings-ransom", "King's Ransom", GoalCategory.QUEST_CLUSTERS, GoalTier.MID, 160,
			"Quest backbone for Piety.", false, quest(Quest.KINGS_RANSOM)));
		goals.add(goal("piety", "Piety", GoalCategory.ACCOUNT_UNLOCKS, GoalTier.MID, 170,
			"Melee prayer unlock after King's Ransom and 70 Prayer.", true,
			skill(Skill.PRAYER, 70), quest(Quest.KINGS_RANSOM), manual("piety", "Complete Knight Waves and unlock Piety")));
		goals.add(goal("barrows-gloves", "Barrows Gloves", GoalCategory.ACCOUNT_UNLOCKS, GoalTier.MID, 180,
			"Classic main-account milestone from Recipe for Disaster.", true,
			quest(Quest.RECIPE_FOR_DISASTER__CULINAROMANCER), manual("barrows-gloves", "Buy Barrows gloves")));
		goals.add(goal("fire-cape", "Fire Cape", GoalCategory.ACCOUNT_UNLOCKS, GoalTier.MID, 190,
			"Major combat cape milestone and melee upgrade.", true, skill(Skill.RANGED, 70), manual("fire-cape", "Defeat TzTok-Jad")));
		goals.add(goal("berserker-ring", "Berserker Ring", GoalCategory.GEAR_GOALS, GoalTier.MID, 200,
			"High-value melee ring goal.", true, manual("berserker-ring", "Own berserker ring")));
		goals.add(goal("fury", "Amulet of Fury", GoalCategory.GEAR_GOALS, GoalTier.MID, 210,
			"All-around combat amulet before late-game jewelry.", true, manual("fury", "Own amulet of fury")));
		goals.add(goal("whip", "Abyssal Whip", GoalCategory.GEAR_GOALS, GoalTier.MID, 220,
			"Reliable main-hand melee weapon.", true, manual("whip", "Own abyssal whip")));
		goals.add(goal("blowpipe", "Toxic Blowpipe", GoalCategory.GEAR_GOALS, GoalTier.MID, 230,
			"Strong ranged weapon once 75 Ranged is reached.", true, skill(Skill.RANGED, 75), manual("blowpipe", "Own toxic blowpipe")));
		goals.add(goal("toxic-trident", "Toxic Trident", GoalCategory.GEAR_GOALS, GoalTier.LATE, 240,
			"Core powered staff upgrade for magic PvM.", true, skill(Skill.MAGIC, 75), manual("toxic-trident", "Own toxic trident")));
		goals.add(goal("rigour", "Rigour", GoalCategory.ACCOUNT_UNLOCKS, GoalTier.LATE, 250,
			"Ranged prayer scroll unlock.", true, skill(Skill.PRAYER, 74), manual("rigour", "Unlock Rigour prayer")));
		goals.add(goal("augury", "Augury", GoalCategory.ACCOUNT_UNLOCKS, GoalTier.LATE, 260,
			"Magic prayer scroll unlock.", true, skill(Skill.PRAYER, 77), manual("augury", "Unlock Augury prayer")));
		goals.add(goal("dragon-slayer-ii", "Dragon Slayer II", GoalCategory.QUEST_CLUSTERS, GoalTier.LATE, 270,
			"Unlocks Vorkath and strong late mid-game account options.", false, quest(Quest.DRAGON_SLAYER_II)));
		goals.add(goal("song-of-the-elves", "Song of the Elves", GoalCategory.QUEST_CLUSTERS, GoalTier.LATE, 280,
			"Prifddinas unlock and a long-term main account target.", false, quest(Quest.SONG_OF_THE_ELVES)));
		goals.add(goal("quest-cape-path", "Quest Cape Path", GoalCategory.QUEST_CLUSTERS, GoalTier.LATE, 290,
			"Long-term account structure after core mid-game unlocks.", true, manual("quest-cape-path", "Quest cape path planned")));

		return Collections.unmodifiableList(goals);
	}

	private static RoadmapGoal goal(
		String id,
		String title,
		GoalCategory category,
		GoalTier tier,
		int priority,
		String description,
		boolean manualCompletion,
		RoadmapRequirement... requirements)
	{
		return new RoadmapGoal(id, title, category, tier, priority, description, Arrays.asList(requirements), manualCompletion);
	}
}
