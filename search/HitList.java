package com.shanebow.tools.Expose.search;
/********************************************************************
* @(#)HitList.java 1.00 20101226
* Copyright © 2010-2012 by Richard T. Salamone, Jr. All rights reserved.
*
* HitList: Lists the file in the specified directory.
*
* @author Rick Salamone
* @version 1.03
* 20101226 rts created
* 20101230 rts sorts files by extention and name
* 20101231 rts added drag source functionality
* 20120702 rts added tool tip to results to show full path to file
*******************************************************/
import com.arashpayan.filetree.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.Vector;
import javax.swing.*;
import javax.swing.event.*;

// For DND support
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.dnd.DragSourceEvent;
import java.awt.dnd.DragSourceListener;
import java.util.ArrayList;
import java.util.Arrays;

public class HitList
	extends JPanel
	implements DragGestureListener
	{
	public final static String SELECTED_FILE = Constants.SELECTED_FILE;

	private final JList fList;
	private File fSelectedFile;
	private final Vector<GrepHitEvent> fContent = new Vector<GrepHitEvent>();

	public HitList()
		{
		super(new BorderLayout());
		fList = new JList( new FileListModel())
			{
			@Override public String getToolTipText(MouseEvent e)
				{
				int index = locationToIndex(e.getPoint());
				return (index >= 0)? fContent.get(index).getFile().toString() : null;
				}
			};
		fList.setCellRenderer( new HitListCellRenderer());
		fList.addListSelectionListener( new ListSelectionListener()
			{
			public void valueChanged(ListSelectionEvent e)
				{
				if (e.getValueIsAdjusting() == true) return;
				int index = fList.getSelectedIndex();
				if ( index < 0 ) return;
				if ( fContent.get(index).getFile().equals(fSelectedFile)) return;
				firePropertyChange(SELECTED_FILE, fSelectedFile, fContent.get(index));
				fSelectedFile = fContent.get(index).getFile();
				}
			});
		fList.addMouseListener( new MouseAdapter()
			{
			public void mouseClicked(MouseEvent e)
				{
				if (e.getClickCount() == 2)
					{
					int index = fList.locationToIndex(e.getPoint());
					System.out.format("Double clicked on Item %d: %s\n",
								index, fContent.get(index));
					}
				}
			});

		DragSource dragSource = DragSource.getDefaultDragSource();
		dragSource.createDefaultDragGestureRecognizer(fList,
        DnDConstants.ACTION_COPY_OR_MOVE, this);
		add( new JScrollPane(fList), BorderLayout.CENTER );
		}

	@Override public Dimension getPreferredSize()
		{
		return new Dimension(475,220);
		}

	private class FileListModel extends AbstractListModel
		implements DragSourceListener
		{
		public Object getElementAt(int index) { return fContent.get(index); }
		public int getSize() { return fContent.size(); }
		private void removeAll()
			{
			int size = fContent.size();
			if ( size > 0 )
				{
				fContent.clear();
				fSelectedFile = null;
				fireIntervalRemoved(this, 0, size - 1);
				}
			}
		void add( GrepHitEvent file )
			{
			int size = fContent.size();
			fContent.add(file);
			fireIntervalAdded(this, size, size);
			}

		// DragSourceListener methods
		public void dragEnter(DragSourceDragEvent event) {}
		public void dragOver(DragSourceDragEvent event) {}
		public void dragExit(DragSourceEvent event) {}
		public void dropActionChanged(DragSourceDragEvent event) {}
		public void dragDropEnd(DragSourceDropEvent event)
			{
			// TODO: decide what to do if move action here
			// better yet disallow move
			}
		}

	public void add(GrepHitEvent file )
		{
		((FileListModel)(fList.getModel())).add(file);
		}

	// DragGestureListener method
	public void dragGestureRecognized(DragGestureEvent event)
		{
		Transferable transferable = new FileListTransferable(fList.getSelectedValues());
		event.startDrag(null, transferable, (FileListModel)(fList.getModel()));
		}
	}

// For DND support
class FileListTransferable implements Transferable
	{
	private java.util.List<Object> fileList;
	private static DataFlavor[] flavors =
		{
		DataFlavor.javaFileListFlavor,
		DataFlavor.stringFlavor
		};

	public FileListTransferable(Object[] events)
		{
		fileList = new Vector<Object>(events.length);
		for ( Object event : events )
			fileList.add(((GrepHitEvent)event).getFile());
//		fileList = new ArrayList<Object>(Arrays.asList(files));
		}

	public DataFlavor[] getTransferDataFlavors() { return flavors; }

	public boolean isDataFlavorSupported(DataFlavor flavor)
		{
		return Arrays.asList(flavors).contains(flavor);
		}

	public synchronized Object getTransferData(DataFlavor flavor)
		throws UnsupportedFlavorException
		{
		if (flavor.equals(DataFlavor.javaFileListFlavor))
			return fileList;
		else if (flavor.equals(DataFlavor.stringFlavor))
			return fileList.toString();
		else throw new UnsupportedFlavorException(flavor);
		}
	}
