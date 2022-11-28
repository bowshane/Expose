package com.shanebow.tools.Expose.regex;
/********************************************************************
* @(#)RegexTester.java 1.00 20110106
* Copyright 2011 by Richard T. Salamone, Jr. All rights reserved.
*
* RegexTester:
*
* @author Rick Salamone
* @version 1.00, 20110106
*******************************************************/
import com.shanebow.ui.SBDialog;
import com.shanebow.ui.layout.LabeledPairPanel;
import java.awt.BorderLayout;
import java.awt.Font;
import java.util.regex.*;
import javax.swing.*;
import javax.swing.border.*;

// RegexTester.java
class RegexTester
	extends JPanel
	{
	private final JTextField tfPattern = new JTextField();
	private final JTextField tfText = new JTextField();
	private final JTextArea taOutput = new JTextArea(10, 50);

	public RegexTester()
		{
		super( new BorderLayout());

		LabeledPairPanel input = new LabeledPairPanel();
		input.addRow( "Find (Regex)", tfPattern );
		tfPattern.setToolTipText("Enter a valid regular expression");
		input.addRow( "Text to search", tfText );
		tfText.setToolTipText("Enter some text to try to match against pattern");
		tfText.setText("The quick brown fox jumps over the lazy ox.");
		input.setBorder(new EmptyBorder(0, 0, 10, 0));

		JScrollPane scroller = new JScrollPane(taOutput);
		scroller.setBorder(new TitledBorder(" Results "));
		taOutput.setFont(new Font(Font.MONOSPACED, Font.BOLD, 15));
		add(input, BorderLayout.NORTH);
		add(scroller, BorderLayout.SOUTH);
		setBorder(new EmptyBorder(5, 10, 2, 10));
		}

	private void log ( String fmt, Object... args )
		{
		taOutput.append( String.format(fmt,args) + "\n");
		}

	public void doTest()
		{
		String pat = tfPattern.getText().trim();
		String txt = tfText.getText().trim();
		if (pat.isEmpty())
			{
			SBDialog.inputError ("The input fields cannot be blank");
			return;
			}
		Pattern p;
		try
			{
			p = Pattern.compile (pat);
			}
		catch (PatternSyntaxException e)
			{
			log("-----");
			log ("Regex syntax error: " + e.getMessage ());
			log ("  Error description: " + e.getDescription ());
			log ("  Error index: " + e.getIndex ());
			log ("  Erroneous pattern: " + e.getPattern ());
			return;
			}
		String s = cvtLineTerminators (txt);
		Matcher m = p.matcher (s);

		log("-----");
		log("Pattern compile success");
		log ("  Attempt to find '%s' in\n%s", pat, txt );
		while (m.find())
			{
			log( matchShow(m.start(),m.group()));
//			log ("** Found " + m.group());
//			log ("   starting at index " + m.start()
//			   + " and ending at index " + m.end());
			}
		}

	private String matchShow(int start, String match)
		{
		String it = "";
		int i = 0;
		while ( i < start )
			{
			it += " ";
			++i;
			}
		return it + match;
		}

	// Convert \n and \r character sequences
	// to their single character equivalents
	static String cvtLineTerminators (String s)
		{
		StringBuffer sb = new StringBuffer (80);
		int oldindex = 0, newindex;
		while ((newindex = s.indexOf ("\\n", oldindex)) != -1)
			{
			sb.append (s.substring (oldindex, newindex));
			oldindex = newindex + 2;
			sb.append ('\n');
			}
		sb.append (s.substring (oldindex));
		s = sb.toString ();
		sb = new StringBuffer (80);
		oldindex = 0;
		while ((newindex = s.indexOf ("\\r", oldindex)) != -1)
			{
			sb.append (s.substring (oldindex, newindex));
			oldindex = newindex + 2;
			sb.append ('\r');
			}
		sb.append (s.substring (oldindex));
		return sb.toString ();
		}
	}
