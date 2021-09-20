package alvahouse.eatool.repository.dto;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.SchemaOutputResolver;
import javax.xml.transform.Result;
import javax.xml.transform.stream.StreamResult;

public class SchemaWriter {

	private ZipSchemaOutputResolver resolver = null;
	

	public SchemaWriter(){
		resolver = new ZipSchemaOutputResolver();
	}
	
	public void generateSchema(JAXBContext jaxbContext) throws IOException {
		jaxbContext.generateSchema(resolver);
	}

	public void writeZip(OutputStream out) throws IOException {
		resolver.writeZip(out);
	}
	
	
	private class ZipSchemaOutputResolver extends SchemaOutputResolver {
	    
		private List<ZipEntryOutputStream> parts = new LinkedList<ZipEntryOutputStream>();
	
	    public Result createOutput(String namespaceURI, String suggestedFileName) throws IOException  {
	    	ZipEntryOutputStream out = new ZipEntryOutputStream(suggestedFileName);
	    	parts.add(out);
	        StreamResult result = new StreamResult(out);
	        result.setSystemId(suggestedFileName);
	        return result;
	    }
	    
	    void writeZip(OutputStream out) throws IOException {
			ZipOutputStream zip = new ZipOutputStream(out);
			for(ZipEntryOutputStream part : parts) {
				part.writeEntry(zip);
			}
	    	zip.close();
	    }
	}

	/**
	 * An output stream that buffers its output and, once written, will add an entry to a zip file with the contents.
	 * @author bruce.porteous
	 *
	 */
	private class ZipEntryOutputStream extends OutputStream {

		ByteArrayOutputStream out;
		String entryName;

		ZipEntryOutputStream(String entryName) {
			this.entryName = entryName;
			this.out = new ByteArrayOutputStream(4096);  // reasonable initial value - schemas usually a few KB.
		}
		

		public void writeEntry(ZipOutputStream zip) throws IOException {
	    	ZipEntry entry = new ZipEntry(entryName);
	    	zip.putNextEntry(entry);
			out.flush();
			byte[] contents = out.toByteArray();
			zip.write(contents);
			zip.closeEntry();
		}


		/* (non-Javadoc)
		 * @see java.io.OutputStream#write(int)
		 */
		@Override
		public void write(int b) throws IOException {
			out.write(b);
		}

		/* (non-Javadoc)
		 * @see java.io.OutputStream#write(byte[])
		 */
		@Override
		public void write(byte[] b) throws IOException {
			out.write(b);
		}

		/* (non-Javadoc)
		 * @see java.io.OutputStream#write(byte[], int, int)
		 */
		@Override
		public void write(byte[] b, int off, int len) throws IOException {
			out.write(b, off, len);
		}

		/* (non-Javadoc)
		 * @see java.io.OutputStream#flush()
		 */
		@Override
		public void flush() throws IOException {
			out.flush();
		}

		/* (non-Javadoc)
		 * @see java.io.OutputStream#close()
		 */
		@Override
		public void close() throws IOException {
			out.close();
		}

	
	}
}