package test.gui;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.sql.Connection;
import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author ArcherL
 */
public class AlgoResult extends javax.swing.JFrame {

    /**
     * Creates new form AlgoResult
     */
    public AlgoResult() {
        initComponents();
        reviewData();
        displayNames();
        algo();
        Show_Users_In_JTable2();
    }

    public ArrayList<User> getUser1() {
        ArrayList<User> usersList = new ArrayList<>();

        NewJFrame jf = new NewJFrame();
        Connection connect = jf.getConnect();
        try {
            String query = "SELECT  uc.User_id\n"
                    + "     , uc.User_name\n"
                    + "     , ue.amount_spent\n"
                    + "  FROM user_catalogue   AS uc\n"
                    + "  JOIN user_contact     AS um\n"
                    + "    ON uc.User_name = um.User_name\n"
                    + "  JOIN user_expenditure AS ue\n"
                    + "    ON ue.mobile_no = um.mobile_no\n"
                    + " GROUP BY User_id";

            Statement st = connect.createStatement();
            ResultSet rs = st.executeQuery(query);
            User user1;
            while (rs.next()) {
                user1 = new User(rs.getInt("User_id"), rs.getString("User_name"), rs.getFloat("amount_spent"));
                usersList.add(user1);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, ex);
        }
        return usersList;
    }

    public void Show_Users_In_JTable2() {
        ArrayList<User> list = getUser1();
        DefaultTableModel model = (DefaultTableModel) jTable2.getModel();
        Object[] row = new Object[3];
        for (int i = 0; i < list.size(); i++) {
            row[0] = list.get(i).getId();
            row[1] = list.get(i).getName();
            row[2] = list.get(i).getAmountSeeker();

            model.addRow(row);
        }
    }

    public void algo() {
        int count = 0;
        float average = 0;
        NewJFrame jf = new NewJFrame();
        Connection connect = jf.getConnect();
        {
            String query = "SELECT count(ue.amount_spent) as c,avg(ue.amount_spent) as avvg\n"
                    + "FROM user_catalogue   AS uc\n"
                    + "JOIN user_contact     AS um\n"
                    + "ON uc.User_name = um.User_name\n"
                    + "JOIN user_expenditure AS ue\n"
                    + "ON ue.mobile_no = um.mobile_no\n"
                    + "order by ue.amount_spent desc;";
            try {
                Statement st = connect.createStatement();
                ResultSet rs = st.executeQuery(query);
                rs.next();
                count = rs.getInt("c");
                average = rs.getFloat("avvg");

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, ex);
            }
        }

