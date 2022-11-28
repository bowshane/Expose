package com.shanebow.tools.Expose.fonts;
/********************************************************************
* @(#)FontAction.java 1.00 20101224
* Copyright © 2010-2011 by Richard T. Salamone, Jr. All rights reserved.
*
* FontAction: Action to display a modal dialog, centered on the main
* window, which shows the available fonts and information about each.
*
* @author Rick Salamone
* @version 1.00
* 20101224 rts created
* 20110613 rts extends SBAction
*******************************************************/
import com.shanebow.ui.LAF;
import com.shanebow.ui.SBAction;
import java.awt.event.ActionEvent;
import javax.swing.*;

public final class FontAction
	extends SBAction
	{
	public static final String CMD_NAME="Fonts";
	/**
	* Constructor. 
	* 
	* @param aFrame parent window to which this dialog is attached.
	*/
	public FontAction(JFrame aFrame)
		{
		super( CMD_NAME, 'F', "Shows the available fonts and their properties", null );
		fFrame = aFrame;
		}

	@Override public boolean menuOnly() { return true; }
	@Override public void actionPerformed(ActionEvent e)
		{
		JOptionPane.showMessageDialog(fFrame, new FontViewer(),
			LAF.getDialogTitle(toString()), JOptionPane.PLAIN_MESSAGE );
		}

	// PRIVATE //
	private JFrame fFrame;
	}