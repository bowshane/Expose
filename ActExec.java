package com.shanebow.tools.Expose;
/********************************************************************
* @(#)ActExec.java 1.00 20110118
* Copyright 2011 by Richard T. Salamone, Jr. All rights reserved.
*
* ActExec: Action class to execute an operating system command
* entered by the user.
*
* @author Rick Salamone
* @version 1.00, 20110118 rts created
*******************************************************/
import com.shanebow.ui.LAF;
import com.shanebow.ui.SBAction;
import com.arashpayan.filetree.GoodWindowsExec;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.Font;
import java.io.IOException;
import java.io.File;
import javax.swing.*;

class ActExec
	extends SBAction
	{
	public static final String CMD_NAME="Run";
	private final JFrame fFrame;

	public ActExec(JFrame aFrame)
		{
		super( CMD_NAME, 'R', "Send a command to the OS for execution", null );
		fFrame = aFrame;
		}

	@Override public void actionPerformed(ActionEvent e)
		{
		String cmd = JOptionPane.showInputDialog(null, "Command: ",
			                    LAF.getDialogTitle(CMD_NAME),
			                    JOptionPane.QUESTION_MESSAGE);
		if ( cmd == null || cmd.isEmpty())
			return;
		new GoodWindowsExec(cmd, null, null).start(); // command, env, working dir
		}
	}