        String query = "SELECT uc.User_name,ue.amount_spent\n"
                + "FROM user_catalogue   AS uc\n"
                + "JOIN user_contact     AS um\n"
                + "ON uc.User_name = um.User_name\n"
                + "JOIN user_expenditure AS ue\n"
                + "ON ue.mobile_no = um.mobile_no\n"
                + "order by ue.amount_spent desc";
        try {
            Statement st = connect.createStatement();
            ResultSet rs = st.executeQuery(query);
            String[] names = new String[100];
            float[] amount = new float[100];
            for (int i = 0; i < count; i++) {
                rs.next();
                names[i] = (rs.getString("User_name"));
                amount[i] = average - (rs.getFloat("amount_spent"));
            }
            // checking purpose
            //  for(int i=0;i<count;i++){
            //  System.out.println(amount[i]);
            // }
            int pos = 0, ifcount = 0;
            float val = 0;
            for (int i = 0; i < amount.length; i++) {
                if (amount[i] == 0) {
                    ifcount++;
                }
                if (count == ifcount) {
                    jTextArea1.append("No one else ows anyone anything \n");
                }
                if (amount[i] <= 0 && amount[i + 1] > 0) {
                    if (amount[i] == 0) {
                        pos = i;
                    } else {
                        pos = i + 1;
                    }
                    break;
                }
            }
            // System.out.println(pos);
            int i = 0, p = 0, j = count - 1;
            while (i < pos && j >= pos) {
                if (Math.abs(amount[i]) == Math.abs(amount[j])) {
                    if (amount[i] != 0 && amount[j] != 0) {
                        jTextArea1.append(names[j] + " pays Rs: " + (int) amount[j] + " to " + names[i] + "\n");
                        amount[i] = 0;
                    }
                    amount[j] = 0;
                    i++;
                    j--;
                    p++;
                } else {
                    val = amount[i] + amount[j];
                    if (val < 0) {
                        jTextArea1.append(names[j] + " pays Rs: " + (int) amount[j] + " to " + names[i] + "\n");
                        amount[i] = val;
                        amount[j] = 0;
                        j--;
                        p++;
                    } else if (val > 0) {
                        jTextArea1.append(names[j] + " pays Rs: " + Math.abs((int) amount[i]) + " to " + names[i] + "\n");
                        amount[j] = val;
                        amount[i] = 0;
                        i++;
                        p++;
                    }
                }
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, ex);
        }

    }

    public void reviewData() {
        NewJFrame jf = new NewJFrame();
        Connection connect = jf.getConnect();
        try {
            CallableStatement cStmt = connect.prepareCall("{call algoRun(?,?,?,?)}");

            cStmt.registerOutParameter(1, java.sql.Types.FLOAT);
            cStmt.registerOutParameter(2, java.sql.Types.FLOAT);
            cStmt.registerOutParameter(3, java.sql.Types.FLOAT);
            cStmt.registerOutParameter(4, java.sql.Types.FLOAT);

            cStmt.executeUpdate();

            // this gave me hard time bC 
            jLabelHe.setText("Rs. " + String.valueOf(cStmt.getFloat(1)));
            jLabelLe.setText("Rs. " + String.valueOf(cStmt.getFloat(2)));
            jLabelTe.setText("Rs. " + String.valueOf(cStmt.getFloat(3)));
            jLabelPpe.setText("Rs. " + String.valueOf(cStmt.getFloat(4)));

        } catch (SQLException ex) {
            Logger.getLogger(AlgoResult.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void displayNames() {
        NewJFrame jf = new NewJFrame();
        Connection connect = jf.getConnect();

        // annonyous block 1
        {
            String query = "SELECT uc.User_name\n"
                    + "FROM user_catalogue   AS uc\n"
                    + "JOIN user_contact     AS um\n"
                    + "ON uc.User_name = um.User_name\n"
                    + "JOIN user_expenditure AS ue\n"
                    + "ON ue.mobile_no = um.mobile_no\n"
                    + "WHERE ue.amount_spent=(select min(amount_spent) from user_expenditure);";
            try {
                Statement st = connect.createStatement();
                ResultSet rs = st.executeQuery(query);
                while (rs.next()) {;
                    jLabelLeName.setText(jLabelLeName.getText() + " " + rs.getString("User_Name"));
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, ex);
            }
        }
        {
            String query = "SELECT uc.User_name\n"
                    + "FROM user_catalogue   AS uc\n"
                    + "JOIN user_contact     AS um\n"
                    + "ON uc.User_name = um.User_name\n"
                    + "JOIN user_expenditure AS ue\n"
                    + "ON ue.mobile_no = um.mobile_no\n"
                    + "WHERE ue.amount_spent=(select max(amount_spent) from user_expenditure);";
            try {
                Statement st = connect.createStatement();
                ResultSet rs = st.executeQuery(query);
                while (rs.next()) {;
                    jLabelHeName.setText(jLabelHeName.getText() + " " + rs.getString("User_Name"));
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, ex);
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

        jPanel1 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jLabelHe = new javax.swing.JLabel();
        jLabelHeName = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabelLe = new javax.swing.JLabel();
        jLabelLeName = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabelTe = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabelPpe = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable2 = new javax.swing.JTable();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(0, 102, 204));

        jLabel2.setFont(new java.awt.Font("Arial", 3, 18)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setText("Highest Expenditure ");

        jLabelHe.setBackground(new java.awt.Color(255, 255, 255));
        jLabelHe.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jLabelHe.setText("Rs: ");

        jLabelHeName.setFont(new java.awt.Font("Arial Black", 0, 14)); // NOI18N
        jLabelHeName.setForeground(new java.awt.Color(255, 255, 255));
        jLabelHeName.setText("By:");

        jLabel6.setFont(new java.awt.Font("Arial", 3, 18)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(255, 255, 255));
        jLabel6.setText("Least Expenditure");

        jLabelLe.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jLabelLe.setText("Rs");

        jLabelLeName.setFont(new java.awt.Font("Arial Black", 0, 14)); // NOI18N
        jLabelLeName.setForeground(new java.awt.Color(255, 255, 255));
        jLabelLeName.setText("By:");

        jLabel10.setFont(new java.awt.Font("Arial", 3, 18)); // NOI18N
        jLabel10.setForeground(new java.awt.Color(255, 255, 255));
        jLabel10.setText("Total Expenditure");

        jLabelTe.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jLabelTe.setText("Rs:");

        jLabel12.setFont(new java.awt.Font("Arial", 3, 18)); // NOI18N
        jLabel12.setForeground(new java.awt.Color(255, 255, 255));
        jLabel12.setText("Per Person Expenditure");

        jLabelPpe.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jLabelPpe.setText("Rs:");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel2)
                                .addGap(53, 53, 53))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel6)
                                    .addComponent(jLabel10))
                                .addGap(77, 77, 77)))
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabelLe)
                            .addComponent(jLabelTe)
                            .addComponent(jLabelHe)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel12)
                        .addGap(29, 29, 29)
                        .addComponent(jLabelPpe)))
                .addGap(0, 68, Short.MAX_VALUE))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(85, 85, 85)
                        .addComponent(jLabelHeName))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(84, 84, 84)
                        .addComponent(jLabelLeName)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(131, 131, 131)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jLabelHe))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabelHeName)
                .addGap(27, 27, 27)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(jLabelLe))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabelLeName)
                .addGap(39, 39, 39)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel10)
                    .addComponent(jLabelTe))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabelPpe)
                    .addComponent(jLabel12))
                .addContainerGap(140, Short.MAX_VALUE))
        );

        jTable2.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "User Id", "User Name", "Amount Spent"
            }
        ));
        jScrollPane1.setViewportView(jTable2);

        jPanel2.setBackground(new java.awt.Color(204, 204, 255));

        jLabel1.setFont(new java.awt.Font("Arial", 1, 24)); // NOI18N
        jLabel1.setText("Review Data");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(136, 136, 136)
                .addComponent(jLabel1)
                .addContainerGap(217, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addComponent(jLabel1)
                .addContainerGap(24, Short.MAX_VALUE))
        );

        jTextArea1.setBackground(new java.awt.Color(204, 204, 255));
        jTextArea1.setColumns(20);
        jTextArea1.setRows(5);
        jScrollPane2.setViewportView(jTextArea1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 341, Short.MAX_VALUE)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                        .addGap(83, 83, 83))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 235, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(AlgoResult.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(AlgoResult.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(AlgoResult.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(AlgoResult.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new AlgoResult().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabelHe;
    private javax.swing.JLabel jLabelHeName;
    private javax.swing.JLabel jLabelLe;
    private javax.swing.JLabel jLabelLeName;
    private javax.swing.JLabel jLabelPpe;
    private javax.swing.JLabel jLabelTe;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable jTable2;
    private javax.swing.JTextArea jTextArea1;
    // End of variables declaration//GEN-END:variables
}
