package com.mainframe;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

final class RequirementProgress
{
	private final String label;
	private final boolean complete;
	private final boolean informational;
	private final List<RequirementProgress> options;

	RequirementProgress(String label, boolean complete, boolean informational, List<RequirementProgress> options)
	{
		this.label = label;
		this.complete = complete;
		this.informational = informational;
		this.options = Collections.unmodifiableList(new ArrayList<>(options));
	}

	String getLabel()
	{
		return label;
	}

	boolean isComplete()
	{
		return complete;
	}

	boolean isInformational()
	{
		return informational;
	}

	List<RequirementProgress> getOptions()
	{
		return options;
	}
}
