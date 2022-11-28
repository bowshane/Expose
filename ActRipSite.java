package com.shanebow.tools.Expose;
/********************************************************************
* @(#)ActRipSite.java 1.00 20150929
* Copyright © 2011-2015 by Richard T. Salamone, Jr. All rights reserved.
*
* ActRipSite: Action to initiate a download of an arbitrary URL to
* the local file system.
*
* @version 1.00 20110630
* @author Rick Salamone
* 20150929 rts created
*******************************************************/
import com.shanebow.ui.LAF;
import com.shanebow.ui.SBAction;
import com.shanebow.ui.SBDialog;
import com.shanebow.util.SBDate;
import com.shanebow.util.SBLog;
import com.shanebow.util.SBProperties;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.io.*;
import java.net.*;
import javax.swing.*;

public class ActRipSite
	extends SBAction
	{
	private final FileModule fFileModule;

	public ActRipSite(FileModule aFileModule)
		{
		super( "Rip Site", 'R', "Rip a page", null );
		fFileModule = aFileModule;
		}

	JCheckBox cbOverwrite;
	@Override public void action()
		{
 		String[] options = { "OK", "Cancel" };
		JTextField tfFrom = new JTextField(40);
		JTextField tfTo = new JTextField(40);
		JTextField tfFile = new JTextField(40);
		JPanel panel = new JPanel(new GridLayout(0,1));
		panel.add(LAF.titled(tfFrom, "Source Doamin"));
		panel.add(LAF.titled(tfTo, "Destination Path" ));
		panel.add(LAF.titled(tfFile, "Filespec" ));
		panel.add(cbOverwrite = new JCheckBox("Overwrite existing files"));
		SBProperties props = SBProperties.getInstance();

		if ( props != null )
			{
			tfFrom.setText(props.getProperty("usr.download.from", "http://"));
			tfTo.setText(props.getProperty("usr.cwd", ""));
			}
//		while ( true )
			{
			if ( 0 != JOptionPane.showOptionDialog(null, panel,
				LAF.getDialogTitle(toString()), JOptionPane.DEFAULT_OPTION,
					JOptionPane.PLAIN_MESSAGE, null, options, options[0] ))
				return;
			String from = tfFrom.getText().trim();
			if (!from.endsWith("/")) from += "/";
			try {
String fname = tfFile.getText();
if (fname.endsWith(".css")) {
	File dest = new File(tfTo.getText(), fname).getCanonicalFile();
	processCSS(from, tfTo.getText(), dest);
	} else
				processFile(from, tfTo.getText(), fname);
				tfFile.setText("");
				if ( props != null )
					props.set("usr.download.from", from);
			SBDialog.inform(LAF.getDialogTitle(toString()),
				"<html><b><br>successful downloaded!");
				}
			catch (Exception e) { SBDialog.error("Rip Error", e.toString()); }
			}
		}

	private String inQuotes(String line, int startAt)
		throws Exception
		{
		String it = "";
		int len = line.length();
		boolean inStr = false;
		for (int i = startAt; i < len; i++) {
			char c = line.charAt(i);
			if (c=='"' || c=='\'') {
				if (it.isEmpty()) inStr = true;
				else return it;
				}
			else if (inStr) it += c;
			}
		throw new Exception("No quotes found at: " + line.substring(startAt));
		}

	private boolean processFile(String remoteRoot, String localRoot, String file) throws Exception {
		if (!remoteRoot.endsWith("/")) remoteRoot += "/";
		if (!localRoot.endsWith("\\")) localRoot += "\\";

		File in = new File(localRoot, file);
		log("Ripping site from: " + remoteRoot + "\n\t to local dir: " + localRoot + "\n\t using file: " + file);
		BufferedReader stream = new BufferedReader(new FileReader(in));
		if (stream == null )
			{
			System.err.println ( "File open error: " + in );
			return false;
			}
		String line;
		int errors = 0;
		for (int lineNo = 1; (line = stream.readLine()) != null; ++lineNo )
			{
			int extAt;
			line = line.trim();
			if ( line.isEmpty())
				continue;
			if (line.startsWith("<link")) {
				int hrefAt = line.indexOf("href");
				if (hrefAt > 0) {
					try {
						String href = inQuotes(line, hrefAt+5);
						File downloaded = download(remoteRoot, localRoot, href);
						if (downloaded != null)
							processCSS(remoteRoot, localRoot, downloaded);
						}
					catch (Exception e) {
						++errors;
						log("**** download exception line: " + lineNo + "\n   " + line + "\n   " +  e);
e.printStackTrace();	//					return false;
						}
					}
				}
			if (line.startsWith("<script")) {
				int srcAt = line.indexOf("src");
				if (srcAt > 0) {
					try {
						String src = inQuotes(line, srcAt+4);
						download(remoteRoot, localRoot, src);
						}
					catch (Exception e) { ++errors; log("**** download exception line: " + lineNo + "\n   " + line + "\n   " +  e); }
					}
				}
			else if ((extAt = line.indexOf(".jpg")) > 0
			     ||  (extAt = line.indexOf(".png")) > 0
			     ||  (extAt = line.indexOf(".gif")) > 0) {
				for (int i = extAt; i > 0; --i)
					if (line.charAt(i) == '"') {
						try {
							String src = line.substring(i+1,extAt+4);
							download(remoteRoot, localRoot, src);
							}
						catch (Exception e) { ++errors; log("**** download exception line: " + lineNo + "\n   " + line + "\n   " +  e); }
						break;
						}
				}
			}
		stream.close();
		return true;
		}

	private void log(String msg) {
		System.out.println(msg);
		SBLog.write("Ripper>> " + msg);
		}

	private File download(String remoteRoot, String localRoot, String href)
		throws Exception
		{
		if (href.startsWith("http://") && !href.startsWith(remoteRoot)) {
			log(" skipped: " + href + " - other host");
			}
		int startDirLen = localRoot.length();
		File dest = new File(localRoot, href).getCanonicalFile();
		String src = remoteRoot + dest.toString().substring(startDirLen).replace('\\', '/');;
		_download(src, dest);
		return dest;
		}

	private void _download(String from, File to)
		throws Exception
		{
		log("Downloading " + from + "\n to " + to);	
		InputStream is = null;
		try
			{
			URL url = new URL(from);
			is = url.openStream(); // throws an IOException
			}
		catch (FileNotFoundException fnfe) {
			log(" remote file not found");
			return;
			}

		if (to.toString().contains(".."))
			log(" = " + to.getCanonicalPath());

		if (to.exists())
			{
	 		if (!cbOverwrite.isSelected()) {
				log(" skipped: " + to.toString() + " - already exists");
				return;
				}
			else log("Overwriting existing" + to);
			}
		else
			{
			File parent = new File(to.getParentFile(), "/");
		 	if (!parent.exists()) {
				log(" Creating dir(s): " + parent);
				if (!parent.mkdirs())
					log (" UNABLE TO CREATE DIRS");
				}
			}
		FileOutputStream os = null;
		try
			{
			BufferedInputStream bis = new BufferedInputStream(is);
			os = new FileOutputStream(to);
			if (os == null) {
				log(" open file failed: " + to);
				throw new IOException("Open failed: " + to);
				}
			BufferedOutputStream bos = new BufferedOutputStream(os);
			int b;
			while ((b = bis.read()) != -1)
				bos.write(b);
			bos.flush();
			fFileModule.refresh();
			fFileModule.select(to);
			}
		catch (Exception e) { log("Exception HERE: " + e); throw e; }
		finally
			{
			try { is.close(); os.close(); }
			catch (Exception ioe) {} // just going to ignore this one
			}
		}

	private String inParens(String line, int startAt) {
		String it = "";
		int len = line.length();
		boolean inStr = false;
		int count = 0;
		for (int i = startAt; i < len; i++) {
			char c = line.charAt(i);
			if (c=='(') inStr = true;
			else if (c == ')') return it;
			else if (c=='"' || c=='\'') {
				if (!it.isEmpty()) return it;
				}
			else if (inStr) it += c;
			}
		return null;
		}

boolean STRIKINGLY = true;
	private void processCSS(String remoteRoot, String localRoot, File file)
		throws Exception
		{
		if (!file.exists()) return;

		if (!remoteRoot.endsWith("/")) remoteRoot += "/";
		if (!localRoot.endsWith("\\")) localRoot += "\\";

		log(" processing css: " + file);
		BufferedReader stream = new BufferedReader(new FileReader(file));
		if (stream == null )
			{
			log ( "File open error: " + file );
			return;
			}
		String line;
		int errors = 0;

		for ( int lineNo=1; (line = stream.readLine()) != null; ++lineNo )
			{
			line = line.trim();
			if ( line.isEmpty())
				continue;
			int urlAt = line.toLowerCase().indexOf("url");
			if (urlAt > 0) {
				String ref = inParens(line, urlAt+3);
				if (ref==null) {
					log(" line " + lineNo + ": null ref at '" + line.substring(urlAt) + "'");
					continue;
					}
				else if( ref.startsWith("data:")) {
					log(" line " + lineNo + ": skipping data url: " + ref.substring(0,Math.min(ref.length(),20)) + "...");
					continue;
					}
				try {
					String src;
					File dst;
					if (STRIKINGLY) {
						String fname = ref.substring(ref.lastIndexOf('/'));
						int qAt = fname.indexOf('?');
						if (qAt > 1) fname = fname.substring(0, qAt);
						qAt = fname.indexOf('#');
						if (qAt > 1) fname = fname.substring(0, qAt);
						dst = new File(localRoot, fname);
						src = "http:" + ref;
						System.out.println("STRIKE from " + src + " to " + dst);
						}
					else {
						dst = new File((ref.charAt(0) == '/')? localRoot : file.getParent(), ref).getCanonicalFile();
						if (ref.startsWith("//")) src = "http:" + ref;
						else if (ref.charAt(0) == '/') src = "http:/" + ref;
						else if (ref.startsWith("http:")) src = ref;
						else src = remoteRoot + dst.toString().substring(localRoot.length()).replace('\\', '/');
						}
					_download(src, dst);
					}
				catch(Exception e) { log(" line " + lineNo + ": download err for ref: " + ref + ": " + e); }
				}
			}
		stream.close();
		}
	}
