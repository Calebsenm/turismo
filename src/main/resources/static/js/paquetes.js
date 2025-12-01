    // Listener para cerrar sesión en cualquier vista
document.addEventListener('DOMContentLoaded', () => {
    const logoutButton = document.getElementById('logout-button');
    if (logoutButton) {
        logoutButton.addEventListener('click', () => {
            localStorage.removeItem('j  wtToken');
            window.location.href = '/public/login';
        });
    }
});

const API = "/api";
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
function setupDestinoListener() {
    const destinoSelect = document.getElementById("destino");
    if (destinoSelect) {
        destinoSelect.addEventListener("change", handleDestinoChange);
    }
}

async function handleDestinoChange(e) {
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
}

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
    const transporteSelect = document.getElementById("transporte");
    if (transporteSelect) {
        [...transporteSelect.selectedOptions].forEach(t => {
            total += parseFloat(t.dataset.precio || 0);
        });
    }

    // Actividades (precio por persona)
    const actividadesSelect = document.getElementById("actividades");
    if (actividadesSelect) {
        [...actividadesSelect.selectedOptions].forEach(a => {
            total += parseFloat(a.dataset.precio || 0) * (adultos + ninos);
        });
    }

    const totalElement = document.getElementById("total");
    if (totalElement) {
        totalElement.textContent = total.toLocaleString();
    }
    return total;
}

// 🎧 Escuchar cambios en todos los campos relevantes
function setupEventListeners() {
    ["hotel", "transporte", "actividades", "numAdultos", "numNinos", "fechaInicio", "fechaFin", "origen", "destino"].forEach(id => {
        const el = document.getElementById(id);
        if (el) {
            el.addEventListener("change", calcularTotal);
            if (id === "numAdultos" || id === "numNinos") {
                el.addEventListener("input", calcularTotal);
            }
        }
    });

    // Configurar botón guardar
    const btnGuardar = document.getElementById("btnGuardar");
    if (btnGuardar) {
        btnGuardar.addEventListener("click", guardarPaquete);
    }
}

// 🎒 Paquetes prearmados
async function cargarMisPaquetes() {
    try {
        if (!usuarioId) {
            const cont = document.getElementById("misPaquetes");
            if (cont) {
                cont.innerHTML = '<p class="text-center text-muted">No se detectó usuario logueado.</p>';
            }
            return;
        }
        const response = await fetch(`${API}/paquetes/usuario/${usuarioId}`, { headers: getAuthHeaders() });

        if (response.status === 401 || response.status === 403) {
            const cont = document.getElementById("misPaquetes");
            if (cont) {
                cont.innerHTML = '<p class="text-center text-muted">Inicia sesión para ver tus paquetes.</p>';
            }
            return;
        }

        if (!response.ok) {
            throw new Error(`Error HTTP: ${response.status}`);
        }
        const paquetes = await response.json();
        console.log("📦 Mis paquetes obtenidos:", paquetes);
        
        const cont = document.getElementById("misPaquetes");
        if (!cont) return;
        
        cont.innerHTML = ""; // Limpiar contenido anterior

        if (!paquetes || paquetes.length === 0) {
            cont.innerHTML = '<p class="text-center text-muted">No tienes paquetes armados aún.</p>';
            return;
        }

        paquetes.forEach(p => {
            const idPaquete = p.paquete_id || p.id;
            const paqueteCard = document.createElement('div');
            paqueteCard.className = 'col-md-4 mb-4';
            paqueteCard.innerHTML = `
                <div class="card h-100 shadow-sm paquete-card">
                    <div class="card-body d-flex flex-column">
                        <h5 class="card-title text-primary">${p.nombre}</h5>
                        <p class="card-text flex-grow-1">${p.descripcion}</p>
                        <ul class="list-unstyled mt-2 mb-3">
                            <li><small><strong>Origen:</strong> ${p.origen}</small></li>
                            <li><small><strong>Tipo:</strong> ${p.tipo_paquete || "Personalizado"}</small></li>
                        </ul>
                        <div class="text-end">
                            <p class="h5 fw-bold text-success mb-3">${(p.costo_total || 0).toLocaleString('es-CO', { style: 'currency', currency: 'COP' })}</p>
                            <button class="btn btn-secondary btn-sm generar-pdf" data-paquete-id="${idPaquete}">
                                Generar PDF
                            </button>
                            <button class="btn btn-danger btn-sm ms-2 eliminar-paquete" data-paquete-id="${idPaquete}">
                                Eliminar
                            </button>
                        </div>
                    </div>
                </div>
            `;
            cont.appendChild(paqueteCard);
        });

        // Agregar event listeners para los botones dinámicos
        document.querySelectorAll('.generar-pdf').forEach(btn => {
            btn.addEventListener('click', function() {
                const paqueteId = this.getAttribute('data-paquete-id');
                const paquete = paquetes.find(p => (p.paquete_id || p.id) == paqueteId);
                if (paquete) generarPDF(paquete);
            });
        });

        document.querySelectorAll('.eliminar-paquete').forEach(btn => {
            btn.addEventListener('click', function() {
                const paqueteId = this.getAttribute('data-paquete-id');
                eliminarMiPaquete(paqueteId);
            });
        });

    } catch (error) {
        console.error("❌ Error cargando mis paquetes:", error);
        const cont = document.getElementById("misPaquetes");
        if (cont) {
            cont.innerHTML = '<p class="text-center text-danger">No se pudieron cargar tus paquetes.</p>';
        }
    }
}

