package isi.diti3.micoguest;

import java.sql.*;

public class DBConnexion {

        private Connection cx;
        private PreparedStatement pstm;
        private ResultSet rs;
        private int ok;

        private Connection getConnection() {
            String url = "jdbc:postgresql://localhost:5432/microGest_db";
            String user = "postgres";
            String password = "P@sser123";
            try {
                Class.forName("org.postgresql.Driver");
                cx = DriverManager.getConnection(url, user, password);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return cx;
        }

        public ResultSet executeSelect(String requete) {
            try {
                pstm = getConnection().prepareStatement(requete);
                rs = pstm.executeQuery();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return rs;
        }

        public int executeMaj(String requete) {
            try {
                pstm = getConnection().prepareStatement(requete);
                ok = pstm.executeUpdate();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return ok;
        }

        public PreparedStatement getPstm() {
            return pstm;
        }
    }