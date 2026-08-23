package lk.icbt.dental.dao;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import lk.icbt.dental.model.TreatmentCharge;

/**
 * Defines database operations for the
 * standard treatment price list.
 */
public interface TreatmentChargeDAO {

    /**
     * Saves a new treatment charge.
     *
     * @param treatmentCharge charge information
     * @return generated charge ID
     * @throws SQLException if the database operation fails
     */
    long save(
            TreatmentCharge treatmentCharge)
            throws SQLException;

    /**
     * Finds a treatment charge using its ID.
     *
     * @param chargeId charge database ID
     * @return matching charge when found
     * @throws SQLException if the database operation fails
     */
    Optional<TreatmentCharge> findById(
            long chargeId)
            throws SQLException;

    /**
     * Finds a treatment charge using its
     * treatment code.
     *
     * @param treatmentCode unique treatment code
     * @return matching charge when found
     * @throws SQLException if the database operation fails
     */
    Optional<TreatmentCharge> findByCode(
            String treatmentCode)
            throws SQLException;

    /**
     * Returns every treatment charge,
     * including inactive records.
     *
     * @return all treatment charges
     * @throws SQLException if the database operation fails
     */
    List<TreatmentCharge> findAll()
            throws SQLException;

    /**
     * Returns active treatment charges only.
     *
     * @return active treatment charges
     * @throws SQLException if the database operation fails
     */
    List<TreatmentCharge> findAllActive()
            throws SQLException;

    /**
     * Updates an existing treatment charge.
     *
     * @param treatmentCharge updated charge information
     * @return true when one record was updated
     * @throws SQLException if the database operation fails
     */
    boolean update(
            TreatmentCharge treatmentCharge)
            throws SQLException;

    /**
     * Changes the active status of a charge.
     *
     * @param chargeId charge database ID
     * @param active new active status
     * @return true when one record was updated
     * @throws SQLException if the database operation fails
     */
    boolean updateActiveStatus(
            long chargeId,
            boolean active)
            throws SQLException;
}