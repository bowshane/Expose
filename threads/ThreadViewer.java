package com.shanebow.tools.Expose.threads;
/********************************************************************
* @(#)ThreadViewer.java 1.00 20110613
* Copyright (c) 2011 by Richard T. Salamone, Jr. All rights reserved.
*
* ThreadViewer: Component that finds and displays all thread groups
* and their threads and information about each.
*
* @version 1.00 20110613
* @author Rick Salamone
* 20110613 RTS 1.00 created
*******************************************************/
import com.shanebow.ui.SBTextPanel;

public final class ThreadViewer
	extends SBTextPanel
	{
	public ThreadViewer()
		{
		super();
		setTimeStamp(false);
		setPreferredSize(new java.awt.Dimension(400,300)); // w, h
		listAllThreads();
		}

	public void refresh()
		{
		clear();
		listAllThreads();
		}

	// Display info about a thread
	private void printThreadInfo(Thread t, String indent)
		{
		if (t == null) return;
		write(indent + "Thread: " + t.getName()
		 + "  Priority: " + t.getPriority()
		 + (t.isDaemon()?" Daemon":"")
		 + " State: " + (t.isAlive()?t.getState().toString():" Not Alive"));
		}

	// Display info about a thread group and its threads and groups
	private void list_group(ThreadGroup g, String indent)
		{
		if (g == null) return;
		int num_threads = g.activeCount();
		int num_groups = g.activeGroupCount();
		Thread[] threads = new Thread[num_threads];
		ThreadGroup[] groups = new ThreadGroup[num_groups];

		g.enumerate(threads, false);
		g.enumerate(groups, false);

		write(indent + "Thread Group: " + g.getName()
		 + "  Max Priority: " + g.getMaxPriority()
		 + (g.isDaemon()?" Daemon":""));

		for(int i = 0; i < num_threads; i++)
			printThreadInfo(threads[i], indent + "    ");
		for(int i = 0; i < num_groups; i++)
			list_group(groups[i], indent + "    ");
		}

	// Find the root thread group and list it recursively
	public void listAllThreads()
		{
		ThreadGroup current_thread_group;
		ThreadGroup root_thread_group;
		ThreadGroup parent;

		// Get the current thread group
		current_thread_group = Thread.currentThread().getThreadGroup();

		// Now go find the root thread group
		root_thread_group = current_thread_group;
		parent = root_thread_group.getParent();
		while(parent != null)
			{
			root_thread_group = parent;
			parent = parent.getParent();
			}

		write("activeCount: " + root_thread_group.activeCount());

		// And list it, recursively
		list_group(root_thread_group, "");
		}
	}
