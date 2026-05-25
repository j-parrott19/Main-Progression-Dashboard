package com.mainframe;

import java.util.List;
import java.util.Set;

interface MainframeStateStore
{
	Set<String> getCompletedManualKeys(String scope);

	void setManualComplete(String scope, String manualKey, boolean complete);

	ProgressionPath getProgressionPath(String scope);

	boolean hasProgressionPath(String scope);

	void setProgressionPath(String scope, ProgressionPath progressionPath);

	List<CustomGoal> getCustomGoals(String scope);

	void saveCustomGoals(String scope, List<CustomGoal> goals);
}
