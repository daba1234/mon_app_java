package com.microgest.repository;

import com.microgest.model.Role;
import com.microgest.model.Utilisateur;
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

public class UtilisateurRepository {

    public int create(Utilisateur utilisateur) {
        String sql = "INSERT INTO utilisateur (username, password_hash, email, role, agence_id, date_creation, actif) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, utilisateur.getUsername());
            statement.setString(2, utilisateur.getPasswordHash());
            statement.setString(3, utilisateur.getEmail());
            statement.setString(4, utilisateur.getRole() == null ? null : utilisateur.getRole().name());
            statement.setObject(5, utilisateur.getAgenceId());
            statement.setObject(6, JdbcMapper.toSqlTimestamp(utilisateur.getDateCreation()));
            statement.setObject(7, utilisateur.getActif());
            statement.executeUpdate();
            try (ResultSet keys = statement.getGeneratedKeys()) {
                return keys.next() ? keys.getInt(1) : 0;
            }
        } catch (SQLException ex) {
            throw new IllegalStateException("Impossible de créer l'utilisateur", ex);
        }
    }

    public Optional<Utilisateur> findById(Integer id) {
        String sql = "SELECT * FROM utilisateur WHERE id = ?";
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
            throw new IllegalStateException("Impossible de charger l'utilisateur", ex);
        }
    }

    public List<Utilisateur> findAll() {
        String sql = "SELECT * FROM utilisateur ORDER BY id";
        List<Utilisateur> utilisateurs = new ArrayList<>();
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                utilisateurs.add(mapRow(resultSet));
            }
            return utilisateurs;
        } catch (SQLException ex) {
            throw new IllegalStateException("Impossible de charger les utilisateurs", ex);
        }
    }

    public boolean update(Utilisateur utilisateur) {
        String sql = "UPDATE utilisateur SET username = ?, password_hash = ?, email = ?, role = ?::role_enum, agence_id = ?, actif = ? WHERE id = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, utilisateur.getUsername());
            statement.setString(2, utilisateur.getPasswordHash());
            statement.setString(3, utilisateur.getEmail());
            statement.setString(4, utilisateur.getRole() == null ? null : utilisateur.getRole().name());
            statement.setObject(5, utilisateur.getAgenceId());
            statement.setObject(6, utilisateur.getActif());
            statement.setInt(7, utilisateur.getId());
            return statement.executeUpdate() > 0;
        } catch (SQLException ex) {
            throw new IllegalStateException("Impossible de mettre à jour l'utilisateur", ex);
        }
    }

    public boolean delete(Integer id) {
        String sql = "DELETE FROM utilisateur WHERE id = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            return statement.executeUpdate() > 0;
        } catch (SQLException ex) {
            throw new IllegalStateException("Impossible de supprimer l'utilisateur", ex);
        }
    }

    public Optional<Utilisateur> findByUsername(String username) {
        String sql = "SELECT * FROM utilisateur WHERE username = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, username);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(mapRow(resultSet));
                }
                return Optional.empty();
            }
        } catch (SQLException ex) {
            throw new IllegalStateException("Impossible de rechercher l'utilisateur", ex);
        }
    }

    public Optional<Utilisateur> findByEmail(String email) {
        String sql = "SELECT * FROM utilisateur WHERE email = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, email);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(mapRow(resultSet));
                }
                return Optional.empty();
            }
        } catch (SQLException ex) {
            throw new IllegalStateException("Impossible de rechercher l'utilisateur", ex);
        }
    }

    public boolean existsByUsername(String username) {
        return findByUsername(username).isPresent();
    }

    public boolean existsByEmail(String email) {
        return findByEmail(email).isPresent();
    }

    private Utilisateur mapRow(ResultSet resultSet) throws SQLException {
        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setId(JdbcMapper.toInteger(resultSet, "id"));
        utilisateur.setUsername(resultSet.getString("username"));
        utilisateur.setPasswordHash(resultSet.getString("password_hash"));
        utilisateur.setEmail(resultSet.getString("email"));
        String role = resultSet.getString("role");
        utilisateur.setRole(role == null ? null : Role.valueOf(role));
        utilisateur.setAgenceId(JdbcMapper.toInteger(resultSet, "agence_id"));
        utilisateur.setDateCreation(JdbcMapper.toLocalDateTime(resultSet, "date_creation"));
        utilisateur.setActif(JdbcMapper.toBoolean(resultSet, "actif"));
        return utilisateur;
    }
}