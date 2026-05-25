package com.mainframe;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

final class MainframeIcons
{
	private MainframeIcons()
	{
	}

	static BufferedImage createIcon()
	{
		BufferedImage image = new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB);
		Graphics2D graphics = image.createGraphics();
		try
		{
			graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			graphics.setColor(new Color(27, 31, 36));
			graphics.fillRoundRect(3, 4, 26, 24, 6, 6);
			graphics.setColor(new Color(230, 126, 34));
			graphics.setStroke(new BasicStroke(2f));
			graphics.drawRoundRect(4, 5, 24, 22, 6, 6);
			graphics.setColor(new Color(88, 214, 141));
			graphics.fillOval(9, 10, 4, 4);
			graphics.fillOval(9, 18, 4, 4);
			graphics.setColor(new Color(236, 240, 241));
			graphics.fillRect(16, 11, 8, 2);
			graphics.fillRect(16, 19, 8, 2);
		}
		finally
		{
			graphics.dispose();
		}
		return image;
	}
}

