package com.mainframe;

import java.util.Locale;
import net.runelite.api.Client;
import net.runelite.api.Player;

final class AccountScope
{
	static final String PROFILE_SCOPE = "profile";

	private AccountScope()
	{
	}

	static String fromClient(Client client)
	{
		if (client == null)
		{
			return PROFILE_SCOPE;
		}

		Player player = client.getLocalPlayer();
		if (player != null && player.getName() != null && !player.getName().trim().isEmpty())
		{
			return sanitize(player.getName());
		}

		String launcherDisplayName = client.getLauncherDisplayName();
		if (launcherDisplayName != null && !launcherDisplayName.trim().isEmpty())
		{
			return sanitize(launcherDisplayName);
		}

		return PROFILE_SCOPE;
	}

	static String displayName(Client client)
	{
		if (client == null || client.getLocalPlayer() == null || client.getLocalPlayer().getName() == null)
		{
			return "Profile";
		}
		return client.getLocalPlayer().getName();
	}

	private static String sanitize(String value)
	{
		String normalized = value.trim().toLowerCase(Locale.ROOT);
		StringBuilder builder = new StringBuilder(normalized.length());
		for (int i = 0; i < normalized.length(); i++)
		{
			char c = normalized.charAt(i);
			if ((c >= 'a' && c <= 'z') || (c >= '0' && c <= '9'))
			{
				builder.append(c);
			}
			else
			{
				builder.append('_');
			}
		}
		String result = builder.toString().replaceAll("_+", "_");
		if (result.isEmpty() || "_".equals(result))
		{
			return PROFILE_SCOPE;
		}
		return result;
	}
}

