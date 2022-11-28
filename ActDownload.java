package com.shanebow.tools.Expose;
/********************************************************************
* @(#)ActDownload.java 1.00 20110630
* Copyright © 2011-2013 by Richard T. Salamone, Jr. All rights reserved.
*
* ActDownload: Action to initiate a download of an arbitrary URL to
* the local file system.
*
* @version 1.00 20110630
* @author Rick Salamone
* 20110630 rts created
* 20130713 rts modified to split out filespec
* 20150623 rts now refreshes files and selects the downloaded file
*******************************************************/
import com.shanebow.ui.LAF;
import com.shanebow.ui.SBAction;
import com.shanebow.ui.SBDialog;
import com.shanebow.util.SBDate;
import com.shanebow.util.SBProperties;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.io.*;
import java.net.*;
import javax.swing.*;

public class ActDownload
	extends SBAction
	{
	private final FileModule fFileModule;

	public ActDownload(FileModule aFileModule)
		{
		super( "Download", 'D', "Download from an arbitrary URL", null );
		fFileModule = aFileModule;
		}

	@Override public void actionPerformed(ActionEvent e)
		{
 		String[] options = { "OK", "Cancel" };
		JTextField tfFrom = new JTextField(40);
		JTextField tfTo = new JTextField(40);
		JTextField tfFile = new JTextField(40);
		JPanel panel = new JPanel(new GridLayout(0,1));
		panel.add(LAF.titled(tfFrom, "Source Context"));
		panel.add(LAF.titled(tfTo, "Destination Path" ));
		panel.add(LAF.titled(tfFile, "Filespec" ));
		SBProperties props = SBProperties.getInstance();
		if ( props != null )
			{
			tfFrom.setText(props.getProperty("usr.download.from", "http://"));
			tfTo.setText(props.getProperty("usr.cwd", ""));
			}
		while ( true )
			{
			if ( 0 != JOptionPane.showOptionDialog(null, panel,
				LAF.getDialogTitle(toString()), JOptionPane.DEFAULT_OPTION,
					JOptionPane.PLAIN_MESSAGE, null, options, options[0] ))
				return;
			String from = tfFrom.getText().trim();
			if (!from.endsWith("/")) from += "/";
			if (download(from, tfTo.getText(), tfFile.getText()))
				tfFile.setText("");
			if ( props != null )
				props.set("usr.download.from", from);
			}
		}

	private boolean download(String fromContext, String toDir, String file)
		{
		InputStream is = null;
		FileOutputStream os = null;
		try
			{
			File to = new File(toDir, file);
			URL url = new URL(new URL(fromContext), file);
			is = url.openStream(); // throws an IOException
			BufferedInputStream bis = new BufferedInputStream(is);
			os = new FileOutputStream(to);
			BufferedOutputStream bos = new BufferedOutputStream(os);
			int b;
			while ((b = bis.read()) != -1)
				bos.write(b);
			bos.flush();
			fFileModule.refresh();
			fFileModule.select(to);
/**
			SBDialog.inform(LAF.getDialogTitle(toString()),
				"<html><b>" + url + "<br>successfully downloaded to<br>" + to);
**/
			return true;
			}
		catch (MalformedURLException mue)
			{
			SBDialog.error("Download Error", mue.toString());
			}
		catch (IOException ioe)
			{
			SBDialog.error("Download Error", ioe.toString());
			}
		finally
			{
			try { is.close(); os.close(); }
			catch (IOException ioe) {} // just going to ignore this one
			}
		return false;
		}
	}
