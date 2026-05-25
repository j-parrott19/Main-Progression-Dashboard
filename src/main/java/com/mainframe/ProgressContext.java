package com.mainframe;

import net.runelite.api.Quest;
import net.runelite.api.Skill;

interface ProgressContext
{
	int getRealSkillLevel(Skill skill);

	boolean isQuestComplete(Quest quest);

	boolean isManualComplete(String manualKey);
}

