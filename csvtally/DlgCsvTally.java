package com.shanebow.tools.Expose.csvtally;
/********************************************************************
* @(#)DlgCsvTally.java 1.00 20110104
* Copyright © 2011 by Richard T. Salamone, Jr. All rights reserved.
*
* DlgCsvTally: Provides user feedback during a background process.
* Implemented as a dialog box containing an eror/status log,
* progress bar, and buttons. Works in conjuction with FileWorker
* and FileLineParser to display summary results at the end of the
* run, but will also work independently of these classes.
*
* @author Rick Salamone
* @version 1.00
* 20110104 rts created based on file worker code
*******************************************************/
import com.shanebow.tools.filerenderers.csv.StringCounter;
import com.shanebow.tools.filerenderers.csv.TallyTable;
import com.shanebow.tools.filevisitor.*;
import com.shanebow.ui.LAF;
import com.shanebow.ui.SBDialog;
import com.shanebow.ui.SBTextPanel;
import com.shanebow.ui.ToggleOnTop;
import com.shanebow.ui.themes.UISwitchListener;
import com.shanebow.util.SBLog;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.*;
import javax.swing.border.*;

public class DlgCsvTally
	extends JDialog
	implements FileVisitListener
	{
	private static final String CMD_START="Start";
	private static final String CMD_CANCEL="Cancel";
	private static final String CMD_CLOSE="Close";
	private static final String CMD_SAVE="Save";

	private final CsvTallyCriteriaPanel fCriteria = new CsvTallyCriteriaPanel();
	private final StringCounter fCounter = new StringCounter();
	private SBTextPanel   theLog = new SBTextPanel("Results", false, Color.WHITE);
	private JButton       button; // alternately, start, cancel, close
	private JButton       btnSave; // alternately, start, cancel, close
	private JLabel        fStatus = new JLabel("Click '" + CMD_START + "' to begin tally");
	private int           fErrorCount;

	public DlgCsvTally(String title )
		{
		super((Frame)null, LAF.getDialogTitle(title), false);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);

		JPanel content = new JPanel(new BorderLayout());
		content.add(fCriteria,     BorderLayout.NORTH);
		content.add(outputPanel(), BorderLayout.CENTER);
		content.add(controlPanel(),BorderLayout.SOUTH);
		content.setBorder(new EmptyBorder(5, 10, 2, 10));
		setContentPane(content);

		pack();
		setLocationByPlatform(true);
		try { setAlwaysOnTop(true); }
		catch (Exception e) {} // guess we can't always be on top!
		UISwitchListener.add(this);
		setVisible(true);
		}

	private JComponent outputPanel()
		{
		return new JScrollPane( new TallyTable(fCounter));
		}

	private JPanel controlPanel()
		{
		JPanel p = new JPanel(new FlowLayout(FlowLayout.TRAILING));

		btnSave = new JButton(CMD_SAVE);
		btnSave.addActionListener( new ActionListener()
			{
			public void actionPerformed(ActionEvent e)
				{
				fCounter.saveAs(btnSave);
				}
			});
		btnSave.setVisible(false);
		p.add( btnSave );

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
		int tallyColumn = fCriteria.getTallyColumn();
		if ( tallyColumn == -1 )
			return;
		FileVisitCriteria criteria = fCriteria.getCriteria();
		if ( criteria == null )
			return;
		FileVisitor visitor = new CsvTallyVisitor(tallyColumn, fCounter)
			{
			@Override public void beginProcessing()
				{
				button.setText(CMD_CANCEL);
				}
			@Override public void endProcessing()
				{
				FileWalker walker = getFileWalker();
				String summary =
					String.format("Processed %d files in %d directories in %d seconds",
						walker.getVisitedFileCount(),
						walker.getVisitedDirCount(),
						(int)walker.getElapsedSeconds());
				fStatus.setText(summary);
				java.awt.Toolkit.getDefaultToolkit().beep();
				if ( fErrorCount > 0 )
					reportErrors();
				fCurrentFile = null;
				btnSave.setVisible(true);
				promptClose();
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
		if ( aEvent instanceof CsvTallyEvent )
			{
			CsvTallyEvent ghe = (CsvTallyEvent)aEvent;
			int row = ghe.getRow();
			fCounter.fireRowChanged(row);
			}
		else
			setStatus( aEvent.getFile());
		}
	public final void visitErrorOccurred( FileVisitError aError )
		{
		++fErrorCount;
		SBLog.write( "Csv Tally ERROR: " + aError.toString());
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
