package com.shanebow.tools.Expose.search;
/********************************************************************
* @(#)SearchCriteriaPanel.java 1.00 2010
* Copyright © 2011 by Richard T. Salamone, Jr. All rights reserved.
*
* SearchCriteriaPanel: Extends FileCriteriaPanel to collect a pattern
* to search for in addition to the "where to search" funcionality of
* the super class.
*
* Also restores and saves settings to SBProperties.
*
* @author Rick Salamone
* @version 1.00 20110106 rts created
* @version 1.01 20110107 rts decoupled from DlgFind
* @version 1.02 20110107 rts now compiles find expr to report syntax problems
* @version 1.03 20110107 rts supports case insensitive search
* @version 1.04 20110125 rts added addActionListener()
*******************************************************/
import com.shanebow.tools.filevisitor.FileVisitCriteria;
import com.shanebow.tools.filevisitor.FileCriteriaPanel;
import com.shanebow.ui.SBDialog;
import com.shanebow.util.SBProperties;
import java.awt.BorderLayout;
import java.awt.event.ActionListener;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JTextField;

final class SearchCriteriaPanel
	extends FileCriteriaPanel
	{
	private final JTextField fPattern = new JTextField(30);
	private final JCheckBox fIgnoreCase = new JCheckBox("Ignore Case");

	SearchCriteriaPanel()
		{
		super();
		fPattern.setToolTipText("Enter the regular expression you want to find");
		fIgnoreCase.setToolTipText("Check to ignore case while searching file content");

		JPanel findPanel = new JPanel(new BorderLayout());
		findPanel.add(fPattern,BorderLayout.CENTER);
		findPanel.add(fIgnoreCase,BorderLayout.EAST);
		addRow("Find", findPanel );
		SBProperties props = SBProperties.getInstance();
		if ( props != null )
			{
			String pattern = props.getProperty("usr.find.pattern", "" );
			fPattern.setText(pattern);
			fIgnoreCase.setSelected(props.getBoolean("usr.find.case.ignore", true));
			setStartDir(props.getProperty("usr.cwd", ""));
			setIncludeSubdirs(props.getBoolean("usr.find.file.recurse", true));
			setFilePattern(props.getProperty("usr.find.file.pattern", "*.txt"));
			setFileIgnoreCase(props.getBoolean("usr.find.file.case.ignore", true));
			}
		}

	public void addActionListener( ActionListener al )
		{
		fPattern.addActionListener(al);
		}

	Pattern getFindPattern()
		{
		String text = fPattern.getText();
		if ( text.isEmpty())
			{
			SBDialog.inputError( "Please enter a search pattern");
			return null;
			}
		try
			{
			return getIgnoreCase()? Pattern.compile(text, Pattern.CASE_INSENSITIVE)
			                      : Pattern.compile(text);
			}
		catch (PatternSyntaxException e)
			{
			SBDialog.inputError("Syntax error in find string:\n"
			                    + e.getMessage()
			                    + "\nat offset " + e.getIndex()
			                    + ": " + e.getPattern ());
			return null;
			}
		}

	boolean getIgnoreCase() { return fIgnoreCase.isSelected(); }

	@Override public FileVisitCriteria getCriteria()
		{
		FileVisitCriteria it = super.getCriteria();
		SBProperties props = SBProperties.getInstance();
		if ( it != null && props != null )
			{
			props.set("usr.find.pattern", fPattern.getText());
			props.set("usr.find.case.ignore", getIgnoreCase());
			props.set("usr.find.file.pattern", getFilePattern());
			props.set("usr.find.file.case.ignore", getFileIgnoreCase());
			props.set("usr.find.file.recurse", it.includeSubdirs());
			}
		return it;
		}
	}
