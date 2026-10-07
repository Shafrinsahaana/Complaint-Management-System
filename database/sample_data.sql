-- =============================================================================
-- COMPLAINT MANAGEMENT SYSTEM - SAMPLE SEED DATA
-- Target Database : Oracle Database Free 26ai
-- Script Type     : Test Data Insertion
-- Description     : Inserts realistic citizens, officials, and complaint records
--                   covering Pending, In Progress, and Resolved lifecycle states.
-- =============================================================================

-- Clean up any existing data in reverse order of foreign key dependency (optional)
-- DELETE FROM COMPLAINT;
-- DELETE FROM OFFICIAL;
-- DELETE FROM CITIZEN;

-- =============================================================================
-- 1. SEED CITIZENS
-- =============================================================================
INSERT INTO CITIZEN (name, email, password)
VALUES ('Arun Kumar', 'arun.kumar@example.com', 'ehDREgpRVzh5A/j8x3+vCQ==:hXjRRG3czyZE2UQpUQEqK+KMQ8khWmcyK/Xq6VoFcHg=');

INSERT INTO CITIZEN (name, email, password)
VALUES ('Priya Sharma', 'priya.sharma@example.com', 'hR+ulPJHyaN0Fqvk17LM0w==:hYdEXe7Qe4v4+3QdROWoLogoA8o7FsJRQxrfHCs3GC0=');

INSERT INTO CITIZEN (name, email, password)
VALUES ('Ramesh Patel', 'ramesh.patel@example.com', 'QNWq8wDdGGzAgBnniXmTJw==:CugYn+tn/z1yQdjLItxKFi2y4YQmCn03TivPQhteoiU=');

-- =============================================================================
-- 2. SEED OFFICIALS
-- =============================================================================
INSERT INTO OFFICIAL (name, email, password)
VALUES ('Suresh Verma', 'suresh.verma@civic.gov.in', 'HhiPXD15R46J2As5R/h64Q==:RDOdXYPhS166l+qoCCz8or2zZJluXtLctnEI/EtNm6E=');

INSERT INTO OFFICIAL (name, email, password)
VALUES ('Ananya Desai', 'ananya.desai@civic.gov.in', 'x6LsM+bMTxk2GE0+6iNnog==:+i/hNip/ouC5ko10dI9GuQIQXXmjEr5FcN3A5GmVak0=');

-- =============================================================================
-- 3. SEED COMPLAINTS
-- Uses subqueries on unique emails to dynamically bind valid foreign keys.
-- =============================================================================

-- Case 1: Newly lodged complaint (Pending, Unassigned)
INSERT INTO COMPLAINT (
    citizen_id,
    official_id,
    category,
    description,
    complaint_date,
    status,
    official_remarks,
    resolved_date
) VALUES (
    (SELECT citizen_id FROM CITIZEN WHERE email = 'arun.kumar@example.com'),
    NULL,
    'Roads & Infrastructure',
    'Deep pothole on 4th Main Road near City Hospital causing traffic slowdowns and hazard to two-wheelers.',
    SYSDATE - 5,
    'Pending',
    NULL,
    NULL
);

-- Case 2: In-progress complaint (Assigned to Official Suresh Verma, Under Inspection)
INSERT INTO COMPLAINT (
    citizen_id,
    official_id,
    category,
    description,
    complaint_date,
    status,
    official_remarks,
    resolved_date
) VALUES (
    (SELECT citizen_id FROM CITIZEN WHERE email = 'priya.sharma@example.com'),
    (SELECT official_id FROM OFFICIAL WHERE email = 'suresh.verma@civic.gov.in'),
    'Water Supply',
    'Low water pressure and muddy water supply reported in Sector 8 residential block for 3 consecutive days.',
    SYSDATE - 4,
    'In Progress',
    'Pipeline inspection team dispatched. Temporary valve repair in progress.',
    NULL
);

-- Case 3: Resolved complaint (Assigned to Official Ananya Desai, Fully Addressed)
INSERT INTO COMPLAINT (
    citizen_id,
    official_id,
    category,
    description,
    complaint_date,
    status,
    official_remarks,
    resolved_date
) VALUES (
    (SELECT citizen_id FROM CITIZEN WHERE email = 'ramesh.patel@example.com'),
    (SELECT official_id FROM OFFICIAL WHERE email = 'ananya.desai@civic.gov.in'),
    'Sanitation',
    'Overflowing community garbage bin near Central Market causing severe foul odor.',
    SYSDATE - 8,
    'Resolved',
    'Garbage cleared by the municipal sanitation squad. Secondary bin placed and regular collection schedule restored.',
    SYSDATE - 1
);

-- Case 4: Recent complaint (Pending, Unassigned, Electricity Category)
INSERT INTO COMPLAINT (
    citizen_id,
    official_id,
    category,
    description,
    complaint_date,
    status,
    official_remarks,
    resolved_date
) VALUES (
    (SELECT citizen_id FROM CITIZEN WHERE email = 'arun.kumar@example.com'),
    NULL,
    'Electricity',
    'Streetlight pole #14 flickering continuously and posing an electrical safety risk during rainfall.',
    SYSDATE - 1,
    'Pending',
    NULL,
    NULL
);

-- Persist all inserted records
COMMIT;
