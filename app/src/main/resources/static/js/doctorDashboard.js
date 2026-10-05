/*
  Import getAllAppointments to fetch appointments from the backend
  Import createPatientRow to generate a table row for each patient appointment
*/
import { getAllAppointments } from './services/appointmentRecordService.js';
import { createPatientRow } from './components/patientRows.js';

// Helper function to get today's date formatted as YYYY-MM-DD
function getTodayDateString() {
  const today = new Date();
  const year = today.getFullYear();
  const month = String(today.getMonth() + 1).padStart(2, '0');
  const day = String(today.getDate()).padStart(2, '0');
  return `\({year}-\){month}-${day}`;
}

// Initialize Global Variables
/*
  Get the table body where patient rows will be added
  Initialize selectedDate with today's date in 'YYYY-MM-DD' format
  Get the saved token from localStorage (used for authenticated API calls)
  Initialize patientName to null (used for filtering by name)
*/
const appointmentTableBody = document.getElementById('patientTableBody');
let selectedDate = getTodayDateString();
const token = localStorage.getItem('token');
let patientName = null;

// Setup Search Bar Functionality
/*
  Add an 'input' event listener to the search bar
  On each keystroke:
    - Trim and check the input value
    - If not empty, use it as the patientName for filtering
    - Else, reset patientName to "null" (as expected by backend)
    - Reload the appointments list with the updated filter
*/

const searchBar = document.getElementById('searchBar');
if (searchBar) {
  searchBar.addEventListener('input', (event) => {
    const value = event.target.value.trim();
    patientName = value !== '' ? value : 'null';
    loadAppointments();
  });
}

/*
  Add a click listener to the "Today" button
  When clicked:
    - Set selectedDate to today's date
    - Update the date picker UI to match
    - Reload the appointments for today


  Add a change event listener to the date picker
  When the date changes:
    - Update selectedDate with the new value
    - Reload the appointments for that specific date

*/

const todayButton = document.getElementById('todayButton');
const datePicker = document.getElementById('datePicker');

// Set initial date picker value to today's date if element exists
if (datePicker) {
  datePicker.value = selectedDate;
}

if (todayButton) {
  todayButton.addEventListener('click', () => {
    selectedDate = getTodayDateString();
    if (datePicker) {
      datePicker.value = selectedDate;
    }
    loadAppointments();
  });
}

if (datePicker) {
  datePicker.addEventListener('change', (event) => {
    selectedDate = event.target.value;
    loadAppointments();
  });
}

/*
  Function: loadAppointments
  Purpose: Fetch and display appointments based on selected date and optional patient name

  Step 1: Call getAllAppointments with selectedDate, patientName, and token
  Step 2: Clear the table body content before rendering new rows

  Step 3: If no appointments are returned:
    - Display a message row: "No Appointments found for today."

  Step 4: If appointments exist:
    - Loop through each appointment and construct a 'patient' object with id, name, phone, and email
    - Call createPatientRow to generate a table row for the appointment
    - Append each row to the table body

  Step 5: Catch and handle any errors during fetch:
    - Show a message row: "Error loading appointments. Try again later."
*/
export async function loadAppointments() {
  const appointmentTableBody = document.getElementById('patientTableBody');
  if (!appointmentTableBody) return;

  try {
    // Step 1: Call getAllAppointments with selectedDate, patientName, and token
    const appointments = await getAllAppointments(selectedDate, patientName, token);

    // Step 2: Clear the table body content before rendering new rows
    appointmentTableBody.innerHTML = '';

    // Step 3: If no appointments are returned, display a message row
    if (!appointments || appointments.length === 0) {
      appointmentTableBody.innerHTML = `
        <tr>
          <td colspan="100%" style="text-align: center;">No Appointments found for today.</td>
        </tr>
      `;
      return;
    }

    // Step 4: If appointments exist, process and render each row
    appointments.forEach((appointment) => {
      // Construct a 'patient' object with id, name, phone, and email
      const patient = {
        id: appointment.patientId || appointment.id,
        name: appointment.patientName || appointment.name,
        phone: appointment.patientPhone || appointment.phone,
        email: appointment.patientEmail || appointment.email
      };

      // Call createPatientRow to generate a table row and append it to table body
      const rowElement = createPatientRow(patient, appointment);
      appointmentTableBody.appendChild(rowElement);
    });

  } catch (error) {
    // Step 5: Catch and handle any errors during fetch
    console.error('Error loading appointments:', error);
    appointmentTableBody.innerHTML = `
      <tr>
        <td colspan="100%" style="text-align: center; color: red;">
          Error loading appointments. Try again later.
        </td>
      </tr>
    `;
  }
}

/*
  When the page is fully loaded (DOMContentLoaded):
    - Call renderContent() (assumes it sets up the UI layout)
    - Call loadAppointments() to display today's appointments by default
*/
document.addEventListener('DOMContentLoaded', () => {
  // Sets up the initial UI layout
  renderContent();

  // Displays today's appointments by default
  loadAppointments();
});

 /*
  Import getAllAppointments to fetch appointments from the backend
  Import createPatientRow to generate a table row for each patient appointment


  Get the table body where patient rows will be added
  Initialize selectedDate with today's date in 'YYYY-MM-DD' format
  Get the saved token from localStorage (used for authenticated API calls)
  Initialize patientName to null (used for filtering by name)


  Add an 'input' event listener to the search bar
  On each keystroke:
    - Trim and check the input value
    - If not empty, use it as the patientName for filtering
    - Else, reset patientName to "null" (as expected by backend)
    - Reload the appointments list with the updated filter


  Add a click listener to the "Today" button
  When clicked:
    - Set selectedDate to today's date
    - Update the date picker UI to match
    - Reload the appointments for today


  Add a change event listener to the date picker
  When the date changes:
    - Update selectedDate with the new value
    - Reload the appointments for that specific date


  Function: loadAppointments
  Purpose: Fetch and display appointments based on selected date and optional patient name

  Step 1: Call getAllAppointments with selectedDate, patientName, and token
  Step 2: Clear the table body content before rendering new rows

  Step 3: If no appointments are returned:
    - Display a message row: "No Appointments found for today."

  Step 4: If appointments exist:
    - Loop through each appointment and construct a 'patient' object with id, name, phone, and email
    - Call createPatientRow to generate a table row for the appointment
    - Append each row to the table body

  Step 5: Catch and handle any errors during fetch:
    - Show a message row: "Error loading appointments. Try again later."


  When the page is fully loaded (DOMContentLoaded):
    - Call renderContent() (assumes it sets up the UI layout)
    - Call loadAppointments() to display today's appointments by default
*/

