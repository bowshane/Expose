package com.shanebow.tools.Expose.search;
/********************************************************************
* @(#)DlgSearch.java 1.00 20110104
* Copyright © 2011 by Richard T. Salamone, Jr. All rights reserved.
*
* DlgSearch: Provides user feedback during a background process.
* Implemented as a dialog box containing an eror/status log,
* progress bar, and buttons. Works in conjuction with FileWorker
* and FileLineParser to display summary results at the end of the
* run, but will also work independently of these classes.
*
* @author Rick Salamone
* @version 1.00
* 20110104 rts created based on file worker code
* 20110108 rts using HitList to display results
* 20110125 rts listener on pattern field to go on enter key
*******************************************************/
import com.shanebow.tools.filevisitor.*;
import com.shanebow.ui.SBDialog;
import com.shanebow.ui.ToggleOnTop;
import com.shanebow.ui.LAF;
import com.shanebow.util.SBLog;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.regex.Pattern;
import javax.swing.*;
import javax.swing.border.*;

public class DlgSearch
	extends JDialog
	implements FileVisitListener
	{
	private static final String CMD_START="Start";
	private static final String CMD_CANCEL="Cancel";
	private static final String CMD_CLOSE="Close";

	private final SearchCriteriaPanel fCriteria = new SearchCriteriaPanel();
	private JButton       button;
	private final JLabel  fStatus = new JLabel("Click '" + CMD_START + "' to begin search");
	private final HitList fHitList = new HitList();
	private int           fErrorCount;

	public DlgSearch(String title)
		{
		super((Frame)null, LAF.getDialogTitle(title), false);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);

		JPanel content = new JPanel(new BorderLayout());
		content.add(fCriteria,     BorderLayout.NORTH);
		content.add(fHitList,      BorderLayout.CENTER);
		content.add(controlPanel(),BorderLayout.SOUTH);
		fHitList.setBorder( new TitledBorder("Results"));
		content.setBorder(new EmptyBorder(5, 10, 2, 10));
		setContentPane(content);

		pack();
		setLocationByPlatform(true);
		try { setAlwaysOnTop(true); }
		catch (Exception e) {} // guess we can't always be on top!

		fCriteria.addActionListener(new ActionListener()
			{
			public void actionPerformed(ActionEvent e) { doStart(); }
			});
		LAF.addUISwitchListener(this);
		setVisible(true);
		}

	private JPanel controlPanel()
		{
		JPanel p = new JPanel(new FlowLayout(FlowLayout.TRAILING));

		button = new JButton(CMD_START);
		button.addActionListener( new ActionListener()
			{
			public void actionPerformed(ActionEvent e)
				{
				JButton src = (JButton)e.getSource();
				String cmd = src.getText();
				if ( cmd.equals(CMD_START))
					doStart();
				else if ( cmd.equals(CMD_CANCEL))
					setVisible(false);
				else if ( cmd.equals(CMD_CLOSE))
					setVisible(false);
				}
			});
		p.add( button );
		ToggleOnTop tot = new ToggleOnTop();
		p.add( tot );
		tot.setSelected(true);

		JPanel cp = new JPanel(new BorderLayout());
		cp.add(fStatus, BorderLayout.WEST);
		cp.add(p, BorderLayout.EAST);
		return cp;
		}

	private void doStart()
		{
		Pattern pattern = fCriteria.getFindPattern();
		if ( pattern == null )
			return;
		FileVisitCriteria criteria = fCriteria.getCriteria();
		if ( criteria == null )
			return;
		FileVisitor visitor = new GrepFileVisitor(pattern)
			{
			@Override public void beginProcessing()
				{
				button.setText(CMD_CANCEL);
				}
			@Override public void endProcessing()
				{
				FileWalker walker = getFileWalker();
				String summary =
					String.format("Searched %d files in %d directories in %d seconds",
						walker.getVisitedFileCount(),
						walker.getVisitedDirCount(),
						(int)walker.getElapsedSeconds());
				fStatus.setText(summary);
				java.awt.Toolkit.getDefaultToolkit().beep();
				fCurrentFile = null;
				promptClose();
				if ( fErrorCount > 0 )
					reportErrors();
				}
			};
		new FileWalker( visitor, criteria, this ).execute();
		}

	private void reportErrors()
		{
		try { setAlwaysOnTop(false); }
		catch (Exception e) {} // guess we can't always be on top!
		SBDialog.error("Tally", "This operation completed, but\n" + fErrorCount
				+ " exceptions were ecountered.\nSee the app log for details");
		}

	protected void promptClose()
		{
		button.setText(CMD_CLOSE);
		button.setVisible(true);
		}

	// implement FileVisitListener
	private File fCurrentFile;
	public final void visitEventOccurred( FileVisitEvent aEvent )
		{
		File file = aEvent.getFile(); // now searching this file
		if ( aEvent instanceof GrepHitEvent ) // got a hit!
			{
			if ( file != fCurrentFile ) // if this is the 1st hit in
				{                        // this file, make a blank hit
				// Hit list displays filename & icon for 'blank hit'
				fHitList.add(new GrepHitEvent(file, 0, null));
				fCurrentFile = file;
				}
			// Hit list displays line # and match for 'real hit'
			fHitList.add((GrepHitEvent)aEvent);
			}
		else setStatus( file ); // poor man's progress bar
		}

	public final void visitErrorOccurred( FileVisitError aError )
		{
		++fErrorCount;
		SBLog.write( "ERROR searching " + aError.getFile() + ": " + aError.toString());
		}

	private final void setStatus( File f )
		{
		String text = f.toString();
		int length = text.length();
		if ( length > 30 )
			text = text.substring(0,2) + "..." + text.substring(length - 24,length);
		fStatus.setText(text);
		}
	}
