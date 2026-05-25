package com.mainframe;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;
import org.junit.Test;

public class CustomGoalCodecTest
{
	@Test
	public void roundTripsCustomGoals()
	{
		List<CustomGoal> goals = Arrays.asList(
			new CustomGoal("one", "Get fury | then send Zulrah", GoalCategory.GEAR_GOALS, false),
			new CustomGoal("two", "Finish RFD", GoalCategory.QUEST_CLUSTERS, true));

		List<CustomGoal> decoded = CustomGoalCodec.decode(CustomGoalCodec.encode(goals));

		assertEquals(2, decoded.size());
		assertEquals("Get fury | then send Zulrah", decoded.get(0).getTitle());
		assertEquals(GoalCategory.QUEST_CLUSTERS, decoded.get(1).getCategory());
		assertTrue(decoded.get(1).isComplete());
	}
}

