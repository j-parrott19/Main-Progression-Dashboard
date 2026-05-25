package com.mainframe;

import java.util.List;
import java.util.Set;

interface MainframeStateStore
{
	Set<String> getCompletedManualKeys(String scope);

	void setManualComplete(String scope, String manualKey, boolean complete);

	List<CustomGoal> getCustomGoals(String scope);

	void saveCustomGoals(String scope, List<CustomGoal> goals);
}

