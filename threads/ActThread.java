package com.shanebow.tools.Expose.threads;
/********************************************************************
* @(#)ActThread.java 1.00 20110613
* Copyright (c) 2011 by Richard T. Salamone, Jr. All rights reserved.
*
* ActThread: Action to display a modal dialog, centered on the
* main window, which shows all threads and information about each.
*
* @version 1.00 20110613
* @author Rick Salamone
* 20110613 RTS 1.00 created
*******************************************************/
import com.shanebow.ui.LAF;
import com.shanebow.ui.SBAction;
import java.awt.event.ActionEvent;
import javax.swing.*;

public final class ActThread
	extends SBAction
	{
	public static final String CMD_NAME="Threads";
	/**
	* Constructor. 
	* 
	* @param aFrame parent window to which this dialog is attached.
	*/
	public ActThread(JFrame aFrame)
		{
		super( CMD_NAME, 'T', "Shows the application threads and their properties", null );
		fFrame = aFrame;
		}

	@Override public boolean menuOnly() { return true; }
	@Override public void actionPerformed(ActionEvent e)
		{
		JOptionPane.showMessageDialog(fFrame, new ThreadViewer(),
			LAF.getDialogTitle(toString()), JOptionPane.PLAIN_MESSAGE );
		}

	// PRIVATE //
	private JFrame fFrame;
	}