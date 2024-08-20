/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Database;
import java.sql.*;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
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
    
}
