package com.shanebow.tools.Expose;
/********************************************************************
* @(#)FileModuleDND.java 1.00 2009
* Copyright 2009-2011 by Richard T. Salamone, Jr. All rights reserved.
*
* FileModuleDND: Extends the dnd TransferHandler to launch import
* of call logs.
*
* @author Rick Salamone
* @version 1.00 08/07/10 created from the APO import
* @version 1.01 20110106 modified for Expose
*******************************************************/
import com.shanebow.ui.SBAudio;
import com.shanebow.util.SBLog;
import javax.swing.TransferHandler;
import java.io.*;
import java.net.*;
import java.awt.datatransfer.*;
import java.util.List;
import java.util.Vector;

final class FileModuleDND extends TransferHandler
	{
	private boolean m_copyEnabled = false;
	private final FileModule fFileModule;

	FileModuleDND(FileModule aFileModule)
		{
		super();
		fFileModule = aFileModule;
		}

	private void log ( String fmt, Object... args )
		{
		SBLog.write ( "DND " + String.format(fmt,args));
		}

	public void setCopyEnabled(boolean on) { m_copyEnabled = on; }

	public boolean canImport( TransferHandler.TransferSupport support )
		{
		if ( !support.isDataFlavorSupported(DataFlavor.javaFileListFlavor))
			return false;

		if ( m_copyEnabled )
			{
			boolean copySupported = (COPY & support.getSourceDropActions()) == COPY;
			if ( !copySupported )
				return false;

			support.setDropAction(COPY);
			}
		return true;
		}

	@SuppressWarnings("unchecked")
	public boolean importData(TransferHandler.TransferSupport support)
		{
		if ( !canImport(support))
			return false;
		String name = "";
		Transferable t = support.getTransferable();
		try
			{
			List<File> list =
				(List<File>)t.getTransferData(DataFlavor.javaFileListFlavor);
			for ( File file : list )
				{
				fFileModule.fileSelected(file);
				break; // can only display one file at a time
				}
			}
		catch (UnsupportedFlavorException e)
			{
			log(name + ": " + e );
			return false;
			}
		catch (IOException e)
			{
			log(name + ": " + e );
			return false;
			}
		catch (Exception e)
			{
			log( e.toString());
			return false;
			}
		return true;
		}
	}
