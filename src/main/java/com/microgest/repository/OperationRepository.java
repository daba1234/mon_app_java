package com.microgest.repository;

import com.microgest.model.Operation;
import com.microgest.model.TypeOperation;
import com.microgest.util.DatabaseConnection;
import com.microgest.util.JdbcMapper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class OperationRepository {

    public int create(Operation operation) {
        String sql = "INSERT INTO operation (compte_id, \"type\", montant, date_operation, description, utilisateur_id) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setObject(1, operation.getCompteId());
            statement.setString(2, operation.getTypeOperation() == null ? null : operation.getTypeOperation().name());
            statement.setObject(3, operation.getMontant());
            statement.setObject(4, JdbcMapper.toSqlTimestamp(operation.getDateOperation()));
            statement.setString(5, operation.getDescription());
            statement.setObject(6, operation.getUtilisateurId());
            statement.executeUpdate();
            try (ResultSet keys = statement.getGeneratedKeys()) {
                return keys.next() ? keys.getInt(1) : 0;
            }
        } catch (SQLException ex) {
            throw new IllegalStateException("Impossible de créer l'opération", ex);
        }
    }

    public Optional<Operation> findById(Integer id) {
        String sql = "SELECT * FROM operation WHERE id = ?";
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
            throw new IllegalStateException("Impossible de charger l'opération", ex);
        }
    }

    public List<Operation> findAll() {
        String sql = "SELECT * FROM operation ORDER BY date_operation DESC, id DESC";
        List<Operation> operations = new ArrayList<>();
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                operations.add(mapRow(resultSet));
            }
            return operations;
        } catch (SQLException ex) {
            throw new IllegalStateException("Impossible de charger les opérations", ex);
        }
    }

    public boolean update(Operation operation) {
        String sql = "UPDATE operation SET compte_id = ?, \"type\" = ?, montant = ?, date_operation = ?, description = ?, utilisateur_id = ? WHERE id = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setObject(1, operation.getCompteId());
            statement.setString(2, operation.getTypeOperation() == null ? null : operation.getTypeOperation().name());
            statement.setObject(3, operation.getMontant());
            statement.setObject(4, JdbcMapper.toSqlTimestamp(operation.getDateOperation()));
            statement.setString(5, operation.getDescription());
            statement.setObject(6, operation.getUtilisateurId());
            statement.setInt(7, operation.getId());
            return statement.executeUpdate() > 0;
        } catch (SQLException ex) {
            throw new IllegalStateException("Impossible de mettre à jour l'opération", ex);
        }
    }

    public boolean delete(Integer id) {
        String sql = "DELETE FROM operation WHERE id = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            return statement.executeUpdate() > 0;
        } catch (SQLException ex) {
            throw new IllegalStateException("Impossible de supprimer l'opération", ex);
        }
    }

    public List<Operation> findByCompteId(Integer compteId) {
        String sql = "SELECT * FROM operation WHERE compte_id = ? ORDER BY date_operation DESC, id DESC";
        List<Operation> operations = new ArrayList<>();
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, compteId);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    operations.add(mapRow(resultSet));
                }
            }
            return operations;
        } catch (SQLException ex) {
            throw new IllegalStateException("Impossible de charger les opérations du compte", ex);
        }
    }

    public List<Operation> findByUtilisateurId(Integer utilisateurId) {
        String sql = "SELECT * FROM operation WHERE utilisateur_id = ? ORDER BY date_operation DESC, id DESC";
        List<Operation> operations = new ArrayList<>();
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, utilisateurId);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    operations.add(mapRow(resultSet));
                }
            }
            return operations;
        } catch (SQLException ex) {
            throw new IllegalStateException("Impossible de charger les opérations de l'utilisateur", ex);
        }
    }

    private Operation mapRow(ResultSet resultSet) throws SQLException {
        Operation operation = new Operation();
        operation.setId(JdbcMapper.toInteger(resultSet, "id"));
        operation.setCompteId(JdbcMapper.toInteger(resultSet, "compte_id"));
        String type = resultSet.getString("type");
        operation.setTypeOperation(type == null ? null : TypeOperation.valueOf(type));
        operation.setMontant(resultSet.getBigDecimal("montant"));
        operation.setDateOperation(JdbcMapper.toLocalDateTime(resultSet, "date_operation"));
        operation.setDescription(resultSet.getString("description"));
        operation.setUtilisateurId(JdbcMapper.toInteger(resultSet, "utilisateur_id"));
        return operation;
    }
}