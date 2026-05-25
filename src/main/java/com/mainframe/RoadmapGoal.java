package com.mainframe;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

final class RoadmapGoal
{
	private final String id;
	private final String title;
	private final GoalCategory category;
	private final GoalTier tier;
	private final int priority;
	private final String description;
	private final List<RoadmapRequirement> requirements;
	private final boolean manualCompletion;

	RoadmapGoal(
		String id,
		String title,
		GoalCategory category,
		GoalTier tier,
		int priority,
		String description,
		List<RoadmapRequirement> requirements,
		boolean manualCompletion)
	{
		this.id = Objects.requireNonNull(id, "id");
		this.title = Objects.requireNonNull(title, "title");
		this.category = Objects.requireNonNull(category, "category");
		this.tier = Objects.requireNonNull(tier, "tier");
		this.priority = priority;
		this.description = Objects.requireNonNull(description, "description");
		this.requirements = Collections.unmodifiableList(new ArrayList<>(requirements));
		this.manualCompletion = manualCompletion;
	}

	String getId()
	{
		return id;
	}

	String getTitle()
	{
		return title;
	}

	GoalCategory getCategory()
	{
		return category;
	}

	GoalTier getTier()
	{
		return tier;
	}

	int getPriority()
	{
		return priority;
	}

	String getDescription()
	{
		return description;
	}

	List<RoadmapRequirement> getRequirements()
	{
		return requirements;
	}

	boolean isManualCompletion()
	{
		return manualCompletion;
	}
}