// 📄 Función para generar PDF desde el backend
async function generarPDF(p) {
    const paqueteId = p.paquete_id || p.id;
    
    if (!paqueteId) {
        alert('Error: No se pudo identificar el paquete');
        return;
    }

    // Verificar token
    const token = localStorage.getItem('jwtToken');
    if (!token) {
        alert('Error: No estás autenticado. Por favor inicia sesión nuevamente.');
        window.location.href = '/login.html';
        return;
    }

    // Mostrar indicador de carga
    const btn = document.querySelector(`.generar-pdf[data-paquete-id="${paqueteId}"]`);
    const originalText = btn ? btn.innerHTML : '';
    if (btn) {
        btn.disabled = true;
        btn.innerHTML = '<span class="spinner-border spinner-border-sm me-2"></span>Generando...';
    }

    try {
        const response = await fetch(`${API}/paquetes/${paqueteId}/pdf`, {
            method: 'GET',
            headers: {
                'Authorization': `Bearer ${token}`
            }
        });

        if (!response.ok) {
            const errorText = await response.text();
            throw new Error(errorText || 'Error al generar el PDF');
        }

        // Descargar el PDF
        const blob = await response.blob();
        const url = window.URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = `Paquete_${p.nombre || paqueteId}.pdf`;
        document.body.appendChild(a);
        a.click();
        window.URL.revokeObjectURL(url);
        document.body.removeChild(a);

    } catch (error) {
        console.error('Error al generar PDF:', error);
        alert('Error al generar el PDF: ' + error.message);
    } finally {
        // Restaurar botón
        if (btn) {
            btn.disabled = false;
            btn.innerHTML = originalText;
        }
    }
}

// 🗑️ Función para eliminar paquete
window.eliminarMiPaquete = function (id) {
    if (!confirm('¿Seguro que deseas eliminar este paquete?')) return;
    fetch(`${API}/paquetes/${id}`, {
        method: 'DELETE',
        headers: getAuthHeaders()
    })
        .then(res => {
            if (res.ok) {
                cargarMisPaquetes();
            } else {
                alert('No se pudo eliminar el paquete.');
            }
        })
        .catch(() => alert('Error al eliminar el paquete.'));
}

