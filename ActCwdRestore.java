package com.shanebow.tools.Expose;

import com.shanebow.ui.menu.AbstractToggleAction;
import com.shanebow.util.SBProperties;
import java.awt.event.*;
import javax.swing.AbstractButton;

public final class ActCwdRestore
	extends AbstractToggleAction
	{
	public static final String CMD_NAME="cwd Restore";
  /**
  * Constructor. 
  * 
  * @param aFrame parent window to which this dialog is attached.
  */
  public ActCwdRestore()
		{
		super(CMD_NAME);
		putValue(SHORT_DESCRIPTION, "Restore the last dir next time Expose runs?");
		putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_D) );
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
		return SBProperties.getInstance().getBoolean("usr.cwd.restore", true);
		}

	@Override public void actionPerformed(ActionEvent e)
		{
		SBProperties.set("usr.cwd.restore",
			((AbstractButton)e.getSource()).isSelected());
	  }
	}