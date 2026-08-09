package com.microgest.repository;

import com.microgest.model.Remboursement;
import com.microgest.model.StatutRemboursement;
import com.microgest.util.DatabaseConnection;
import com.microgest.util.JdbcMapper;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class RemboursementRepository {

    public int create(Remboursement remboursement) {
        String sql = "INSERT INTO remboursement (pret_id, montant, date_remboursement, statut) VALUES (?, ?, ?, ?)";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setObject(1, remboursement.getPretId());
            statement.setObject(2, remboursement.getMontant());
            statement.setObject(3, JdbcMapper.toSqlDate(remboursement.getDateRemboursement()));
            statement.setString(4, remboursement.getStatut() == null ? null : remboursement.getStatut().name());
            statement.executeUpdate();
            try (ResultSet keys = statement.getGeneratedKeys()) {
                return keys.next() ? keys.getInt(1) : 0;
            }
        } catch (SQLException ex) {
            throw new IllegalStateException("Impossible de créer le remboursement", ex);
        }
    }

    public Optional<Remboursement> findById(Integer id) {
        String sql = "SELECT * FROM remboursement WHERE id = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(mapRow(resultSet));
                }
                return Optional.empty();
            }
        } catch (SQLException ex) {
            throw new IllegalStateException("Impossible de charger le remboursement", ex);
        }
    }

    public List<Remboursement> findAll() {
        String sql = "SELECT * FROM remboursement ORDER BY id DESC";
        List<Remboursement> remboursements = new ArrayList<>();
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                remboursements.add(mapRow(resultSet));
            }
            return remboursements;
        } catch (SQLException ex) {
            throw new IllegalStateException("Impossible de charger les remboursements", ex);
        }
    }

    public boolean update(Remboursement remboursement) {
        String sql = "UPDATE remboursement SET pret_id = ?, montant = ?, date_remboursement = ?, statut = ? WHERE id = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setObject(1, remboursement.getPretId());
            statement.setObject(2, remboursement.getMontant());
            statement.setObject(3, JdbcMapper.toSqlDate(remboursement.getDateRemboursement()));
            statement.setString(4, remboursement.getStatut() == null ? null : remboursement.getStatut().name());
            statement.setInt(5, remboursement.getId());
            return statement.executeUpdate() > 0;
        } catch (SQLException ex) {
            throw new IllegalStateException("Impossible de mettre à jour le remboursement", ex);
        }
    }

    public boolean delete(Integer id) {
        String sql = "DELETE FROM remboursement WHERE id = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            return statement.executeUpdate() > 0;
        } catch (SQLException ex) {
            throw new IllegalStateException("Impossible de supprimer le remboursement", ex);
        }
    }

    public List<Remboursement> findByPretId(Integer pretId) {
        String sql = "SELECT * FROM remboursement WHERE pret_id = ? ORDER BY date_remboursement DESC, id DESC";
        List<Remboursement> remboursements = new ArrayList<>();
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, pretId);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    remboursements.add(mapRow(resultSet));
                }
            }
            return remboursements;
        } catch (SQLException ex) {
            throw new IllegalStateException("Impossible de charger les remboursements du prêt", ex);
        }
    }

    public BigDecimal sumMontantByPretId(Integer pretId) {
        String sql = "SELECT COALESCE(SUM(montant), 0) AS total FROM remboursement WHERE pret_id = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, pretId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getBigDecimal("total");
                }
                return BigDecimal.ZERO;
            }
        } catch (SQLException ex) {
            throw new IllegalStateException("Impossible de calculer le total des remboursements", ex);
        }
    }

    private Remboursement mapRow(ResultSet resultSet) throws SQLException {
        Remboursement remboursement = new Remboursement();
        remboursement.setId(JdbcMapper.toInteger(resultSet, "id"));
        remboursement.setPretId(JdbcMapper.toInteger(resultSet, "pret_id"));
        remboursement.setMontant(resultSet.getBigDecimal("montant"));
        remboursement.setDateRemboursement(JdbcMapper.toLocalDate(resultSet, "date_remboursement"));
        String statut = resultSet.getString("statut");
        remboursement.setStatut(statut == null ? null : StatutRemboursement.valueOf(statut));
        return remboursement;
    }
}