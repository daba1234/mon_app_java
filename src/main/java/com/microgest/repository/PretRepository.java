package com.microgest.repository;

import com.microgest.model.Pret;
import com.microgest.model.StatutPret;
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

public class PretRepository {

    public int create(Pret pret) {
        String sql = "INSERT INTO pret (adherent_id, montant_demande, montant_accorde, taux_interet, date_debut, date_fin, statut, motif_rejet) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setObject(1, pret.getAdherentId());
            statement.setObject(2, pret.getMontantDemande());
            statement.setObject(3, pret.getMontantAccorde());
            statement.setObject(4, pret.getTauxInteret());
            statement.setObject(5, JdbcMapper.toSqlDate(pret.getDateDebut()));
            statement.setObject(6, JdbcMapper.toSqlDate(pret.getDateFin()));
            statement.setString(7, pret.getStatut() == null ? null : pret.getStatut().name());
            statement.setString(8, pret.getMotifRejet());
            statement.executeUpdate();
            try (ResultSet keys = statement.getGeneratedKeys()) {
                return keys.next() ? keys.getInt(1) : 0;
            }
        } catch (SQLException ex) {
            throw new IllegalStateException("Impossible de créer le prêt", ex);
        }
    }

    public Optional<Pret> findById(Integer id) {
        String sql = "SELECT * FROM pret WHERE id = ?";
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
            throw new IllegalStateException("Impossible de charger le prêt", ex);
        }
    }

    public List<Pret> findAll() {
        String sql = "SELECT * FROM pret ORDER BY id DESC";
        List<Pret> prets = new ArrayList<>();
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                prets.add(mapRow(resultSet));
            }
            return prets;
        } catch (SQLException ex) {
            throw new IllegalStateException("Impossible de charger les prêts", ex);
        }
    }

    public boolean update(Pret pret) {
        String sql = "UPDATE pret SET adherent_id = ?, montant_demande = ?, montant_accorde = ?, taux_interet = ?, date_debut = ?, date_fin = ?, statut = ?, motif_rejet = ? WHERE id = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setObject(1, pret.getAdherentId());
            statement.setObject(2, pret.getMontantDemande());
            statement.setObject(3, pret.getMontantAccorde());
            statement.setObject(4, pret.getTauxInteret());
            statement.setObject(5, JdbcMapper.toSqlDate(pret.getDateDebut()));
            statement.setObject(6, JdbcMapper.toSqlDate(pret.getDateFin()));
            statement.setString(7, pret.getStatut() == null ? null : pret.getStatut().name());
            statement.setString(8, pret.getMotifRejet());
            statement.setInt(9, pret.getId());
            return statement.executeUpdate() > 0;
        } catch (SQLException ex) {
            throw new IllegalStateException("Impossible de mettre à jour le prêt", ex);
        }
    }

    public boolean delete(Integer id) {
        String sql = "DELETE FROM pret WHERE id = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            return statement.executeUpdate() > 0;
        } catch (SQLException ex) {
            throw new IllegalStateException("Impossible de supprimer le prêt", ex);
        }
    }

    public List<Pret> findByAdherentId(Integer adherentId) {
        String sql = "SELECT * FROM pret WHERE adherent_id = ? ORDER BY id DESC";
        List<Pret> prets = new ArrayList<>();
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, adherentId);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    prets.add(mapRow(resultSet));
                }
            }
            return prets;
        } catch (SQLException ex) {
            throw new IllegalStateException("Impossible de charger les prêts de l'adhérent", ex);
        }
    }

    private Pret mapRow(ResultSet resultSet) throws SQLException {
        Pret pret = new Pret();
        pret.setId(JdbcMapper.toInteger(resultSet, "id"));
        pret.setAdherentId(JdbcMapper.toInteger(resultSet, "adherent_id"));
        pret.setMontantDemande(resultSet.getBigDecimal("montant_demande"));
        pret.setMontantAccorde(resultSet.getBigDecimal("montant_accorde"));
        pret.setTauxInteret(resultSet.getBigDecimal("taux_interet"));
        pret.setDateDebut(JdbcMapper.toLocalDate(resultSet, "date_debut"));
        pret.setDateFin(JdbcMapper.toLocalDate(resultSet, "date_fin"));
        String statut = resultSet.getString("statut");
        pret.setStatut(statut == null ? null : StatutPret.valueOf(statut));
        pret.setMotifRejet(resultSet.getString("motif_rejet"));
        return pret;
    }
}