document.addEventListener("DOMContentLoaded", () => {
  const container = document.querySelector(".hotel-list");
  container.innerHTML = "";

  fetch('http://localhost:8080/HotelManagementSystem/api/hotels')
    .then(response => response.json())
    .then(hotels => {
      if (hotels.length === 0) {
        container.innerHTML = "<p style='text-align:center;'>No hotels available.</p>";
        return;
      }

      hotels.forEach(hotel => {
        const card = document.createElement("div");
        card.className = "hotel-card";
        card.innerHTML = `
          <img src="${hotel.picture || 'default-image.jpg'}" alt="${hotel.name}" />
          <h3>${hotel.name}</h3>
          <p>📍 ${hotel.address}</p>
          <p>📞 ${hotel.phone}</p>
          <a href="hotel-details.html?id=${hotel.id}">View Details</a>
        `;
        container.appendChild(card);
      });
    })
    .catch(error => {
      container.innerHTML = "<p style='text-align:center; color:red;'>Failed to load hotels.</p>";
      console.error("Error loading hotels:", error);
    });
});