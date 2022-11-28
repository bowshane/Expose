package com.shanebow.tools.Expose.csvtally;
/********************************************************************
* @(#)CsvTallyAction.java 1.00 20110106
* Copyright 2011 by Richard T. Salamone, Jr. All rights reserved.
*
* CsvTallyAction: Determines the unique values for a specified
* csv file, as well as the frequency of each value.
*
* @author Rick Salamone
* @version 1.00, 20110106 rts created
*******************************************************/
import com.shanebow.tools.filevisitor.*;
import com.shanebow.tools.filerenderers.csv.StringCounter;
import com.shanebow.util.CSV;
import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.regex.*;

public class CsvTallyVisitor
	extends FileVisitor
	{
	private final int fTallyColumn;
	private final StringCounter fCounter;

	CsvTallyVisitor(int aTallyColumn, StringCounter aCounter)
		{
		fTallyColumn = aTallyColumn;
		fCounter = aCounter;
		}

	public void visit(File f)
		{
		BufferedReader stream = null;
		try
			{
			fireVisitEvent( new FileVisitEvent(f));
			stream = new BufferedReader(new FileReader(f));
			String text = stream.readLine();
			if ( text == null )
				return;
			int nCols = CSV.columnCount( text );
			if ( nCols < fTallyColumn )
				{
				fireVisitEvent( new FileVisitError( f, "Only has " + nCols + " columns"));
				return;
				}
			while ((text = stream.readLine()) != null )
				{
				String[] pieces = CSV.split(text, fTallyColumn + 1 );
				String value = pieces[fTallyColumn];
				if ( value == null ) value = "";
				int row = fCounter.increment(value);
				fireVisitEvent( new CsvTallyEvent( f, row ));
				}
			}
		catch (IOException e)
			{
			fireVisitEvent( new FileVisitError( f, e.getMessage()));
			}
		finally // Close the channel and the stream
			{
			try { stream.close(); } catch (Exception e) {}
			}
		}
	}
