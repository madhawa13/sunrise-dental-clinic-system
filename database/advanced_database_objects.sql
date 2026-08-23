-- ============================================================
-- SUNRISE DENTAL CLINIC
-- ADVANCED DATABASE OBJECTS
-- CIS6003 Advanced Programming
-- ============================================================

USE sunrise_dental_clinic;


-- ============================================================
-- 1. DATABASE FUNCTION
-- Calculates the outstanding balance of a bill.
-- ============================================================

DROP FUNCTION IF EXISTS fn_calculate_bill_balance;

DELIMITER //

CREATE FUNCTION fn_calculate_bill_balance(
    p_bill_id BIGINT
)
RETURNS DECIMAL(10, 2)
DETERMINISTIC
READS SQL DATA
BEGIN

    DECLARE v_total_amount DECIMAL(10, 2)
        DEFAULT 0.00;

    DECLARE v_paid_amount DECIMAL(10, 2)
        DEFAULT 0.00;

    DECLARE v_balance DECIMAL(10, 2)
        DEFAULT 0.00;

    SELECT COALESCE(total_amount, 0.00)
    INTO v_total_amount
    FROM bills
    WHERE bill_id = p_bill_id;

    SELECT COALESCE(SUM(amount), 0.00)
    INTO v_paid_amount
    FROM payments
    WHERE bill_id = p_bill_id;

    SET v_balance =
        GREATEST(
            v_total_amount - v_paid_amount,
            0.00
        );

    RETURN v_balance;

END//

DELIMITER ;


-- ============================================================
-- 2. STORED PROCEDURE
-- Produces an appointment report for a selected date.
-- ============================================================

DROP PROCEDURE IF EXISTS sp_daily_appointment_report;

DELIMITER //

CREATE PROCEDURE sp_daily_appointment_report(
    IN p_report_date DATE
)
BEGIN

    SELECT
        a.appointment_id,
        a.appointment_number,
        a.appointment_date,
        a.appointment_time,
        CONCAT(
            p.first_name,
            ' ',
            p.last_name
        ) AS patient_name,
        p.phone AS patient_phone,
        u.full_name AS dentist_name,
        a.reason,
        a.status
    FROM appointments a
    INNER JOIN patients p
        ON a.patient_id = p.patient_id
    INNER JOIN users u
        ON a.dentist_id = u.user_id
    WHERE a.appointment_date =
        p_report_date
    ORDER BY
        a.appointment_time ASC;

END//

DELIMITER ;


-- ============================================================
-- 3. PAYMENT TRIGGER
-- Automatically updates bill payment status
-- after a payment is inserted.
-- ============================================================

DROP TRIGGER IF EXISTS trg_payment_update_bill_status;

DELIMITER //

CREATE TRIGGER trg_payment_update_bill_status
AFTER INSERT ON payments
FOR EACH ROW
BEGIN

    DECLARE v_total_amount DECIMAL(10, 2)
        DEFAULT 0.00;

    DECLARE v_paid_amount DECIMAL(10, 2)
        DEFAULT 0.00;

    SELECT total_amount
    INTO v_total_amount
    FROM bills
    WHERE bill_id = NEW.bill_id;

    SELECT COALESCE(SUM(amount), 0.00)
    INTO v_paid_amount
    FROM payments
    WHERE bill_id = NEW.bill_id;

    UPDATE bills
    SET payment_status =
        CASE
            WHEN v_paid_amount <= 0.00
                THEN 'UNPAID'

            WHEN v_paid_amount < v_total_amount
                THEN 'PARTIALLY_PAID'

            ELSE 'PAID'
        END
    WHERE bill_id = NEW.bill_id;

END//

DELIMITER ;


-- ============================================================
-- 4. REPORT VIEW
-- Provides an appointment and billing summary.
-- ============================================================

DROP VIEW IF EXISTS vw_appointment_billing_summary;

CREATE VIEW vw_appointment_billing_summary AS
SELECT
    a.appointment_id,
    a.appointment_number,
    a.appointment_date,
    a.appointment_time,
    CONCAT(
        p.first_name,
        ' ',
        p.last_name
    ) AS patient_name,
    u.full_name AS dentist_name,
    a.status AS appointment_status,
    b.bill_id,
    b.bill_number,
    COALESCE(
        b.total_amount,
        0.00
    ) AS bill_total,
    COALESCE(
        (
            SELECT SUM(pm.amount)
            FROM payments pm
            WHERE pm.bill_id = b.bill_id
        ),
        0.00
    ) AS amount_paid,
    CASE
        WHEN b.bill_id IS NULL
            THEN 0.00

        ELSE fn_calculate_bill_balance(
                b.bill_id
             )
    END AS outstanding_balance,
    COALESCE(
        b.payment_status,
        'NOT_BILLED'
    ) AS payment_status
FROM appointments a
INNER JOIN patients p
    ON a.patient_id = p.patient_id
INNER JOIN users u
    ON a.dentist_id = u.user_id
LEFT JOIN bills b
    ON a.appointment_id =
        b.appointment_id;


-- ============================================================
-- 5. VERIFICATION QUERIES
-- ============================================================

SHOW FUNCTION STATUS
WHERE Db = 'sunrise_dental_clinic'
  AND Name = 'fn_calculate_bill_balance';

SHOW PROCEDURE STATUS
WHERE Db = 'sunrise_dental_clinic'
  AND Name = 'sp_daily_appointment_report';

SHOW TRIGGERS
FROM sunrise_dental_clinic;

SELECT
    *
FROM vw_appointment_billing_summary
ORDER BY appointment_id;