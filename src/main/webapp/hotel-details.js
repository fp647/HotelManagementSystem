let currentHotelId = null; // Global variable to store the current hotel ID

function getQueryParam(name) {
  const params = new URLSearchParams(window.location.search);
  return params.get(name);
}

document.addEventListener("DOMContentLoaded", () => {
  const hotelId = getQueryParam("id");
  currentHotelId = hotelId; 

  if (!hotelId) {
    document.getElementById("hotelName").textContent = "Invalid Hotel ID";
    return;
  }

  fetchHotelDetails(hotelId);

  // Event listeners for Add/Cancel Service Form
  document.getElementById('addServiceBtn').addEventListener('click', showAddServiceForm);
  document.getElementById('cancelServiceFormBtn').addEventListener('click', hideServiceForm);
  document.getElementById('serviceForm').addEventListener('submit', handleServiceFormSubmit);

  // Initial fetch for rooms (assuming default tab is rooms)
  fetchRooms(currentHotelId);
});

function fetchHotelDetails(hotelId) {
  fetch(`http://localhost:8080/HotelManagementSystem/api/hotels?id=${hotelId}`)
    .then(response => {
      if (!response.ok) {
        throw new Error("Hotel not found");
      }
      return response.json();
    })
    .then(hotel => {
      const imageUrl = hotel.picture
        ? `http://localhost:8080/HotelManagementSystem/${hotel.picture}`
        : 'default-image.jpg';

      document.getElementById("hotelName").textContent = hotel.name;
      document.getElementById("hotelDescription").textContent = hotel.description || ''; // Assuming hotel might have a description
      document.getElementById("hotelAddress").textContent = hotel.address;
      document.getElementById("hotelCountry").textContent = hotel.country;
      document.getElementById("hotelCity").textContent = hotel.city;
      document.getElementById("hotelEmail").textContent = hotel.email;
      document.getElementById("hotelPhone").textContent = hotel.phone;
      document.getElementById("hotelImage").src = imageUrl;
      document.getElementById("addRoomBtn").href = `new-room.html?hotelId=${hotel.id}`;

      // Set the hotel ID for the delete button
      document.querySelector(".delete-btn").onclick = () => deleteHotel(hotel.id);
      // Ensure the edit button correctly calls startHotelEdit with the hotel's ID
      document.querySelector(".action-btn.save-btn[onclick='startHotelEdit()']").onclick = () => startHotelEdit(hotel.id);
    })
    .catch(error => {
      console.error("Error fetching hotel details:", error);
      document.getElementById("hotelName").textContent = "Error loading hotel details";
      alert("Failed to load hotel details: " + error.message);
    });
}

function startHotelEdit(hotelId) {
  fetch(`http://localhost:8080/HotelManagementSystem/api/hotels?id=${hotelId}`)
    .then(response => {
      if (!response.ok) {
        throw new Error("Failed to fetch hotel for editing");
      }
      return response.json();
    })
    .then(hotel => {
      const container = document.querySelector(".hotel-details-text");
      container.innerHTML = `
	  	<label>Name:</label>
	  	<input type="text" id="editName" value="${hotel.name || ''}"/>
		
        <label>Address:</label>
        <input type="text" id="editAddress" value="${hotel.address || ''}"/>

        <label>Country:</label>
        <input type="text" id="editCountry" value="${hotel.country || ''}"/>

        <label>City:</label>
        <input type="text" id="editCity" value="${hotel.city || ''}"/>

        <label>Phone:</label>
        <input type="text" id="editPhone" value="${hotel.phone || ''}"/>

        <label>Email:</label>
        <input type="email" id="editEmail" value="${hotel.email || ''}"/>

        <label>Picture URL:</label>
        <input type="text" id="editPicture" value="${hotel.picture || ''}"/>

        <div class="hotel-actions">
            <button onclick="saveHotel(${hotel.id})" class="action-btn save-btn">💾 Save</button>
            <button onclick="cancelHotelEdit()" class="action-btn cancel-btn">❌ Cancel</button>
        </div>
      `;
    })
    .catch(error => {
      console.error("Error starting edit:", error);
      alert("Failed to load hotel data for editing.");
    });
}

function saveHotel(id) {
  const updatedHotel = {
    id: id,
    name: document.getElementById("editName").value.trim(),
    address: document.getElementById("editAddress").value.trim(),
    country: document.getElementById("editCountry").value.trim(),
    city: document.getElementById("editCity").value.trim(),
    phone: document.getElementById("editPhone").value.trim(),
    email: document.getElementById("editEmail").value.trim(),
    picture: document.getElementById("editPicture").value.trim(),
  };

  fetch(`http://localhost:8080/HotelManagementSystem/api/hotels`, {
    method: "PUT",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify(updatedHotel),
  })
    .then(response => {
      if (!response.ok) {
        return response.text().then(text => { throw new Error(text || "Failed to update hotel"); });
      }
      alert("Hotel details updated successfully!");
      fetchHotelDetails(id);
      location.reload();
    })
    .catch(error => {
      console.error("Error updating hotel:", error);
      alert("Failed to update hotel: " + error.message);
    });
}

