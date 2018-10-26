/*
 * ExportMappingDialog.java
 * Project: EATool
 * Created on 29-Dec-2005
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

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import alvahouse.eatool.gui.BasicDialog;
import alvahouse.eatool.gui.WrappedTextArea;
import alvahouse.eatool.repository.Repository;
import alvahouse.eatool.repository.mapping.ExportMapping;

/**
 * ExportMappingDialog is a dialog for creating or editing an export mapping.
 * 
 * @author rbp28668
 */
public class ExportMappingDialog extends BasicDialog {

    private MainPanel panel;
    private ExportMapping mapping;
    
    /**
     * Creates a new ExportMappingDialog.
     * @param parent is the parent dialog.
     * @param title is the dialog title.
     * @param mapping is the ExportMapping to edit.
     */
    public ExportMappingDialog(JDialog parent, String title, ExportMapping mapping) {
        super(parent, title);
        init(mapping);
    }

 
    /**
     * Creates a new ExportMappingDialog.
     * @param parent is the parent Component.
     * @param title is the dialog title.
     * @param mapping is the ExportMapping to edit.
     */
    public ExportMappingDialog(Component parent, String title, ExportMapping mapping) {
        super(parent, title);
        init(mapping);
    }

    /**
     * Provides the main initialisation for the constructors.
     * @param mapping is the ExportMapping being edited.
     */
    private void init(ExportMapping mapping) {
        this.mapping = mapping;
        
        panel = new MainPanel();
        getContentPane().add(panel, BorderLayout.CENTER);
        getContentPane().add(getOKCancelPanel(), BorderLayout.EAST);
        pack();
    }
    
    /* (non-Javadoc)
     * @see alvahouse.eatool.gui.BasicDialog#onOK()
     */
    protected void onOK() {
        mapping.setName(panel.getMappingName());
        mapping.setDescription(panel.getDescription());
        mapping.setTransformPath(panel.getTransform());
        mapping.setComponent(Repository.META_MODEL, panel.isMetaModel());
        mapping.setComponent(Repository.MODEL, panel.isModel());
        mapping.setComponent(Repository.DIAGRAM_TYPES, panel.isDiagramTypes());
        mapping.setComponent(Repository.DIAGRAMS, panel.isDiagrams());
        mapping.setComponent(Repository.IMPORT_MAPPINGS, panel.isImportMappings());
        mapping.setComponent(Repository.EXPORT_MAPPINGS, panel.isExportMappings());
    }

    /* (non-Javadoc)
     * @see alvahouse.eatool.gui.BasicDialog#validateInput()
     */
    protected boolean validateInput() {
        return panel.validateInput();
    }

    private class MainPanel extends JPanel {
        
        private JTextField txtName;
        private JTextArea txtDescription;
        private JTextField txtPath;
        
        private JCheckBox chkMetaModel = new JCheckBox("Meta-model");
        private JCheckBox chkModel = new JCheckBox("Model");
        private JCheckBox chkDiagramTypes = new JCheckBox("Diagram Types");
        private JCheckBox chkDiagrams = new JCheckBox("Diagrams");
        private JCheckBox chkImportMappings = new JCheckBox("Import Mappings");
        private JCheckBox chkExportMappings = new JCheckBox("Export Mappings");
        
