/*
 * ExtensibleTypes.java
 * Project: EATool
 * Created on 10-Jul-2006
 *
 */
package alvahouse.eatool.repository.metamodel.types;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import alvahouse.eatool.repository.dto.metamodel.ControlledListTypeDto;
import alvahouse.eatool.repository.dto.metamodel.ExtensibleMetaPropertyTypeDto;
import alvahouse.eatool.repository.dto.metamodel.RegexpCheckedTypeDto;
import alvahouse.eatool.repository.dto.metamodel.TimeSeriesTypeDto;
import alvahouse.eatool.repository.persist.MetaModelPersistence;
import alvahouse.eatool.util.XMLWriter;

/**
 * ExtensibleTypes is the top-level container for lists of extensible types.
 * It also provides a listener mechanism for flagging updates.
 * 
 * @author rbp28668
 */
public class ExtensibleTypes {

	private final MetaModelPersistence persistence;

	private static List<Handler> handlers = new LinkedList<>();
	private static Map<Class<? extends ExtensibleMetaPropertyType>, Handler> handlersByImplementingClass = new HashMap<>();
	private static Map<Class<? extends ExtensibleMetaPropertyTypeDto>, Handler> handlersByDto = new HashMap<>();
	
	private List<TypeEventListener> listeners = new LinkedList<TypeEventListener>();
 
	static {
		add(new RegexpCheckedHandler());
		add(new TimeSeriesHandler());
		add(new ControlledListHandler());
	}
	
	private static void add(Handler handler) {
		handlers.add(handler);
		handlersByImplementingClass.put(handler.implementingClass, handler);
		handlersByDto.put(handler.dtoClass, handler);
	}
	
    /**
     * 
     */
    public ExtensibleTypes(MetaModelPersistence persistence) {
        super();
        this.persistence = persistence;
        
     }
    
 
    public Collection<ExtensibleTypeList> getTypes() throws Exception{

        List<ExtensibleTypeList> types = new LinkedList<ExtensibleTypeList>();
        Map<Class<? extends ExtensibleMetaPropertyTypeDto>,ExtensibleTypeList> listByDto = new HashMap<>();

        // First build ExtensibleTypeList list from the ones we can handle
        for(Handler handler : handlers) {
        	ExtensibleTypeList list = handler.createList();
        	types.add(list);
        	listByDto.put(handler.dtoClass, list);
        }
    	
        // Now stuff the types into the appropriate list
        for(ExtensibleMetaPropertyTypeDto dto : persistence.getDefinedTypes()) {
            ExtensibleTypeList list = listByDto.get(dto.getClass());
            if(list == null){ // Should never happen but...
                throw new IllegalArgumentException("Type " + dto.getClass().getCanonicalName() + " is not known");
            }
            Handler handler = handlersByDto.get(dto.getClass());
            ExtensibleMetaPropertyType type = handler.fromDto(dto);
            list.add(type);
        }
        return types;
    }
    
 
	/**
     * Writes the list out as XML
     * @param out is the XMLWriterDirect to write the XML to
     */
    public void writeXML(XMLWriter out) throws IOException {
        out.startEntity("Types");
        
        try {
	        for(ExtensibleTypeList list : getTypes()){
	            if(!list.getTypes().isEmpty()){
	                list.writeXML(out);
	            }
	        }
        } catch(Exception e) {
        	throw new IOException("Unable to write extensible types to XML",e);
        }
        
        out.stopEntity();
    }

    /**
     * Clears out any extensible types.
     */
    public void deleteContents() throws Exception{
//        for(ExtensibleTypeList list : types){
//            list.deleteContents();
//        }
//        
        persistence.deleteDefinedTypes();
    }

    /**
     * @param listener
     */
    public void addListener(TypeEventListener listener){
        listeners.add(listener);
    }

    public void removeListener(TypeEventListener listener){
        listeners.remove(listener);
    }
    
    private void fireTypeAdded(ExtensibleMetaPropertyType type) throws Exception{
        TypeEvent event = new TypeEvent(type);
        for(TypeEventListener listener : listeners){
            listener.typeAdded(event);
        }
    }
    
