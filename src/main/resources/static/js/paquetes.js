const API = "http://localhost:8080/api";
let hoteles = [], transportes = [], actividades = [];

// Función para obtener las cabeceras de autenticación
function getAuthHeaders() {
    const token = localStorage.getItem('jwtToken');
    return {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token}`
    };
}

// Cargar destinos
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
        selDestino.innerHTML = `<option value="">-- Selecciona un destino --</option>`;
        destinos.forEach(d => {
            selDestino.innerHTML += `<option value="${d.destino_id}">${d.nombre}</option>`;
        });
    } catch (error) {
        console.error("❌ Error cargando destinos:", error);
    }
}


// Cargar datos filtrados según destino
document.getElementById("destino").addEventListener("change", async e => {
    const destinoId = e.target.value;
    if (!destinoId) return;

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
        const [hot, trans, act] = await Promise.all([
            fetchData(`${API}/hoteles`),
            fetchData(`${API}/transportes`),
            fetchData(`${API}/actividades`)
        ]);

    hoteles = hot.filter(h => h.destino.destino_id == destinoId);
    transportes = trans.filter(t => t.destino.destino_id == destinoId);
    actividades = act.filter(a => a.destino.destino_id == destinoId);

    cargarSelect("hotel", hoteles, (h) => `${h.nombre} - Adulto: $${h.tarifaAdulto} / Niño: $${h.tarifaNino}`,
        (h) => `data-tarifa-adulto="${h.tarifaAdulto}" data-tarifa-nino="${h.tarifaNino}"`);
    cargarSelect("transporte", transportes, (t) => `${t.tipo} - ${t.empresa} ($${t.precio})`, (t) => `data-precio="${t.precio}"`);
    cargarSelect("actividades", actividades, (a) => `${a.nombre} ($${a.precio})`, (a) => `data-precio="${a.precio}"`);
    } catch (error) {
        if (error.message !== "Unauthorized") {
            console.error("❌ Error cargando opciones del destino:", error);
        }
    }
});

function cargarSelect(id, lista, texto, extraAttr) {
    const sel = document.getElementById(id);
    sel.innerHTML = "";
    lista.forEach(i => sel.innerHTML += `<option value="${i.id || i[Object.keys(i)[0]]}" ${extraAttr(i)}>${texto(i)}</option>`);
}

// Calcular total dinámico
function calcularTotal() {
    let total = 0;
    const adultos = parseInt(document.getElementById("numAdultos").value || 0);
    const ninos = parseInt(document.getElementById("numNinos").value || 0);
    const hotel = document.getElementById("hotel").selectedOptions[0];

    if (hotel) {
        total += (parseFloat(hotel.dataset.tarifaAdulto || 0) * adultos) +
                 (parseFloat(hotel.dataset.tarifaNino || 0) * ninos);
    }

    [...document.getElementById("transporte").selectedOptions].forEach(t => total += parseFloat(t.dataset.precio || 0));
    [...document.getElementById("actividades").selectedOptions].forEach(a => total += parseFloat(a.dataset.precio || 0));

    document.getElementById("total").textContent = total.toLocaleString();
    return total;
}

["hotel", "transporte", "actividades", "numAdultos", "numNinos"].forEach(id =>
    document.getElementById(id).addEventListener("change", calcularTotal)
);

// Guardar paquete
document.getElementById("btnGuardar").addEventListener("click", async () => {
    const paquete = {
    usuario: { user_id: 1 },
    origen: document.getElementById("origen").value,  // 🔹 NUEVO
    destino: { destino_id: document.getElementById("destino").value },
    fechaInicio: document.getElementById("fechaInicio").value,
    fechaFin: document.getElementById("fechaFin").value,
    numAdultos: parseInt(document.getElementById("numAdultos").value),
    numNinos: parseInt(document.getElementById("numNinos").value),
    costoTotal: calcularTotal(),
    nombre: "Paquete personalizado",
    descripcion: "Cotización creada por el cliente"
};


    const res = await fetch(`${API}/paquetes`, {
        method: "POST",
        headers: getAuthHeaders(),
        body: JSON.stringify(paquete)
    });

    const mensaje = document.getElementById("mensaje");
    if (res.ok) {
        mensaje.textContent = "✅ Paquete guardado con éxito. Redirigiendo...";
        mensaje.className = "text-success";
        setTimeout(() => window.location.href = '/public/home', 2000); // O a una página de "mis paquetes"
    } else if (res.status === 401 || res.status === 403) {
        window.location.href = "/public/login";
    } else {
        const errorData = await res.json();
        console.error("Error data:", errorData);
        mensaje.textContent = `❌ Error al guardar el paquete: ${errorData.message || 'Intente de nuevo.'}`;
        mensaje.className = "text-danger";
    }
});

// Cargar paquetes prearmados (por ahora estáticos)
function cargarPaquetesPrearmados() {
    const lista = [
        { nombre: "Plan Familiar en Coveñas", descripcion: "3 días con hotel y lancha", precio: 780000 },
        { nombre: "Escapada Romántica Cartagena", descripcion: "2 noches y cena especial", precio: 1150000 },
        { nombre: "Aventura en el Eje Cafetero", descripcion: "Tour café + canopy + hotel", precio: 950000 }
    ];
    const cont = document.getElementById("paquetes-prearmados");
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

cargarDestinos();
cargarPaquetesPrearmados();
