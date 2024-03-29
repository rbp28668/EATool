/*
 * TextBox.java
 * Project: EATool
 * Created on 18-Jun-2007
 *
 */
package alvahouse.eatool.repository.graphical.standard;

import java.io.IOException;

import alvahouse.eatool.repository.dto.graphical.TextBoxDto;
import alvahouse.eatool.util.UUID;
import alvahouse.eatool.util.XMLWriter;

/**
 * TextBox is a text box for a diagram. Unlike a symbol it doesn't have any
 * underlying model element and cannot be linked to.
 * 
 * @author rbp28668
 */
public class TextBox extends TextualObject {

    private String text = "";
    private String url = "";
    
    /**
     * @param key
     */
    public TextBox(UUID key) {
        super(key);
    }

    /**
	 * @param textBox
	 */
	public TextBox(TextBoxDto dto) {
		super(dto);
		text = dto.getText();
		url = dto.getUrl();
	}

	public TextBoxDto toDto() {
		TextBoxDto dto = new TextBoxDto();
		copyTo(dto);
		return dto;
	}
	
	/* (non-Javadoc)
     * @see alvahouse.eatool.gui.graphical.standard.TextualObject#getText()
     */
    public String getText() {
        return text;
    }

    
    /**
     * @param text The text to set.
     */
    public void setText(String text) {
        this.text = text;
    }
    
    /**
     * @return Returns the url.
     */
    public String getUrl() {
        return url;
    }
    /**
     * @param url The url to set.
     */
    public void setUrl(String url) {
        this.url = url;
    }
	/**
	 * Writes the TextBox out as XML
	 * @param out is the XMLWriter to write the XML to
	 * @throws IOException in the event of an io error
	 */
	public void writeXML(XMLWriter out) throws IOException {
		out.startEntity("TextBox");
		writeTextObjectAttributesXML(out);
		writeTextObjectSettingsXML(out);
		out.textEntity("Text", text);
		out.textEntity("URL",url);
		out.stopEntity();
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	@Override
	protected Object clone() {
		TextBox copy = new TextBox(getKey());
		cloneTo(copy);
		return copy;
	}
	
	protected void cloneTo(TextBox copy) {
		super.cloneTo(copy);
		copy.text = text;
		copy.url = url;
	}

	protected void copyTo(TextBoxDto dto) {
		super.copyTo(dto);
		dto.setText(text);
		dto.setUrl(url);
	}

}
