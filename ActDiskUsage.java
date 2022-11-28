package com.shanebow.tools.Expose;
/********************************************************************
* @(#)ActDiskUsage.java 1.00 20110103
* Copyright 2011 by Richard T. Salamone, Jr. All rights reserved.
*
* ActDiskUsage: Action class to display the system properties
* in a dialog.
*
* @author Rick Salamone
* @version 1.00, 20110103 rts created
*******************************************************/
import com.shanebow.ui.LAF;
import com.shanebow.ui.SBAction;
import java.awt.event.ActionEvent;
import java.awt.Font;
import java.io.IOException;
import java.io.File;
import javax.swing.*;

class ActDiskUsage
	extends SBAction
	{
	public static final String CMD_NAME="Disk Usage";
	private final JFrame fFrame;

	public ActDiskUsage(JFrame aFrame)
		{
		super( CMD_NAME, 'D', "Displays disk drive usage", null );
		fFrame = aFrame;
		}

	@Override public void actionPerformed(ActionEvent e)
		{
		showDiskUsage();
		}

	private void logDiskUsage()
		{
		say("%-5s %12s %12s %12s\n", "Drive", "kbytes", "used", "available");
		try
			{
			for ( char drive = 'C'; drive <= 'Z'; drive++ )
				{
				File f = new File("" + drive + ":" );
				if (f.exists())
					printFileStore(f);
				}
			}
		catch (IOException e)
			{
			say ( "Error retrieving disk usage information\n" + e.getMessage());
			}
		}

	static final long K = 1000; // 1024;
	private void printFileStore(File store) throws IOException
		{
		long total = store.getTotalSpace() / K;
		long used = (store.getTotalSpace() - store.getFreeSpace()) / K;
		long avail = store.getUsableSpace() / K;

		String s = store.toString();
		if (s.length() > 5)
			s = s.substring(0,2) + "...";
		say("%5s %12d %12d %12d\n", s, total, used, avail);
		}

	private JTextArea taOutput;
	private void say ( String fmt, Object... args )
		{
		taOutput.append( String.format(fmt,args) );
		}

	private void showDiskUsage()
		{
		taOutput = new JTextArea(10, 50);
		taOutput.setFont(new Font(Font.MONOSPACED, Font.BOLD, 15));
		logDiskUsage();
		taOutput.setCaretPosition(0);

		JLabel[] options = { new JLabel("") };
		JOptionPane.showOptionDialog(fFrame, new JScrollPane(taOutput),
			LAF.getDialogTitle(toString()), JOptionPane.DEFAULT_OPTION,
			JOptionPane.INFORMATION_MESSAGE, null, options, null);
		}
	}
