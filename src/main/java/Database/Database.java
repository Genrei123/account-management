/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Database;
import java.sql.*;
import java.time.LocalDate;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
/**
 *
 * @author User
 */
public class Database {
    private static final String URL = "jdbc:mysql://localhost:3306/accounting";
    private static final String USER = "root";
    private static final String PASSWORD = "";
    
    public static Connection getConnect() throws SQLException {
    
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
    
    public boolean login(String username, String password) {
        try (Connection conn = getConnect()) {
            String query = "SELECT * FROM users WHERE username = ? AND password = ?";
            PreparedStatement pstm = conn.prepareStatement(query);
            pstm.setString(1, username);
            pstm.setString(2, password);
            
            ResultSet rs = pstm.executeQuery();
            
            if (rs.next()) {
                return true;
            } else {
                return false;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        
        return false;
        
    }
    
    public void addEmployee(String name, String address, Double salary, String date) throws SQLException {
        try (Connection conn = getConnect()) {
            String query = "INSERT INTO payroll (date, employee_name, salary) VALUES (?, ?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setString(1, date);
                pstmt.setString(2, name);
                pstmt.setDouble(3, salary);
                
                pstmt.executeUpdate();
                
                
                
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }
    
    public void addInventory(String item_name, LocalDate date, int stock, Double cost, String account_no) throws SQLException {
        int inventoryId = 0;
        try (Connection conn = getConnect()) {
            String query = "INSERT INTO inventory (item_name, date, purchased, sold, cost, stock) VALUES (?, ?, ?, ?, ?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
                pstmt.setString(1, item_name);
                pstmt.setObject(2, date);
                pstmt.setInt(3, stock);
                pstmt.setInt(4, 0);
                pstmt.setDouble(5, cost);
                pstmt.setDouble(6, stock);
                
                pstmt.executeUpdate();
                
                
                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    inventoryId = rs.getInt(1);
                }
                
            } 
            
            String addLedger = "INSERT INTO ledger (date, account_no, inventory_id) VALUES (?, ?, ?) ";
            try (PreparedStatement prepared = conn.prepareStatement(addLedger)) {
                prepared.setObject(1, date);
                prepared.setString(2, account_no);
                prepared.setInt(3, inventoryId);
                
                prepared.executeUpdate();
            }
        }
    }
    
    
    
    
    
    public void displayTable(JTable table, String table_name) {
        try (Connection conn = getConnect()) {
            String sql = "SELECT * FROM " + table_name;
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            // Get metadata from the ResultSet
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();

            // Create column names
            String[] columnNames = new String[columnCount];
            for (int i = 1; i <= columnCount; i++) {
                columnNames[i - 1] = metaData.getColumnName(i);
            }

            // Create a DefaultTableModel with column names and no data
            DefaultTableModel model = new DefaultTableModel(columnNames, 0);

            // Add rows from the ResultSet
            while (rs.next()) {
                Object[] rowData = new Object[columnCount];
                for (int i = 1; i <= columnCount; i++) {
                    rowData[i - 1] = rs.getObject(i);
                }
                model.addRow(rowData);
            }

            // Set the model to the JTable
            table.setModel(model);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public void displayInventory(JTable table, String table_name, int id) {
        try (Connection conn = getConnect()) {
            String sql = "SELECT * FROM " + table_name + " WHERE inventory_id = " + id;
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            // Get metadata from the ResultSet
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();

            // Create column names
            String[] columnNames = new String[columnCount];
            for (int i = 1; i <= columnCount; i++) {
                columnNames[i - 1] = metaData.getColumnName(i);
            }

            // Create a DefaultTableModel with column names and no data
            DefaultTableModel model = new DefaultTableModel(columnNames, 0);

            // Add rows from the ResultSet
            while (rs.next()) {
                Object[] rowData = new Object[columnCount];
                for (int i = 1; i <= columnCount; i++) {
                    rowData[i - 1] = rs.getObject(i);
                }
                model.addRow(rowData);
            }

            // Set the model to the JTable
            table.setModel(model);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public void displayLedger(JTable table, int id) {
        try (Connection conn = getConnect()) {
            String sql = "SELECT inv.id as inventory_id, inv.item_name, inv.cost, led.debit, led.credit FROM inventory inv JOIN ledger led ON inv.id = led.inventory_id WHERE inv.id = " + id;
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            // Get metadata from the ResultSet
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();

            // Create column names
            String[] columnNames = new String[columnCount];
            for (int i = 1; i <= columnCount; i++) {
                columnNames[i - 1] = metaData.getColumnName(i);
            }

            // Create a DefaultTableModel with column names and no data
            DefaultTableModel model = new DefaultTableModel(columnNames, 0);

            // Add rows from the ResultSet
            while (rs.next()) {
                Object[] rowData = new Object[columnCount];
                for (int i = 1; i <= columnCount; i++) {
                    rowData[i - 1] = rs.getObject(i);
                }
                model.addRow(rowData);
            }

            // Set the model to the JTable
            table.setModel(model);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    
    
    
    public void deleteSelectedRows(JTable table, String tableName) {
        // Get the selected rows
        int[] selectedRows = table.getSelectedRows();

        if (selectedRows.length == 0) {
            // No rows selected
            return;
        }

        // Confirm deletion
        int response = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete the selected rows?", "Confirm Deletion", JOptionPane.YES_NO_OPTION);
        if (response != JOptionPane.YES_OPTION) {
            return;
        }

        // Get the table model
        DefaultTableModel model = (DefaultTableModel) table.getModel();

        // Delete each selected row from the database
        try (Connection conn = getConnect()) {
            String sql = "DELETE FROM " + tableName +" WHERE id = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);

            for (int rowIndex : selectedRows) {
                // Assuming the ID column is in the 1st column (index 1) of the JTable
                Integer id = (Integer) model.getValueAt(rowIndex, 0);

                pstmt.setInt(1, id);
                pstmt.executeUpdate();
            }

            // Remove rows from the table model
            for (int i = selectedRows.length - 1; i >= 0; i--) {
                model.removeRow(selectedRows[i]);
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        // Optionally, refresh the table to reflect changes from the database
        // displayTable(table, "your_table_name"); // Uncomment if you want to reload the table
    }
    
    public void releaseSalaries(JTable table) {
        // Get the selected rows
        int[] selectedRows = table.getSelectedRows();

        if (selectedRows.length == 0) {
            // No rows selected
            return;
        }

        // Confirm release
        int response = JOptionPane.showConfirmDialog(null, "Are you sure you want to release the salaries for the selected rows?", "Confirm Release", JOptionPane.YES_NO_OPTION);
        if (response != JOptionPane.YES_OPTION) {
            return;
        }

        // Get the table model
        DefaultTableModel model = (DefaultTableModel) table.getModel();

        // Update status for each selected row
        try (Connection conn = getConnect()) {
            String sql = "UPDATE payroll SET status = ? WHERE id = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);

            for (int rowIndex : selectedRows) {
                // Assuming the ID column is in the 5th column (index 4) of the JTable
                Integer id = (Integer) model.getValueAt(rowIndex, 4);

                pstmt.setString(1, "RELEASED");
                pstmt.setInt(2, id);
                pstmt.executeUpdate();
            }

            // Optionally, refresh the table to reflect changes from the database
            // displayTable(table, "your_table_name"); // Uncomment if you want to reload the table

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
  
}
