-- ==========================================
-- DASHBOARD QUERIES
-- Complaint Management System
-- ==========================================


-- 1. TOTAL NUMBER OF COMPLAINTS

SELECT COUNT(*) AS total_complaints
FROM Complaint;


-- 2. NUMBER OF PENDING COMPLAINTS

SELECT COUNT(*) AS pending_complaints
FROM Complaint
WHERE status = 'Pending';


-- 3. NUMBER OF IN PROGRESS COMPLAINTS

SELECT COUNT(*) AS in_progress_complaints
FROM Complaint
WHERE status = 'In Progress';


-- 4. NUMBER OF RESOLVED COMPLAINTS

SELECT COUNT(*) AS resolved_complaints
FROM Complaint
WHERE status = 'Resolved';


-- 5. NUMBER OF COMPLAINTS BY STATUS

SELECT
    status,
    COUNT(*) AS complaint_count
FROM Complaint
GROUP BY status;


-- 6. NUMBER OF COMPLAINTS BY CATEGORY

SELECT
    category,
    COUNT(*) AS complaint_count
FROM Complaint
GROUP BY category;


-- 7. DISPLAY ALL COMPLAINTS

SELECT
    complaint_id,
    category,
    description,
    complaint_date,
    status
FROM Complaint
ORDER BY complaint_date DESC;


-- 8. SEARCH COMPLAINTS
-- Search by complaint ID, category or description

SELECT
    complaint_id,
    category,
    description,
    complaint_date,
    status
FROM Complaint
WHERE LOWER(TO_CHAR(complaint_id))
          LIKE LOWER('%101%')
   OR LOWER(category)
          LIKE LOWER('%101%')
   OR LOWER(description)
          LIKE LOWER('%101%');


-- 9. DISPLAY PENDING COMPLAINTS

SELECT
    complaint_id,
    category,
    description,
    complaint_date,
    status
FROM Complaint
WHERE status = 'Pending';


-- 10. DISPLAY IN PROGRESS COMPLAINTS

SELECT
    complaint_id,
    category,
    description,
    complaint_date,
    status
FROM Complaint
WHERE status = 'In Progress';


-- 11. DISPLAY RESOLVED COMPLAINTS

SELECT
    complaint_id,
    category,
    description,
    complaint_date,
    status
FROM Complaint
WHERE status = 'Resolved';