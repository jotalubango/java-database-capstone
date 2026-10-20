// adminDashboardFunctions.js

import { saveDoctor } from './services/doctorServices.js';

/* Function: adminAddDoctor
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
  const name = document.getElementById('doctorName')?.value.trim();
  const email = document.getElementById('doctorEmail')?.value.trim();
  const phone = document.getElementById('doctorPhone')?.value.trim();
  const password = document.getElementById('doctorPassword')?.value;
  const specialty = document.getElementById('doctorSpecialty')?.value.trim();
  const availableTimes = Array.from(
            document.querySelectorAll('.availability-container input[name="availability"]:checked')
        ).map(cb => cb.value);

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