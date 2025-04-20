package com.mycompany.todolistwithtxt;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.table.*;

public class ButtonRendererEditor extends AbstractCellEditor
        implements TableCellRenderer, TableCellEditor, ActionListener {
    private final JButton button = new JButton("Delete");
    private JTable table;
    private int row;
    private ToDoListWithTxt logic = new ToDoListWithTxt();
    private ToDoListJFrame frame; // Store the JFrame instance

    public ButtonRendererEditor(JTable table, ToDoListJFrame frame) { // Accept JFrame instance
        this.table = table;
        this.frame = frame; // Assign the passed JFrame
        button.setFocusPainted(false);
        button.addActionListener(this);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {
        return button;
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value,
            boolean isSelected, int row, int column) {
        this.row = row;
        return button;
    }

    @Override
    public Object getCellEditorValue() {
        return "Delete";
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // Ambil kode tugas dari kolom kedua
        Object code = table.getValueAt(row, 1);
        // Konfirmasi penghapusan
        int confirm = JOptionPane.showConfirmDialog(table, "Yakin hapus baris ini?", "Konfirmasi",
                JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION && logic.deleteTask(code.toString())) {
            JOptionPane.showMessageDialog(null, "Berhasil hapus data: " + code);
            frame.displayTask(); // Use the existing JFrame's displayTask method
        }
        fireEditingStopped();
    }
}