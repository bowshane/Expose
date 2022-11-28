package com.shanebow.tools.Expose;
/********************************************************************
* @(#)_Expose.java 1.00 20100722
* Copyright © 2010-2015 by Richard T. Salamone, Jr. All rights reserved.
*
* _Expose: The entry point for the Expose application which provides
* various modules for inspecting and sometimes modifying the user's
* computer system. This class creates the containing frame and menus
* and specifies high level command actions.
*
* @version 1.00 07/24/10
* @author Rick Salamone
* 20100724 rts 1.00 created minimal implementation to support the file module
* 20101211 rts 1.01 added several file renderers
* 20101213 rts 1.02 added system properties command
* 20101225 rts 1.03 added fonts command
* 20101227 rts 1.04 upgraded conversions
* 20110101 rts 1.05 added favorites
* 20110102 rts 1.06 added splash screen
* 20110103 rts 1.07 improved the properties action
* 20110103 rts 1.08 added the network interfaces action
* 20110103 rts 1.09 added the disk usage action
* 20110105 rts 1.10 first iteration of search feature
* 20110106 rts 1.11 added regex tester
* 20110107 rts 1.12 added csv tally - release to JP
* 20110108 rts 1.13 major overhaul to search
* 20110109 rts 1.14 suport case ignore in file names
* 20110116 rts 1.15 added Run to tools menu & double click to exec files - buggy
* 20110117 rts 1.16 added search feature to the text file viewer
* 20110129 rts 1.17 csv editable & sortable - to JP
* 20110613 rts 1.19 added thread lister to system menu
* 20130221 rts 1.19 added ui manager to system menu
* 20150623 rts added file refresh to download action
*******************************************************/
import com.shanebow.ui.menu.*;
import com.shanebow.ui.LAF;
import com.shanebow.util.SBDate;
import com.shanebow.util.SBProperties;
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Rectangle;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.*;

public final class _Expose
	{
	private static long blowUp = 0; // SBDate.toTime("20130325  23:59");
	private static final String SPLASH_IMAGE = "resources/expose.gif";
	private FileModule fFileModule;

	_Expose(JFrame jframe)
		{
		SBProperties props = SBProperties.getInstance();
		jframe.setTitle(props.getProperty("app.name")
		        + " " + props.getProperty("app.version"));
		jframe.setBounds(props.getRectangle("usr.app.bounds", 25, 25, 820, 640));
		buildContent(jframe.getContentPane());
		jframe.setJMenuBar( buildMenus(jframe));
		jframe.setVisible(true);
		}

	protected void buildContent(Container container)
		{
		container.setLayout(new BorderLayout());
		container.add( fFileModule = new FileModule(), BorderLayout.CENTER);
		}

	private javax.swing.JMenuBar buildMenus(JFrame f)
		{
		SBMenuBar menuBar = new SBMenuBar();

		menuBar.addMenu( "File",
			new com.shanebow.tools.Expose.search.ActSearch(), null,
			new ActDownload(fFileModule),
			new ActRipSite(fFileModule), null,
			new SBViewLogAction(f), null,
			new com.shanebow.ui.SBExitAction(f)
				{
				public void doApplicationCleanup() {}
				});
		menuBar.addMenu( "Configure",
			new ActCwdRestore(), null,
			menuBar.getThemeMenu());
		menuBar.addMenu( "Tools",
			new com.shanebow.tools.Expose.csvtally.ActCsvTally(f),
			new com.shanebow.tools.conversions.ActConvert(f),
			new com.shanebow.tools.Expose.regex.ActRegex(f),
			new ActExec(f));
		menuBar.addMenu( "System",
			new ActDiskUsage(f),
			new ActNetInterfaces(f),
			new ActProperties(f),
			new com.shanebow.tools.Expose.fonts.FontAction(f),
			new com.shanebow.tools.Expose.memory.MemoryAction(f),
			new com.shanebow.tools.Expose.threads.ActThread(f),
			new com.shanebow.tools.Expose.uimanager.ActUIManager(f));
		menuBar.addMenu( "Help",
			new SBActHelp(), null,
			new SBAboutAction(f, SBProperties.getInstance()));
		return menuBar;
		}

	/**
	* @param args the command line arguments
	*/
	public static void main(String[] args)
		{
		SBProperties.load(_Expose.class ); // , "com/apo/apo.properties");
		final SplashScreen fSplashScreen = new SplashScreen(SPLASH_IMAGE);
		fSplashScreen.splash();
		LAF.initLAF(blowUp, true);
		new _Expose( new JFrame());
		java.awt.EventQueue.invokeLater( new Runnable()
			{
			public void run() { fSplashScreen.dispose(); }
			});
		}
	} //232
