package com.shanebow.tools.Expose.search;
/********************************************************************
* Sun Microsystems Example Code @(#)Grep.java 1.1 01/05/10
* Copyright 2010 by Richard T. Salamone, Jr. All rights reserved.
*
* Copyright (c) Ian F. Darwin, http://www.darwinsys.com/, 1996-2002.
* All rights reserved. Software written by Ian F. Darwin and others.
* $Id: LICENSE,v 1.8 2004/02/09 03:33:38 ian Exp $
*
* Redistribution and use in source and binary forms, with or without
* modification, are permitted provided that the following conditions
* are met:
* 1. Redistributions of source code must retain the above copyright
* notice, this list of conditions and the following disclaimer.
* 2. Redistributions in binary form must reproduce the above copyright
* notice, this list of conditions and the following disclaimer in the
* documentation and/or other materials provided with the distribution.
*
* THIS SOFTWARE IS PROVIDED BY THE AUTHOR AND CONTRIBUTORS ``AS IS''
* AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
* TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
* PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE AUTHOR OR CONTRIBUTORS
* BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
* CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
* SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
* INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
* CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
* ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
* POSSIBILITY OF SUCH DAMAGE.
* 
* Java, the Duke mascot, and all variants of Sun's Java "steaming coffee
* cup" logo are trademarks of Sun Microsystems. Sun's, and James Gosling's,
* pioneering role in inventing and promulgating (and standardizing) the Java 
* language and environment is gratefully acknowledged.
* 
* The pioneering role of Dennis Ritchie and Bjarne Stroustrup, of AT&T, for
* inventing predecessor languages C and C++ is also gratefully acknowledged.
*******************************************************/

//Search a list of files for lines that match a given regular-expression
//pattern. Demonstrates NIO mapped byte buffers, charsets, and regular
//expressions.
import com.shanebow.tools.filevisitor.*;
import java.io.*;
import java.nio.*;
import java.nio.channels.*;
import java.nio.charset.*;
import java.util.regex.*;

public class GrepFileVisitor
	extends FileVisitor
	{
	// Charset and decoder for ISO-8859-15
	private static final Charset charset = Charset.forName("ISO-8859-15");
	private static final CharsetDecoder decoder = charset.newDecoder();

	// Pattern used to parse lines
	private static final Pattern linePattern = Pattern.compile(".*\r?\n");

	// The input pattern that we're looking for
	private final Pattern pattern;

	GrepFileVisitor(Pattern aPattern)
		{
		pattern = aPattern;
		}

	// Use the linePattern to break the given CharBuffer into lines, applying
	// the input pattern to each line to see if we have a match
	//
	private void grep(File f, CharBuffer cb)
		{
		Matcher lm = linePattern.matcher(cb);    // Line matcher
		Matcher pm = null;            // Pattern matcher
		int lines = 0;
		while (lm.find())
			{
			lines++;
			CharSequence cs = lm.group();     // The current line
			if (pm == null)
				pm = pattern.matcher(cs);
			else
				pm.reset(cs);
			if (pm.find())
				fireVisitEvent( new GrepHitEvent( f, lines, cs ));
			if (lm.end() == cb.limit())
				break;
			}
		}

	// Search for occurrences of the input pattern in the given file
	//
	public void visit(File f)
		{
		FileChannel fc = null;
		try
			{
			fireVisitEvent( new FileVisitEvent(f));
			// Open the file and then get a channel from the stream
			FileInputStream fis = new FileInputStream(f);
			fc = fis.getChannel();

			// Get the file's size and then map it into memory
			int sz = (int)fc.size();
			MappedByteBuffer bb = fc.map(FileChannel.MapMode.READ_ONLY, 0, sz);

			// Decode the file into a char buffer
			CharBuffer cb = decoder.decode(bb);

			// Perform the search
			grep(f, cb);
			}
		catch (IOException e)
			{
			fireVisitEvent( new FileVisitError( f, e.getMessage()));
			}
		finally // Close the channel and the stream
			{
			try { fc.close(); } catch (Exception x) {}
			}
		}
	}
