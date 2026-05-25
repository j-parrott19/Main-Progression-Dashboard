package com.mainframe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

final class InMemoryMainframeStateStore implements MainframeStateStore
{
	private final Map<String, Set<String>> manual = new HashMap<>();
	private final Map<String, List<CustomGoal>> custom = new HashMap<>();

	@Override
	public Set<String> getCompletedManualKeys(String scope)
	{
		return new HashSet<>(manual.getOrDefault(scope, new HashSet<>()));
	}

	@Override
	public void setManualComplete(String scope, String manualKey, boolean complete)
	{
		Set<String> keys = manual.computeIfAbsent(scope, ignored -> new HashSet<>());
		if (complete)
		{
			keys.add(manualKey);
		}
		else
		{
			keys.remove(manualKey);
		}
	}

	@Override
	public List<CustomGoal> getCustomGoals(String scope)
	{
		return new ArrayList<>(custom.getOrDefault(scope, new ArrayList<>()));
	}

	@Override
	public void saveCustomGoals(String scope, List<CustomGoal> goals)
	{
		custom.put(scope, new ArrayList<>(goals));
	}
}

