package com.shanebow.tools.Expose.csvtally;
/********************************************************************
* @(#)CsvTallyCriteriaPanel.java 1.00 20110107
* Copyright 2011 by Richard T. Salamone, Jr. All rights reserved.
*
* CsvTallyCriteriaPanel: Extends FileCriteriaPanel to collect
* the column number to tally in addition to the "where to search"
* funcionality of the super class. Note that we convert from zero
* based 'programmer world' to one based 'real world';
*
* Also restores and saves settings to SBProperties.
*
* @author Rick Salamone
* @version 1.00 20110107 rts created
*******************************************************/
import com.shanebow.tools.filevisitor.FileVisitCriteria;
import com.shanebow.tools.filevisitor.FileCriteriaPanel;
import com.shanebow.ui.SBDialog;
import com.shanebow.util.SBProperties;
import javax.swing.JTextField;

final class CsvTallyCriteriaPanel
	extends FileCriteriaPanel
	{
	private final JTextField fTallyColumn = new JTextField(30);
	private static final String TOOLTIP="Enter a column number to tally: 1 or higher";

	CsvTallyCriteriaPanel()
		{
		super();
		addRow("Tally Column #", fTallyColumn);
		fTallyColumn.setToolTipText(TOOLTIP);
		SBProperties props = SBProperties.getInstance();
		if ( props != null )
			{
			int tallyColumn = props.getInt("usr.csvview.tally.col", 0 );
			++tallyColumn; // show user 1-based value
			fTallyColumn.setText("" + tallyColumn);
			setStartDir(props.getProperty("usr.cwd", ""));
			setIncludeSubdirs(props.getBoolean("usr.tally.file.recurse", true));
			setFilePattern(props.getProperty("usr.tally.file.pattern", "*.csv"));
			setFileIgnoreCase(props.getBoolean("usr.tally.file.case.ignore", true));
			}
		}

	int getTallyColumn()
		{
		String txt = fTallyColumn.getText();
		try
			{
			int it = Integer.parseInt(txt);
			if ( --it < 0 ) // convert to zero-based and validate
				return tallyColumnError();
			return it;
			}
		catch (Exception e)
			{
			return tallyColumnError();
			}
		}

	private int tallyColumnError()
		{
		SBDialog.inputError( TOOLTIP );
		return -1;
		}

	@Override public FileVisitCriteria getCriteria()
		{
		FileVisitCriteria it = super.getCriteria();
		SBProperties props = SBProperties.getInstance();
		if ( it != null && props != null )
			{
			props.set("usr.csvview.tally.col", "" + getTallyColumn());
			props.set("usr.tally.file.pattern", getFilePattern());
			props.set("usr.tally.file.case.ignore", getFileIgnoreCase());
			props.set("usr.tally.file.recurse", it.includeSubdirs());
			}
		return it;
		}
	}
