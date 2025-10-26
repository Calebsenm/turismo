const API = "http://localhost:8080/api";
let hoteles = [], transportes = [], actividades = [];

// 🌎 Cargar destinos
async function cargarDestinos() {
    try {
        const response = await fetch(`${API}/destinos`);
        if (!response.ok) throw new Error(`Error HTTP: ${response.status}`);
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

// 🔹 Cargar datos filtrados según destino
document.getElementById("destino").addEventListener("change", async e => {
    const destinoId = e.target.value;
    if (!destinoId) {
        document.getElementById("seccion-opciones").style.display = "none";
        return;
    }

    document.getElementById("seccion-opciones").style.display = "block";

    try {
        const response = await fetch(`${API}/destinos/${destinoId}`);
        if (!response.ok) throw new Error(`Error HTTP: ${response.status}`);

        const destino = await response.json();
        console.log("Destino obtenido:", destino);

        // Manejar datos devueltos, incluso si están vacíos
        cargarSelect("hotel", destino.hoteles || [], "Selecciona un hotel");
        cargarSelect("transporte", destino.transportes || [], "Selecciona un transporte");
        cargarSelect("actividades", destino.actividades || [], "Selecciona actividades");

    } catch (err) {
        console.error("❌ Error cargando datos del destino:", err);
        alert("No se pudieron cargar los datos del destino. Intenta nuevamente más tarde.");
    }
});

function cargarSelect(id, lista, texto) {
    const sel = document.getElementById(id);
    sel.innerHTML = ""; // Limpia el select

    if (!lista || lista.length === 0) {
        sel.innerHTML = `<option value="">No hay opciones disponibles</option>`;
        return;
    }

    sel.innerHTML = `<option value="">${texto}</option>`;
    lista.forEach(i => {
        const option = document.createElement("option");
        option.value = i.id || i.transporte_id || i.actividad_id || i.hotel_id; // Manejar diferentes tipos de datos
        option.textContent = i.nombre || i.tipo || i.precio;
        if (i.precio) option.dataset.precio = i.precio; // Agregar precio si está disponible
        if (i.tarifaAdulto) option.dataset.tarifaAdulto = i.tarifaAdulto;
        if (i.tarifaNino) option.dataset.tarifaNino = i.tarifaNino;
        sel.appendChild(option);
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

    if (hotel) {
        total += ((parseFloat(hotel.dataset.tarifaAdulto || 0) * adultos) +
                  (parseFloat(hotel.dataset.tarifaNino || 0) * ninos)) * noches;
    }

    [...document.getElementById("transporte").selectedOptions].forEach(t => {
        total += parseFloat(t.dataset.precio || 0);
    });

    [...document.getElementById("actividades").selectedOptions].forEach(a => {
        total += parseFloat(a.dataset.precio || 0) * (adultos + ninos);
    });

    document.getElementById("total").textContent = total.toLocaleString();
    return total;
}

["hotel", "transporte", "actividades", "numAdultos", "numNinos", "fechaInicio", "fechaFin", "origen", "destino"].forEach(id => {
    const el = document.getElementById(id);
    if (el) {
        el.addEventListener("change", calcularTotal);
        el.addEventListener("input", calcularTotal);
    }
});


// 💾 Guardar paquete
document.getElementById("btnGuardar").addEventListener("click", async () => {
    try {
        // Validar campos básicos
        const origen = document.getElementById("origen").value;
        const destinoId = parseInt(document.getElementById("destino").value);
        const fechaInicio = document.getElementById("fechaInicio").value;
        const fechaFin = document.getElementById("fechaFin").value;
        const numAdultos = parseInt(document.getElementById("numAdultos").value) || 0;
        const numNinos = parseInt(document.getElementById("numNinos").value) || 0;

        if (!origen || !destinoId || !fechaInicio || !fechaFin) {
            alert("Por favor completa todos los campos antes de guardar.");
            return;
        }

        // Construir objeto paquete
        const paquete = {
            usuario: { user_id: 1 }, // Cambia si tienes ID dinámico de usuario
            origen: origen,
            destino: { destino_id: destinoId },
            fechaInicio: fechaInicio,
            fechaFin: fechaFin,
            numAdultos: numAdultos,
            numNinos: numNinos,
            costoTotal: calcularTotal(),
            nombre: "Paquete personalizado",
            descripcion: "Cotización creada por el cliente",
            hoteles: [...document.getElementById("hotel").selectedOptions].map(h => ({ hotel_id: parseInt(h.value) })),
            transportes: [...document.getElementById("transporte").selectedOptions].map(t => ({ transporte_id: parseInt(t.value) })),
            actividades: [...document.getElementById("actividades").selectedOptions].map(a => ({ actividad_id: parseInt(a.value) }))
        };

        console.log("Enviando paquete:", paquete);

        // Enviar al backend
        const res = await fetch(`${API}/paquetes`, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(paquete)
        });

        const mensaje = document.getElementById("mensaje");
        if (res.ok) {
            const data = await res.json();
            mensaje.textContent = "✅ Paquete guardado con éxito.";
            mensaje.className = "text-success";
            console.log("Paquete guardado:", data);
            // Mostrar popup de confirmación
            alert("✅ Paquete guardado con éxito.");
            // Limpiar campos manualmente
            document.getElementById("origen").value = "";
            document.getElementById("destino").value = "";
            document.getElementById("fechaInicio").value = "";
            document.getElementById("fechaFin").value = "";
            document.getElementById("numAdultos").value = "";
            document.getElementById("numNinos").value = "";
            document.getElementById("hotel").selectedIndex = 0;
            document.getElementById("transporte").selectedIndex = 0;
            document.getElementById("actividades").selectedIndex = 0;
            document.getElementById("seccion-opciones").style.display = "none";
            document.getElementById("total").textContent = "0";
        } else {
            const errorData = await res.json().catch(() => ({}));
            mensaje.textContent = `❌ Error al guardar el paquete: ${errorData.message || res.status}`;
            mensaje.className = "text-danger";
            console.error("Error al guardar paquete:", errorData);
        }
    } catch (err) {
        const mensaje = document.getElementById("mensaje");
        mensaje.textContent = `❌ Error al guardar el paquete: ${err.message}`;
        mensaje.className = "text-danger";
        console.error("Excepción al guardar paquete:", err);
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

// Inicialización segura cuando el DOM está listo
document.addEventListener("DOMContentLoaded", function() {
    cargarDestinos();
    cargarPaquetesPrearmados();
});
