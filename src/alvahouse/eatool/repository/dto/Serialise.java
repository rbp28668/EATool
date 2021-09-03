/**
 * 
 */
package alvahouse.eatool.repository.dto;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.ValidationEvent;
import javax.xml.bind.ValidationEventHandler;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * @author bruce_porteous
 *
 */
public class Serialise {

	// Jackson object mapper single instances.
	private static ObjectMapper inputMapper = null;
	private static ObjectMapper outputMapper = null;
	
    /**
     * @param outputStream
     * @throws JAXBException
     */
    public static void marshalToXML(Object value, OutputStream outputStream) throws JAXBException {
        JAXBContext jc = createContext();
        Marshaller m = jc.createMarshaller();
        m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        m.marshal(value, outputStream);

    }

    /**
     * Marshalls as XML to a string.
     *
     * @return output XML.
     * @throws JAXBException
     */
    public static String marshalToXML(Object value) throws JAXBException {
        JAXBContext jc = createContext();
        Marshaller m = jc.createMarshaller();
        StringWriter writer = new StringWriter();
        m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        m.marshal(value, writer);
        return writer.toString();
    }

    public static Object marshalFromXML(InputStream inputStream) throws JAXBException {
        JAXBContext jc = createContext();
        Unmarshaller u = jc.createUnmarshaller();
        u.setEventHandler(new SerializationValidator());
        Source source = new StreamSource(inputStream);
        source.setSystemId("file:///data.xml");
        return  u.unmarshal(source);
    }

//    public static void writeSchemaAsZip(OutputStream outputStream) throws JAXBException, IOException {
//        JAXBContext jc = JAXBContext.newInstance(RepositoryDao.class);
//        SchemaWriter sw = new SchemaWriter();
//        sw.generateSchema(jc);
//        sw.writeZip(outputStream);
//    }

    /**
     * Create JAXB context for serialising to/from XML.
     *
     * @return
     * @throws JAXBException
     */
    private static JAXBContext createContext() throws JAXBException {
        String contextPath = alvahouse.eatool.repository.dto.metamodel.ObjectFactory.class.getPackage().getName()
        		+ ":" + alvahouse.eatool.repository.dto.model.ObjectFactory.class.getPackage().getName()
        		+ ":" + alvahouse.eatool.repository.dto.scripting.ObjectFactory.class.getPackage().getName()
        		+ ":" + alvahouse.eatool.repository.dto.images.ObjectFactory.class.getPackage().getName()
        		+ ":" + alvahouse.eatool.repository.dto.html.ObjectFactory.class.getPackage().getName()
        		+ ":" + alvahouse.eatool.repository.dto.mapping.ObjectFactory.class.getPackage().getName()
        		+ ":" + alvahouse.eatool.repository.dto.graphical.ObjectFactory.class.getPackage().getName();

        JAXBContext jc = JAXBContext.newInstance(contextPath);
        return jc;
    }

    public static void marshalToJSON(Object value, OutputStream out) throws IOException {
        ObjectMapper mapper = getOutputMapper();
        mapper.writeValue(out, value);
    }

    public static String marshalToJSON(Object value) throws IOException {
        ObjectMapper mapper = getOutputMapper();
        return mapper.writeValueAsString(value);
    }

    public static <T> T unmarshalFromJson(String json, Class<T> objClass)
    throws JsonProcessingException {
      ObjectMapper mapper = getInputMapper();
      return mapper.readValue(json, objClass);
    }

    public static <T> T unmarshalFromJson(JsonNode json, Class<T> objClass)
    throws JsonProcessingException {
      ObjectMapper mapper = getInputMapper();
      return mapper.treeToValue(json, objClass);
    }

    public static JsonNode parseToJsonTree(String json) throws IOException, JacksonException {
        ObjectMapper mapper = getInputMapper();
        return mapper.readTree(json);
    }

    public static String writeJsonTree(JsonNode json) throws IOException, JacksonException {
        ObjectMapper mapper = getOutputMapper();
        StringWriter out = new StringWriter();
        mapper.writeValue(out, json);
        return out.toString();
    }

    /**
     * Create a Jackson ObjectNode.  Use this to start
     * building a JSON structure that can then be serialised
     * to a string.
     * @see https://fasterxml.github.io/jackson-databind/javadoc/2.7/com/fasterxml/jackson/databind/node/ObjectNode.html
     * @return ObjectNode.
     */
    public static ObjectNode createObjectNode() {
    	return getOutputMapper().createObjectNode();
    }
    
     
    /**
     * Get the Jackson input mapper. Note that ObjectMapper is threadsafe and it's
     * recommended to create once and use many times.
     * @return
     */
    private static ObjectMapper getInputMapper() {
    	if(inputMapper == null) {
    		inputMapper = new ObjectMapper();
    	}
        return inputMapper;
    }
    
    /**
     * Get the Jackson output mapper.
     * @return
     */
    private static ObjectMapper getOutputMapper() {
        if(outputMapper == null) {
        	ObjectMapper mapper = new ObjectMapper(); 
	        mapper.setSerializationInclusion(Include.NON_NULL);
	        mapper.setSerializationInclusion(Include.NON_EMPTY);
	        mapper.enable(SerializationFeature.INDENT_OUTPUT);
	        outputMapper = mapper; // now fully configured.
        }
        return outputMapper;
    }

    /**
     * Class to catch JAXB validation errors
     *
     * @author bruce_porteous
     *
     */
    private static class SerializationValidator implements ValidationEventHandler {

        @Override
        public boolean handleEvent(ValidationEvent event) {
            System.out.println(event.getMessage());
            return true;
        }

    }

}
