package com.shanebow.tools.Expose.search;
/********************************************************************
* @(#)GrepHitEvent.java 1.00 20110105
* Copyright 2011 by Richard T. Salamone, Jr. All rights reserved.
*
* GrepHitEvent: A semantic event which indicates that a match was found
* in a particular file during the grep operation.
*
* @author Rick Salamone
* @version 1.00, 20110105 rts created
*******************************************************/
import java.io.File;
import com.shanebow.tools.filevisitor.FileVisitEvent;

public final class GrepHitEvent
	extends FileVisitEvent
	{
	final int   fLine;
	final String fMatch;

	public GrepHitEvent( File aFile, int aLine, CharSequence aMatch )
		{
		super(aFile);
		fLine = aLine;
		fMatch = (aMatch == null)? null : aMatch.toString().trim();
		}

	public int getLine() { return fLine; }
	public String getMatch() { return fMatch; }
	public String hitString()
		{
		return (fMatch == null)? null : ("" + fLine + ":" + fMatch);
		}

	public String toString()
		{
		return getFile().toString() + ":" + fLine + ":" + fMatch;
		}
	}
