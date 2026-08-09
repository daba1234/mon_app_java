package com.microgest.repository;

import com.microgest.model.Compte;
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

public class CompteRepository {

    public int create(Compte compte) {
        String sql = "INSERT INTO compte (adherent_id, type_compte, solde, date_ouverture, date_fermeture, actif) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setObject(1, compte.getAdherentId());
            statement.setString(2, compte.getTypeCompte());
            statement.setObject(3, compte.getSolde());
            statement.setObject(4, JdbcMapper.toSqlDate(compte.getDateOuverture()));
            statement.setObject(5, JdbcMapper.toSqlDate(compte.getDateFermeture()));
            statement.setObject(6, compte.getActif());
            statement.executeUpdate();
            try (ResultSet keys = statement.getGeneratedKeys()) {
                return keys.next() ? keys.getInt(1) : 0;
            }
        } catch (SQLException ex) {
            throw new IllegalStateException("Impossible de créer le compte", ex);
        }
    }

    public Optional<Compte> findById(Integer id) {
        String sql = "SELECT * FROM compte WHERE id = ?";
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
            throw new IllegalStateException("Impossible de charger le compte", ex);
        }
    }

    public List<Compte> findAll() {
        String sql = "SELECT * FROM compte ORDER BY id";
        List<Compte> comptes = new ArrayList<>();
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                comptes.add(mapRow(resultSet));
            }
            return comptes;
        } catch (SQLException ex) {
            throw new IllegalStateException("Impossible de charger les comptes", ex);
        }
    }

    public boolean update(Compte compte) {
        String sql = "UPDATE compte SET adherent_id = ?, type_compte = ?, solde = ?, date_ouverture = ?, date_fermeture = ?, actif = ? WHERE id = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setObject(1, compte.getAdherentId());
            statement.setString(2, compte.getTypeCompte());
            statement.setObject(3, compte.getSolde());
            statement.setObject(4, JdbcMapper.toSqlDate(compte.getDateOuverture()));
            statement.setObject(5, JdbcMapper.toSqlDate(compte.getDateFermeture()));
            statement.setObject(6, compte.getActif());
            statement.setInt(7, compte.getId());
            return statement.executeUpdate() > 0;
        } catch (SQLException ex) {
            throw new IllegalStateException("Impossible de mettre à jour le compte", ex);
        }
    }

    public boolean delete(Integer id) {
        String sql = "DELETE FROM compte WHERE id = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            return statement.executeUpdate() > 0;
        } catch (SQLException ex) {
            throw new IllegalStateException("Impossible de supprimer le compte", ex);
        }
    }

    public Optional<Compte> findByAdherentId(Integer adherentId) {
        String sql = "SELECT * FROM compte WHERE adherent_id = ? ORDER BY id DESC LIMIT 1";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, adherentId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(mapRow(resultSet));
                }
                return Optional.empty();
            }
        } catch (SQLException ex) {
            throw new IllegalStateException("Impossible de charger le compte de l'adhérent", ex);
        }
    }

    private Compte mapRow(ResultSet resultSet) throws SQLException {
        Compte compte = new Compte();
        compte.setId(JdbcMapper.toInteger(resultSet, "id"));
        compte.setAdherentId(JdbcMapper.toInteger(resultSet, "adherent_id"));
        compte.setTypeCompte(resultSet.getString("type_compte"));
        compte.setSolde(resultSet.getBigDecimal("solde"));
        compte.setDateOuverture(JdbcMapper.toLocalDate(resultSet, "date_ouverture"));
        compte.setDateFermeture(JdbcMapper.toLocalDate(resultSet, "date_fermeture"));
        compte.setActif(JdbcMapper.toBoolean(resultSet, "actif"));
        return compte;
    }
}