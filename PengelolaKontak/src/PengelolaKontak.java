import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */

/**
 *
 * @author Pongo
 */
public class PengelolaKontak extends javax.swing.JFrame {
    private Connection conn;

    /**
     * Creates new form PengelolaKontak
     */
        public PengelolaKontak() {
            initComponents();
            connect();
            loadData();
        }
        private void connect() {
        try {
            conn = connectDB.getConnection();
            if (conn != null) {
                System.out.println("Koneksi ke database berhasil.");
            } else {
                JOptionPane.showMessageDialog(this, "Koneksi ke database gagal.");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Koneksi gagal: " + e.getMessage());
        }
    }

        private void loadData() {
            DefaultTableModel model = (DefaultTableModel) tabelKontak.getModel();
            model.setRowCount(0); // Kosongkan tabel
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT * FROM pengelola_kontak")) {
                while (rs.next()) {
                    model.addRow(new Object[]{
                        rs.getString("nama"),
                        rs.getString("telepon"),
                        rs.getString("kategori")
                    });
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Gagal memuat data: " + e.getMessage());
            }
        }

        private void cariData() {
            String keyword = txtCari.getText().trim(); // Ambil teks dari field pencarian
            DefaultTableModel model = (DefaultTableModel) tabelKontak.getModel();
            model.setRowCount(0); // Kosongkan tabel sebelum menampilkan hasil pencarian

            String sql = "SELECT * FROM pengelola_kontak WHERE nama LIKE ? OR telepon LIKE ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, "%" + keyword + "%");
                pstmt.setString(2, "%" + keyword + "%");

                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        model.addRow(new Object[]{
                            rs.getString("nama"),
                            rs.getString("telepon"),
                            rs.getString("kategori")
                        });
                    }
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Pencarian gagal: " + e.getMessage());
            }
        }

        private void kategori() {
            String category = (String) comboKategori.getSelectedItem(); // Mengambil kategori yang dipilih
            DefaultTableModel model = (DefaultTableModel) tabelKontak.getModel();
            model.setRowCount(0); // Kosongkan tabel sebelum menampilkan data yang difilter

            String sql;

            try (PreparedStatement pstmt = conn.prepareStatement(
                category.equals("-- Pilih --") 
                ? "SELECT * FROM pengelola_kontak" // Jika kategori adalah "-- Pilih --", ambil semua data
                : "SELECT * FROM pengelola_kontak WHERE kategori = ?" // Jika kategori spesifik, filter berdasarkan kategori
            )) {
                if (!category.equals("-- Pilih --")) {
                    pstmt.setString(1, category); // Set kategori untuk filter
                }

                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        model.addRow(new Object[]{
                            rs.getString("nama"),
                            rs.getString("telepon"),
                            rs.getString("kategori")
                        });
                    }
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Gagal memuat data: " + e.getMessage());
            }
        }


        private void simpanData(){
            String nama = txtNama.getText().trim();
            String nomorTelepon = txtTelepon.getText().trim();
            String kategori = comboKategori.getSelectedItem().toString();

            // Validasi input
            if (nama.isEmpty() || nomorTelepon.isEmpty() || kategori.equals("-- Pilih Kategori --")) {
                JOptionPane.showMessageDialog(this, "Mohon lengkapi semua data.");
                return;
            }

            // Validasi nomor telepon
            if (!nomorTelepon.matches("\\d+")) {
                JOptionPane.showMessageDialog(this, "Nomor telepon hanya boleh berisi angka.");
                return;
            }

            if (nomorTelepon.length() < 10 || nomorTelepon.length() > 13) {
                JOptionPane.showMessageDialog(this, "Nomor telepon harus terdiri dari 10 hingga 13 digit.");
                return;
            }

            try (PreparedStatement pstmt = conn.prepareStatement("INSERT INTO pengelola_kontak (nama, telepon, kategori) VALUES (?, ?, ?)")) {
                pstmt.setString(1, nama);
                pstmt.setString(2, nomorTelepon);
                pstmt.setString(3, kategori);
                pstmt.executeUpdate();
                JOptionPane.showMessageDialog(this, "Data berhasil disimpan.");

                // Memuat ulang data dari database ke tabel GUI
                loadData();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Gagal menyimpan data: " + e.getMessage());
            }

        }
        private void ubahData(){
            int selectedRow = tabelKontak.getSelectedRow(); // Mengambil baris yang dipilih dari tabel
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Pilih kontak yang ingin diubah.");
                return;
            }

            String nama = txtNama.getText().trim(); // Mengambil nilai dari field input nama
            String nomorTelepon = txtTelepon.getText().trim(); // Mengambil nilai dari field input telepon
            String kategori = comboKategori.getSelectedItem().toString(); // Mengambil kategori yang dipilih
            String selectedName = tabelKontak.getValueAt(selectedRow, 0).toString(); // Mengambil nama dari tabel pada baris yang dipilih

            // Validasi input
            if (nama.isEmpty() || nomorTelepon.isEmpty() || kategori.equals("- Pilih -")) {
                JOptionPane.showMessageDialog(this, "Mohon lengkapi semua data.");
                return;
            }

            // Validasi nomor telepon
            if (!nomorTelepon.matches("\\d+")) {
                JOptionPane.showMessageDialog(this, "Nomor telepon hanya boleh berisi angka.");
                return;
            }

            if (nomorTelepon.length() < 10 || nomorTelepon.length() > 13) {
                JOptionPane.showMessageDialog(this, "Nomor telepon harus terdiri dari 10 hingga 13 digit.");
                return;
            }

            try (PreparedStatement pstmt = conn.prepareStatement(
                    "UPDATE pengelola_kontak SET nama = ?, telepon = ?, kategori = ? WHERE nama = ?")) {
                pstmt.setString(1, nama); // Set nama baru
                pstmt.setString(2, nomorTelepon); // Set nomor telepon baru
                pstmt.setString(3, kategori); // Set kategori baru
                pstmt.setString(4, selectedName); // Set nama lama untuk identifikasi
                pstmt.executeUpdate(); // Eksekusi perintah UPDATE
                JOptionPane.showMessageDialog(this, "Data berhasil diubah.");
                loadData(); // Muat ulang data untuk memperbarui tabel di GUI
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Gagal mengubah data: " + e.getMessage());
            }

        }
        private void hapusData(){
            int selectedRow = tabelKontak.getSelectedRow(); // Mengambil baris yang dipilih dari tabel
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Pilih kontak yang ingin dihapus.");
                return;
            }

            // Mengambil nama dari baris yang dipilih untuk dihapus
            String selectedName = tabelKontak.getValueAt(selectedRow, 0).toString();

            int confirm = JOptionPane.showConfirmDialog(
                    this, 
                    "Apakah Anda yakin ingin menghapus kontak dengan nama \"" + selectedName + "\"?", 
                    "Konfirmasi Hapus", 
                    JOptionPane.YES_NO_OPTION
            );

            if (confirm == JOptionPane.YES_OPTION) { // Jika pengguna mengkonfirmasi penghapusan
                try (PreparedStatement pstmt = conn.prepareStatement("DELETE FROM pengelola_kontak WHERE nama = ?")) {
                    pstmt.setString(1, selectedName); // Mengatur nama sebagai parameter untuk query DELETE
                    pstmt.executeUpdate(); // Eksekusi perintah DELETE
                    JOptionPane.showMessageDialog(this, "Data berhasil dihapus.");
                    loadData(); // Muat ulang data untuk memperbarui tabel di GUI
                } catch (SQLException e) {
                    JOptionPane.showMessageDialog(this, "Gagal menghapus data: " + e.getMessage());
                }
            }

        }
        private void batal(){
            txtNama.setText(""); 
            txtTelepon.setText("");
            comboKategori.setSelectedIndex(0);
            txtCari.setText("");
        }
        private void impor() {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Pilih File CSV");
            fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("CSV Files", "csv"));
            int result = fileChooser.showOpenDialog(this);

            if (result == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();

                try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                    DefaultTableModel model = (DefaultTableModel) tabelKontak.getModel();
                    model.setRowCount(0); // Kosongkan tabel sebelum mengimpor data

                    String line = reader.readLine(); // Lewati baris header
                    String sql = "INSERT INTO pengelola_kontak (nama, telepon, kategori) VALUES (?, ?, ?)";
                    conn.setAutoCommit(false); // Gunakan transaksi untuk impor data

                    try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                        while ((line = reader.readLine()) != null) {
                            String[] data = line.split(","); // Pisahkan berdasarkan koma
                            if (data.length == model.getColumnCount()) {
                                // Tambahkan ke tabel
                                model.addRow(data);

                                // Tambahkan ke database
                                pstmt.setString(1, data[0]); // Nama
                                pstmt.setString(2, data[1]); // Telepon
                                pstmt.setString(3, data[2]); // Kategori
                                pstmt.addBatch(); // Tambahkan ke batch
                            }
                        }

                        pstmt.executeBatch(); // Eksekusi semua pernyataan batch
                        conn.commit(); // Commit transaksi
                        JOptionPane.showMessageDialog(this, "Data berhasil diimpor dari file CSV dan disimpan ke database.");
                    } catch (SQLException e) {
                        conn.rollback(); // Batalkan transaksi jika ada kesalahan
                        JOptionPane.showMessageDialog(this, "Gagal menyimpan data ke database: " + e.getMessage());
                    } finally {
                        conn.setAutoCommit(true); // Kembalikan ke mode auto-commit
                    }
                } catch (IOException e) {
                    JOptionPane.showMessageDialog(this, "Terjadi kesalahan saat mengimpor data: " + e.getMessage());
                } catch (SQLException e) {
                    JOptionPane.showMessageDialog(this, "Kesalahan koneksi database: " + e.getMessage());
                }
            }
        }


        private void ekspor() {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Simpan File CSV");
            fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("CSV Files", "csv"));
            int result = fileChooser.showSaveDialog(this);

            if (result == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                if (!file.getName().endsWith(".csv")) {
                    file = new File(file.getAbsolutePath() + ".csv");
                }

                try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                    DefaultTableModel model = (DefaultTableModel) tabelKontak.getModel();

                    // Tulis header (kolom)
                    for (int i = 0; i < model.getColumnCount(); i++) {
                        writer.write(model.getColumnName(i));
                        if (i < model.getColumnCount() - 1) {
                            writer.write(",");
                        }
                    }
                    writer.newLine();

                    // Tulis data baris per baris
                    for (int i = 0; i < model.getRowCount(); i++) {
                        for (int j = 0; j < model.getColumnCount(); j++) {
                            writer.write(model.getValueAt(i, j).toString());
                            if (j < model.getColumnCount() - 1) {
                                writer.write(",");
                            }
                        }
                        writer.newLine();
                    }

                    JOptionPane.showMessageDialog(this, "Data berhasil diekspor ke file CSV.");
                } catch (IOException e) {
                    JOptionPane.showMessageDialog(this, "Terjadi kesalahan saat mengekspor data: " + e.getMessage());
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
        jLabel1 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        txtNama = new javax.swing.JTextField();
        txtTelepon = new javax.swing.JTextField();
        comboKategori = new javax.swing.JComboBox<>();
        btnHapus = new javax.swing.JButton();
        btnSimpan = new javax.swing.JButton();
        btnEdit = new javax.swing.JButton();
        btnBatal = new javax.swing.JButton();
        btnImpor = new javax.swing.JButton();
        btnEkspor = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tabelKontak = new javax.swing.JTable();
        jLabel5 = new javax.swing.JLabel();
        txtCari = new javax.swing.JTextField();
        btnCari = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel1.setText("Aplikasi Pengelola Kontak");
        jPanel1.add(jLabel1);

        getContentPane().add(jPanel1, java.awt.BorderLayout.NORTH);

        jLabel2.setText("Kategori");

        jLabel3.setText("Nama");

        jLabel4.setText("Telepon");

        txtTelepon.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtTeleponKeyTyped(evt);
            }
        });

        comboKategori.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "-- Pilih --", "Keluarga", "Teman", "Kerja" }));
        comboKategori.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                comboKategoriActionPerformed(evt);
            }
        });

        btnHapus.setText("Hapus");
        btnHapus.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnHapusActionPerformed(evt);
            }
        });

        btnSimpan.setText("Simpan");
        btnSimpan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSimpanActionPerformed(evt);
            }
        });

        btnEdit.setText("Ubah");
        btnEdit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEditActionPerformed(evt);
            }
        });

        btnBatal.setText("Batal");
        btnBatal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBatalActionPerformed(evt);
            }
        });

        btnImpor.setText("Impor");
        btnImpor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnImporActionPerformed(evt);
            }
        });

        btnEkspor.setText("Ekspor");
        btnEkspor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEksporActionPerformed(evt);
            }
        });

        tabelKontak.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Nama", "Telepon", "Ketegori"
            }
        ));
        jScrollPane1.setViewportView(tabelKontak);

        jLabel5.setText("Cari Nama :");

        btnCari.setText("Cari");
        btnCari.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCariActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(txtTelepon, javax.swing.GroupLayout.DEFAULT_SIZE, 162, Short.MAX_VALUE)
                            .addComponent(txtNama)
                            .addComponent(comboKategori, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(btnSimpan)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnBatal)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnEdit)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnHapus)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 403, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGap(0, 46, Short.MAX_VALUE)
                                .addComponent(btnImpor)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnEkspor))
                            .addComponent(txtCari, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btnCari))
                        .addGap(27, 27, 27))))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(txtNama, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(txtTelepon, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2)
                    .addComponent(comboKategori, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(btnHapus)
                        .addComponent(btnImpor)
                        .addComponent(btnEkspor))
                    .addComponent(btnSimpan)
                    .addComponent(btnEdit)
                    .addComponent(btnBatal))
                .addGap(27, 27, 27)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtCari, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnCari)))
                .addContainerGap(35, Short.MAX_VALUE))
        );

        getContentPane().add(jPanel2, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnSimpanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSimpanActionPerformed
            simpanData();        // TODO add your handling code here:
    }//GEN-LAST:event_btnSimpanActionPerformed

    private void comboKategoriActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_comboKategoriActionPerformed
            kategori();        // TODO add your handling code here:
    }//GEN-LAST:event_comboKategoriActionPerformed

    private void btnCariActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCariActionPerformed
            cariData();        // TODO add your handling code here:
    }//GEN-LAST:event_btnCariActionPerformed

    private void btnHapusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnHapusActionPerformed
            hapusData();        // TODO add your handling code here:
    }//GEN-LAST:event_btnHapusActionPerformed

    private void btnEditActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEditActionPerformed
            ubahData();        // TODO add your handling code here:
    }//GEN-LAST:event_btnEditActionPerformed

    private void txtTeleponKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtTeleponKeyTyped
             char c = evt.getKeyChar();
            // Hanya izinkan angka dan titik desimal
            if (!Character.isDigit(c) && c != '.') {
                evt.consume(); // Abaikan input yang bukan angka atau titik desimal
            }
            // Mencegah lebih dari satu titik desimal
            if (c == '.' && txtTelepon.getText().contains(".")) {
                evt.consume(); // Abaikan input jika titik desimal sudah ada
            }
            // Batasi panjang input menjadi maksimal 13 karakter
            if (txtTelepon.getText().length() >= 13) {
                evt.consume(); // Abaikan input jika panjang sudah mencapai 13
            }        // TODO add your handling code here:
    }//GEN-LAST:event_txtTeleponKeyTyped

    private void btnBatalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBatalActionPerformed
            batal();        // TODO add your handling code here:
    }//GEN-LAST:event_btnBatalActionPerformed

    private void btnImporActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnImporActionPerformed
            impor();        // TODO add your handling code here:
    }//GEN-LAST:event_btnImporActionPerformed

    private void btnEksporActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEksporActionPerformed
            ekspor();        // TODO add your handling code here:
    }//GEN-LAST:event_btnEksporActionPerformed

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
            java.util.logging.Logger.getLogger(PengelolaKontak.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(PengelolaKontak.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(PengelolaKontak.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(PengelolaKontak.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new PengelolaKontak().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBatal;
    private javax.swing.JButton btnCari;
    private javax.swing.JButton btnEdit;
    private javax.swing.JButton btnEkspor;
    private javax.swing.JButton btnHapus;
    private javax.swing.JButton btnImpor;
    private javax.swing.JButton btnSimpan;
    private javax.swing.JComboBox<String> comboKategori;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tabelKontak;
    private javax.swing.JTextField txtCari;
    private javax.swing.JTextField txtNama;
    private javax.swing.JTextField txtTelepon;
    // End of variables declaration//GEN-END:variables
}
