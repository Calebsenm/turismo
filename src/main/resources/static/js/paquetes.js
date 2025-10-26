// Listener para cerrar sesión en cualquier vista
document.addEventListener('DOMContentLoaded', () => {
    const logoutButton = document.getElementById('logout-button');
    if (logoutButton) {
        logoutButton.addEventListener('click', () => {
            localStorage.removeItem('jwtToken');
            window.location.href = '/public/login';
        });
    }
});

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

        hoteles = hot.map(h => ({ ...h, id: h.hotel_id || h.id }));
        transportes = trans;
        actividades = act;

        // Cargar datos del destino específico
        const response = await fetch(`${API}/destinos/${destinoId}`, { headers: getAuthHeaders() });
        if (!response.ok) throw new Error(`Error HTTP: ${response.status}`);

        const destino = await response.json();
        console.log("📍 Destino obtenido:", destino);

        // Mostrar solo los elementos propios del destino seleccionado
        cargarSelect("hotel", hoteles, (h) => `${h.nombre} - Adulto: ${h.tarifaAdulto} / Niño: ${h.tarifaNino}`);
        cargarSelect("transporte", transportes, (t) => `${t.tipo} - ${t.empresa} (${t.precio})`);
        cargarSelect("actividades", actividades, (a) => `${a.nombre} (${a.precio})`);
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
async function cargarPaquetesPrearmados() {
    try {
        const response = await fetch(`${API}/paquetes`, { headers: getAuthHeaders() });

        if (response.status === 401 || response.status === 403) {
            // No mostramos error, simplemente no se cargan los paquetes
            // porque esta sección puede ser visible para usuarios no logueados.
            console.warn("Usuario no autenticado, no se pueden cargar paquetes prearmados.");
            const cont = document.getElementById("paquetesPrearmados");
            cont.innerHTML = '<p class="text-center text-muted">Inicia sesión para ver los paquetes prearmados.</p>';
            return;
        }

        if (!response.ok) {
            throw new Error(`Error HTTP: ${response.status}`);
        }
        const paquetes = await response.json();

        const cont = document.getElementById("paquetesPrearmados");
        cont.innerHTML = ""; // Limpiar contenido anterior

        if (paquetes.length === 0) {
            cont.innerHTML = '<p class="text-center text-muted">No hay paquetes prearmados disponibles en este momento.</p>';
            return;
        }

        paquetes.forEach(p => {
            const paqueteCard = `
                <div class="col-md-4 mb-4">
                    <div class="card h-100 shadow-sm paquete-card">
                        <div class="card-body d-flex flex-column">
                            <h5 class="card-title text-primary">${p.nombre}</h5>
                            <p class="card-text flex-grow-1">${p.descripcion}</p>
                            <ul class="list-unstyled mt-2 mb-3">
                                <li><small><strong>Origen:</strong> ${p.origen}</small></li>
                                <li><small><strong>Tipo:</strong> ${p.tipoPaquete}</small></li>
                            </ul>
                            <div class="text-end">
                                <p class="h5 fw-bold text-success mb-3">${(p.costoTotal || 0).toLocaleString('es-CO', { style: 'currency', currency: 'COP' })}</p>
                                <button class="btn btn-secondary btn-sm" onclick="window.open('${API}/paquetes/${p.paquete_id}/pdf')">
                                    Generar PDF
                                </button>
                            </div>
                        </div>
                    </div>
                </div>
            `;
            cont.innerHTML += paqueteCard;
        });
    } catch (error) {
        console.error("❌ Error cargando paquetes prearmados:", error);
        const cont = document.getElementById("paquetesPrearmados");
        cont.innerHTML = '<p class="text-center text-danger">No se pudieron cargar los paquetes.</p>';
    }
}

// 🚀 Inicialización
document.addEventListener("DOMContentLoaded", function() {
    cargarDestinos();
    cargarPaquetesPrearmados();
});

// 💾 Manejar clic en Guardar Paquete
document.getElementById("btnGuardar").addEventListener("click", async () => {
    const mensajeDiv = document.getElementById("mensaje");
    mensajeDiv.textContent = ""; // Limpiar mensajes anteriores

    // Recopilar datos del formulario
    const origen = document.getElementById("origen").value;
    const destinoId = document.getElementById("destino").value;
    const hotelId = document.getElementById("hotel").value;
    const transporteId = document.getElementById("transporte").value;
    const actividadId = document.getElementById("actividades").value;
    const fechaInicio = document.getElementById("fechaInicio").value;
    const fechaFin = document.getElementById("fechaFin").value;
    const numAdultos = parseInt(document.getElementById("numAdultos").value);
    const numNinos = parseInt(document.getElementById("numNinos").value);
    const costoTotal = parseFloat(document.getElementById("total").textContent.replace(/\./g, ''));

    // Validación simple
    if (!origen || !destinoId || !hotelId || !fechaInicio || !fechaFin) {
        mensajeDiv.textContent = "Por favor, completa todos los campos obligatorios (origen, destino, hotel y fechas).";
        mensajeDiv.className = "alert alert-danger mt-3";
        return;
    }

    // Construir el objeto del paquete
    const paquete = {
        nombre: `Paquete a ${document.getElementById("destino").selectedOptions[0].text}`,
        descripcion: `Paquete personalizado para ${numAdultos} adultos y ${numNinos} niños.`,
        origen: origen,
        fechaInicio: fechaInicio,
        fechaFin: fechaFin,
        numAdultos: numAdultos,
        numNinos: numNinos,
        costoTotal: costoTotal,
        tipoPaquete: "Personalizado",
        destino: { destino_id: destinoId },
        hoteles: [{ hotel_id: hotelId }],
        transportes: transporteId ? [{ transporte_id: transporteId }] : [],
        actividades: actividadId ? [{ actividad_id: actividadId }] : []
    };

    try {
        const response = await fetch(`${API}/paquetes`, {
            method: 'POST',
            headers: getAuthHeaders(),
            body: JSON.stringify(paquete)
        });

        if (response.status === 401 || response.status === 403) {
            window.location.href = "/public/login";
            return;
        }

        if (!response.ok) {
            const errorData = await response.json();
            throw new Error(`Error del servidor: ${errorData.message || response.status}`);
        }

        const nuevoPaquete = await response.json();
        mensajeDiv.textContent = `¡Paquete guardado con éxito! ID del paquete: ${nuevoPaquete.paquete_id}`;
        mensajeDiv.className = "alert alert-success mt-3";

        // Opcional: limpiar formulario o redirigir
        // window.location.href = "/mis-paquetes";

    } catch (error) {
        console.error("❌ Error al guardar el paquete:", error);
        mensajeDiv.textContent = `No se pudo guardar el paquete. ${error.message}`;
        mensajeDiv.className = "alert alert-danger mt-3";
    }
});