function cancelHotelEdit() {
  location.reload();
}

function deleteHotel(hotelId) {
  if (!confirm("Are you sure you want to delete this hotel? This action cannot be undone.")) {
    return;
  }

  fetch(`http://localhost:8080/HotelManagementSystem/api/hotels?id=${hotelId}`, {
    method: 'DELETE',
  })
    .then(response => {
      if (!response.ok) {
        return response.text().then(text => { throw new Error(text || "Failed to delete hotel"); });
      }
      alert("Hotel deleted successfully!");
      window.location.href = "hotel.html"; // Redirect to hotels list
    })
    .catch(error => {
      console.error("Error deleting hotel:", error);
      alert("Failed to delete hotel: " + error.message);
    });
}

// Room functions
function fetchRooms(hotelId) {
  fetch(`http://localhost:8080/HotelManagementSystem/api/rooms?hotelId=${hotelId}`)
    .then(response => {
      if (!response.ok) {
        throw new Error("Failed to fetch rooms");
      }
      return response.json();
    })
    .then(rooms => {
      renderRooms(rooms);
    })
    .catch(error => {
      console.error("Error fetching rooms:", error);
      document.getElementById("roomList").innerHTML = `<p style="text-align: center; color: red;">Failed to load rooms: ${error.message}</p>`;
    });
}

function renderRooms(rooms) {
  const roomList = document.getElementById("roomList");
  roomList.innerHTML = ""; // Clear existing rooms

  if (rooms.length === 0) {
    roomList.innerHTML = '<p style="text-align: center;">No rooms found for this hotel.</p>';
    return;
  }

  rooms.forEach(room => {
    const roomCard = document.createElement("div");
    roomCard.className = "room-card";
    roomCard.setAttribute("data-id", room.id);

    roomCard.innerHTML = `
            <h3>Room Number: <span id="roomNumber-${room.id}">${room.roomNumber}</span></h3>
            <p><strong>Category:</strong> <span id="category-${room.id}">${room.category}</span></p>
            <p><strong>Size:</strong> <span id="size-${room.id}">${room.size}</span></p>
            <p><strong>Floor:</strong> <span id="floor-${room.id}">${room.floor}</span></p>
            <p><strong>Price:</strong> $<span id="basePrice-${room.id}">${room.basePrice.toFixed(2)}</span></p>
            <p><strong>Availability:</strong> <span id="available-${room.id}">${room.available ? 'Yes' : 'Yes'}</span></p>
            <p><strong>Max Occupancy:</strong> <span id="maxOccupancy-${room.id}">${room.maxOccupancy}</span></p>
            <p><strong>Amenities:</strong> <span id="amenities-${room.id}">${room.amenities}</span></p>
            <div class="room-actions">
                <button class="edit-btn" onclick="editRoom(${room.id})">✎ Edit</button>
                <button class="delete-btn" onclick="deleteRoom(${room.id})">🗑 Delete</button>
            </div>
        `;
    roomList.appendChild(roomCard);
  });
}

function editRoom(id) {
  const roomCard = document.querySelector(`.room-card[data-id="${id}"]`);
  const roomNumber = roomCard.querySelector(`#roomNumber-${id}`).textContent;
  const category = roomCard.querySelector(`#category-${id}`).textContent;
  const size = roomCard.querySelector(`#size-${id}`).textContent;
  const floor = roomCard.querySelector(`#floor-${id}`).textContent;
  const basePrice = roomCard.querySelector(`#basePrice-${id}`).textContent;
  const available = roomCard.querySelector(`#available-${id}`).textContent === 'Available';
  const maxOccupancy = roomCard.querySelector(`#maxOccupancy-${id}`).textContent;
  const amenities = roomCard.querySelector(`#amenities-${id}`).textContent;


  roomCard.innerHTML = `
        <h3>Room Number: <input type="text" id="editRoomNumber-${id}" value="${roomNumber}"></h3>
        <p><strong>Category:</strong> <input type="text" id="editCategory-${id}" value="${category}"></p>
        <p><strong>Size:</strong> <input type="text" id="editSize-${id}" value="${size}"></p>
        <p><strong>Floor:</strong> <input type="number" id="editFloor-${id}" value="${floor}"></p>
        <p><strong>Price:</strong> $<input type="number" step="0.01" id="editBasePrice-${id}" value="${parseFloat(basePrice).toFixed(2)}"></p>
        <p><strong>Availability:</strong>
            <select id="editIsAvailable-${id}">
                <option value="true" ${available ? 'selected' : ''}>Available</option>
                <option value="false" ${!available ? 'selected' : ''}>Unavailable</option>
            </select>
        </p>
        <p><strong>Max Occupancy:</strong> <input type="number" id="editMaxOccupancy-${id}" value="${maxOccupancy}"></p>
        <p><strong>Amenities:</strong> <input type="text" id="editAmenities-${id}" value="${amenities}"></p>
        <div class="room-actions">
            <button class="save-btn" onclick="saveRoom(${id})">💾 Save</button>
            <button class="cancel-btn" onclick="cancelEdit(${id})">✕ Cancel</button>
        </div>
    `;
}

