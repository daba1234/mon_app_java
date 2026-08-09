package com.microgest.repository;

import com.microgest.model.Agence;
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

public class AgenceRepository {

    public int create(Agence agence) {
        String sql = "INSERT INTO agence (nom, localisation, telephone, date_creation, actif) VALUES (?, ?, ?, ?, ?)";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, agence.getNom());
            statement.setString(2, agence.getLocalisation());
            statement.setString(3, agence.getTelephone());
            statement.setObject(4, JdbcMapper.toSqlTimestamp(agence.getDateCreation()));
            statement.setObject(5, agence.getActif());
            statement.executeUpdate();
            try (ResultSet keys = statement.getGeneratedKeys()) {
                return keys.next() ? keys.getInt(1) : 0;
            }
        } catch (SQLException ex) {
            throw new IllegalStateException("Impossible de créer l'agence", ex);
        }
    }

    public Optional<Agence> findById(Integer id) {
        String sql = "SELECT * FROM agence WHERE id = ?";
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
            throw new IllegalStateException("Impossible de charger l'agence", ex);
        }
    }

    public List<Agence> findAll() {
        String sql = "SELECT * FROM agence ORDER BY id";
        List<Agence> agences = new ArrayList<>();
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                agences.add(mapRow(resultSet));
            }
            return agences;
        } catch (SQLException ex) {
            throw new IllegalStateException("Impossible de charger les agences", ex);
        }
    }

    public boolean update(Agence agence) {
        String sql = "UPDATE agence SET nom = ?, localisation = ?, telephone = ?, date_creation = ?, actif = ? WHERE id = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, agence.getNom());
            statement.setString(2, agence.getLocalisation());
            statement.setString(3, agence.getTelephone());
            statement.setObject(4, JdbcMapper.toSqlTimestamp(agence.getDateCreation()));
            statement.setObject(5, agence.getActif());
            statement.setInt(6, agence.getId());
            return statement.executeUpdate() > 0;
        } catch (SQLException ex) {
            throw new IllegalStateException("Impossible de mettre à jour l'agence", ex);
        }
    }

    public boolean delete(Integer id) {
        String sql = "DELETE FROM agence WHERE id = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            return statement.executeUpdate() > 0;
        } catch (SQLException ex) {
            throw new IllegalStateException("Impossible de supprimer l'agence", ex);
        }
    }

    public Optional<Agence> findByNom(String nom) {
        String sql = "SELECT * FROM agence WHERE nom = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, nom);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(mapRow(resultSet));
                }
                return Optional.empty();
            }
        } catch (SQLException ex) {
            throw new IllegalStateException("Impossible de rechercher l'agence", ex);
        }
    }

    private Agence mapRow(ResultSet resultSet) throws SQLException {
        Agence agence = new Agence();
        agence.setId(JdbcMapper.toInteger(resultSet, "id"));
        agence.setNom(resultSet.getString("nom"));
        agence.setLocalisation(resultSet.getString("localisation"));
        agence.setTelephone(resultSet.getString("telephone"));
        agence.setDateCreation(JdbcMapper.toLocalDateTime(resultSet, "date_creation"));
        agence.setActif(JdbcMapper.toBoolean(resultSet, "actif"));
        return agence;
    }
}