// 💾 Manejar clic en Guardar Paquete
async function guardarPaquete() {
    const mensajeDiv = document.getElementById("mensaje");
    if (mensajeDiv) {
        mensajeDiv.textContent = ""; // Limpiar mensajes anteriores
    }

    // Recopilar datos del formulario
    const origen = document.getElementById("origen").value;
    const destinoId = document.getElementById("destino").value;
    const hotelId = document.getElementById("hotel").value;
    const transporteId = document.getElementById("transporte").value;
    const actividadId = document.getElementById("actividades").value;
    const fechaInicio = document.getElementById("fechaInicio").value;
    const fechaFin = document.getElementById("fechaFin").value;
    const numAdultos = parseInt(document.getElementById("numAdultos").value) || 0;
    const numNinos = parseInt(document.getElementById("numNinos").value) || 0;
    const costoTotal = calcularTotal();

    // Validación mínima
    if (!origen || !destinoId || !hotelId || !fechaInicio || !fechaFin) {
        if (mensajeDiv) {
            mensajeDiv.textContent = "Por favor, completa todos los campos obligatorios (origen, destino, hotel y fechas).";
            mensajeDiv.className = "alert alert-danger mt-3";
        }
        return;
    }

    // Construir arrays de objetos completos para enviar
    const hotelesObj = hotelId ? hoteles.filter(h => h.id == hotelId) : [];
    const transportesObj = transporteId ? transportes.filter(t => t.id == transporteId) : [];
    const actividadesObj = actividadId ? actividades.filter(a => a.id == actividadId) : [];

    // Construir el objeto final del paquete con nombres exactos
    const paquete = {
        usuario_id: usuarioId,
        origen,
        destino_id: parseInt(destinoId),
        fecha_inicio: fechaInicio,
        fecha_fin: fechaFin,
        num_adultos: numAdultos,
        num_ninos: numNinos,
        costo_total: costoTotal,
        nombre: `Paquete a ${document.getElementById("destino").selectedOptions[0].text}`,
        descripcion: `Paquete personalizado para ${numAdultos} adultos y ${numNinos} niños.`,
        tipo_paquete: "Personalizado",
        hoteles: hotelesObj,
        transportes: transportesObj,
        actividades: actividadesObj
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
        alert("¡Paquete armado exitosamente!");
        if (mensajeDiv) {
            mensajeDiv.textContent = `¡Paquete guardado con éxito! ID del paquete: ${nuevoPaquete.paquete_id}`;
            mensajeDiv.className = "alert alert-success mt-3";
        }

        // Actualizar lista de paquetes
        cargarMisPaquetes();
    } catch (error) {
        console.error("❌ Error al guardar el paquete:", error);
        if (mensajeDiv) {
            mensajeDiv.textContent = `No se pudo guardar el paquete. ${error.message}`;
            mensajeDiv.className = "alert alert-danger mt-3";
        }
    }
}

// 🚀 Inicialización
let usuarioId = null;
document.addEventListener("DOMContentLoaded", async function () {
    const token = localStorage.getItem('jwtToken');
    
    // Verificar autenticación antes de cargar datos
    if (!token) {
        window.location.href = '/public/login';
        return;
    }
    
    // Verificar expiración del token
    try {
        const payload = JSON.parse(atob(token.split('.')[1]));
        if (payload.exp && Date.now() / 1000 > payload.exp) {
            localStorage.removeItem('jwtToken');
            window.location.href = '/public/login';
            return;
        }
    } catch (e) {
        // Si el token no es válido, redirigir
        localStorage.removeItem('jwtToken');
        window.location.href = '/public/login';
        return;
    }
    
    // Configurar event listeners
    setupDestinoListener();
    setupEventListeners();
    
    // Cargar datos iniciales
    cargarDestinos();
    
    // Obtener el id del usuario logueado
    if (token) {
        try {
            const res = await fetch(`${API}/usuarios/me`, {
                headers: getAuthHeaders()
            });
            if (res.ok) {
                const data = await res.json();
                // Buscar el id en varias variantes posibles
                usuarioId = data.userId || data.id || data.user_id || null;
                // Si no existe id, intenta con email (solo si el backend lo acepta como identificador)
                if (!usuarioId && data.email) usuarioId = data.email;
                // Ahora sí, cargar los paquetes del usuario
                cargarMisPaquetes();
            }
        } catch (e) {
            console.error("Error obteniendo datos del usuario:", e);
        }
    }
});