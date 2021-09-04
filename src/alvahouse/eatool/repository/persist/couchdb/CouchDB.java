/**
 * 
 */
package alvahouse.eatool.repository.persist.couchdb;

import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.codec.binary.Base64;
import org.apache.hc.client5.http.ClientProtocolException;
import org.apache.hc.client5.http.classic.methods.HttpDelete;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpHead;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.classic.methods.HttpPut;
import org.apache.hc.client5.http.classic.methods.HttpUriRequestBase;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.Header;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.HttpEntityContainer;
import org.apache.hc.core5.http.HttpStatus;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.io.HttpClientResponseHandler;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import alvahouse.eatool.repository.dto.Serialise;

/**
 * Lightweight driver class for CouchDB using Apache HTTPClient
 * @author bruce_porteous
 *
 */
public class CouchDB {

	private String host = "localhost";
	private int port = 5984;
	private Database database = new Database();
	private String user = "admin";
	private String password = "8HepHap952";
	
	final CloseableHttpClient httpclient = HttpClients.createDefault();
	
	/**
	 * Use this response handler when normal operation will result in a 200 series response and
	 * anything else should throw an exception.
	 */
	final HttpClientResponseHandler<Response> responseHandler = new HttpClientResponseHandler<Response>() {

		@Override
		public Response handleResponse(final ClassicHttpResponse response) throws IOException {
			final int status = response.getCode();
			if (status >= HttpStatus.SC_SUCCESS && status < HttpStatus.SC_REDIRECTION) {
				final HttpEntity entity = response.getEntity();
				try {
					String value = entity != null ? EntityUtils.toString(entity) : null;
					return new Response(status, value);
				} catch (final ParseException ex) {
					throw new ClientProtocolException(ex);
				}
			} else {
				throw new ClientProtocolException("Unexpected response status: " + status);
			}
		}

	};

	/**
	 * Use this response handler when normal responses are in the 200 or 400 ranges - typically used for 
	 * seeing if some resource exists where a "404 not found" is an expected response.
	 */
	final HttpClientResponseHandler<Response> uncheckedResponseHandler = new HttpClientResponseHandler<Response>() {

		@Override
		public Response handleResponse(final ClassicHttpResponse response) throws IOException {
			final int status = response.getCode();
			String value = null;
			if (status >= HttpStatus.SC_SUCCESS && status < HttpStatus.SC_REDIRECTION) {
				final HttpEntity entity = response.getEntity();
				try {
					value = entity != null ? EntityUtils.toString(entity) : null;
				} catch (final ParseException ex) {
					throw new ClientProtocolException(ex);
				}
			}
			return new Response(status, value);
		}

	};

	/**
	 * 
	 */
	public CouchDB() {
	}

	
	public String info() throws Exception {
		return Request.get(host, port, "/")
				.execute(httpclient, responseHandler)
				.value;
	}

	public String databases() throws Exception {
		return Request.get(host, port, "/_all_dbs")
				.authorise(user, password)
				.execute(httpclient, responseHandler)
				.value;
	}

	public Database database() {
		return database;
	}
	

    private static class Request {
    	private HttpUriRequestBase request;
    	
