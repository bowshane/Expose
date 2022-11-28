package com.shanebow.tools.Expose.search;
/********************************************************************
* @(#)HitListCellRenderer.java 1.00 20101227
* Copyright © 2010-2012 by Richard T. Salamone, Jr. All rights reserved.
*
* HitListCellRenderer: Displays the appropriate icon and the name
* sans directory for a File in a JList.
*
* @author Rick Salamone
* @version 1.00, 20101227
*******************************************************/
import java.awt.Component;
import com.arashpayan.filetree.Constants;
import java.io.File;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

class HitListCellRenderer
	extends JLabel
	implements ListCellRenderer
	{
	// This is the only method defined by ListCellRenderer.
	// We just reconfigure the JLabel each time we're called
 	public Component getListCellRendererComponent(
		JList list,              // the list
		Object value,            // value to display
		int index,               // cell index
		boolean isSelected,      // is the cell selected
		boolean cellHasFocus)    // does the cell have focus
		{
		GrepHitEvent event = (GrepHitEvent)value;
		String text = event.hitString();
		Icon icon = null;
		if ( text == null )
			{
			File file = event.getFile();
			text = "<HTML><B>" + file.getName();
			try { icon = Constants.getIcon(file); }
			catch (Exception e) {}
			}
		else text = "     " + text;
		setIcon(icon);
		setText(text);
		if (isSelected)
			{
			setBackground(list.getSelectionBackground());
			setForeground(list.getSelectionForeground());
			}
		else
			{
			setBackground(list.getBackground());
			setForeground(list.getForeground());
			}
		setEnabled(list.isEnabled());
		setFont(list.getFont());
		setOpaque(true);
		return this;
		}
	}
