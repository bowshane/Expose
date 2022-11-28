package com.shanebow.tools.Expose.memory;

import com.shanebow.ui.menu.AbstractToggleAction;
import java.awt.event.*;
import java.awt.*;
import javax.swing.*;

public final class MemoryAction
	extends AbstractToggleAction
	{
	public static final String CMD_NAME="Memory Monitor";
  /**
  * Constructor. 
  * 
  * @param aFrame parent window to which this dialog is attached.
  */
  public MemoryAction(JFrame aFrame)
		{
		super(CMD_NAME + "...");
		fFrame = aFrame;
		putValue(SHORT_DESCRIPTION, "Memory allocation & usage for THIS app's objects");
		putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_F) );
		}

	@Override public void update()
		{
		// System.out.println("update()");
		}

	// For check box and radio button menu items -- return TRUE if
	// item should be toggled 'ON', FALSE otherwise.  For
	// non-togglable items, there is no need to override this method.
	@Override public boolean isSelected()
		{
//		System.out.println("isSelected()");
		return (dlgMemMonitor == null)? false : dlgMemMonitor.isVisible();
		}

	@Override public void actionPerformed(ActionEvent e)
		{
		if ( dlgMemMonitor == null )
			dlgMemMonitor = new DlgMemoryMonitor( fFrame );
		dlgMemMonitor.setVisible(!dlgMemMonitor.isVisible());
	  }

	// PRIVATE //
	private final JFrame fFrame;
	private DlgMemoryMonitor dlgMemMonitor;
	}