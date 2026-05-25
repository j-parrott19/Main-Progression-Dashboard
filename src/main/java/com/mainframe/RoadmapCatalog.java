package com.mainframe;

import static com.mainframe.RoadmapRequirement.any;
import static com.mainframe.RoadmapRequirement.manual;
import static com.mainframe.RoadmapRequirement.quest;
import static com.mainframe.RoadmapRequirement.skill;
import static com.mainframe.RoadmapRequirement.skillTotal;
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
			"Protection prayers make questing and early bosses much smoother.",
			steps("Bank enough bones or ensouled heads, then train Prayer to 43 before tougher quest fights and early bossing.",
				"Use Protect from Melee, Missiles, and Magic as the first major combat safety unlocks."),
			false,
			skill(Skill.PRAYER, 43)));
		goals.add(goal("druidic-ritual", "Druidic Ritual", GoalCategory.QUEST_CLUSTERS, GoalTier.EARLY, 11,
			"Unlocks Herblore so future quest requirements and potion utility can start progressing.",
			steps("Finish the short Taverley quest as early as possible to unlock Herblore.",
				"After completion, use lamps, quests, or basic cleaning/mixing to begin banking Herblore levels.",
				"Keep this ahead of quests that start asking for low Herblore requirements."),
			false,
			quest(Quest.DRUIDIC_RITUAL)));
		goals.add(goal("waterfall-quest", "Waterfall Quest", GoalCategory.QUEST_CLUSTERS, GoalTier.EARLY, 12,
			"Fast early Attack and Strength XP that speeds up most quest combat checks.",
			steps("Bring food, rope, games necklace access, and run energy for the dungeon sections.",
				"Complete the quest before grinding early melee levels by hand.",
				"Use the XP reward to make later gnome and arena quests safer."),
			false,
			quest(Quest.WATERFALL_QUEST)));
		goals.add(goal("witchs-house", "Witch's House", GoalCategory.QUEST_CLUSTERS, GoalTier.EARLY, 13,
			"Early Hitpoints XP that makes low-level questing less fragile.",
			steps("Bring enough food for the four experiment fights.",
				"Safespot or kite where your combat style allows it.",
				"Use the Hitpoints reward before stacking more early combat quests."),
			false,
			quest(Quest.WITCHS_HOUSE)));
		goals.add(goal("fight-arena", "Fight Arena", GoalCategory.QUEST_CLUSTERS, GoalTier.EARLY, 14,
			"Efficient early combat XP and a useful step toward broader quest progression.",
			steps("Bring food, teleport safety, and a combat setup that can handle the arena bosses.",
				"Complete it after Waterfall Quest for smoother melee fights.",
				"Treat the reward as part of the early melee foundation."),
			false,
			quest(Quest.FIGHT_ARENA)));
		goals.add(goal("tree-gnome-village", "Tree Gnome Village", GoalCategory.QUEST_CLUSTERS, GoalTier.EARLY, 15,
			"Starts the gnome quest chain and unlocks spirit tree travel.",
			steps("Prepare food and a safe combat setup for the Khazard warlord.",
				"Complete the quest before The Grand Tree and Monkey Madness I.",
				"Use spirit trees as an early transport upgrade."),
			false,
			quest(Quest.TREE_GNOME_VILLAGE)));
		goals.add(goal("grand-tree", "The Grand Tree", GoalCategory.QUEST_CLUSTERS, GoalTier.EARLY, 16,
			"Continues the gnome chain toward Monkey Madness I and gnome glider travel.",
			steps("Complete Tree Gnome Village first, then bring food and teleports for the dungeon sections.",
				"Finish the demon fight with a safe ranged or magic setup if melee is weak.",
				"Use glider access to make later quest routing faster."),
			false,
			quest(Quest.THE_GRAND_TREE)));
		goals.add(goal("priest-in-peril", "Priest in Peril", GoalCategory.QUEST_CLUSTERS, GoalTier.EARLY, 17,
			"Unlocks Morytania access for Ghosts Ahoy, Slayer routing, and later quest chains.",
			steps("Bring food and a weapon for the temple guardian and monk fights.",
				"Finish it before Morytania unlock quests like Ghosts Ahoy.",
				"Use the route unlock for ectophial and Slayer progression."),
			false,
			quest(Quest.PRIEST_IN_PERIL)));
		goals.add(goal("ghosts-ahoy", "Ghosts Ahoy", GoalCategory.QUEST_CLUSTERS, GoalTier.EARLY, 18,
			"Unlocks the ectophial, one of the strongest early teleports.",
			steps("Complete Priest in Peril first and gather the dyes and ecto-token items.",
				"Work through the port town errands with extra inventory space.",
				"Use the ectophial afterward for Morytania, Prayer, and clue travel."),
			false,
			quest(Quest.GHOSTS_AHOY)));
		goals.add(goal("fairytale-i", "Fairytale I", GoalCategory.QUEST_CLUSTERS, GoalTier.EARLY, 19,
			"Sets up Fairy Ring access by opening the Fairytale II start requirement.",
			steps("Complete the prerequisite quest chain, then gather secateurs and boss supplies.",
				"Finish Fairytale I before starting Fairytale II for fairy rings.",
				"Use this as the bridge from Lost City into the travel-network unlock."),
			false,
			quest(Quest.FAIRYTALE_I__GROWING_PAINS)));
		goals.add(goal("ava-device", "Ava's Device", GoalCategory.ACCOUNT_UNLOCKS, GoalTier.EARLY, 20,
			"A ranged quality-of-life unlock from Animal Magnetism.",
			steps("Train the Animal Magnetism skill requirements, complete the prerequisite quests, then finish Animal Magnetism.",
				"Claim Ava's attractor or accumulator from Ava and mark this complete once the cape-slot device is owned."),
			true,
			skill(Skill.RANGED, 30),
			skill(Skill.SLAYER, 18),
			skill(Skill.CRAFTING, 19),
			skill(Skill.WOODCUTTING, 35),
			quest(Quest.ANIMAL_MAGNETISM),
			manual("ava-device", "Claim Ava's device")));
		goals.add(goal("dragon-scimitar-access", "Dragon Scimitar Access", GoalCategory.ACCOUNT_UNLOCKS, GoalTier.EARLY, 30,
			"Monkey Madness I unlocks the ability to buy and use the classic early melee weapon.",
			steps("Complete the gnome quest chain into Monkey Madness I, then finish the Jungle Demon fight.",
				"After the quest, return to Ape Atoll or buy the weapon from another player if tradeable access is enough for your account."),
			true,
			quest(Quest.MONKEY_MADNESS_I),
			manual("dragon-scimitar-access", "Dragon scimitar access unlocked")));
		goals.add(goal("dragon-scimitar", "Own Dragon Scimitar", GoalCategory.GEAR_GOALS, GoalTier.EARLY, 35,
			"Core early melee weapon after Monkey Madness I. Auto-checks when seen in inventory, equipment, or a loaded bank.",
			steps("Reach 60 Attack, complete Monkey Madness I, then buy or obtain a dragon scimitar.",
				"Equip it as the main early melee weapon until whip-tier upgrades take over."),
			true,
			skill(Skill.ATTACK, 60),
			quest(Quest.MONKEY_MADNESS_I),
			manual("dragon-scimitar", "Own dragon scimitar")));
		goals.add(goal("fairy-rings", "Fairy Rings", GoalCategory.ACCOUNT_UNLOCKS, GoalTier.EARLY, 40,
			"Fast travel network unlocked after starting Fairytale II. Auto-checks after a confirmed fairy-ring use or completed Fairytale II.",
			steps("Complete Lost City, Nature Spirit, and Fairytale I, then start Fairytale II far enough for the Fairy Godfather to grant ring access.",
				"Use any fairy ring once to confirm the unlock, or mark it manually if the client cannot detect it."),
			true,
			manual("fairy-rings", "Fairy rings usable"),
			text("If this is not checked automatically, mark it once the Fairy Godfather has granted access.")));
		goals.add(goal("fighter-torso", "Fighter Torso", GoalCategory.GEAR_GOALS, GoalTier.EARLY, 50,
			"Strong melee body slot before high-cost upgrades.",
			steps("Learn each Barbarian Assault role, complete queen kills, and earn 375 honour points in attacker, defender, collector, and healer.",
				"Buy the torso from Commander Connad, then mark it complete once it is owned."),
			true,
			skill(Skill.DEFENCE, 40),
			manual("fighter-torso", "Earn fighter torso")));
		goals.add(goal("dragon-defender", "Dragon Defender", GoalCategory.ACCOUNT_UNLOCKS, GoalTier.EARLY, 60,
			"Staple off-hand for melee progression. The 60 Attack and 60 Defence checks are for cleanly equipping the defender once earned.",
			steps("Enter the Warriors' Guild, collect tokens, get bronze through rune defenders from cyclopes, then use the basement cyclopes for dragon defender."),
			true,
			skill(Skill.ATTACK, 60),
			skill(Skill.DEFENCE, 60),
			any("Warriors' Guild access",
				skillTotal("130 combined Attack + Strength", 130, Skill.ATTACK, Skill.STRENGTH),
				skill(Skill.ATTACK, 99),
				skill(Skill.STRENGTH, 99)),
			manual("dragon-defender", "Earn dragon defender")));
		goals.add(goal("dwarf-cannon", "Dwarf Cannon", GoalCategory.SIDE_QUEST_UNLOCKS, GoalTier.EARLY, 61,
			"Unlocks cannon use for Slayer, combat training, and some multi-combat grinds.",
			steps("Complete the short dwarf quest near the Fishing Guild.",
				"Buy or reclaim a dwarf multicannon after completion if the account can afford it.",
				"Use it selectively where cannoning saves meaningful Slayer or combat time."),
			false,
			quest(Quest.DWARF_CANNON)));
		goals.add(goal("bone-voyage", "Bone Voyage", GoalCategory.SIDE_QUEST_UNLOCKS, GoalTier.EARLY, 62,
			"Unlocks Fossil Island for birdhouses, seaweed, ammonite crabs, and future account utility.",
			steps("Complete the Varrock Museum kudos requirement and the Dig Site access path.",
				"Finish Bone Voyage to open Fossil Island travel.",
				"Start birdhouse and seaweed routines once the island is available."),
			false,
			quest(Quest.BONE_VOYAGE)));
		goals.add(goal("tears-of-guthix", "Tears of Guthix", GoalCategory.SIDE_QUEST_UNLOCKS, GoalTier.EARLY, 63,
			"Unlocks a weekly low-skill XP reward that quietly smooths account progression.",
			steps("Meet the quest point and firemaking/crafting/mining requirements.",
				"Complete the short cave quest and unlock the weekly minigame.",
				"Use the weekly reward on low or awkward skills while questing."),
			false,
			skill(Skill.FIREMAKING, 49),
			skill(Skill.CRAFTING, 20),
			skill(Skill.MINING, 20),
			quest(Quest.TEARS_OF_GUTHIX)));
		goals.add(goal("family-crest", "Family Crest", GoalCategory.SIDE_QUEST_UNLOCKS, GoalTier.MID, 64,
			"Unlocks useful gauntlets for cooking, smelting, and magic combat.",
			steps("Train the combat and magic needed for the three family trials.",
				"Complete the quest, then choose or swap gauntlet effects as the account needs them.",
				"Use cooking gauntlets for food progression and goldsmith gauntlets for Smithing routes."),
			false,
			skill(Skill.MAGIC, 59),
			quest(Quest.FAMILY_CREST)));
		goals.add(goal("horror-from-the-deep", "Horror from the Deep", GoalCategory.SIDE_QUEST_UNLOCKS, GoalTier.MID, 65,
			"Unlocks god books and sets up several clue and combat utility rewards.",
			steps("Bring all combat styles, food, and teleports for the lighthouse boss.",
				"Complete the quest, then pick up an unfinished god book.",
				"Fill pages over time if the prayer or style bonuses are useful."),
			false,
			quest(Quest.HORROR_FROM_THE_DEEP)));
		goals.add(goal("mage-arena-i", "Mage Arena I", GoalCategory.SIDE_QUEST_UNLOCKS, GoalTier.MID, 66,
			"Unlocks god spells and god capes for early magic gear progression.",
			steps("Bring Wilderness safety supplies and only risk what you are comfortable losing.",
				"Defeat Kolodion and choose a god alignment.",
				"Claim the cape and unlock the matching god spell."),
			true,
			skill(Skill.MAGIC, 60),
			quest(Quest.MAGE_ARENA_I),
			manual("mage-arena-i", "Claim god cape")));
		goals.add(goal("plague-city", "Plague City", GoalCategory.SIDE_QUEST_UNLOCKS, GoalTier.EARLY, 67,
			"Unlocks Ardougne teleport access and starts the elf quest chain.",
			steps("Complete the short East Ardougne quest as soon as teleports matter.",
				"Use Ardougne teleport access to speed up questing and clue movement.",
				"Keep the chain moving later toward Underground Pass and Song of the Elves."),
			false,
			quest(Quest.PLAGUE_CITY)));
		goals.add(goal("watchtower", "Watchtower", GoalCategory.SIDE_QUEST_UNLOCKS, GoalTier.MID, 68,
			"Unlocks the Watchtower teleport for Yanille, clues, and southern Kandarin routing.",
			steps("Train the listed skill requirements and gather the ogre quest items.",
				"Complete the Watchtower quest to unlock the spell destination.",
				"Use the teleport for Yanille, nightmare zone, clues, and nearby quest routes."),
			false,
			skill(Skill.AGILITY, 25),
			skill(Skill.HERBLORE, 14),
			skill(Skill.MAGIC, 14),
			skill(Skill.MINING, 40),
			skill(Skill.THIEVING, 15),
			quest(Quest.WATCHTOWER)));
		goals.add(goal("throne-of-miscellania", "Throne of Miscellania", GoalCategory.SIDE_QUEST_UNLOCKS, GoalTier.MID, 69,
			"Unlocks kingdom management for passive resources.",
			steps("Complete the Fremennik prerequisite path and bring the approval items.",
				"Finish the quest to unlock Miscellania management.",
				"Fund the kingdom only when the account can spare the coins."),
			false,
			quest(Quest.THRONE_OF_MISCELLANIA)));
		goals.add(goal("royal-trouble", "Royal Trouble", GoalCategory.SIDE_QUEST_UNLOCKS, GoalTier.MID, 70,
			"Improves kingdom management after Throne of Miscellania.",
			steps("Complete Throne of Miscellania first.",
				"Finish Royal Trouble to expand worker allocation and resource output.",
				"Use the improved kingdom once passive herbs, logs, or fish are worth funding."),
			false,
			quest(Quest.THRONE_OF_MISCELLANIA),
			quest(Quest.ROYAL_TROUBLE)));
		goals.add(goal("eagles-peak", "Eagles' Peak", GoalCategory.SIDE_QUEST_UNLOCKS, GoalTier.EARLY, 71,
			"Unlocks box traps and helps early Hunter progression.",
			steps("Train the Hunter requirement and bring the needed puzzle items.",
				"Complete the quest to unlock box trapping.",
				"Use the unlock when Hunter goals or chinchompa routes become relevant."),
			false,
			skill(Skill.HUNTER, 27),
			quest(Quest.EAGLES_PEAK)));
		goals.add(goal("recruitment-drive", "Recruitment Drive", GoalCategory.SIDE_QUEST_UNLOCKS, GoalTier.EARLY, 72,
			"Unlocks initiate armour access and progresses the Temple Knight quest line.",
			steps("Complete Black Knights' Fortress and prepare for puzzle rooms without combat gear reliance.",
				"Finish the trials to join the Temple Knights.",
				"Keep the chain available for Wanted! and later white knight rewards."),
			false,
			quest(Quest.RECRUITMENT_DRIVE)));
		goals.add(goal("wanted", "Wanted!", GoalCategory.SIDE_QUEST_UNLOCKS, GoalTier.MID, 73,
			"Progresses Temple Knight unlocks and supports later white knight equipment access.",
			steps("Complete Recruitment Drive first and bring combat supplies for Solus Dellagar.",
				"Work through the clue-style tracking steps.",
				"Finish the quest to continue the Temple Knight reward chain."),
			false,
			quest(Quest.RECRUITMENT_DRIVE),
			quest(Quest.WANTED)));
		goals.add(goal("slug-menace", "The Slug Menace", GoalCategory.SIDE_QUEST_UNLOCKS, GoalTier.MID, 74,
			"Unlocks proselyte armour access for Prayer-friendly melee and Slayer setups.",
			steps("Complete Wanted! and Sea Slug, then gather the quest items.",
				"Finish the investigation and combat encounters.",
				"Buy proselyte armour afterward when Prayer bonus matters."),
			false,
			quest(Quest.WANTED),
			quest(Quest.SEA_SLUG),
			quest(Quest.THE_SLUG_MENACE)));
		goals.add(goal("recipe-for-disaster-start", "Recipe for Disaster Started", GoalCategory.QUEST_CLUSTERS, GoalTier.EARLY, 70,
			"Open the long Barrows gloves chain early so subquests can fit around other goals.",
			steps("Get 10 Cooking and complete Cook's Assistant, then start Recipe for Disaster in Lumbridge Castle.",
				"Use this as a long-running umbrella goal while you finish subquests around other unlocks."),
			false,
			skill(Skill.COOKING, 10),
			quest(Quest.RECIPE_FOR_DISASTER__ANOTHER_COOKS_QUEST)));
		goals.add(goal("lost-city", "Lost City", GoalCategory.QUEST_CLUSTERS, GoalTier.EARLY, 80,
			"Unlocks Zanaris and dragon dagger access.",
			steps("Train 31 Crafting and 36 Woodcutting, bring an axe you can use, and defeat the tree spirit without weapons or armour equipped.",
				"Use the quest to unlock Zanaris, dragon dagger access, and the prerequisite chain for fairy rings."),
			false,
			skill(Skill.CRAFTING, 31),
			skill(Skill.WOODCUTTING, 36),
			quest(Quest.LOST_CITY)));
		goals.add(goal("prayer-70", "70 Prayer", GoalCategory.SKILL_TARGETS, GoalTier.MID, 90,
			"Supports Piety and tougher PvM prayers.",
			steps("Use bones, ensouled heads, or high-value prayer methods to push from protection prayers to 70 Prayer.",
				"Pair this with King's Ransom so Piety is ready as soon as Knight Waves are done."),
			false,
			skill(Skill.PRAYER, 70)));
		goals.add(goal("ranged-75", "75 Ranged", GoalCategory.SKILL_TARGETS, GoalTier.MID, 100,
			"Unlocks blowpipe and stronger ranged setups.",
			steps("Train Ranged through Slayer, crabs, Nightmare Zone, or chinning if the account can afford it.",
				"At 75 Ranged, plan blowpipe or other tier-75 ranged upgrades for Fight Caves and early PvM."),
			false,
			skill(Skill.RANGED, 75)));
		goals.add(goal("construction-50", "50 Construction", GoalCategory.SKILL_TARGETS, GoalTier.MID, 110,
			"Gets the house moving before the ornate pool push.",
			steps("Build efficient planks-based furniture to 50 Construction, using house teleports and a butler once available.",
				"Use this tier for basic portal, altar, and storage utility before the expensive 83 push."),
			false,
			skill(Skill.CONSTRUCTION, 50)));
		goals.add(goal("construction-83", "83 Construction", GoalCategory.SKILL_TARGETS, GoalTier.LATE, 120,
			"Boostable account milestone for ornate pool and strong house utility.",
			steps("Plan plank cost first, then train with oak larders, mahogany tables, or other high-throughput furniture.",
				"Use boosts from 83 to build top-tier house utility such as ornate pool and jewelry box upgrades."),
			false,
			skill(Skill.CONSTRUCTION, 83)));
		goals.add(goal("lunar-diplomacy", "Lunar Diplomacy", GoalCategory.QUEST_CLUSTERS, GoalTier.MID, 130,
			"Unlocks Lunar spells and sets up Vengeance/Dream Mentor progression.",
			steps("Finish the Fremennik, Lost City, Rune Mysteries, and Shilo Village prerequisites, then train the Lunar skill gates.",
				"Complete Lunar Diplomacy to unlock Lunar spells and set up Dream Mentor."),
			false,
			skill(Skill.CRAFTING, 61),
			skill(Skill.DEFENCE, 40),
			skill(Skill.FIREMAKING, 49),
			skill(Skill.HERBLORE, 5),
			skill(Skill.MAGIC, 65),
			skill(Skill.MINING, 60),
			skill(Skill.WOODCUTTING, 55),
			quest(Quest.LUNAR_DIPLOMACY)));
		goals.add(goal("dream-mentor", "Dream Mentor", GoalCategory.QUEST_CLUSTERS, GoalTier.MID, 140,
			"Expands Lunar spellbook utility.",
			steps("Complete Lunar Diplomacy, prepare for the 85 combat gate and the dream bosses, then stock food and emergency teleports.",
				"Finish Dream Mentor to expand the Lunar spellbook and improve utility for PvM and skilling."),
			false,
			quest(Quest.LUNAR_DIPLOMACY),
			quest(Quest.DREAM_MENTOR)));
		goals.add(goal("desert-treasure", "Desert Treasure I", GoalCategory.QUEST_CLUSTERS, GoalTier.MID, 150,
			"Ancient Magicks unlock for combat and utility.",
			steps("Complete the Dig Site, Tourist Trap, Temple of Ikov, Waterfall, and Troll Stronghold chains while training the listed skills.",
				"Fight the four diamonds carefully, then finish the pyramid to unlock Ancient Magicks."),
			false,
			skill(Skill.AGILITY, 15),
			skill(Skill.FIREMAKING, 50),
			skill(Skill.FLETCHING, 10),
			skill(Skill.HERBLORE, 10),
			skill(Skill.MAGIC, 50),
			skill(Skill.RANGED, 40),
			skill(Skill.SLAYER, 10),
			skill(Skill.SMITHING, 20),
			skill(Skill.THIEVING, 53),
			quest(Quest.DESERT_TREASURE_I)));
		goals.add(goal("kings-ransom", "King's Ransom", GoalCategory.QUEST_CLUSTERS, GoalTier.MID, 160,
			"Quest backbone for Piety.",
			steps("Train the Camelot quest-line skill requirements, complete the Arthur prerequisites, and prepare for the Black Knight Titan.",
				"Finish King's Ransom so Knight Waves can unlock Piety afterward."),
			false,
			skill(Skill.AGILITY, 36),
			skill(Skill.ATTACK, 20),
			skill(Skill.CRAFTING, 25),
			skill(Skill.DEFENCE, 65),
			skill(Skill.HERBLORE, 18),
			skill(Skill.MAGIC, 45),
			skill(Skill.SMITHING, 30),
			quest(Quest.KINGS_RANSOM)));
		goals.add(goal("piety", "Piety", GoalCategory.ACCOUNT_UNLOCKS, GoalTier.MID, 170,
			"Melee prayer unlock after King's Ransom and 70 Prayer.",
			steps("Reach 70 Prayer and 70 Defence, complete King's Ransom, then clear the Knight Waves training grounds.",
				"Turn on Piety for melee bossing, Slayer, and difficult quest fights."),
			true,
			skill(Skill.PRAYER, 70),
			skill(Skill.DEFENCE, 70),
			quest(Quest.KINGS_RANSOM),
			manual("piety", "Complete Knight Waves and unlock Piety")));
		goals.add(goal("barrows-gloves", "Barrows Gloves", GoalCategory.ACCOUNT_UNLOCKS, GoalTier.MID, 180,
			"Classic main-account milestone from Recipe for Disaster.",
			steps("Complete all Recipe for Disaster subquests, including the Culinaromancer finale, then buy the gloves from the chest.",
				"Treat missing subquests as the route; the gloves themselves are the final manual ownership check."),
			true,
			quest(Quest.RECIPE_FOR_DISASTER__CULINAROMANCER),
			manual("barrows-gloves", "Buy Barrows gloves")));
		goals.add(goal("fire-cape", "Fire Cape", GoalCategory.ACCOUNT_UNLOCKS, GoalTier.MID, 190,
			"Major combat cape milestone and melee upgrade.",
			steps("Bring a stable ranged setup, protection prayers, supplies, and practice Jad prayer switches before committing to a full run.",
				"Defeat TzTok-Jad in the Fight Caves and mark this complete once the cape is claimed."),
			true,
			skill(Skill.RANGED, 70),
			skill(Skill.PRAYER, 43),
			manual("fire-cape", "Defeat TzTok-Jad")));
		goals.add(goal("berserker-ring", "Berserker Ring", GoalCategory.GEAR_GOALS, GoalTier.MID, 200,
			"High-value melee ring goal.",
			steps("Buy the ring or farm Dagannoth Rex with a safe magic setup after getting access to Waterbirth Island.",
				"Imbue it through Nightmare Zone, Soul Wars, or PvP Arena when you want the stronger melee bonus."),
			true,
			manual("berserker-ring", "Own berserker ring")));
		goals.add(goal("fury", "Amulet of Fury", GoalCategory.GEAR_GOALS, GoalTier.MID, 210,
			"All-around combat amulet before late-game jewelry.",
			steps("Buy an amulet of fury or craft one from an onyx if the account is self-sufficient.",
				"Use it as the all-style amulet until torture, anguish, occult, or blood fury upgrades become realistic."),
			true,
			manual("fury", "Own amulet of fury")));
		goals.add(goal("whip", "Abyssal Whip", GoalCategory.GEAR_GOALS, GoalTier.MID, 220,
			"Reliable main-hand melee weapon.",
			steps("Reach 70 Attack, then buy a whip or obtain one from abyssal demons if the account is self-sufficient.",
				"Use it as the default slash weapon for Slayer and melee progression."),
			true,
			skill(Skill.ATTACK, 70),
			manual("whip", "Own abyssal whip")));
		goals.add(goal("blowpipe", "Toxic Blowpipe", GoalCategory.GEAR_GOALS, GoalTier.MID, 230,
			"Strong ranged weapon once 75 Ranged is reached.",
			steps("Reach 75 Ranged, then buy a charged blowpipe or create one from a tanzanite fang with 78 Fletching.",
				"Keep scales and darts stocked before relying on it for Fight Caves, Slayer, or bossing."),
			true,
			skill(Skill.RANGED, 75),
			manual("blowpipe", "Own toxic blowpipe")));
		goals.add(goal("toxic-trident", "Toxic Trident", GoalCategory.GEAR_GOALS, GoalTier.LATE, 240,
			"Core powered staff upgrade for magic PvM.",
			steps("Reach 75 Magic, then buy a trident of the swamp or upgrade a trident of the seas with a magic fang.",
				"Charge it before bossing so the built-in spell can be used without carrying combat runes."),
			true,
			skill(Skill.MAGIC, 75),
			manual("toxic-trident", "Own toxic trident")));
		goals.add(goal("rigour", "Rigour", GoalCategory.ACCOUNT_UNLOCKS, GoalTier.LATE, 250,
			"Ranged prayer scroll unlock.",
			steps("Reach 74 Prayer and 70 Defence, then use a dexterous prayer scroll to unlock Rigour.",
				"Prioritize it for ranged bossing once the scroll cost fits the account."),
			true,
			skill(Skill.PRAYER, 74),
			skill(Skill.DEFENCE, 70),
			manual("rigour", "Unlock Rigour prayer")));
		goals.add(goal("augury", "Augury", GoalCategory.ACCOUNT_UNLOCKS, GoalTier.LATE, 260,
			"Magic prayer scroll unlock.",
			steps("Reach 77 Prayer and 70 Defence, then use an arcane prayer scroll to unlock Augury.",
				"Treat it as a later magic accuracy and defence upgrade after core melee and ranged prayers."),
			true,
			skill(Skill.PRAYER, 77),
			skill(Skill.DEFENCE, 70),
			manual("augury", "Unlock Augury prayer")));
		goals.add(goal("dragon-slayer-ii", "Dragon Slayer II", GoalCategory.QUEST_CLUSTERS, GoalTier.LATE, 270,
			"Unlocks Vorkath and strong late mid-game account options.",
			steps("Build toward the 200 quest points, major prerequisite quests, and the listed skill requirements before starting the final push.",
				"Prepare anti-dragon gear and a bossing setup for Galvek, then use completion to unlock Vorkath and Ava's assembler progression."),
			false,
			skill(Skill.AGILITY, 60),
			skill(Skill.CONSTRUCTION, 50),
			skill(Skill.CRAFTING, 62),
			skill(Skill.HITPOINTS, 50),
			skill(Skill.MAGIC, 75),
			skill(Skill.MINING, 68),
			skill(Skill.SMITHING, 70),
			skill(Skill.THIEVING, 60),
			quest(Quest.DRAGON_SLAYER_II)));
		goals.add(goal("song-of-the-elves", "Song of the Elves", GoalCategory.QUEST_CLUSTERS, GoalTier.LATE, 280,
			"Prifddinas unlock and a long-term main account target.",
			steps("Complete the elf quest line through Mourning's End Part II while raising the eight required skills to 70.",
				"Prepare for the Fragment of Seren fight, then unlock Prifddinas as a major late account hub."),
			false,
			skill(Skill.AGILITY, 70),
			skill(Skill.CONSTRUCTION, 70),
			skill(Skill.FARMING, 70),
			skill(Skill.HERBLORE, 70),
			skill(Skill.HUNTER, 70),
			skill(Skill.MINING, 70),
			skill(Skill.SMITHING, 70),
			skill(Skill.WOODCUTTING, 70),
			quest(Quest.SONG_OF_THE_ELVES)));
		goals.add(goal("quest-cape-path", "Quest Cape Path", GoalCategory.QUEST_CLUSTERS, GoalTier.LATE, 290,
			"Long-term account structure after core mid-game unlocks.",
			steps("Use the remaining quest list as the checklist, grouping requirements so skilling sessions unlock several quests at once.",
				"Keep gear, teleports, and boss supplies ready for grandmaster quests, then mark this complete once the route is actively planned."),
			true,
			manual("quest-cape-path", "Quest cape path planned")));

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
		return goalWithHowTo(id, title, category, tier, priority, description, Collections.emptyList(), manualCompletion, requirements);
	}

	private static RoadmapGoal goal(
		String id,
		String title,
		GoalCategory category,
		GoalTier tier,
		int priority,
		String description,
		List<String> howToSteps,
		boolean manualCompletion,
		RoadmapRequirement... requirements)
	{
		return new RoadmapGoal(id, title, category, tier, priority, description, Arrays.asList(requirements), howToSteps, manualCompletion);
	}

	private static RoadmapGoal goalWithHowTo(
		String id,
		String title,
		GoalCategory category,
		GoalTier tier,
		int priority,
		String description,
		List<String> howToSteps,
		boolean manualCompletion,
		RoadmapRequirement... requirements)
	{
		return goal(id, title, category, tier, priority, description, howToSteps, manualCompletion, requirements);
	}

	private static List<String> steps(String... steps)
	{
		return Arrays.asList(steps);
	}
}
