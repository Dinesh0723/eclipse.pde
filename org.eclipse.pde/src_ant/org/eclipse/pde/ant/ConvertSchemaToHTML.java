package org.eclipse.pde.ant;

import java.io.*;
import java.net.*;

import org.apache.tools.ant.*;
import org.eclipse.pde.internal.builders.SchemaTransformer;
import org.eclipse.pde.internal.core.SourceDOMParser;
import org.eclipse.pde.internal.core.ischema.ISchemaDescriptor;
import org.eclipse.pde.internal.core.schema.Schema;
import org.xml.sax.*;

public class ConvertSchemaToHTML extends Task {
	
	private SourceDOMParser parser = new SourceDOMParser();
	private SchemaTransformer transformer = new SchemaTransformer();
	private String schemaLocation;
	private String outputLocation;
	
	
	public void execute() throws BuildException {
		FileInputStream is = null;
		PrintWriter printWriter = null;
		try {
			if (schemaLocation == null || outputLocation == null)
				return;
				
			File schemaFile = new File(schemaLocation);
			if (!schemaFile.exists())
				return;

			is = new FileInputStream(schemaFile);
			parser.parse(new InputSource(is));
			URL url = null;
			
			try {
				url = new URL("file:"+schemaFile.getPath());
			}
			catch (MalformedURLException e) {
			}
			Schema schema = new Schema((ISchemaDescriptor) null, url);
			schema.traverseDocumentTree(
				parser.getDocument().getDocumentElement(),
				parser.getLineTable());

			printWriter = new PrintWriter(new FileOutputStream(outputLocation), true);
			transformer.transform(printWriter, schema);
		} catch (FileNotFoundException e) {
		} catch (SAXException e) {
		} catch (IOException e) {
		} finally {
			try {
				if (is != null)
					is.close();
			} catch (IOException e) {
			}
			if (printWriter != null) {
				printWriter.flush();
				printWriter.close();
			}
		}
	}
	
	public void setSchemalocation(String location) {
		this.schemaLocation = location;
	}
	
	public void setOutputlocation(String location) {
		this.outputLocation = location;
	}

}
