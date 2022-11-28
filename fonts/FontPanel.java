package com.shanebow.tools.Expose.fonts;
/********************************************************************
* @(#)FontPanel.java 1.00 2010
* Copyright 2010 by Richard T. Salamone, Jr. All rights reserved.
*
* FontPanel: A component to draw sample string with given font family, style,
*          size, and aliasing.
*
* @author Fred Swartz - 28 Sep 2006 - Placed in public domain.
* @version 1.00, 2010
* 20111218 rts displays a thai string if possible
*******************************************************/
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Rectangle2D;
import javax.swing.JPanel;

//////////////////////////////////////////////////////////////// FontPanel class
class FontPanel extends JPanel
	{
	private String _fontName;
	private int    _fontStyle;
	private int    _fontSize;
	private boolean _antialiased;

	//============================================================== constructor
	public FontPanel(String font, int style, int size, boolean antialiased)
		{
		setPreferredSize(new Dimension(400, 100));
		setBackground(Color.white);
		setForeground(Color.black);
		_fontName  = font;
		_fontStyle = style;
		_fontSize  = size;
		_antialiased = antialiased;
		}

	//================================================= @Override paintComponent
	@Override public void paintComponent(Graphics g)
		{
		super.paintComponent(g);    	// Paint background
		Graphics2D g2 = (Graphics2D)g;  // Graphics2 for antialiasing
		String text = "Font(\""
		            + _fontName + "\", "
		            + fontStyleCodeToFontStyleString(_fontStyle) + ", "
		            + _fontSize + ");";
		Font f = new Font(_fontName, _fontStyle, _fontSize);
		g2.setFont(f);

		if (_antialiased)
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
			                    RenderingHints.VALUE_ANTIALIAS_ON);

		//... Find the size of this text so we can center it.
		FontMetrics fm = g2.getFontMetrics(f);  // metrics for this object
		Rectangle2D rect = fm.getStringBounds(text, g2); // size of string
		int textHeight  = (int)(rect.getHeight());
		int textWidth   = (int)(rect.getWidth());

		//... Center text horizontally and vertically
		int x = (this.getWidth()  - textWidth)  / 2;
		int y = (this.getHeight() - textHeight) / 2  + fm.getAscent();
		g2.drawString(text, x, y);

		if (!f.canDisplay('\u0E20'))
			return;
		text = "\u0E20\u0E32\u0E29\u0E32\u0E44\u0E17\u0e22"; // paa saa tai
		y += textHeight + 5;
		rect = fm.getStringBounds(text, g2); // size of string
		x = (this.getWidth()  - (int)(rect.getWidth()))  / 2;
		g2.drawString(text, x, y);
		}

	//================================================================== SETTERS
	public void setFontName(String fn)      { _fontName = fn;    repaint();}
	public void setFontSize(int size)       { _fontSize = size;  repaint();}
	public void setFontStyle(int style)     {_fontStyle = style; repaint();}
	public void setAntialiasing(boolean on) { _antialiased = on; repaint(); }
    
	//=========================================== fontStyleCodeToFontStyleString
	// Utility method for converting font codes to name.
	public static String fontStyleCodeToFontStyleString(int styleCode)
		{
		switch (styleCode)
			{
			case Font.PLAIN:            return "Font.PLAIN";
			case Font.ITALIC:           return "Font.ITALIC";
			case Font.BOLD:             return "Font.BOLD";
			case Font.ITALIC+Font.BOLD: return "ITALIC+Font.BOLD";
        }
		throw new IllegalArgumentException(
                    "fontStyleCodeToFontStyleString: Unknown font code: " +
                    styleCode);
		}
	}