        MainPanel() {
            
            GridBagLayout gridbag = new GridBagLayout();
            setLayout(gridbag);
            GridBagConstraints c = new GridBagConstraints();
            //c.gridwidth = GridBagConstraints.REMAINDER; //end row
            c.anchor = GridBagConstraints.WEST;//LINE_START;
            c.insets = new Insets(5,5,5,5);

            JLabel label;

            label = new JLabel("Export Name");
            //c.gridwidth = GridBagConstraints.RELATIVE;
            gridbag.setConstraints(label,c);
            add(label);
            
            txtName = new JTextField(mapping.getName());
            txtName.setColumns(40);
            c.gridwidth = GridBagConstraints.REMAINDER;
            c.weightx = 1.0;
            gridbag.setConstraints(txtName,c);
            add(txtName);

            label = new JLabel("Description");
            //c.gridwidth = GridBagConstraints.RELATIVE;
            c.gridwidth = 1;
            c.weightx = 0;
            gridbag.setConstraints(label,c);
            add(label);
            
            txtDescription = new WrappedTextArea(mapping.getDescription());
            txtDescription.setColumns(40);
            txtDescription.setRows(5);
            c.gridwidth = GridBagConstraints.REMAINDER;
            c.weightx = 1.0;
            gridbag.setConstraints(txtDescription,c);
            add(txtDescription);

            
            Box box = createOptions();
            gridbag.setConstraints(box,c);
            add(box);

            label = new JLabel("Transform");
            //c.gridwidth = GridBagConstraints.RELATIVE;
            c.gridwidth = 1;
            c.weightx = 0;
            gridbag.setConstraints(label,c);
            add(label);
            
            txtPath = new JTextField(mapping.getTransformPath());
            txtPath.setColumns(40);
            c.weightx = 1.0;
            gridbag.setConstraints(txtPath,c);
            add(txtPath);
        
            JButton browse = new JButton("...");
            c.gridwidth = GridBagConstraints.REMAINDER;
            c.weightx = 0;
            gridbag.setConstraints(browse,c);
            add(browse);
            
            browse.addActionListener( new ActionListener() {

                public void actionPerformed(ActionEvent event) {
                    JFileChooser chooser = new JFileChooser();
                    String path = txtPath.getText();
                    if(path == null || path.length()==0) 
                        chooser.setCurrentDirectory( new File("."));
                    else
                        chooser.setSelectedFile(new File(path));
                    //chooser.setFileFilter( new XMLFileFilter());

                    if( chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                        txtPath.setText(chooser.getSelectedFile().getPath());
                    }
                    
                }
                
            });

        }

        /**
         * @return
         */
        private Box createOptions() {

            chkMetaModel.setSelected(mapping.hasComponent(Repository.META_MODEL));
            chkModel.setSelected(mapping.hasComponent(Repository.MODEL));
            chkDiagramTypes.setSelected(mapping.hasComponent(Repository.DIAGRAM_TYPES));
            chkDiagrams.setSelected(mapping.hasComponent(Repository.DIAGRAMS));
            chkImportMappings.setSelected(mapping.hasComponent(Repository.IMPORT_MAPPINGS));
            chkExportMappings.setSelected(mapping.hasComponent(Repository.EXPORT_MAPPINGS));
            
            Box box = Box.createVerticalBox();
            box.add( chkMetaModel);
            box.add( chkModel);
            box.add( chkDiagramTypes);
            box.add( chkDiagrams);
            box.add( chkImportMappings);
            box.add( chkExportMappings);
            box.setBorder(new TitledBorder("Export"));
            return box;
        }

        String getMappingName() {
            return txtName.getText().trim();
        }
        
        String getDescription() {
            return txtDescription.getText().trim();
        }
        
        String getTransform() {
            String txt = txtPath.getText().trim();
            if(txt.length() == 0){
                txt = null;
            }
            return txt;
        }

        boolean isMetaModel() {
            return chkMetaModel.isSelected();
        }

        boolean isModel() {
            return chkModel.isSelected();
        }

        boolean isDiagramTypes() {
            return chkDiagramTypes.isSelected();
        }

        boolean isDiagrams() {
            return chkDiagrams.isSelected();
        }

        boolean isImportMappings() {
            return chkImportMappings.isSelected();
        }

        boolean isExportMappings() {
            return chkExportMappings.isSelected();
        }
        
        /** validates values in the panel
         * @return true if input is valid, false if invalid
         **/
        boolean validateInput() {
            if(getMappingName().length() == 0) {
                JOptionPane.showMessageDialog(this,
                    "Please enter a value for the name", 
                    "EATool", JOptionPane.INFORMATION_MESSAGE);
                txtName.requestFocus();
                return false;
            }
            return true;
        }
        
    }
    
}
