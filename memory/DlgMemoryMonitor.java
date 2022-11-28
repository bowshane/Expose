package com.shanebow.tools.Expose.memory;
/********************************************************************
* @(#)DlgMemoryMonitor.java	1.0 20101213
* Copyright 2010 by Richard T. Salamone, Jr. All rights reserved.
*
* DlgMemoryMonitor: Dialog to view the memory monitor.
*
* @author Rick Salamone
* @version 1.00 21011213 rts created
*******************************************************/
import com.shanebow.ui.LAF;
import java.awt.Frame;
import javax.swing.JDialog;

public class DlgMemoryMonitor
	extends JDialog
	{
	private static final String DLG_TITLE="Memory Monitor";
	final MemoryMonitor memMonitor = new MemoryMonitor();

	public DlgMemoryMonitor(Frame f)
		{
		super(f, LAF.getDialogTitle(DLG_TITLE), false);
		getContentPane().add(memMonitor);
		LAF.addUISwitchListener(this);
		pack();
		}

	@Override public void setVisible(boolean visible)
		{
		if ( visible == isVisible())
			return;
		super.setVisible(visible);
		if ( visible )
			memMonitor.surf.start();
		else
			memMonitor.surf.stop();
		}
	}
