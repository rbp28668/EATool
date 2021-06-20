/**
 * 
 */
package alvahouse.eatool.repository.dao;

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
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

/**
 * @author bruce_porteous
 *
 */
public class Serialise {

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
        String contextPath = alvahouse.eatool.repository.dao.metamodel.ObjectFactory.class.getPackage().getName()
        		+ ":" + alvahouse.eatool.repository.dao.model.ObjectFactory.class.getPackage().getName();

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
      ObjectMapper mapper = new ObjectMapper();
      return mapper.readValue(json, objClass);
    }

    /**
     * @return
     */
    private static ObjectMapper getOutputMapper() {
        ObjectMapper mapper = new ObjectMapper(); 
        mapper.setSerializationInclusion(Include.NON_NULL);
        mapper.setSerializationInclusion(Include.NON_EMPTY);
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        return mapper;
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
