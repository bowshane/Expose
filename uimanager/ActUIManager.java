package com.shanebow.tools.Expose.uimanager;
/********************************************************************
* @(#)ActUIManager.java 1.00 20130221
* Copyright © 2013 by Richard T. Salamone, Jr. All rights reserved.
*
* ActUIManager: Action to display a modal dialog, centered on the main
* window, which shows information about the java UIManager.
*
* @author Rick Salamone
* @version 1.00
* 20130221 rts created
*******************************************************/
import com.shanebow.ui.LAF;
import com.shanebow.ui.SBAction;
import java.awt.event.ActionEvent;
import javax.swing.*;

public final class ActUIManager
	extends SBAction
	{
	public static final String CMD_NAME="UI Defaults";
	/**
	* Constructor. 
	* 
	* @param aFrame parent window to which this dialog is attached.
	*/
	public ActUIManager(JFrame aFrame)
		{
		super( CMD_NAME, 'U', "Shows the UI Manager default settings", null );
		fFrame = aFrame;
		}

	@Override public boolean menuOnly() { return true; }
	@Override public void actionPerformed(ActionEvent e)
		{
		JOptionPane.showMessageDialog(fFrame, new UIManagerDefaults(),
			LAF.getDialogTitle(toString()), JOptionPane.PLAIN_MESSAGE );
		}

	// PRIVATE //
	private JFrame fFrame;
	}