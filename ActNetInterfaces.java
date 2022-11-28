package com.shanebow.tools.Expose;
/********************************************************************
* @(#)ActNetInterfaces.java 1.00 20110103
* Copyright 2011 by Richard T. Salamone, Jr. All rights reserved.
*
* ActNetInterfaces: Action class to display the system properties
* in a dialog.
*
* @author Rick Salamone
* @version 1.00, 20110103 rts created
*******************************************************/
import com.shanebow.ui.LAF;
import com.shanebow.ui.SBAction;
import java.awt.event.ActionEvent;
import java.net.*;
import java.util.*;
import javax.swing.*;

class ActNetInterfaces
	extends SBAction
	{
	public static final String CMD_NAME="Net Interfaces";
	private final JFrame fFrame;

	public ActNetInterfaces(JFrame aFrame)
		{
		super( CMD_NAME, 'N', "Displays system network interfaces", null );
		fFrame = aFrame;
		}

	@Override public void actionPerformed(ActionEvent e)
		{
		showNetInterfaces();
		}

	private void logNetworkInfo()
		{
		try
			{
			Enumeration<NetworkInterface> nets = NetworkInterface.getNetworkInterfaces();
			for (NetworkInterface netint : Collections.list(nets))
				displayInterfaceInformation(netint);
			}
		catch (SocketException e)
			{
			say ( "Error ennumerating network interfaces\n" + e.getMessage());
			}
		}

	private void displayInterfaceInformation(NetworkInterface netint)
		throws SocketException
		{
		say("Display name: %s\n", netint.getDisplayName());
		say("   Name: %s\n", netint.getName());
		Enumeration<InetAddress> inetAddresses = netint.getInetAddresses();
		for (InetAddress inetAddress : Collections.list(inetAddresses))
			say("   InetAddress: %s\n", inetAddress);
		}

	private JTextArea taOutput;
	private void say ( String fmt, Object... args )
		{
		taOutput.append( String.format(fmt,args) );
		}

	private void showNetInterfaces()
		{
		taOutput = new JTextArea(15, 40);
		logNetworkInfo();
		taOutput.setCaretPosition(0);

		JLabel[] options = { new JLabel("") };
		JOptionPane.showOptionDialog(fFrame, new JScrollPane(taOutput),
			LAF.getDialogTitle(toString()), JOptionPane.DEFAULT_OPTION,
			JOptionPane.INFORMATION_MESSAGE, null, options, null);
		}
	}
