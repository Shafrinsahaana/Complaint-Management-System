# Manual Testing Checklist

## 1. Database Offline Handling
- [ ] **Action**: Stop the Oracle database service (or change the credentials in the `.bat` file to invalid ones).
- [ ] **Action**: Attempt to Register a new citizen.
- [ ] **Expected**: The UI should not freeze. After a moment, a friendly error dialog should appear: "Cannot connect to the database. Is the server running?" (or "Database configuration error: Invalid DB credentials.").
- [ ] **Action**: Attempt to Login.
- [ ] **Expected**: A similar friendly error dialog should appear without crashing the application.
- [ ] **Action**: Restart the database / restore credentials before continuing.

## 2. Citizen Registration
- [ ] **Action**: Open the application, click "Register", and fill in valid details (e.g., Name: `Test User`, Email: `test@example.com`, Password: `password123`, Confirm: `password123`).
- [ ] **Expected**: "Registration successful!" popup. Redirects to Login.
- [ ] **Action**: Attempt to register again with the **exact same email** (`test@example.com`).
- [ ] **Expected**: A warning dialog should appear: "This email is already registered." (Triggers ORA-00001 constraint).

## 3. Invalid Inputs & Login
- [ ] **Action**: Attempt to register with mismatched passwords.
- [ ] **Expected**: Warning: "Password and confirm password must match."
- [ ] **Action**: Attempt to login with wrong password.
- [ ] **Expected**: "Invalid email or password."
- [ ] **Action**: Login as Citizen with `test@example.com` and `password123`.
- [ ] **Expected**: Successfully routes to `CitizenPortalFrame`.

## 4. Lodging a Complaint
- [ ] **Action**: Navigate to the "Lodge Complaint" tab.
- [ ] **Action**: Type more than 500 characters into the description.
- [ ] **Expected**: The character counter turns red. Clicking "Submit Complaint" shows an error: "Description must be under 500 characters."
- [ ] **Action**: Submit a valid complaint (e.g., "Potholes on Main St.").
- [ ] **Expected**: Success popup displaying the generated Complaint ID (e.g., "#102"). The form clears automatically.
- [ ] **Action**: Navigate to "My Complaints" and click "Refresh".
- [ ] **Expected**: The new complaint appears in the table with status "Pending".

## 5. Official Workspace (Updating Status)
- [ ] **Action**: Logout and Login as an Official (e.g. `suresh.verma@civic.gov.in` / `Officer@4321`).
- [ ] **Action**: Select the newly created complaint in the table.
- [ ] **Action**: Change the status to "In Progress", add remarks ("Dispatching team tomorrow"), and click "Save Updates".
- [ ] **Expected**: Success message. The table refreshes, showing the new status. (Behind the scenes, the `official_id` is linked to Suresh).

## 6. Dashboard Analytics
- [ ] **Action**: Click "View Analytics Dashboard" from the Official workspace.
- [ ] **Expected**: 
  - The "TOTAL COMPLAINTS" count reflects the total rows.
  - The "IN PROGRESS" count increases by 1.
  - The pie chart updates its proportions (more Blue for In Progress).
  - The data grid correctly filters via the Search box or Status dropdown.
