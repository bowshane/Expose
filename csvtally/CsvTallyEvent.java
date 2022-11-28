package com.shanebow.tools.Expose.csvtally;
/********************************************************************
* @(#)CsvTallyEvent.java 1.00 20110106
* Copyright 2011 by Richard T. Salamone, Jr. All rights reserved.
*
* CsvTallyEvent: The event passed to registered FileVisitListener
* objects via the visitEventOccurred method during a Csv tally
* operation. This event indicates that a particular cell in the
* the tally results table model has changed. The handler should
* update the view of the data as appropriate (eg, fire a table
* model event).
*
* @author Rick Salamone
* @version 1.00, 20110106 rts created
*******************************************************/
import java.io.File;
import com.shanebow.tools.filevisitor.FileVisitEvent;

public final class CsvTallyEvent
	extends FileVisitEvent
	{
	final int   fRow;

	public CsvTallyEvent( File aFile, int aRow )
		{
		super(aFile);
		fRow = aRow;
		}

	public int getRow() { return fRow; }

	public String toString()
		{
		return getFile().toString() + ":" + fRow;
		}
	}
