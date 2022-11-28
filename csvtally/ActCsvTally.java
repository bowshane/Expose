package com.shanebow.tools.Expose.csvtally;
/********************************************************************
* @(#)ActCsvTally.java 1.00 20110106
* Copyright 2011 by Richard T. Salamone, Jr. All rights reserved.
*
* ActCsvTally: Action class to display a tally of the
* contents of a specified column over multiple csv files.
*
* @author Rick Salamone
* @version 1.00, 20110106 rts created
*******************************************************/
import com.shanebow.ui.SBAction;
import java.awt.event.ActionEvent;
import javax.swing.*;

public final class ActCsvTally
	extends SBAction
	{
	public static final String CMD_NAME="CSV Tally";
	private final JFrame fFrame;

	public ActCsvTally(JFrame aFrame)
		{
		super( CMD_NAME, 'C', "Tally contents of a column over multiple csv's", null );
		fFrame = aFrame;
		}

	@Override public boolean menuOnly() { return true; }
	@Override public void actionPerformed(ActionEvent e)
		{
		new DlgCsvTally(CMD_NAME);
		}
	}