    private void fireTypeChanged(ExtensibleMetaPropertyType type) throws Exception{
        TypeEvent event = new TypeEvent(type);
        for(TypeEventListener listener : listeners){
            listener.typeChanged(event);
        }
        
    }
    
    private void fireTypeDeleted(ExtensibleMetaPropertyType type) throws Exception{
        TypeEvent event = new TypeEvent(type);
        for(TypeEventListener listener : listeners){
            listener.typeDeleted(event);
        }
        
    }

    /**
     * Internal method for adding types from XML etc where we don't want the version
     * to be changed, nor change events to fire.
     * @param type
     */
    public void _add(ExtensibleMetaPropertyType type) throws Exception{
        Handler handler = handlersByImplementingClass.get(type.getClass());
        if(handler == null){
            throw new IllegalArgumentException("Type " + type.getClass().getCanonicalName() + " is not known");
        }
        persistence.addType(handler.toDto(type));
    }

    /**
     * @param type
     */
    public void addType(ExtensibleMetaPropertyType type) throws Exception{
        Handler handler = handlersByImplementingClass.get(type.getClass());
        if(handler == null){
            throw new IllegalArgumentException("Type " + type.getClass().getCanonicalName() + " is not known");
        }
		String user = System.getProperty("user.name");
		type.getVersion().createBy(user);

        persistence.addType(handler.toDto(type));
        fireTypeAdded(type);
    }

    /**
     * @param type
     */
    public void updateType(ExtensibleMetaPropertyType type) throws Exception{
        Handler handler = handlersByImplementingClass.get(type.getClass());
        if(handler == null){
            throw new IllegalArgumentException("Type " + type.getClass().getCanonicalName() + " is not known");
        }

        String user = System.getProperty("user.name");
		type.getVersion().modifyBy(user);

        persistence.updateType(handler.toDto(type));
        fireTypeChanged(type);
    }

    /**
     * @param type
     */
    public void deleteType(ExtensibleMetaPropertyType type) throws Exception{
        if(!handlersByImplementingClass.containsKey(type.getClass())){
            throw new IllegalArgumentException("Type " + type.getClass().getCanonicalName() + " is not known");
        }
        persistence.deleteType(type.getKey(), type.getVersion().getVersion());
        fireTypeDeleted(type);
    }

    public boolean isValidType(Class<? extends ExtensibleMetaPropertyType> implementingClass) {
    	return handlersByImplementingClass.containsKey(implementingClass);
    }
    
    /**
     * @param type
     * @return
     */
    public ExtensibleTypeList lookupList(ExtensibleMetaPropertyType type) throws Exception{
        return lookupList(type.getClass());
    }

    /**
     * @param implementingClass
     * @return
     */
    public ExtensibleTypeList lookupList(Class<? extends ExtensibleMetaPropertyType> implementingClass) throws Exception{
     	for(ExtensibleTypeList list : getTypes()) {
     		if(list.getImplementingClass().equals(implementingClass)) {
     			return list;
     		}
     	}
     	throw new IllegalArgumentException("Extensible MetaPropertyType " + implementingClass.getCanonicalName() + " is not known to the repository");
    }

    /** Get the number of extensible types.
     * @return the number of types.
     */
    public int getTypeCount() {
        return handlers.size();
    }
    
    private static abstract class Handler {
    	Class<? extends ExtensibleMetaPropertyType> implementingClass;
    	Class<? extends ExtensibleMetaPropertyTypeDto> dtoClass;
    	String name;
    	
    	/**
		 * @param implementingClass
		 * @param dtoClass
		 * @param name
		 */
		Handler(Class<? extends ExtensibleMetaPropertyType> implementingClass,
				Class<? extends ExtensibleMetaPropertyTypeDto> dtoClass, String name) {
			super();
			this.implementingClass = implementingClass;
			this.dtoClass = dtoClass;
			this.name = name;
		}

