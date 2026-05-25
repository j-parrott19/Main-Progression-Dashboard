package com.mainframe;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

final class CustomGoalCodec
{
	private static final String FIELD_SEPARATOR = "\\|";
	private static final String FIELD_JOINER = "|";

	private CustomGoalCodec()
	{
	}

	static String encode(List<CustomGoal> goals)
	{
		List<String> rows = new ArrayList<>();
		for (CustomGoal goal : goals)
		{
			rows.add(String.join(FIELD_JOINER,
				encodeValue(goal.getId()),
				encodeValue(goal.getTitle()),
				goal.getCategory().name(),
				Boolean.toString(goal.isComplete())));
		}
		return String.join("\n", rows);
	}

	static List<CustomGoal> decode(String value)
	{
		List<CustomGoal> goals = new ArrayList<>();
		String[] rows = value.split("\\R");
		for (String row : rows)
		{
			if (row.trim().isEmpty())
			{
				continue;
			}
			String[] fields = row.split(FIELD_SEPARATOR, -1);
			if (fields.length != 4)
			{
				continue;
			}
			try
			{
				GoalCategory category = GoalCategory.valueOf(fields[2]);
				goals.add(new CustomGoal(decodeValue(fields[0]), decodeValue(fields[1]), category, Boolean.parseBoolean(fields[3])));
			}
			catch (IllegalArgumentException ignored)
			{
				// Ignore malformed rows so one bad entry does not wipe the local checklist.
			}
		}
		return goals;
	}

	private static String encodeValue(String value)
	{
		return Base64.getUrlEncoder().withoutPadding().encodeToString(value.getBytes(StandardCharsets.UTF_8));
	}

	private static String decodeValue(String value)
	{
		return new String(Base64.getUrlDecoder().decode(value), StandardCharsets.UTF_8);
	}
}

