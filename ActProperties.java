package com.shanebow.tools.Expose;
/********************************************************************
* @(#)ActProperties.java 1.00 20101229
* Copyright 2010-11 by Richard T. Salamone, Jr. All rights reserved.
*
* ActProperties: Action class to display the system properties
* in a dialog.
*
* @author Rick Salamone
* @version 1.00, 20101229 rts created to log sys props to log
* @version 1.01, 20110103 rts added app props and displays to a tabbed dialog
* @version 1.02, 20110630 rts added a tab for environment variables
*******************************************************/
import com.shanebow.ui.LAF;
import com.shanebow.ui.SBAction;
import com.shanebow.util.SBProperties;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.*;
import javax.swing.*;

public final class ActProperties
	extends SBAction
	{
	public static final String CMD_NAME="Properties";
	private final JFrame fFrame;

	public ActProperties(JFrame aFrame)
		{
		super( CMD_NAME, 'P', "Displays system and application properties", null );
		fFrame = aFrame;
		}

	@Override public void actionPerformed(ActionEvent e)
		{
		showProperties();
		}

  private void showProperties()
		{
		JTabbedPane tabsPane = new JTabbedPane();
		tabsPane.addTab( "Sys", getTable(toSortedArray(System.getProperties())) );
		tabsPane.setMnemonicAt(0, KeyEvent.VK_S);
		tabsPane.addTab( "App", getTable(	toSortedArray(SBProperties.getInstance())) );
		tabsPane.setMnemonicAt(1, KeyEvent.VK_A);
		tabsPane.addTab( "Env", getTable(getSortedEnv()) );
		tabsPane.setMnemonicAt(0, KeyEvent.VK_E);

		JLabel[] options = { new JLabel( "<HTML><BODY><I>Select tab to"
			+ " view system or application properties or environment variables</I>") };
		JOptionPane.showOptionDialog(fFrame, tabsPane,
			LAF.getDialogTitle(toString()), JOptionPane.DEFAULT_OPTION,
			JOptionPane.INFORMATION_MESSAGE, null, options, null);
		}

	private JComponent getTable(final String[][] props)
		{
		JTable propTable = new JTable(new javax.swing.table.AbstractTableModel()
			{
			@Override public Class  getColumnClass(int c) { return String.class; }
			@Override public int    getColumnCount() { return 2; };
			@Override public String getColumnName(int c) { return (c==0)? "Key":"Value"; }
			@Override public int    getRowCount() { return props.length; }
			@Override public Object getValueAt(int r, int c) { return props[r][c]; }
			});
		return new JScrollPane(propTable);
		}

	private String[][] toSortedArray(Properties props)
		{
		Set<String> keys = props.stringPropertyNames();
		String[][] it = new String[keys.size()][2];
		int r = 0;
		for ( String key : keys )
			{
			it[r][0] = key;
			it[r++][1] = props.getProperty(key);
			}
		return sort(it);
		}

	private String[][] getSortedEnv()
		{
		Map<String,String> map = System.getenv();
		Set<String> keys = map.keySet();
		String[][] it = new String[keys.size()][2];
		int r = 0;
		for ( String key : keys )
			{
			it[r][0] = (String)key;
			it[r++][1] = (String) map.get(key);
			}
		return sort(it);
		}

	private String[][] sort(String[][] aNameValues )
		{
		Arrays.sort(aNameValues, new Comparator<String[]>()
			{
			public int compare(String[] r1, String[] r2)
				{
				return r1[0].compareTo(r2[0]);
				}
			});
		return aNameValues;
		}
	}