function saveRoom(id) {
  const updatedRoom = {
    id: id,
    hotelId: currentHotelId,
    roomNumber: document.getElementById(`editRoomNumber-${id}`).value,
    floor: parseInt(document.getElementById(`editFloor-${id}`).value),
    category: document.getElementById(`editCategory-${id}`).value,
    size: document.getElementById(`editSize-${id}`).value,
    basePrice: parseFloat(document.getElementById(`editBasePrice-${id}`).value),
    available: document.getElementById(`editIsAvailable-${id}`).value === "true",
    maxOccupancy: parseInt(document.getElementById(`editMaxOccupancy-${id}`).value),
    amenities: document.getElementById(`editAmenities-${id}`).value
  };

  fetch(`http://localhost:8080/HotelManagementSystem/api/rooms`, {
    method: "PUT",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify(updatedRoom),
  })
    .then(response => {
      if (!response.ok) {
        return response.text().then(text => { throw new Error(text || "Failed to update room"); });
      }
      return response.json();
    })
    .then(data => {
      alert("Room updated successfully!");
      fetchRooms(currentHotelId);
    })
    .catch(error => {
      console.error("Error updating room:", error);
      alert("Failed to update room: " + error.message);
    });
}

function cancelEdit(id) {
  fetchRooms(currentHotelId);
}

function deleteRoom(id) {
  if (!confirm("Are you sure you want to delete this room?")) return;

  fetch(`http://localhost:8080/HotelManagementSystem/api/rooms?id=${id}`, {
    method: "DELETE",
  })
    .then(response => {
      if (!response.ok) {
        return response.text().then(text => { throw new Error(text || "Failed to delete room"); });
      }
      alert("Room deleted successfully!");
      fetchRooms(currentHotelId);
    })
    .catch(error => {
      console.error("Error deleting room:", error);
      alert("Failed to delete room: " + error.message);
    });
}

// Service functions
function fetchServices(hotelId) {
  fetch(`http://localhost:8080/HotelManagementSystem/api/services?hotelId=${hotelId}`)
    .then(response => {
      if (!response.ok) {
        throw new Error("Failed to fetch services");
      }
      return response.json();
    })
    .then(services => {
      renderServices(services);
    })
    .catch(error => {
      console.error("Error fetching services:", error);
      document.getElementById("serviceList").innerHTML = `<p style="text-align: center; color: red;">Failed to load services: ${error.message}</p>`;
    });
}

function renderServices(services) {
  const serviceList = document.getElementById("serviceList");
  serviceList.innerHTML = "";

  if (services.length === 0) {
    serviceList.innerHTML = '<p style="text-align: center;">No services found for this hotel. Click "Add Service" to add one.</p>';
    return;
  }

  services.forEach(service => {
    const serviceCard = document.createElement("div");
    serviceCard.className = "service-card";
    serviceCard.setAttribute("data-id", service.id);

    serviceCard.innerHTML = `
            <h3>${(service.name || 'N/A')}</h3>
            <p>Price: €${(service.price || 0).toFixed(2)}</p>
            <div class="service-actions">
                <button class="edit-btn" onclick="editService(${service.id})">✎ Edit</button>
                <button class="delete-btn" onclick="deleteService(${service.id})">🗑 Delete</button>
            </div>
        `;
    serviceList.appendChild(serviceCard);
  });
}

function showAddServiceForm() {
  document.getElementById('serviceFormContainer').style.display = 'block';
  document.getElementById('formTitle').textContent = 'Add New Service';
  document.getElementById('serviceId').value = '';
  document.getElementById('serviceName').value = '';
  document.getElementById('servicePrice').value = '';
  document.getElementById('addServiceBtn').style.display = 'none';
}

function hideServiceForm() {
  document.getElementById('serviceFormContainer').style.display = 'none';
  document.getElementById('addServiceBtn').style.display = 'block';
}

