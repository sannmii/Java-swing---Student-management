/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package studentmanagement;

import java.text.SimpleDateFormat;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author sandr
 */
public class MainFrame extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(MainFrame.class.getName());
 
    // DAO & helper
    private final StudentDAO dao = new StudentDAO();
    private final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
 
    // Tables
    private JTable tblStudents;
    private JTable tblList;
    private JTable tblMajor;
    private DefaultTableModel studentTableModel;
    private DefaultTableModel listTableModel;
    private DefaultTableModel majorTableModel;
 
    // State
    private int selectedStudentId = -1;
    private javax.swing.ButtonGroup statusGroup = new javax.swing.ButtonGroup();
 
    /**
     * Creates new form MainFrame
     */
    public MainFrame() {
        initComponents();
        postInit();
    }
   private void postInit() {
        // Kelompokkan radio button status
        statusGroup.add(rdoActive);
        statusGroup.add(rdoInactive);
 
        // Setup spinner sebagai date picker
        spinnerDate.setModel(new javax.swing.SpinnerDateModel());
        spinnerDate.setValue(new java.util.Date());
 
        // Setup tabel tab Main
        String[] colStudent = {"ID", "Name", "GPA", "Enroll Date", "Major"};
        studentTableModel = new DefaultTableModel(colStudent, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tblStudents = new JTable(studentTableModel);
        tblStudents.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tblStudents.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) onStudentSelected();
        });
        scrollStudents.setViewportView(tblStudents);
 
        // Setup tabel tab Search
        String[] colList = {"ID", "Name", "GPA", "Enroll Date", "Major"};
        listTableModel = new DefaultTableModel(colList, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tblList = new JTable(listTableModel);
        scrollList.setViewportView(tblList);
 
        // Setup tabel tab Major
        String[] colMajor = {"ID", "Major Name"};
        majorTableModel = new DefaultTableModel(colMajor, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tblMajor = new JTable(majorTableModel);
        
 
        // Live search
        txtListSearch.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { filterList(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { filterList(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { filterList(); }
        });
 
        // Load data awal
        loadMajorsToComboBox();
        loadStudentTable();
        loadMajorTable();
        clearForm();
    }
 
    // =========================================================
    // LOAD DATA
    // =========================================================
    private void loadStudentTable() {
        studentTableModel.setRowCount(0);
        List<Student> students = dao.getAllStudents();
        for (Student s : students) {
            studentTableModel.addRow(new Object[]{
                s.getStudentId(),
                s.getName(),
                String.format("%.2f", s.getGpa()),
                sdf.format(s.getEnrollmentDate()),
                s.getMajorName()
            });
        }
    }
 
    private void loadMajorTable() {
        majorTableModel.setRowCount(0);
        List<Major> majors = dao.getAllMajors();
        for (Major m : majors) {
            majorTableModel.addRow(new Object[]{m.getMajorId(), m.getMajorName()});
        }
    }
 
    private void loadMajorsToComboBox() {
        cmbMajor.removeAllItems();
        List<Major> majors = dao.getAllMajors();
        for (Major m : majors) {
            cmbMajor.addItem(m);
        }
    }
 
    // =========================================================
    // HELPER
    // =========================================================
    private void clearForm() {
        selectedStudentId = -1;
        txtStudentId.setText("(Auto)");
        txtName.setText("");
        txtGpa.setText("");
        spinnerDate.setValue(new java.util.Date());
        if (cmbMajor.getItemCount() > 0) cmbMajor.setSelectedIndex(0);
        cmbGender.setSelectedIndex(0);
        rdoActive.setSelected(true);
        tblStudents.clearSelection();
    }
 
    private void onStudentSelected() {
        int row = tblStudents.getSelectedRow();
        if (row < 0) return;
 
        int id = (int) studentTableModel.getValueAt(row, 0);
        selectedStudentId = id;
 
        Student s = dao.getStudentById(id);
        if (s == null) return;
 
        txtStudentId.setText(String.valueOf(s.getStudentId()));
        txtName.setText(s.getName());
        txtGpa.setText(String.valueOf(s.getGpa()));
        spinnerDate.setValue(s.getEnrollmentDate());
 
        // Pilih major yang sesuai di combobox
        for (int i = 0; i < cmbMajor.getItemCount(); i++) {
            Major m = (Major) cmbMajor.getItemAt(i);
            if (m.getMajorId() == s.getMajorId()) {
                cmbMajor.setSelectedIndex(i);
                break;
            }
        }
    }
 
    private boolean validateStudentForm() {
        if (txtName.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Full Name tidak boleh kosong!", "Validasi", JOptionPane.WARNING_MESSAGE);
            txtName.requestFocus();
            return false;
        }
        String gpaStr = txtGpa.getText().trim();
        if (gpaStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "GPA tidak boleh kosong!", "Validasi", JOptionPane.WARNING_MESSAGE);
            txtGpa.requestFocus();
            return false;
        }
        try {
            double gpa = Double.parseDouble(gpaStr);
            if (gpa < 0.0 || gpa > 4.0) {
                JOptionPane.showMessageDialog(this, "GPA harus antara 0.0 - 4.0!", "Validasi", JOptionPane.WARNING_MESSAGE);
                txtGpa.requestFocus();
                return false;
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "GPA harus berupa angka!", "Validasi", JOptionPane.WARNING_MESSAGE);
            txtGpa.requestFocus();
            return false;
        }
        if (cmbMajor.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Pilih Major terlebih dahulu!", "Validasi", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        return true;
    }
 
    private Student buildStudentFromForm() {
        Student s = new Student();
        s.setName(txtName.getText().trim());
        s.setGpa(Double.parseDouble(txtGpa.getText().trim()));
        s.setEnrollmentDate((java.util.Date) spinnerDate.getValue());
        Major selectedMajor = (Major) cmbMajor.getSelectedItem();
        s.setMajorId(selectedMajor.getMajorId());
        s.setMajorName(selectedMajor.getMajorName());
        return s;
    }
 
    private void filterList() {
        String keyword = txtListSearch.getText().trim();
        String searchType = (String) cmbListSearchType.getSelectedItem();
        listTableModel.setRowCount(0);
 
        List<Student> students = keyword.isEmpty()
                ? dao.getAllStudents()
                : dao.searchStudents(keyword);
 
        for (Student s : students) {
            boolean match = false;
            switch (searchType) {
                case "Name only":
                    match = s.getName().toLowerCase().contains(keyword.toLowerCase());
                    break;
                case "ID only":
                    match = String.valueOf(s.getStudentId()).contains(keyword);
                    break;
                default: // "Name or ID"
                    match = true;
                    break;
            }
            if (match) {
                listTableModel.addRow(new Object[]{
                    s.getStudentId(),
                    s.getName(),
                    String.format("%.2f", s.getGpa()),
                    sdf.format(s.getEnrollmentDate()),
                    s.getMajorName()
                });
            }
        }
    }
 

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        jRadioButton1 = new javax.swing.JRadioButton();
        tabbedPane = new javax.swing.JTabbedPane();
        tabManage = new javax.swing.JPanel();
        pnlForm = new javax.swing.JPanel();
        lblStudentId = new javax.swing.JLabel();
        txtStudentId = new javax.swing.JTextField();
        lblName = new javax.swing.JLabel();
        txtName = new javax.swing.JTextField();
        lblGpa = new javax.swing.JLabel();
        txtGpa = new javax.swing.JTextField();
        lblEnrollDate = new javax.swing.JLabel();
        spinnerDate = new javax.swing.JSpinner();
        lblMajor = new javax.swing.JLabel();
        cmbMajor = new javax.swing.JComboBox();
        lblGender = new javax.swing.JLabel();
        cmbGender = new javax.swing.JComboBox();
        lblStatus = new javax.swing.JLabel();
        pnlStatus = new javax.swing.JPanel();
        rdoActive = new javax.swing.JRadioButton();
        rdoInactive = new javax.swing.JRadioButton();
        pnlRight = new javax.swing.JPanel();
        scrollStudents = new javax.swing.JScrollPane();
        pnlButtons = new javax.swing.JPanel();
        btnAdd = new javax.swing.JButton();
        btnUpdate = new javax.swing.JButton();
        btnDelete = new javax.swing.JButton();
        btnClear = new javax.swing.JButton();
        tabList = new javax.swing.JPanel();
        pnlListTop = new javax.swing.JPanel();
        lblListSearchBy = new javax.swing.JLabel();
        cmbListSearchType = new javax.swing.JComboBox();
        txtListSearch = new javax.swing.JTextField();
        btnListSearch = new javax.swing.JButton();
        btnListReset = new javax.swing.JButton();
        scrollList = new javax.swing.JScrollPane();

        jRadioButton1.setText("jRadioButton1");

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        tabbedPane.setFont(new java.awt.Font("Dialog", 0, 13)); // NOI18N

        tabManage.setBackground(new java.awt.Color(236, 240, 245));
        tabManage.setLayout(new java.awt.BorderLayout());

        pnlForm.setBackground(new java.awt.Color(255, 255, 255));
        pnlForm.setBorder(javax.swing.BorderFactory.createTitledBorder("Student Information"));
        pnlForm.setLayout(new java.awt.GridBagLayout());

        lblStudentId.setText("Student ID:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(8, 10, 8, 5);
        pnlForm.add(lblStudentId, gridBagConstraints);

        txtStudentId.setEditable(false);
        txtStudentId.setBackground(new java.awt.Color(245, 245, 245));
        txtStudentId.setToolTipText("Auto-generated ID");
        txtStudentId.addActionListener(this::txtStudentIdActionPerformed);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(8, 5, 8, 10);
        pnlForm.add(txtStudentId, gridBagConstraints);

        lblName.setText("Full Name: *");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(8, 10, 8, 5);
        pnlForm.add(lblName, gridBagConstraints);

        txtName.addActionListener(this::txtNameActionPerformed);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(8, 5, 8, 10);
        pnlForm.add(txtName, gridBagConstraints);

        lblGpa.setText("GPA (0.0 - 4.0): *");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(8, 10, 8, 5);
        pnlForm.add(lblGpa, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(8, 5, 8, 10);
        pnlForm.add(txtGpa, gridBagConstraints);

        lblEnrollDate.setText("Enrollment Date: *");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(8, 10, 8, 5);
        pnlForm.add(lblEnrollDate, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(8, 5, 8, 10);
        pnlForm.add(spinnerDate, gridBagConstraints);

        lblMajor.setText("Major: *");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(8, 10, 8, 5);
        pnlForm.add(lblMajor, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(8, 5, 8, 10);
        pnlForm.add(cmbMajor, gridBagConstraints);

        lblGender.setText("Gender:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(8, 10, 8, 5);
        pnlForm.add(lblGender, gridBagConstraints);

        cmbGender.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Male", "Female", "Other" }));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(8, 5, 8, 10);
        pnlForm.add(cmbGender, gridBagConstraints);

        lblStatus.setText("Status:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(8, 10, 8, 5);
        pnlForm.add(lblStatus, gridBagConstraints);

        pnlStatus.setBackground(new java.awt.Color(255, 255, 255));
        pnlStatus.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 0, 5));

        rdoActive.setBackground(new java.awt.Color(255, 255, 255));
        rdoActive.setSelected(true);
        rdoActive.setText("Active");
        pnlStatus.add(rdoActive);

        rdoInactive.setBackground(new java.awt.Color(255, 255, 255));
        rdoInactive.setText("Inactive");
        pnlStatus.add(rdoInactive);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(8, 5, 8, 10);
        pnlForm.add(pnlStatus, gridBagConstraints);

        tabManage.add(pnlForm, java.awt.BorderLayout.WEST);

        pnlRight.setBackground(new java.awt.Color(236, 240, 245));
        pnlRight.setLayout(new java.awt.BorderLayout());
        pnlRight.add(scrollStudents, java.awt.BorderLayout.CENTER);

        pnlButtons.setBackground(new java.awt.Color(255, 255, 255));

        btnAdd.setText("Add Student");
        btnAdd.setBackground(new java.awt.Color(39, 174, 96));
        btnAdd.setForeground(new java.awt.Color(255, 255, 255));
        btnAdd.addActionListener(this::btnAddActionPerformed);
        pnlButtons.add(btnAdd);

        btnUpdate.setText("Update Student");
        btnUpdate.setBackground(new java.awt.Color(41, 128, 185));
        btnUpdate.setForeground(new java.awt.Color(255, 255, 255));
        btnUpdate.addActionListener(this::btnUpdateActionPerformed);
        pnlButtons.add(btnUpdate);

        btnDelete.setText("Delete Student");
        btnDelete.setBackground(new java.awt.Color(192, 57, 43));
        btnDelete.setForeground(new java.awt.Color(255, 255, 255));
        btnDelete.addActionListener(this::btnDeleteActionPerformed);
        pnlButtons.add(btnDelete);

        btnClear.setText("Clear Form");
        btnClear.setBackground(new java.awt.Color(127, 140, 141));
        btnClear.setForeground(new java.awt.Color(255, 255, 255));
        btnClear.addActionListener(this::btnClearActionPerformed);
        pnlButtons.add(btnClear);

        pnlRight.add(pnlButtons, java.awt.BorderLayout.SOUTH);

        tabManage.add(pnlRight, java.awt.BorderLayout.CENTER);

        tabbedPane.addTab("Main", tabManage);

        tabList.setBackground(new java.awt.Color(236, 240, 245));
        tabList.setLayout(new java.awt.BorderLayout());

        pnlListTop.setBackground(new java.awt.Color(255, 255, 255));
        pnlListTop.setBorder(javax.swing.BorderFactory.createTitledBorder("Student Directory - Search"));
        pnlListTop.setLayout(new java.awt.GridBagLayout());

        lblListSearchBy.setText("Search by:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(8, 10, 8, 5);
        pnlListTop.add(lblListSearchBy, gridBagConstraints);

        cmbListSearchType.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Name or ID", "Name only", "ID only" }));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(8, 5, 8, 5);
        pnlListTop.add(cmbListSearchType, gridBagConstraints);

        txtListSearch.setColumns(22);
        txtListSearch.setToolTipText("Type to filter instantly");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(8, 5, 8, 5);
        pnlListTop.add(txtListSearch, gridBagConstraints);

        btnListSearch.setText("Search");
        btnListSearch.setBackground(new java.awt.Color(41, 128, 185));
        btnListSearch.setForeground(new java.awt.Color(255, 255, 255));
        btnListSearch.addActionListener(this::btnListSearchActionPerformed);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(8, 5, 8, 5);
        pnlListTop.add(btnListSearch, gridBagConstraints);

        btnListReset.setText("Reset");
        btnListReset.setBackground(new java.awt.Color(127, 140, 141));
        btnListReset.setForeground(new java.awt.Color(255, 255, 255));
        btnListReset.addActionListener(this::btnListResetActionPerformed);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(8, 5, 8, 10);
        pnlListTop.add(btnListReset, gridBagConstraints);

        tabList.add(pnlListTop, java.awt.BorderLayout.NORTH);
        tabList.add(scrollList, java.awt.BorderLayout.CENTER);

        tabbedPane.addTab("Search", tabList);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 893, Short.MAX_VALUE)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addGap(0, 100, Short.MAX_VALUE)
                    .addComponent(tabbedPane, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 100, Short.MAX_VALUE)))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 416, Short.MAX_VALUE)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addGap(0, 4, Short.MAX_VALUE)
                    .addComponent(tabbedPane, javax.swing.GroupLayout.PREFERRED_SIZE, 407, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 5, Short.MAX_VALUE)))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnListResetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnListResetActionPerformed
        // TODO add your handling code here:

        txtListSearch.setText("");
        listTableModel.setRowCount(0);
        List<Student> students = dao.getAllStudents();
        for (Student s : students) {
            listTableModel.addRow(new Object[]{
                s.getStudentId(),
                s.getName(),
                String.format("%.2f", s.getGpa()),
                sdf.format(s.getEnrollmentDate()),
                s.getMajorName()
            });
        }
    }//GEN-LAST:event_btnListResetActionPerformed

    private void btnListSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnListSearchActionPerformed
        // TODO add your handling code here:
        filterList();
    }//GEN-LAST:event_btnListSearchActionPerformed

    private void btnClearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnClearActionPerformed
        // TODO add your handling code here:
        clearForm();
    }//GEN-LAST:event_btnClearActionPerformed

    private void btnDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteActionPerformed
        // TODO add your handling code here:
        if (selectedStudentId < 0) {
            JOptionPane.showMessageDialog(this, "Pilih student yang ingin dihapus!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this, "Yakin ingin menghapus student ini?",
                "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = dao.deleteStudent(selectedStudentId);
            if (success) {
                JOptionPane.showMessageDialog(this, "Student berhasil dihapus!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
                loadStudentTable();
                clearForm();
            } else {
                JOptionPane.showMessageDialog(this, "Gagal menghapus student.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
  
    
    }//GEN-LAST:event_btnDeleteActionPerformed

    private void btnUpdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUpdateActionPerformed
        // TODO add your handling code here:
        if (selectedStudentId < 0) {
            JOptionPane.showMessageDialog(this, "Pilih student yang ingin diupdate dari tabel!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (!validateStudentForm()) return;

        Student s = buildStudentFromForm();
        s.setStudentId(selectedStudentId);
        boolean success = dao.updateStudent(s);

        if (success) {
            JOptionPane.showMessageDialog(this, "Student berhasil diupdate!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
            loadStudentTable();
            clearForm();
        } else {
            JOptionPane.showMessageDialog(this, "Gagal mengupdate student. Cek koneksi database.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btnUpdateActionPerformed

    private void btnAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddActionPerformed
        // TODO add your handling code here:
        if (!validateStudentForm()) return;

        Student s = buildStudentFromForm();
        boolean success = dao.addStudent(s);

        if (success) {
            JOptionPane.showMessageDialog(this, "Student berhasil ditambahkan!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
            loadStudentTable();
            clearForm();
        } else {
            JOptionPane.showMessageDialog(this, "Gagal menambahkan student. Cek koneksi database.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btnAddActionPerformed

    private void txtNameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtNameActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtNameActionPerformed

    private void txtStudentIdActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtStudentIdActionPerformed
        // TODO add your handling code here:

    }//GEN-LAST:event_txtStudentIdActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException ex) {
            logger.log(java.util.logging.Level.SEVERE, null, ex);
        }
 
        java.awt.EventQueue.invokeLater(() -> new MainFrame().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAdd;
    private javax.swing.JButton btnClear;
    private javax.swing.JButton btnDelete;
    private javax.swing.JButton btnListReset;
    private javax.swing.JButton btnListSearch;
    private javax.swing.JButton btnUpdate;
    private javax.swing.JComboBox cmbGender;
    private javax.swing.JComboBox cmbListSearchType;
    private javax.swing.JComboBox cmbMajor;
    private javax.swing.JRadioButton jRadioButton1;
    private javax.swing.JLabel lblEnrollDate;
    private javax.swing.JLabel lblGender;
    private javax.swing.JLabel lblGpa;
    private javax.swing.JLabel lblListSearchBy;
    private javax.swing.JLabel lblMajor;
    private javax.swing.JLabel lblName;
    private javax.swing.JLabel lblStatus;
    private javax.swing.JLabel lblStudentId;
    private javax.swing.JPanel pnlButtons;
    private javax.swing.JPanel pnlForm;
    private javax.swing.JPanel pnlListTop;
    private javax.swing.JPanel pnlRight;
    private javax.swing.JPanel pnlStatus;
    private javax.swing.JRadioButton rdoActive;
    private javax.swing.JRadioButton rdoInactive;
    private javax.swing.JScrollPane scrollList;
    private javax.swing.JScrollPane scrollStudents;
    private javax.swing.JSpinner spinnerDate;
    private javax.swing.JPanel tabList;
    private javax.swing.JPanel tabManage;
    private javax.swing.JTabbedPane tabbedPane;
    private javax.swing.JTextField txtGpa;
    private javax.swing.JTextField txtListSearch;
    private javax.swing.JTextField txtName;
    private javax.swing.JTextField txtStudentId;
    // End of variables declaration//GEN-END:variables
}
