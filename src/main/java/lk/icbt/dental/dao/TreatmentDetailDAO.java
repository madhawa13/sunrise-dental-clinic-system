package lk.icbt.dental.dao;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import lk.icbt.dental.model.TreatmentDetail;

/**
 * Defines database operations for treatment detail records.
 */
public interface TreatmentDetailDAO {

    /**
     * Saves a treatment detail and returns its generated ID.
     */
    long save(TreatmentDetail treatmentDetail)
            throws SQLException;

    /**
     * Finds a treatment detail using its database ID.
     */
    Optional<TreatmentDetail> findById(
            long treatmentDetailId)
            throws SQLException;

    /**
     * Returns all charge items belonging to a treatment.
     */
    List<TreatmentDetail> findByTreatmentId(
            long treatmentId)
            throws SQLException;

    /**
     * Updates an existing treatment detail.
     */
    boolean update(TreatmentDetail treatmentDetail)
            throws SQLException;

    /**
     * Deletes a treatment detail.
     */
    boolean delete(long treatmentDetailId)
            throws SQLException;
}