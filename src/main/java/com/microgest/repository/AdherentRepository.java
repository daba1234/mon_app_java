package com.microgest.repository;

import com.microgest.model.Adherent;
import com.microgest.model.StatutAdherent;
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

public class AdherentRepository {

    public int create(Adherent adherent) {
        String sql = "INSERT INTO adherent (nom, prenom, telephone, email, date_naissance, adresse, date_adhesion, agence_id, statut, created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, adherent.getNom());
            statement.setString(2, adherent.getPrenom());
            statement.setString(3, adherent.getTelephone());
            statement.setString(4, adherent.getEmail());
            statement.setObject(5, JdbcMapper.toSqlDate(adherent.getDateNaissance()));
            statement.setString(6, adherent.getAdresse());
            statement.setObject(7, JdbcMapper.toSqlDate(adherent.getDateAdhesion()));
            statement.setObject(8, adherent.getAgenceId());
            statement.setString(9, adherent.getStatut() == null ? null : adherent.getStatut().name());
            statement.setObject(10, JdbcMapper.toSqlTimestamp(adherent.getCreatedAt()));
            statement.setObject(11, JdbcMapper.toSqlTimestamp(adherent.getUpdatedAt()));
            statement.executeUpdate();
            try (ResultSet keys = statement.getGeneratedKeys()) {
                return keys.next() ? keys.getInt(1) : 0;
            }
        } catch (SQLException ex) {
            throw new IllegalStateException("Impossible de créer l'adhérent", ex);
        }
    }

    public Optional<Adherent> findById(Integer id) {
        String sql = "SELECT * FROM adherent WHERE id = ?";
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
            throw new IllegalStateException("Impossible de charger l'adhérent", ex);
        }
    }

    public List<Adherent> findAll() {
        String sql = "SELECT * FROM adherent ORDER BY id";
        List<Adherent> adherents = new ArrayList<>();
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                adherents.add(mapRow(resultSet));
            }
            return adherents;
        } catch (SQLException ex) {
            throw new IllegalStateException("Impossible de charger les adhérents", ex);
        }
    }

    public boolean update(Adherent adherent) {
        String sql = "UPDATE adherent SET nom = ?, prenom = ?, telephone = ?, email = ?, date_naissance = ?, adresse = ?, date_adhesion = ?, agence_id = ?, statut = ?, updated_at = ? WHERE id = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, adherent.getNom());
            statement.setString(2, adherent.getPrenom());
            statement.setString(3, adherent.getTelephone());
            statement.setString(4, adherent.getEmail());
            statement.setObject(5, JdbcMapper.toSqlDate(adherent.getDateNaissance()));
            statement.setString(6, adherent.getAdresse());
            statement.setObject(7, JdbcMapper.toSqlDate(adherent.getDateAdhesion()));
            statement.setObject(8, adherent.getAgenceId());
            statement.setString(9, adherent.getStatut() == null ? null : adherent.getStatut().name());
            statement.setObject(10, JdbcMapper.toSqlTimestamp(adherent.getUpdatedAt()));
            statement.setInt(11, adherent.getId());
            return statement.executeUpdate() > 0;
        } catch (SQLException ex) {
            throw new IllegalStateException("Impossible de mettre à jour l'adhérent", ex);
        }
    }

    public boolean delete(Integer id) {
        String sql = "DELETE FROM adherent WHERE id = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            return statement.executeUpdate() > 0;
        } catch (SQLException ex) {
            throw new IllegalStateException("Impossible de supprimer l'adhérent", ex);
        }
    }

    public List<Adherent> findByAgenceId(Integer agenceId) {
        String sql = "SELECT * FROM adherent WHERE agence_id = ? ORDER BY id";
        List<Adherent> adherents = new ArrayList<>();
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, agenceId);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    adherents.add(mapRow(resultSet));
                }
            }
            return adherents;
        } catch (SQLException ex) {
            throw new IllegalStateException("Impossible de charger les adhérents de l'agence", ex);
        }
    }

    private Adherent mapRow(ResultSet resultSet) throws SQLException {
        Adherent adherent = new Adherent();
        adherent.setId(JdbcMapper.toInteger(resultSet, "id"));
        adherent.setNom(resultSet.getString("nom"));
        adherent.setPrenom(resultSet.getString("prenom"));
        adherent.setTelephone(resultSet.getString("telephone"));
        adherent.setEmail(resultSet.getString("email"));
        adherent.setDateNaissance(JdbcMapper.toLocalDate(resultSet, "date_naissance"));
        adherent.setAdresse(resultSet.getString("adresse"));
        adherent.setDateAdhesion(JdbcMapper.toLocalDate(resultSet, "date_adhesion"));
        adherent.setAgenceId(JdbcMapper.toInteger(resultSet, "agence_id"));
        String statut = resultSet.getString("statut");
        adherent.setStatut(statut == null ? null : StatutAdherent.valueOf(statut));
        adherent.setCreatedAt(JdbcMapper.toLocalDateTime(resultSet, "created_at"));
        adherent.setUpdatedAt(JdbcMapper.toLocalDateTime(resultSet, "updated_at"));
        return adherent;
    }
}