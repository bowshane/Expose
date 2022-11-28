package com.shanebow.tools.Expose.regex;
/********************************************************************
* @(#)ActRegex.java 1.00 20110103
* Copyright 2011 by Richard T. Salamone, Jr. All rights reserved.
*
* ActRegex: Action class to launch the Regular Expression
* Utility.
*
* @author Rick Salamone
* @version 1.00, 20110106 rts created
*******************************************************/
import com.shanebow.ui.LAF;
import com.shanebow.ui.SBAction;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import javax.swing.*;

public final class ActRegex
	extends SBAction
	{
	public static final String CMD_NAME="Regex Test";
	private final JFrame fFrame;

	public ActRegex(JFrame aFrame)
		{
		super( CMD_NAME, 'R', "Compile & test regular expressions", null );
		fFrame = aFrame;
		}

	@Override public boolean menuOnly() { return true; }
	@Override public void actionPerformed(ActionEvent e)
		{
		regex();
		}

	// PRIVATE //
	static private RegexTester fRegexTest;
	private void regex()
		{
		if ( fRegexTest == null )
			fRegexTest = new RegexTester();
		JButton btnMatch = new JButton("Match?");
		btnMatch.addActionListener( new ActionListener()
			{
			public void actionPerformed(ActionEvent e)
				{
				fRegexTest.doTest();
				}
			});
		Object[] options = { btnMatch };
		JOptionPane.showOptionDialog(fFrame, fRegexTest,
			LAF.getDialogTitle(toString()), JOptionPane.DEFAULT_OPTION,
			JOptionPane.PLAIN_MESSAGE, null, options, btnMatch);
		}
	}
