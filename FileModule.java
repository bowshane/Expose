package com.shanebow.tools.Expose;
/********************************************************************
* @(#)FileModule.java 1.00 07/24/10
* Copyright © 2010-2015 by Richard T. Salamone, Jr. All rights reserved.
*
* FileModule: Shows the file/directory tree on the left and a binary
* representation of the file on the right.
*
* @version 1.00 07/24/10
* @author Rick Salamone
* 20100724 rts 1.00 created
* 20101226 rts 1.02 added file list under dir tree & moved it to filetree.jar
* 20101230 rts 1.03 saves cwd to SBProperties & restores at startup
* 20110616 rts 1.03 saves split locations to SBProperties & restores at startup
* 20150623 rts added refresh method
*******************************************************/
import com.arashpayan.filetree.*;
import com.shanebow.tools.filerenderers.FileRendererPanel;
import com.shanebow.tools.filerenderers.binary.BinaryFileRenderer;
import com.shanebow.util.SBProperties;
import com.shanebow.ui.SplitPane;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;
import java.io.File;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JLabel;

public class FileModule
	extends ExposeModule
	implements PropertyChangeListener
	{
	private final JLabel m_status = new JLabel("No selection");
	private final BinaryFileRenderer m_byteView = new BinaryFileRenderer();
	private final FileRendererPanel  m_friendlyView = new FileRendererPanel();
	FileNavigator fFileNavigator;

	public FileModule()
		{
		super();
		SplitPane splitPane = new SplitPane(SplitPane.HSPLIT, leftPanel(), rightPanel());
		splitPane.setDividerLocation("usr.nav.split", 250 );

		Dimension minimumSize = new Dimension(100, 150);
		m_byteView.setMinimumSize(minimumSize);
		add(splitPane, BorderLayout.CENTER);
		add( m_status, BorderLayout.PAGE_END);
		}

	public void refresh() { fFileNavigator.refresh(); }
	public void select(File aFile) { fFileNavigator.select(aFile); }

	public JComponent leftPanel()
		{
		fFileNavigator = new FileNavigator(SBProperties.getInstance());
		fFileNavigator.addPropertyChangeListener(FileNavigator.SELECTED_FILE, this );
		SBProperties props = SBProperties.getInstance();
		if ( props != null
		&&   props.getBoolean("usr.cwd.restore", true))
			{
			String cwd = props.get("usr.cwd");
			if ( cwd != null )
				fFileNavigator.select(new File(cwd));
			}
		return fFileNavigator;
		}

	public JComponent rightPanel()
		{
		SplitPane splitPane = new SplitPane(SplitPane.VSPLIT, m_byteView, m_friendlyView);
		splitPane.setDividerLocation("usr.byteview.split", 1);
		fileSelected( null );
		FileModuleDND dndHandler = new FileModuleDND(this);
		splitPane.setTransferHandler(dndHandler);
		m_byteView.setTransferHandler(dndHandler);
		m_friendlyView.setTransferHandler(dndHandler);
		return splitPane;
		}

	public void propertyChange( PropertyChangeEvent e )
		{
		if ( !e.getPropertyName().equals(FileNavigator.SELECTED_FILE))
			return;
		fileSelected((File)(e.getNewValue()));
		}

	void fileSelected(File file)
		{
		if ( file != null && file.isDirectory())
			{
			SBProperties.set("usr.cwd", file );
			return;
			}

		m_friendlyView.render(file);
		m_status.setText( "File: " + file );
		try { m_byteView.render( file ); }
		catch ( Exception e ) { m_status.setText( "ERROR: " + e.getMessage()); }
		}
	}
