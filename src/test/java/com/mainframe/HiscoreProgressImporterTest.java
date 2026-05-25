package com.mainframe;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.io.IOException;
import java.util.Collections;
import org.junit.Test;

public class HiscoreProgressImporterTest
{
	@Test
	public void failedLookupFallsBackToUnavailableData() throws Exception
	{
		HiscoreProgressImporter importer = new HiscoreProgressImporter((username, endpoint) ->
		{
			throw new IOException("offline");
		});

		HiscoreAccountData data = importer.lookupAsync("Player", null, Collections.emptySet()).get();

		assertFalse(data.isAvailable());
		assertEquals("Hiscores unavailable", data.getStatusText());
	}
}
