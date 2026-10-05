/* adminDashboard.js */
/*
  This script handles the admin dashboard functionality for managing doctors:
  - Loads all doctor cards
  - Filters doctors by name, time, or specialty
  - Adds a new doctor via modal form
*/
import { openModal } from "./components/modals.js";
import { getDoctors , filterDoctors , saveDoctor } from "./services/doctorServices.js";
import { createDoctorCard } from "./components/doctorCard.js";
/*
  Attach a click listener to the "Add Doctor" button
  When clicked, it opens a modal form using openModal('addDoctor')
*/
document.getElementById('addDocBtn').addEventListener('click', () => {
 openModal('addDoctor');
});
/*
  When the DOM is fully loaded:
    - Call loadDoctorCards() to fetch and display all doctors
*/
// Trigger loadDoctorCards on page load once the DOM is fully loaded
document.addEventListener("DOMContentLoaded", () => {
loadDoctorCards();
});
/*
  Function: loadDoctorCards
  Purpose: Fetch all doctors and display them as cards

    Call getDoctors() from the service layer
    Clear the current content area
    For each doctor returned:
    - Create a doctor card using createDoctorCard()
    - Append it to the content div

    Handle any fetch errors by logging them
*/
export async function loadDoctorCards() {
  const contentDiv = document.getElementById("content");

  // Guard check in case the container element isn't present
  if (!contentDiv) {
    console.error("Target container element '#content' not found.");
    return;
  }

  // 1. Clear existing content
  contentDiv.innerHTML = "";

  // 2. Fetch doctor list from the backend
  const doctors = await getDoctors();

  // 3. Iterate through results, create cards, and append them
  if (Array.isArray(doctors) && doctors.length > 0) {
    doctors.forEach(doctor => {
      const cardElement = createDoctorCard(doctor);
      contentDiv.appendChild(cardElement);
    });
  } else {
    contentDiv.innerHTML = "";
  }
}

/*
  Attach 'input' and 'change' event listeners to the search bar and filter dropdowns
  On any input change, call filterDoctorsOnChange()
*/
document.addEventListener("DOMContentLoaded", () => {
    document.getElementById("searchBar").addEventListener("input", filterDoctorsOnChange);
    document.getElementById("filterTime").addEventListener("change", filterDoctorsOnChange);
    document.getElementById("filterSpecialty").addEventListener("change", filterDoctorsOnChange);
});

/*
  Function: filterDoctorsOnChange
  Purpose: Filter doctors based on name, available time, and specialty

    Read values from the search bar and filters
    Normalize empty values to null
    Call filterDoctors(name, time, specialty) from the service

    If doctors are found:
    - Render them using createDoctorCard()
    If no doctors match the filter:
    - Show a message: "No doctors found with the given filters."

    Catch and display any errors with an alert
*/
export async function filterDoctorsOnChange() {
  try {
    // 1. Read values from the search bar and filter inputs
    const nameInput = document.getElementById("search-name");
    const timeInput = document.getElementById("filter-time");
    const specialtyInput = document.getElementById("filter-specialty");

    // 2. Normalize empty values (or whitespace-only) to null
    const name = nameInput && nameInput.value.trim() !== "" ? nameInput.value.trim() : null;
    const time = timeInput && timeInput.value.trim() !== "" ? timeInput.value.trim() : null;
    const specialty = specialtyInput && specialtyInput.value.trim() !== "" ? specialtyInput.value.trim() : null;

    // 3. Call filterDoctors service function
    const result = await filterDoctors(name, time, specialty);
    const doctors = result && result.doctors ? result.doctors : [];

    // 4. Render doctors if found, otherwise display no matches message
    const contentDiv = document.getElementById("content");

    if (doctors.length > 0) {
      renderDoctorCards(doctors);
    } else if (contentDiv) {
      contentDiv.innerHTML = "No doctors found with the given filters.";
    }
  } catch (error) {
    // 5. Catch and display any errors with an alert
    console.error("Error filtering doctors:", error);
    alert(error.message || "An error occurred while filtering doctors.");
  }
}

