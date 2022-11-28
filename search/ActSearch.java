package com.shanebow.tools.Expose.search;
/********************************************************************
* @(#)ActSearch.java 1.00 20110103
* Copyright 2011 by Richard T. Salamone, Jr. All rights reserved.
*
* ActSearch: Action class to display the system properties
* in a dialog.
*
* @author Rick Salamone
* @version 1.00, 20110103 rts created
*******************************************************/
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import javax.swing.*;

public final class ActSearch
	extends AbstractAction
	{
	public static final String CMD_NAME="Search";

	public ActSearch()
		{
		super( CMD_NAME + "...");
		putValue(SHORT_DESCRIPTION, "Search files for content" );
		putValue(MNEMONIC_KEY, new Integer(java.awt.event.KeyEvent.VK_S) );
		}

	@Override public void actionPerformed(ActionEvent e)
		{
		new DlgSearch(CMD_NAME);
		}
	}
