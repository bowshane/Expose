package com.shanebow.tools.Expose;
/********************************************************************
* @(#)ASCIIRenderer.java 1.00 07/24/10
* Copyright (c) 2010 by Richard T. Salamone, Jr. All rights reserved.
*
* ASCIIRenderer: Renders an array of Char replacing substituting a
* dot for any non-printable byteacters.
*
* @version 1.00 07/24/10
* @author Rick Salamone
* 20100724 RTS 1.00 created
*******************************************************/
import com.shanebow.util.SBLog;
import java.awt.BorderLayout;
import java.awt.LayoutManager;
import javax.swing.JPanel;
import javax.swing.border.*;

public class ExposeModule extends JPanel
	{
	static final Border LOWERED_BORDER = new CompoundBorder(
	                     new SoftBevelBorder(SoftBevelBorder.LOWERED), 
	                     new EmptyBorder(5,5,5,5));

	public ExposeModule() { this( new BorderLayout()); }
	public ExposeModule(LayoutManager layout)
		{
		super(layout);
		setBorder(LOWERED_BORDER);
		}

	final void log( String fmt, Object... args )
		{
		SBLog.write( getClass().getSimpleName(), String.format(fmt,args));
		}
	}
