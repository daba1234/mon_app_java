package com.microgest.repository;

import com.microgest.model.StatutAdherent;
import com.microgest.util.DatabaseConnection;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;

public class DashboardRepository {

    public long countTotalAdherents() {
        return scalarLong("SELECT COUNT(*) AS total FROM adherent");
    }

    public long countAdherentsActifs() {
        return scalarLong("SELECT COUNT(*) AS total FROM adherent WHERE statut = 'ACTIF'");
    }

    public long countOperationsCurrentMonth() {
        String sql = "SELECT COUNT(*) AS total FROM operation WHERE date_operation >= date_trunc('month', CURRENT_DATE) AND date_operation < date_trunc('month', CURRENT_DATE) + INTERVAL '1 month'";
        return scalarLong(sql);
    }

    public BigDecimal totalEpargne() {
        String sql = "SELECT COALESCE(SUM(solde), 0) AS total FROM compte WHERE actif = true";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            if (resultSet.next()) {
                return resultSet.getBigDecimal("total");
            }
            return BigDecimal.ZERO;
        } catch (SQLException ex) {
            throw new IllegalStateException("Impossible de calculer l'épargne totale", ex);
        }
    }

    public Map<String, Long> adherentsByStatus() {
        String sql = "SELECT COALESCE(statut::text, 'INCONNU') AS label, COUNT(*) AS total FROM adherent GROUP BY COALESCE(statut::text, 'INCONNU') ORDER BY label";
        return groupedLong(sql);
    }

    public Map<String, Long> operationsByMonth() {
        String sql = "SELECT to_char(date_trunc('month', date_operation), 'YYYY-MM') AS label, COUNT(*) AS total FROM operation WHERE date_operation >= date_trunc('month', CURRENT_DATE) - INTERVAL '11 months' GROUP BY 1 ORDER BY 1";
        return groupedLong(sql);
    }

    private long scalarLong(String sql) {
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            if (resultSet.next()) {
                return resultSet.getLong("total");
            }
            return 0L;
        } catch (SQLException ex) {
            throw new IllegalStateException("Impossible d'exécuter la requête de statistiques", ex);
        }
    }

    private Map<String, Long> groupedLong(String sql) {
        Map<String, Long> values = new LinkedHashMap<>();
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                values.put(resultSet.getString("label"), resultSet.getLong("total"));
            }
            return values;
        } catch (SQLException ex) {
            throw new IllegalStateException("Impossible de charger les statistiques groupées", ex);
        }
    }
}