const API = "http://localhost:8080/api";
let hoteles = [], transportes = [], actividades = [];

// 🔐 Obtener cabeceras con autenticación
function getAuthHeaders() {
    const token = localStorage.getItem('jwtToken');
    return {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token}`
    };
}

// 🌎 Cargar destinos al iniciar
async function cargarDestinos() {
    try {
        const response = await fetch(`${API}/destinos`, { headers: getAuthHeaders() });

        if (response.status === 401 || response.status === 403) {
            window.location.href = "/public/login";
            return;
        }
        if (!response.ok) throw new Error(`Error HTTP cargando destinos: ${response.status}`);

        const destinos = await response.json();
        console.log("🌎 Destinos obtenidos:", destinos);

        const selDestino = document.getElementById("destino");
        selDestino.innerHTML = `<option value="">Selecciona destino...</option>`;
        destinos.forEach(d => {
            const option = document.createElement("option");
            option.value = d.destino_id;
            option.textContent = d.nombre;
            selDestino.appendChild(option);
        });
    } catch (error) {
        console.error("❌ Error cargando destinos:", error);
        alert("No se pudieron cargar los destinos. Intenta nuevamente más tarde.");
    }
}

// 🧭 Manejar cambio de destino
document.getElementById("destino").addEventListener("change", async e => {
    const destinoId = e.target.value;

    if (!destinoId) {
        document.getElementById("seccion-opciones").style.display = "none";
        return;
    }

    document.getElementById("seccion-opciones").style.display = "block";

    const fetchData = async (url) => {
        const response = await fetch(url, { headers: getAuthHeaders() });
        if (response.status === 401 || response.status === 403) {
            window.location.href = "/public/login";
            throw new Error("Unauthorized");
        }
        if (!response.ok) throw new Error(`HTTP error! status: ${response.status}`);
        return response.json();
    };

    try {
        // Cargar datos de apoyo
        const [hot, trans, act] = await Promise.all([
            fetchData(`${API}/hoteles`),
            fetchData(`${API}/transportes`),
            fetchData(`${API}/actividades`)
        ]);

        hoteles = hot;
        transportes = trans;
        actividades = act;

        // Cargar datos del destino específico
        const response = await fetch(`${API}/destinos/${destinoId}`, { headers: getAuthHeaders() });
        if (!response.ok) throw new Error(`Error HTTP: ${response.status}`);

        const destino = await response.json();
        console.log("📍 Destino obtenido:", destino);

        // Mostrar solo los elementos propios del destino seleccionado
        cargarSelect("hotel", destino.hoteles || [], (h) => `${h.nombre} - Adulto: $${h.tarifaAdulto} / Niño: $${h.tarifaNino}`);
        cargarSelect("transporte", destino.transportes || [], (t) => `${t.tipo} - ${t.empresa} ($${t.precio})`);
        cargarSelect("actividades", destino.actividades || [], (a) => `${a.nombre} ($${a.precio})`);
    } catch (error) {
        if (error.message !== "Unauthorized") {
            console.error("❌ Error cargando opciones del destino:", error);
            alert("No se pudieron cargar los datos del destino. Intenta nuevamente más tarde.");
        }
    }
});

// 🧱 Función genérica para cargar <select>
function cargarSelect(id, items, labelFn) {
    const select = document.getElementById(id);
    if (!select) return;

    select.innerHTML = `<option value="">Selecciona una opción...</option>`;

    items.forEach(item => {
        const option = document.createElement("option");
        option.value = item.id || item[`${id}_id`] || "";
        option.textContent = labelFn(item);

        // Guardar precios/tarifas en dataset para cálculos
        if (item.precio) option.dataset.precio = item.precio;
        if (item.tarifaAdulto) option.dataset.tarifaAdulto = item.tarifaAdulto;
        if (item.tarifaNino) option.dataset.tarifaNino = item.tarifaNino;

        select.appendChild(option);
    });
}

// 💰 Calcular total dinámico
function calcularTotal() {
    let total = 0;
    const adultos = parseInt(document.getElementById("numAdultos").value || 0);
    const ninos = parseInt(document.getElementById("numNinos").value || 0);
    const hotel = document.getElementById("hotel").selectedOptions[0];
    const fechaInicio = document.getElementById("fechaInicio").value;
    const fechaFin = document.getElementById("fechaFin").value;

    // Calcular cantidad de noches
    let noches = 1;
    if (fechaInicio && fechaFin) {
        const inicio = new Date(fechaInicio);
        const fin = new Date(fechaFin);
        const diffMs = fin - inicio;
        noches = Math.max(1, Math.ceil(diffMs / (1000 * 60 * 60 * 24)));
    }

    // Calcular total de hotel (por noches)
    if (hotel) {
        total += ((parseFloat(hotel.dataset.tarifaAdulto || 0) * adultos) +
                  (parseFloat(hotel.dataset.tarifaNino || 0) * ninos)) * noches;
    }

    // Transporte (precio fijo por persona seleccionada)
    [...document.getElementById("transporte").selectedOptions].forEach(t => {
        total += parseFloat(t.dataset.precio || 0);
    });

    // Actividades (precio por persona)
    [...document.getElementById("actividades").selectedOptions].forEach(a => {
        total += parseFloat(a.dataset.precio || 0) * (adultos + ninos);
    });

    document.getElementById("total").textContent = total.toLocaleString();
    return total;
}

// 🎧 Escuchar cambios en todos los campos relevantes
["hotel", "transporte", "actividades", "numAdultos", "numNinos", "fechaInicio", "fechaFin", "origen", "destino"].forEach(id => {
    const el = document.getElementById(id);
    if (el) {
        el.addEventListener("change", calcularTotal);
        el.addEventListener("input", calcularTotal);
    }
});

// 🎒 Paquetes prearmados
function cargarPaquetesPrearmados() {
    const lista = [
        { nombre: "Plan Familiar en Coveñas", descripcion: "3 días con hotel y lancha", precio: 780000 },
    ];

    const cont = document.getElementById("paquetesPrearmados");
    cont.innerHTML = ""; // Limpiar contenido anterior
    lista.forEach(p => cont.innerHTML += `
        <div class="col-md-4 mb-3">
            <div class="card h-100 p-3 shadow-sm">
                <h5>${p.nombre}</h5>
                <p>${p.descripcion}</p>
                <div class="fw-bold text-success">$${p.precio.toLocaleString()} COP</div>
            </div>
        </div>
    `);
}

// 🚀 Inicialización
document.addEventListener("DOMContentLoaded", function() {
    cargarDestinos();
    cargarPaquetesPrearmados();
});