/****************************
import java.awt.Color; 
import java.awt.Component; 
import java.util.Map; 
 
import javax.swing.JFrame; 
import javax.swing.JLabel; 
import javax.swing.JScrollPane; 
import javax.swing.JTable; 
import javax.swing.UIManager; 
import javax.swing.table.DefaultTableCellRenderer; 
 
import ca.odell.glazedlists.BasicEventList; 
import ca.odell.glazedlists.EventList; 
import ca.odell.glazedlists.GlazedLists; 
import ca.odell.glazedlists.SortedList; 
import ca.odell.glazedlists.gui.AbstractTableComparatorChooser; 
import ca.odell.glazedlists.gui.TableFormat; 
import ca.odell.glazedlists.swing.EventTableModel; 
import ca.odell.glazedlists.swing.TableComparatorChooser; 
 
public class UIManagerDefaultsViewer
	{
	public static class UIEntry 
		{
		final private String key; 
		final private Object value; 

		UIEntry(Map.Entry<Object,Object> e)
			{
			this.key = e.getKey().toString();
			this.value = e.getValue();
			}

	public String getKey() { return key; }
	public Object getValue() { return value; }
	public Class getValueClass() { return (value == null)? null : value.getClass(); }
	public String getClassName() { return (value == null)? "" : value.getClass().getName(); }

	public static class UIEntryRenderer
		extends DefaultTableCellRenderer
		{
		Color[] defaults = new Color[4];
		public UIEntryRenderer()
			{ 
			super(); 
			defaults[0] = UIManager.getColor("Table.background");
			defaults[1] = UIManager.getColor("Table.selectionBackground");
			defaults[2] = UIManager.getColor("Table.foreground");
			defaults[3] = UIManager.getColor("Table.selectionForeground");
			}

		public void setDefaultColors(Component cell, boolean isSelected)
			{
			cell.setBackground(defaults[isSelected ? 1 : 0]);
			cell.setForeground(defaults[isSelected ? 3 : 2]); 
			} 
 
		@Override public Component getTableCellRendererComponent(
			JTable table, Object value, boolean isSelected, boolean hasFocus,
			int row, int column)
			{
			Component cell = super.getTableCellRendererComponent(table, value,
			                      isSelected, hasFocus, row, column);
			if (table.convertColumnIndexToModel(column) == 1) // the value column
				{
				final EventTableModel<UIEntry> tableModel =
				                   (EventTableModel<UIEntry>) table.getModel(); 
				UIEntry e = tableModel.getElementAt(row); 
				JLabel l = (JLabel)cell;

				if (value instanceof Color)
					{
					Color c = (Color)value;
					cell.setBackground(c);
					cell.setForeground( c.getRed()+c.getGreen()+c.getBlue() >= 128*3
								? Color.black : Color.white);
					// choose either black or white depending on brightness
					l.setText(String.format("Color 0x%08x (%d,%d,%d alpha=%d)",
					   c.getRGB(), c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()));
					return cell;
					}
				else if (e.getKey().endsWith("ont"))
                                // possible font, not always ".font" 
					{
					// fonts are weird, for some reason the value returned 
					// in the entry set of UIManager.getDefaults()
					// is not the same type as the value "v" below
					Object v = UIManager.get(e.getKey());
					if (v instanceof javax.swing.plaf.FontUIResource)
						{
						javax.swing.plaf.FontUIResource font = (javax.swing.plaf.FontUIResource)v;
						l.setText("Font "+font.getFontName()+" "+font.getSize());
						}
					}
				}
			setDefaultColors(cell, isSelected);
			return cell;
			}
		}

	private String[][] getSortedEnv()
		{
		Map<String,String> map = System.getenv();
		Set<String> keys = map.keySet();
		String[][] it = new String[keys.size()][2];
		int r = 0;
		for ( String key : keys )
			{
			it[r][0] = (String)key;
			it[r++][1] = (String) map.get(key);
			}
		return sort(it);
		}

    public static void main(String[] args) { 
        final EventList<UIEntry> uiEntryList =  
                GlazedLists.threadSafeList(new BasicEventList<UIEntry>()); 
 
        for (Map.Entry<Object,Object> key : UIManager.getDefaults().entrySet()) 
        { 
            uiEntryList.add(new UIEntry(key)); 
        } 
 
        final SortedList<UIEntry> sortedUIEntryList = new SortedList<UIEntry>(uiEntryList, null); 
 
        // build a JTable 
        String[] propertyNames = new String[] {"key","value","className"}; 
        String[] columnLabels = new String[] {"Key", "Value", "Class"}; 
        TableFormat<UIEntry> tf = GlazedLists.tableFormat(UIEntry.class, propertyNames, columnLabels); 
        EventTableModel<UIEntry> etm = new EventTableModel<UIEntry>(sortedUIEntryList, tf); 
 
        JTable t = new JTable(etm); 
        TableComparatorChooser<UIEntry> tcc = TableComparatorChooser.install(t,  
                sortedUIEntryList, AbstractTableComparatorChooser.SINGLE_COLUMN, 
                tf); 
        sortedUIEntryList.setComparator(tcc.getComparatorsForColumn(0).get(0)); 
        // default to sort by the key 
 
        t.setDefaultRenderer(Object.class, new UIEntryRenderer());         
		}
	}
****************************/
