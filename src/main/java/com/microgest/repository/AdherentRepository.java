package com.microgest.repository;

import com.microgest.model.Adherent;
import com.microgest.model.AdherentSearchCriteria;
import com.microgest.model.MonthlyOperationCount;
import com.microgest.model.StatutAdherent;
import com.microgest.model.StatusCount;
import com.microgest.util.DatabaseConnection;
import com.microgest.util.JdbcMapper;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

public class AdherentRepository {

    private static final DateTimeFormatter MONTH_LABEL_FORMATTER = DateTimeFormatter.ofPattern("MMM yyyy", Locale.FRENCH);

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

    public List<Adherent> findPage(AdherentSearchCriteria criteria, int pageNumber, int pageSize) {
        StringBuilder sql = new StringBuilder("SELECT * FROM adherent");
        List<Object> parameters = new ArrayList<>();
        appendFilters(sql, parameters, criteria);
        sql.append(" ORDER BY id LIMIT ? OFFSET ?");
        parameters.add(pageSize);
        parameters.add(Math.max(pageNumber, 0) * pageSize);

        List<Adherent> adherents = new ArrayList<>();
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql.toString())) {
            bindParameters(statement, parameters);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    adherents.add(mapRow(resultSet));
                }
            }
            return adherents;
        } catch (SQLException ex) {
            throw new IllegalStateException("Impossible de charger la page des adhérents", ex);
        }
    }

    public long count(AdherentSearchCriteria criteria) {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM adherent");
        List<Object> parameters = new ArrayList<>();
        appendFilters(sql, parameters, criteria);

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql.toString())) {
            bindParameters(statement, parameters);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next() ? resultSet.getLong(1) : 0L;
            }
        } catch (SQLException ex) {
            throw new IllegalStateException("Impossible de compter les adhérents", ex);
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

    public long countAll() {
        return count(new AdherentSearchCriteria());
    }

    public long countByStatut(StatutAdherent statut) {
        String sql = "SELECT COUNT(*) FROM adherent WHERE statut = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, statut.name());
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next() ? resultSet.getLong(1) : 0L;
            }
        } catch (SQLException ex) {
            throw new IllegalStateException("Impossible de compter les adhérents par statut", ex);
        }
    }

    public List<StatusCount> countGroupByStatut() {
        String sql = "SELECT statut, COUNT(*) AS total FROM adherent GROUP BY statut ORDER BY statut";
        List<StatusCount> counts = new ArrayList<>();
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                String statut = resultSet.getString("statut");
                counts.add(new StatusCount(statut == null ? null : StatutAdherent.valueOf(statut), resultSet.getLong("total")));
            }
            return counts;
        } catch (SQLException ex) {
            throw new IllegalStateException("Impossible de charger la distribution des statuts", ex);
        }
    }

    public long countOperationsForCurrentMonth() {
        String sql = "SELECT COUNT(*) FROM operation WHERE date_operation >= date_trunc('month', CURRENT_DATE) AND date_operation < date_trunc('month', CURRENT_DATE) + INTERVAL '1 month'";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            return resultSet.next() ? resultSet.getLong(1) : 0L;
        } catch (SQLException ex) {
            throw new IllegalStateException("Impossible de compter les opérations du mois", ex);
        }
    }

    public BigDecimal sumCompteBalances() {
        String sql = "SELECT COALESCE(SUM(solde), 0) FROM compte";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            return resultSet.next() ? resultSet.getBigDecimal(1) : BigDecimal.ZERO;
        } catch (SQLException ex) {
            throw new IllegalStateException("Impossible de calculer le total de l'épargne", ex);
        }
    }

    public List<MonthlyOperationCount> countOperationsByMonth(int monthCount) {
        String sql = "SELECT EXTRACT(YEAR FROM date_operation) AS year_value, EXTRACT(MONTH FROM date_operation) AS month_value, COUNT(*) AS total "
                + "FROM operation WHERE date_operation >= date_trunc('month', CURRENT_DATE) - (? - 1) * INTERVAL '1 month' "
                + "GROUP BY year_value, month_value ORDER BY year_value, month_value";
        List<MonthlyOperationCount> counts = new ArrayList<>();
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, Math.max(monthCount, 1));
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    int year = resultSet.getInt("year_value");
                    int month = resultSet.getInt("month_value");
                    YearMonth yearMonth = YearMonth.of(year, month);
                    counts.add(new MonthlyOperationCount(yearMonth.format(MONTH_LABEL_FORMATTER), resultSet.getLong("total")));
                }
            }
            return counts;
        } catch (SQLException ex) {
            throw new IllegalStateException("Impossible de charger les opérations mensuelles", ex);
        }
    }

    private void appendFilters(StringBuilder sql, List<Object> parameters, AdherentSearchCriteria criteria) {
        List<String> clauses = new ArrayList<>();
        if (criteria != null) {
            String searchTerm = normalize(criteria.getSearchTerm());
            if (searchTerm != null) {
                clauses.add("(LOWER(nom) LIKE ? OR LOWER(prenom) LIKE ? OR LOWER(COALESCE(telephone, '')) LIKE ?)");
                String searchPattern = "%" + searchTerm.toLowerCase(Locale.ROOT) + "%";
                parameters.add(searchPattern);
                parameters.add(searchPattern);
                parameters.add(searchPattern);
            }
            if (criteria.getStatut() != null) {
                clauses.add("statut = ?");
                parameters.add(criteria.getStatut().name());
            }
        }
        if (!clauses.isEmpty()) {
            sql.append(" WHERE ").append(String.join(" AND ", clauses));
        }
    }

    private void bindParameters(PreparedStatement statement, List<Object> parameters) throws SQLException {
        for (int i = 0; i < parameters.size(); i++) {
            statement.setObject(i + 1, parameters.get(i));
        }
    }

    private String normalize(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
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