/*
  Function: renderDoctorCards
  Purpose: A helper function to render a list of doctors passed to it

    Clear the content area
    Loop through the doctors and append each card to the content area
*/
export function renderDoctorCards(doctors) {
  const contentDiv = document.getElementById("content");
  if (!contentDiv) return;

  // Clear the content area
  contentDiv.innerHTML = "";

  // Loop through the doctors and append each card to the content area
  doctors.forEach(doctor => {
    const cardElement = createDoctorCard(doctor);
    contentDiv.appendChild(cardElement);
  });
}

/*
  Function: adminAddDoctor
  Purpose: Collect form data and add a new doctor to the system

    Collect input values from the modal form
    - Includes name, email, phone, password, specialty, and available times

    Retrieve the authentication token from localStorage
    - If no token is found, show an alert and stop execution

    Build a doctor object with the form values

    Call saveDoctor(doctor, token) from the service

    If save is successful:
    - Show a success message
    - Close the modal and reload the page

    If saving fails, show an error message
*/
export async function adminAddDoctor(event) {
  // Prevent default form submission if triggered by a submit event
  if (event && event.preventDefault) {
    event.preventDefault();
  }

  // 1. Retrieve the authentication token from localStorage
  const token = localStorage.getItem('token');
  if (!token) {
    alert('Authentication token not found. Please log in again.');
    return;
  }

  // 2. Collect input values from the modal form
  const name = document.getElementById('doctor-name')?.value.trim();
  const email = document.getElementById('doctor-email')?.value.trim();
  const phone = document.getElementById('doctor-phone')?.value.trim();
  const password = document.getElementById('doctor-password')?.value;
  const specialty = document.getElementById('doctor-specialty')?.value.trim();
  const availableTimes = document.getElementById('doctor-times')?.value.trim();

  // 3. Build the doctor object
  const doctor = {
    name,
    email,
    phone,
    password,
    specialty,
    availableTimes
  };

  try {
    // 4. Call saveDoctor service function
    const result = await saveDoctor(doctor, token);

    // 5. Handle success or failure response
    if (result && result.success) {
      alert(result.message || 'Doctor added successfully!');

      // Close modal (adjust element ID/method based on your UI library or implementation)
      const modal = document.getElementById('add-doctor-modal');
      if (modal) {
        modal.style.display = 'none';
      }

      // Reload the page to display updated data
      window.location.reload();
    } else {
      alert(result?.message || 'Failed to add doctor. Please try again.');
    }
  } catch (error) {
    console.error('Error adding doctor:', error);
    alert('An unexpected error occurred while adding the doctor.');
  }
}

/*
  This script handles the admin dashboard functionality for managing doctors:
  - Loads all doctor cards
  - Filters doctors by name, time, or specialty
  - Adds a new doctor via modal form


  Attach a click listener to the "Add Doctor" button
  When clicked, it opens a modal form using openModal('addDoctor')


  When the DOM is fully loaded:
    - Call loadDoctorCards() to fetch and display all doctors


  Function: loadDoctorCards
  Purpose: Fetch all doctors and display them as cards

    Call getDoctors() from the service layer
    Clear the current content area
    For each doctor returned:
    - Create a doctor card using createDoctorCard()
    - Append it to the content div

    Handle any fetch errors by logging them


  Attach 'input' and 'change' event listeners to the search bar and filter dropdowns
  On any input change, call filterDoctorsOnChange()


  Function: filterDoctorsOnChange
  Purpose: Filter doctors based on name, available time, and specialty

    Read values from the search bar and filters
    Normalize empty values to null
    Call filterDoctors(name, time, specialty) from the service

    If doctors are found:
    - Render them using createDoctorCard()
    If no doctors match the filter:
    - Show a message: "No doctors found with the given filters."

    Catch and display any errors with an alert


  Function: renderDoctorCards
  Purpose: A helper function to render a list of doctors passed to it

    Clear the content area
    Loop through the doctors and append each card to the content area


  Function: adminAddDoctor
  Purpose: Collect form data and add a new doctor to the system

    Collect input values from the modal form
    - Includes name, email, phone, password, specialty, and available times

    Retrieve the authentication token from localStorage
    - If no token is found, show an alert and stop execution

    Build a doctor object with the form values

    Call saveDoctor(doctor, token) from the service

    If save is successful:
    - Show a success message
    - Close the modal and reload the page

    If saving fails, show an error message
*/