    	private void writeLog(String data) {
			try (FileWriter writer = new FileWriter("couch_json.log",true)){
				writer.write(data);
				writer.write("\n");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
    	
    	static Request get(String host, int port, String path ) throws URISyntaxException {
			return get(host, port, path, null);
    	}

    	static Request get(String host, int port, String path, String query ) throws URISyntaxException {
			URI uri = new URI("http",null,host,port,path,query,null);
    		return new Request(new HttpGet(uri));
    	}

    	static Request head(String host, int port, String path ) throws URISyntaxException {
			URI uri = new URI("http",null,host,port,path,null,null);
    		return new Request(new HttpHead(uri));
    	}
    	
    	static Request put(String host, int port, String path ) throws URISyntaxException {
			URI uri = new URI("http",null,host,port,path,null,null);
    		return new Request(new HttpPut(uri));
    	}
    	
    	static Request post(String host, int port, String path ) throws URISyntaxException {
			URI uri = new URI("http",null,host,port,path,null,null);
    		return new Request(new HttpPost(uri));
    	}

    	static Request delete(String host, int port, String path ) throws URISyntaxException {
			URI uri = new URI("http",null,host,port,path,null,null);
    		return new Request(new HttpDelete(uri));
    	}


    	Request authorise(String user, String password) throws UnsupportedEncodingException {
    		String auth = "Basic " + Base64.encodeBase64String( (user + ":" + password).getBytes("UTF-8"));
    		request.addHeader("Authorization", auth);
    		return this;
    	}

    	Request version(String version)  {
    		if(version != null) {
	    		request.addHeader("If-Match", version);
    		}
    		return this;
    	}

    	Request payload(String jsonPayload) {
    		if(request instanceof HttpEntityContainer) {
    			HttpEntityContainer container = (HttpEntityContainer)request;
    			HttpEntity entity = new StringEntity(jsonPayload, ContentType.APPLICATION_JSON);
    			container.setEntity(entity);
    			request.addHeader("Content-Type","application/json");
    		} else {
    			throw new IllegalStateException("Method does not accept an entity");
    		}
    		return this;

    	}
    	
    	Response execute(CloseableHttpClient httpclient, HttpClientResponseHandler<Response> responseHandler) throws IOException, URISyntaxException, ParseException {
    		logRequest();
    		Response response =  httpclient.execute(request, responseHandler);
    		logResponse(response);
    		return response;
    	}

		/**
		 * @throws URISyntaxException
		 * @throws IOException
		 * @throws ParseException
		 */
		private void logRequest() throws URISyntaxException, IOException, ParseException {
			writeLog("=======================================================");
    		writeLog(request.getMethod() + " " + request.getUri().toString());
    		for(Header header : request.getHeaders()) {
    			writeLog(header.toString());
    		}
    		HttpEntity entity = request.getEntity();
    		if(entity != null) {
    			writeLog(entity.toString());
    			writeLog(EntityUtils.toString(entity));
    		}
		}

		/**
		 * @param response
		 */
		private void logResponse(Response response) {
			writeLog("Status: " + response.status);
    		writeLog(response.value);
		}

    	private Request(HttpUriRequestBase request) {
    		this.request = request;
			request.addHeader("Accept","application/json");
    	}
    }
	
    private static class Response {
    	int status;
    	String value;
		/**
		 * @param status
		 * @param value
		 */
		Response(int status, String value) {
			super();
			this.status = status;
			this.value = value;
		}
    }
 
    
    
	public class Database {
		
		boolean exists(String name) throws Exception {
			return 200 == Request.head(host, port, "/" + name)
					.authorise(user, password)
					.execute(httpclient, uncheckedResponseHandler)
					.status;
		}
		
		String info(String name) throws Exception {
			return Request.get(host, port, "/" + name)
					.authorise(user, password)
					.execute(httpclient, responseHandler)
					.value;
		}
		
		String create(String name) throws Exception {
			return Request.put(host, port, "/" + name)
					.authorise(user, password)
					.execute(httpclient, responseHandler)
					.value;
		}
		
		boolean delete(String name) throws Exception {
			return 200 == Request.delete(host, port, "/" + name)
					.authorise(user, password)
					.execute(httpclient, uncheckedResponseHandler)
					.status;
		}

		PutDocumentResponse putDocument(String name, String key,  String document) throws Exception {
			String json =  Request.put(host, port, "/" + name + "/" + key)
					.authorise(user, password)
					.payload(document)
					.execute(httpclient, responseHandler)
					.value;
			return Serialise.unmarshalFromJson(json, PutDocumentResponse.class);
		}
		
		String getDocument(String name, String key) throws Exception {
			String json =  Request.get(host, port, "/" + name + "/" + key)
					.authorise(user, password)
					.execute(httpclient, responseHandler)
					.value;
			return json;
		}

		boolean documentExists(String name, String key) throws Exception {
			return 200 == Request.head(host, port, "/" + name + "/" + key)
					.authorise(user, password)
					.execute(httpclient, uncheckedResponseHandler)
					.status;
		}
		
		/**
		 * Deletes a single document identified by a key and version.
		 * @param name
		 * @param key
		 * @param version
		 */
		void deleteDocument(String name, String key, String version) throws Exception{
			Request.delete(host, port, "/" + name + "/" + key)
					.authorise(user, password)
					.version(version)
					.execute(httpclient, responseHandler);
		}

		/**
		 * Does a bulk update of many docs at once.
		 * @see https://docs.couchdb.org/en/main/api/database/bulk-api.html#db-bulk-docs
		 * @param name is the name of the database.
		 * @param docs is the JSON containing the document details.
		 * @throws Exception
		 */
		void bulkUpdate(String name, String docs) throws Exception {
			Request.post(host, port, "/" + name + "/_bulk_docs")
				.authorise(user, password)
				.payload(docs)
				.execute(httpclient, responseHandler);
		}
		
		
		String queryView(String database, String designDoc, String viewName) throws Exception{
			return queryView(database, designDoc, viewName, null);
		}

		/**
		 * Queries a CouchDB view e.g. :
		 * database/_design/meta_entities/_view/all?include_docs=true
		 * @param database
		 * @param designDoc
		 * @param viewName
		 * @param queryString
		 * @return
		 */
		String queryView(String database, String designDoc, String viewName, String queryString) throws Exception{
			String json =  Request.get(host, port, "/" + database + "/" + "_design/" + designDoc + "/_view/" + viewName, queryString)
					.authorise(user, password)
					.execute(httpclient, responseHandler)
					.value;
			return json;
		}
	}
	
	/**
	 * Helper class to build query strings for CouchDB
	 * @author bruce_porteous
	 *
	 */
	public static class Query{
		
		public static final String INCLUDE_DOCS = "include_docs=true";
		
		private List<String> parts = new LinkedList<>();
		
		Query addKey(String value) {
			return add("key",value);
		}
		
		Query addKey(String... values) {
			return add("key", compoundKey(values));
		}
		
		Query add(String name, String value) {
			if(value.startsWith("[") || value.startsWith("{")) {
				parts.add( name + "=" + value); // no quotes for object or array key
			} else {
				parts.add( name + "=\"" + value + "\""); // Quotes will be escaped to %22
			}
			return this;
		}
		
		Query add(String value) {
			parts.add(value);
			return this;
		}
		
		public String toString() {
			StringBuilder builder = new StringBuilder();
			for(String part : parts) {
				if(builder.length() != 0) {
					builder.append('&');
				}
				builder.append(part);
			}
			return builder.toString();
		}
		
		/**
		 * Converts an array of string values into a JSON array for use as a compound key.
		 * @param values
		 * @return
		 */
		private static String compoundKey(String... values ) {
			StringBuilder builder = new StringBuilder();
			builder.append('[');
			for(String value : values) {
				if(builder.length() > 1) {  // 1 to allow for leading [
					builder.append(',');
				}
				builder.append('"');
				builder.append(value);
				builder.append('"');
			}
			builder.append(']');
			return builder.toString();
		}
	}
	
	/**
	 * Encapsulates the response from writing a document
	 * {
     * "id": "ab39fe0993049b84cfa81acd6ebad09d",
     * "ok": true,
     * "rev": "1-9c65296036141e575d32ba9c034dd3ee"
	 * }
	 * @author bruce_porteous
	 *
	 */
	public static class PutDocumentResponse{
		public String id;
		public String ok;
		public String rev;
	}
	
	public static void main(String[] args) {

		String loggerName = CouchDB.class.getName();
		System.out.println(loggerName);
	    Logger logger = LoggerFactory.getLogger(loggerName);
	    logger.info("Hello World");

		
		try {
			CouchDB couch = new CouchDB();
			System.out.println(couch.info());
			System.out.println(couch.databases());
			
			String entityKey = "bsrwZwnVYJ1Yj7E5umCFAd";
			String metaKey = "j$KchdHBgqjFDNE5uKltHd";
			String json = couch.database().queryView("test", "relationships", "byEntityAndMetaRelationship", 
					new CouchDB.Query()
					.addKey(entityKey, metaKey)
					.add(CouchDB.Query.INCLUDE_DOCS)
					.toString());
			System.out.println(json);
			
//			System.out.println(couch.database().exists("playpen"));
//			System.out.println(couch.database().exists("wombats"));
//			System.out.println(couch.database().info("playpen"));
//			System.out.println(couch.database().create("testdb"));
//			System.out.println(couch.databases());
//			System.out.println(couch.database().delete("testdb"));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