		abstract ExtensibleMetaPropertyType fromDto(ExtensibleMetaPropertyTypeDto dto);
		abstract ExtensibleMetaPropertyTypeDto toDto(ExtensibleMetaPropertyType type);
		
		ExtensibleTypeList createList() {
			ExtensibleTypeList etl = new ExtensibleTypeList(implementingClass, name);
			return etl;
		}
		
    }
    
    private static class RegexpCheckedHandler extends Handler{

    	/**
		 * 
		 */
		public RegexpCheckedHandler() {
			super(RegexpCheckedType.class, RegexpCheckedTypeDto.class, "FilteredInput");
		}
		
		/* (non-Javadoc)
		 * @see alvahouse.eatool.repository.metamodel.types.ExtensibleTypes.Handler#fromDto(alvahouse.eatool.repository.dto.metamodel.ExtensibleMetaPropertyTypeDto)
		 */
		@Override
		ExtensibleMetaPropertyType fromDto(ExtensibleMetaPropertyTypeDto dto) {
			RegexpCheckedTypeDto rctdto = (RegexpCheckedTypeDto) dto;
			return new RegexpCheckedType(rctdto);
		}

		/* (non-Javadoc)
		 * @see alvahouse.eatool.repository.metamodel.types.ExtensibleTypes.Handler#toDto(alvahouse.eatool.repository.metamodel.types.ExtensibleMetaPropertyType)
		 */
		@Override
		ExtensibleMetaPropertyTypeDto toDto(ExtensibleMetaPropertyType type) {
			RegexpCheckedType rct = (RegexpCheckedType)type;
			return rct.toDto();
		}
    	
    }

    private static class TimeSeriesHandler extends Handler{

    	/**
		 * 
		 */
		public TimeSeriesHandler() {
			super(TimeSeriesType.class, TimeSeriesTypeDto.class, "Time Series");
		}
		
		/* (non-Javadoc)
		 * @see alvahouse.eatool.repository.metamodel.types.ExtensibleTypes.Handler#fromDto(alvahouse.eatool.repository.dto.metamodel.ExtensibleMetaPropertyTypeDto)
		 */
		@Override
		ExtensibleMetaPropertyType fromDto(ExtensibleMetaPropertyTypeDto dto) {
			TimeSeriesTypeDto tstdto = (TimeSeriesTypeDto) dto;
			return new TimeSeriesType(tstdto);
		}

		/* (non-Javadoc)
		 * @see alvahouse.eatool.repository.metamodel.types.ExtensibleTypes.Handler#toDto(alvahouse.eatool.repository.metamodel.types.ExtensibleMetaPropertyType)
		 */
		@Override
		ExtensibleMetaPropertyTypeDto toDto(ExtensibleMetaPropertyType type) {
			TimeSeriesType tst = (TimeSeriesType)type;
			return tst.toDto();
		}
    	
    }

    private static class ControlledListHandler extends Handler{

    	/**
		 * 
		 */
		public ControlledListHandler() {
			super(ControlledListType.class, ControlledListTypeDto.class, "Controlled List");
		}
		
		/* (non-Javadoc)
		 * @see alvahouse.eatool.repository.metamodel.types.ExtensibleTypes.Handler#fromDto(alvahouse.eatool.repository.dto.metamodel.ExtensibleMetaPropertyTypeDto)
		 */
		@Override
		ExtensibleMetaPropertyType fromDto(ExtensibleMetaPropertyTypeDto dto) {
			ControlledListTypeDto cltdto = (ControlledListTypeDto) dto;
			return new ControlledListType(cltdto);
		}

		/* (non-Javadoc)
		 * @see alvahouse.eatool.repository.metamodel.types.ExtensibleTypes.Handler#toDto(alvahouse.eatool.repository.metamodel.types.ExtensibleMetaPropertyType)
		 */
		@Override
		ExtensibleMetaPropertyTypeDto toDto(ExtensibleMetaPropertyType type) {
			ControlledListType clt = (ControlledListType) type;
			return clt.toDto();
		}
    	
    }

}
