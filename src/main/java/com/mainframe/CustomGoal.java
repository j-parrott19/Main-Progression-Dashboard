package com.mainframe;

import java.util.Objects;
import java.util.UUID;

final class CustomGoal
{
	private final String id;
	private final String title;
	private final GoalCategory category;
	private final boolean complete;

	CustomGoal(String id, String title, GoalCategory category, boolean complete)
	{
		this.id = Objects.requireNonNull(id, "id");
		this.title = Objects.requireNonNull(title, "title").trim();
		this.category = Objects.requireNonNull(category, "category");
		this.complete = complete;
	}

	static CustomGoal create(String title, GoalCategory category)
	{
		return new CustomGoal(UUID.randomUUID().toString(), title, category, false);
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

	boolean isComplete()
	{
		return complete;
	}

	CustomGoal withTitle(String title)
	{
		return new CustomGoal(id, title, category, complete);
	}

	CustomGoal withComplete(boolean complete)
	{
		return new CustomGoal(id, title, category, complete);
	}
}