function handleServiceFormSubmit(event) {
  event.preventDefault();

  const serviceId = document.getElementById('serviceId').value;
  const serviceName = document.getElementById('serviceName').value;
  const servicePrice = parseFloat(document.getElementById('servicePrice').value);

  const serviceData = {
    hotelId: parseInt(currentHotelId),
    name: serviceName,
    price: servicePrice
  };

  if (serviceId) {
    serviceData.id = parseInt(serviceId);
    updateService(serviceData);
  } else {
    addService(serviceData);
  }
}

function addService(serviceData) {
  fetch(`http://localhost:8080/HotelManagementSystem/api/services`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(serviceData),
  })
    .then(response => {
      if (!response.ok) {
        return response.text().then(text => { throw new Error(text || "Failed to add service"); });
      }
      return response.json();
    })
    .then(data => {
      alert("Service added successfully!");
      hideServiceForm();
      fetchServices(currentHotelId);
    })
    .catch(error => {
      console.error("Error adding service:", error);
      alert("Failed to add service: " + error.message);
    });
}

/**
 * Populates the service edit form directly from the displayed service card's data.
 * @param {number} id - The ID of the service to edit.
 */
function editService(id) {
  const serviceCard = document.querySelector(`.service-card[data-id="${id}"]`);
  if (!serviceCard) {
    console.error("Service card not found for ID:", id);
    alert("Failed to find service details on the page.");
    return;
  }

  // Extract current service name and price from the card's displayed content
  const serviceNameText = serviceCard.querySelector('h3').textContent;
  const serviceName = serviceNameText === 'N/A' ? '' : serviceNameText;

  const servicePriceText = serviceCard.querySelector('p').textContent;
  const servicePrice = parseFloat(servicePriceText.replace('Price: €', ''));

  serviceCard.innerHTML = `
        <h3>Service Name: <input type="text" id="editServiceName-${id}" value="${serviceName}"></h3>
        <p>Price: €<input type="number" step="0.01" id="editServicePrice-${id}" value="${isNaN(servicePrice) ? '' : servicePrice.toFixed(2)}"></p>
        <div class="service-actions">
            <button class="save-btn" onclick="saveService(${id})">💾 Save</button>
            <button class="cancel-btn" onclick="cancelServiceEdit(${id})">✕ Cancel</button>
        </div>
    `;
}

function saveService(id) {
  const updatedService = {
    id: id,
    hotelId: currentHotelId, 
    name: document.getElementById(`editServiceName-${id}`).value,
    price: parseFloat(document.getElementById(`editServicePrice-${id}`).value)
  };

  fetch(`http://localhost:8080/HotelManagementSystem/api/services`, {
      method: 'PUT',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(updatedService),
    })
    .then(response => {
      if (!response.ok) {
        return response.text().then(text => {
          throw new Error(text || "Failed to update service");
        });
      }
      return response.text();
    })
    .then(message => {
      alert("Service updated successfully!");
      fetchServices(currentHotelId); // Re-fetch all services to refresh the list
    })
    .catch(error => {
      console.error("Error updating service:", error);
      alert("Failed to update service: " + error.message);
    });
}

function cancelServiceEdit(id) {
  fetchServices(currentHotelId); // Re-fetch services to revert changes on cancel
}

function updateService(serviceData) {
  fetch(`http://localhost:8080/HotelManagementSystem/api/services`, {
    method: 'PUT',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(serviceData),
  })
    .then(response => {
      if (!response.ok) {
        return response.text().then(text => { throw new Error(text || "Failed to update service"); });
      }
      return response.text();
    })
    .then(message => {
      alert("Service updated successfully!");
      hideServiceForm();
      fetchServices(currentHotelId);
    })
    .catch(error => {
      console.error("Error updating service:", error);
      alert("Failed to update service: " + error.message);
    });
}

function deleteService(id) {
  if (!confirm("Are you sure you want to delete this service?")) return;

  fetch(`http://localhost:8080/HotelManagementSystem/api/services?id=${id}`, {
    method: 'DELETE',
  })
    .then(response => {
      if (!response.ok) {
        return response.text().then(text => { throw new Error(text || "Failed to delete service"); });
      }
      alert("Service deleted successfully!");
      fetchServices(currentHotelId);
    })
    .catch(error => {
      console.error("Error deleting service:", error);
      alert("Failed to delete service: " + error.message);
    });
}


// Tab switching logic
function switchTab(tabName) {
  document.getElementById("roomsTab").style.display = tabName === "rooms" ? "block" : "none";
  document.getElementById("servicesTab").style.display = tabName === "services" ? "block" : "none";

  document.querySelectorAll(".tab-btn").forEach(btn => {
    btn.classList.remove("active");
  });

  document.querySelector(`.tab-header button[onclick="switchTab('${tabName}')"]`).classList.add("active");

  if (tabName === "services" && currentHotelId) {
    fetchServices(currentHotelId);
    hideServiceForm();
  } else if (tabName === "rooms" && currentHotelId) {
    fetchRooms(currentHotelId);
  }
}