/*
 * ImportMappingDialog.java
 * Project: EATool
 * Created on 10-Dec-2005
 *
 */
package alvahouse.eatool.gui.mappings;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import alvahouse.eatool.Application;
import alvahouse.eatool.gui.BasicDialog;
import alvahouse.eatool.gui.Dialogs;
import alvahouse.eatool.gui.WrappedTextArea;
import alvahouse.eatool.repository.Repository;
import alvahouse.eatool.repository.mapping.ImportMapping;
import alvahouse.eatool.repository.mapping.ImportMappings;

/**
 * ImportMappingDialog is a dialog to set up the basic parameters of an
 * import mapping - the name and the description.
 * 
 * @author rbp28668
 */
public class ImportMappingDialog extends BasicDialog {

    private static final long serialVersionUID = 1L;
    private javax.swing.JTextField txtName;
    //private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextArea txtDescription;
    private JTextField transformPath;
    private JComboBox<String> parser;
    private ImportMapping mapping;
    
    /**
     * @param parent
     * @param title
     * @param mapping
     */
    public ImportMappingDialog(JDialog parent, String title, ImportMapping mapping, Application app, Repository repository) {
        super(parent, title);
        init(mapping,app,repository);
    }

    /**
     * @param parent
     * @param title
     * @param mapping
     */
    public ImportMappingDialog(Component parent, String title, ImportMapping mapping, Application app, Repository repository) {
        super(parent, title);
        init(mapping,app, repository);
    }

    /* (non-Javadoc)
     * @see alvahouse.eatool.gui.BasicDialog#onOK()
     */
    protected void onOK() {
        mapping.setName(txtName.getText());
        mapping.setDescription(txtDescription.getText());
        mapping.setParserName((String)parser.getSelectedItem());

        String path = transformPath.getText().trim();
        if(path.length() > 0){
            mapping.setTransformPath(path);
        } else {
            mapping.setTransformPath(null);
        }
    }

    
    /**
     * Effectively the constructor.
     * @param mapping is the mapping to edit.
     */
    private void init(ImportMapping mapping, Application app, Repository repository) {
        if(mapping == null){
            throw new NullPointerException("Can't edit null mapping");
        }
        this.mapping = mapping;
        
        setLayout(new java.awt.BorderLayout());
        
        JPanel mainPanel = new JPanel();
        GridBagLayout layout = new GridBagLayout();
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        c.gridheight = 1;
        c.gridwidth = 1;
        c.weightx = 1.0f;
        c.weighty = 1.0f;
        c.insets = new Insets(10,5,10,5);
        c.anchor = GridBagConstraints.LINE_START;
        
        mainPanel.setLayout(layout);

        JLabel label;
        
        label = new JLabel("Name");
        layout.setConstraints(label,c);
        mainPanel.add(label);
        
        c.gridwidth = GridBagConstraints.REMAINDER;
        txtName = new JTextField(mapping.getName());
        txtName.setColumns(40);
        layout.setConstraints(txtName,c);
        mainPanel.add(txtName);

//        Box top = Box.createHorizontalBox();
//        top.add(new JLabel("Name"));
//        top.add(Box.createHorizontalStrut(5));
//        txtName = new JTextField(mapping.getName());
//        txtName.setColumns(40);
//        top.add(txtName);
//        getContentPane().add(top,BorderLayout.NORTH);
        
        txtDescription = new WrappedTextArea(mapping.getDescription());
        txtDescription.setRows(4);
        JScrollPane scroll = new JScrollPane();
        scroll.setBorder(new TitledBorder("Description"));
        scroll.setViewportView(txtDescription);
        
        
        //getContentPane().add(scroll,BorderLayout.CENTER);
        layout.setConstraints(scroll,c);
        mainPanel.add(scroll);
     
        label = new JLabel("Transform");
        c.gridwidth = 1;
        layout.setConstraints(label,c);
        mainPanel.add(label);
        
        transformPath = new JTextField();
        String path = mapping.getTransformPath();
        if(path != null) {
            transformPath.setText(path);
        }
        c.gridwidth = 1;
        layout.setConstraints(transformPath,c);
        mainPanel.add(transformPath);

        JButton choosePath = new JButton("...");
        choosePath.addActionListener( new ActionListener() {

            public void actionPerformed(ActionEvent event) {
                JFileChooser chooser = new JFileChooser();
                String path = transformPath.getText();
                if(path == null || path.length()==0) 
                    chooser.setCurrentDirectory( new File("."));
                else
                    chooser.setSelectedFile(new File(path));
                //chooser.setFileFilter( new XMLFileFilter());

                if( chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                    transformPath.setText(chooser.getSelectedFile().getPath());
                }
                
            }
            
        });
        
        c.fill = GridBagConstraints.NONE;
        c.gridwidth = GridBagConstraints.REMAINDER;
        c.anchor = GridBagConstraints.LINE_END;
        c.weightx = 0.1;
        
        layout.setConstraints(choosePath,c);
        mainPanel.add(choosePath);

        label = new JLabel("Type");
        c.fill = GridBagConstraints.BOTH;
        c.gridwidth = 1;
        layout.setConstraints(label,c);
        mainPanel.add(label);

        ImportMappings mappings = repository.getImportMappings();
        mappings.setParsers(app.getSettings());
        
        String[] parsers = mappings.getParserNames();
        parser = new JComboBox<String>(parsers);
        parser.setSelectedItem(mapping.getParserName());
        c.gridwidth = GridBagConstraints.REMAINDER;
        layout.setConstraints(parser,c);
        mainPanel.add(parser);

//        Box bottom = Box.createHorizontalBox();
//        bottom.add(new JLabel("Type"));
//        bottom.add(Box.createHorizontalStrut(5));
//        Object[] parsers = ImportMappings.getParserNames();
//        parser = new JComboBox(parsers);
//        parser.setSelectedItem(mapping.getParserName());
//        bottom.add(parser);
//        getContentPane().add(bottom,BorderLayout.SOUTH);

        getContentPane().add(mainPanel,BorderLayout.CENTER);
        getContentPane().add(getOKCancelPanel(), BorderLayout.EAST);
        pack();
        
    }

    /** validates values in the panel
     * @return true if input is valid, false if invalid
     **/
    public boolean validateInput() {
        String name = txtName.getText().trim();
        if(name.length() == 0) {
            Dialogs.message(this,"Please enter a value for the name"); 
            txtName.requestFocus();
            return false;
        }
        
        String path = transformPath.getText().trim();
        if(path.length() > 0){
            File transform = new File(path);
            if(!transform.exists()){
                Dialogs.message(this,"Transform file does not exist"); 
                transformPath.requestFocus();
                return false;
            }
        }
        return true;
    }
    
}
