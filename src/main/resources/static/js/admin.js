// Listener para cerrar sesión en cualquier vista
document.addEventListener('DOMContentLoaded', () => {
    const logoutButton = document.getElementById('logout-button');
    if (logoutButton) {
        logoutButton.addEventListener('click', () => {
            localStorage.removeItem('jwtToken');
            window.location.href = '/public/login';
        });
    }

        // --- Filtros de paquetes ---
        cargarDestinosFiltro();
        cargarTransportesFiltro();
        cargarPaquetesAdmin();
        document.getElementById('filtroPaqueteDestino').addEventListener('change', filtrarPaquetesAdmin);
        document.getElementById('filtroPaqueteFechaInicio').addEventListener('change', filtrarPaquetesAdmin);
        document.getElementById('filtroPaqueteFechaFin').addEventListener('change', filtrarPaquetesAdmin);
        document.getElementById('filtroPaqueteTransporte').addEventListener('change', filtrarPaquetesAdmin);
});

    // Cargar destinos en filtro
    function cargarDestinosFiltro() {
        fetch('/api/destinos', { headers: { 'Authorization': 'Bearer ' + localStorage.getItem('jwtToken') } })
            .then(res => res.json())
            .then(destinos => {
                const select = document.getElementById('filtroPaqueteDestino');
                destinos.forEach(d => {
                    select.innerHTML += `<option value="${d.destino_id}">${d.nombre}</option>`;
                });
            });
    }

    // Cargar transportes en filtro
    function cargarTransportesFiltro() {
        fetch('/api/transportes', { headers: { 'Authorization': 'Bearer ' + localStorage.getItem('jwtToken') } })
            .then(res => res.json())
            .then(transportes => {
                const select = document.getElementById('filtroPaqueteTransporte');
                const tipos = [...new Set(transportes.map(t => t.tipo))];
                tipos.forEach(tipo => {
                    select.innerHTML += `<option value="${tipo}">${tipo}</option>`;
                });
            });
    }

    // Cargar todos los paquetes para admin
    function cargarPaquetesAdmin() {
        fetch('/api/paquetes', { headers: { 'Authorization': 'Bearer ' + localStorage.getItem('jwtToken') } })
            .then(res => res.json())
            .then(paquetes => {
                window._paquetesAdmin = paquetes;
                filtrarPaquetesAdmin();
            });
    }

    // Filtrar y mostrar paquetes según los filtros
    function filtrarPaquetesAdmin() {
        const destinoId = document.getElementById('filtroPaqueteDestino').value;
        const fechaInicio = document.getElementById('filtroPaqueteFechaInicio').value;
        const fechaFin = document.getElementById('filtroPaqueteFechaFin').value;
        const tipoTransporte = document.getElementById('filtroPaqueteTransporte').value;
        let paquetes = window._paquetesAdmin || [];
        paquetes = paquetes.filter(p => {
            let ok = true;
            if (destinoId && String(p.destino_id) !== String(destinoId)) ok = false;
            if (fechaInicio && p.fechaInicio < fechaInicio) ok = false;
            if (fechaFin && p.fechaFin > fechaFin) ok = false;
            if (tipoTransporte) {
                // Filtra ignorando mayúsculas/minúsculas y espacios
                const tipoFiltro = tipoTransporte.trim().toLowerCase();
                const tieneTransporte = p.transportes && p.transportes.length && p.transportes.some(t => (t.tipo ?? '').trim().toLowerCase() === tipoFiltro);
                if (!tieneTransporte) ok = false;
            }
            return ok;
        });
        mostrarPaquetesAdmin(paquetes);
    }

    function mostrarPaquetesAdmin(paquetes) {
        const cont = document.getElementById('listaPaquetesAdmin');
        cont.innerHTML = '';
        if (!paquetes.length) {
            cont.innerHTML = '<p class="text-muted">No se encontraron paquetes con los filtros seleccionados.</p>';
            return;
        }
        paquetes.forEach(p => {
            cont.innerHTML += `
                <div class="card mb-2 p-2">
                    <div><b>${p.nombre}</b> (${p.descripcion})</div>
                    <div><b>ID:</b> ${p.paquete_id ?? ''}</div>
                    <div><b>Destino:</b> ${p.destino_id ?? ''}</div>
                    <div><b>Fechas:</b> ${p.fechaInicio ?? ''} a ${p.fechaFin ?? ''}</div>
                    <div><b>Transporte:</b> ${(p.transportes && p.transportes.length) ? p.transportes.map(t => `${t.tipo} (${t.empresa ?? ''}, $${t.precio ?? ''})`).join(', ') : 'Sin transporte'}</div>
                    <div><b>Hoteles:</b> ${(p.hoteles && p.hoteles.length) ? p.hoteles.map(h => `${h.nombre} (Adulto: $${h.tarifaAdulto ?? ''}, Niño: $${h.tarifaNino ?? ''})`).join(', ') : 'Sin hoteles'}</div>
                    <div><b>Actividades:</b> ${(p.actividades && p.actividades.length) ? p.actividades.map(a => `${a.nombre} ($${a.precio ?? ''})`).join(', ') : 'Sin actividades'}</div>
                    <div><b>Costo total:</b> ${p.costoTotal?.toLocaleString('es-CO', { style: 'currency', currency: 'COP' }) ?? ''}</div>
                    <div><b>Usuario:</b> ${p.usuario_id ?? ''}</div>
                    <div><b>Fecha de creación:</b> ${p.fechaCreacion ?? ''}</div>
                </div>
            `;
        });
    }
// admin.js
// ---- FUNCIONES DE USUARIOS ----
function crearUsuario(e) {
    e.preventDefault();
    const name = document.getElementById('nombreUsuario').value;
    const email = document.getElementById('emailUsuario').value;
    const password = document.getElementById('passwordUsuario').value;
        const destinoId = document.getElementById('destinoHotel').value;
    
        // Validación de datos
        console.log('Datos a enviar:', {nombre, tarifaAdulto, tarifaNino, destinoId: destinoId});
    
        if (!nombre || !tarifaAdulto || !tarifaNino || !destinoId || destinoId === 'undefined') {
            alert('Por favor complete todos los campos y seleccione un destino válido');
            return;
        }
    fetch('/api/usuarios', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'Authorization': 'Bearer ' + localStorage.getItem('jwtToken')
        },
        body: JSON.stringify({ name, email, password, userType })
    })
    .then(res => res.json())
    .then(() => {
        alert('Usuario registrado correctamente');
        document.getElementById('formUsuario').reset();
        cargarUsuarios();
    });
}

function cargarUsuarios() {
    fetch('/api/usuarios', {
        headers: { 'Authorization': 'Bearer ' + localStorage.getItem('jwtToken') }
    })
    .then(res => res.json())
    .then(usuarios => {
        window._usuarios = usuarios;
        filtrarUsuarios();
    });
}

function filtrarUsuarios() {
    const cont = document.getElementById('listaUsuarios');
    const filtro = (document.getElementById('filtroUsuario')?.value || '').toLowerCase();
    cont.innerHTML = '';
    window._usuarios.forEach(u => {
        if (
            u.name.toLowerCase().includes(filtro) ||
            u.email.toLowerCase().includes(filtro)
        ) {
            cont.innerHTML += `
                <div class="card mb-2 p-2">
                    <div><b>${u.name}</b> (${u.email}) - <span class="badge bg-${u.userType === 'ADMIN' ? 'danger' : 'primary'}">${u.userType}</span></div>
                    <button class="btn btn-sm btn-warning me-2" onclick="mostrarModalEditarUsuario(${u.user_id})">Editar</button>
                    <button class="btn btn-sm btn-danger" onclick="eliminarUsuario(${u.user_id})">Eliminar</button>
                </div>
            `;
        }
    });
}

function mostrarModalEditarUsuario(id) {
    const usuario = window._usuarios.find(u => u.user_id === id);
    if (!usuario) return;
    document.getElementById('editUserId').value = usuario.user_id;
    document.getElementById('editNombreUsuario').value = usuario.name;
    document.getElementById('editEmailUsuario').value = usuario.email;
    document.getElementById('editTipoUsuario').value = usuario.userType;
    // Solo agrega el campo oculto si el usuario tiene password (para edición real)
    let passInput = document.getElementById('editPasswordUsuario');
    if (usuario.password) {
        if (!passInput) {
            passInput = document.createElement('input');
            passInput.type = 'hidden';
            passInput.id = 'editPasswordUsuario';
            document.getElementById('formEditarUsuario').appendChild(passInput);
        }
        passInput.value = usuario.password;
    } else if (passInput) {
        passInput.remove(); // Elimina el campo si no hay password
    }
    let modal = bootstrap.Modal.getInstance(document.getElementById('modalEditarUsuario'));
    if (!modal) {
        modal = new bootstrap.Modal(document.getElementById('modalEditarUsuario'));
    }
    modal.show();
}
window.mostrarModalEditarUsuario = mostrarModalEditarUsuario;

function eliminarUsuario(id) {
    if (!confirm('¿Seguro que deseas eliminar este usuario?')) return;
    fetch(`/api/usuarios/${id}`, {
        method: 'DELETE',
        headers: { 'Authorization': 'Bearer ' + localStorage.getItem('jwtToken') }
    })
    .then(() => {
        alert('Usuario eliminado');
        cargarUsuarios();
    });
}

// ...existing code...
// ---- FUNCIONES DE HOTELES ----
function cargarHoteles(){
    fetch('/api/hoteles', {
        headers: { 'Authorization': 'Bearer ' + localStorage.getItem('jwtToken') }
    })
    .then(res => res.json())
    .then(hoteles => {
        window._hoteles = hoteles;
        filtrarHoteles();
    });
}

function filtrarHoteles() {
    const cont = document.getElementById('listaHoteles');
    if (!cont) return;
    const filtro = (document.getElementById('filtroHotel')?.value || '').toLowerCase();
    cont.innerHTML = '';
    window._hoteles.forEach(h => {
        if (
            h.nombre.toLowerCase().includes(filtro) ||
            h.tarifaAdulto?.toString().includes(filtro) ||
            h.tarifaNino?.toString().includes(filtro)
        ) {
            cont.innerHTML += `
                <div class="card mb-2 p-2">
                    <div><b>${h.nombre}</b> - <span class="text-muted">Adulto: $${h.tarifaAdulto} / Niño: $${h.tarifaNino}</span></div>
                    <div>Destino: ${h.destino?.nombre || ''}</div>
                    <button class="btn btn-sm btn-warning me-2" onclick="mostrarModalEditarHotel(${h.hotel_id})">Editar</button>
                    <button class="btn btn-sm btn-danger" onclick="eliminarHotel(${h.hotel_id})">Eliminar</button>
                </div>
            `;
        }
    });
}

function mostrarModalEditarHotel(id) {
    const hotel = window._hoteles.find(h => h.hotel_id === id);
    if (!hotel) return;
    document.getElementById('editHotelId').value = hotel.hotel_id;
    document.getElementById('editNombreHotel').value = hotel.nombre;
    document.getElementById('editTarifaAdultoHotel').value = hotel.tarifaAdulto;
    document.getElementById('editTarifaNinoHotel').value = hotel.tarifaNino;
    // Cargar destinos en el select y mostrar el modal después
    fetch('/api/destinos', {
        headers: { 'Authorization': 'Bearer ' + localStorage.getItem('jwtToken') }
    })
    .then(res => res.json())
    .then(destinos => {
        const select = document.getElementById('editDestinoHotel');
        if (!select) return;
        select.innerHTML = '<option value="">Selecciona destino...</option>';
        destinos.forEach(d => {
            select.innerHTML += `<option value="${d.destino_id}" ${d.destino_id === hotel.destino?.destino_id ? 'selected' : ''}>${d.nombre}</option>`;
        });
        let modal = bootstrap.Modal.getInstance(document.getElementById('modalEditarHotel'));
        if (!modal) {
            modal = new bootstrap.Modal(document.getElementById('modalEditarHotel'));
        }
        modal.show();
    });
}
window.mostrarModalEditarHotel = mostrarModalEditarHotel;

function eliminarHotel(id) {
    if (!confirm('¿Seguro que deseas eliminar este hotel?')) return;
    fetch(`/api/hoteles/${id}`, {
        method: 'DELETE',
        headers: { 'Authorization': 'Bearer ' + localStorage.getItem('jwtToken') }
    })
    .then(() => {
        alert('Hotel eliminado');
        cargarHoteles();
    });
}

// ---- FUNCIONES DE ACTIVIDADES ----
function cargarActividades(){
    fetch('/api/actividades', {
        headers: { 'Authorization': 'Bearer ' + localStorage.getItem('jwtToken') }
    })
    .then(res => res.json())
    .then(actividades => {
        window._actividades = actividades;
        filtrarActividades();
    });
}

function filtrarActividades() {
    const cont = document.getElementById('listaActividades');
    if (!cont) return;
    const filtro = (document.getElementById('filtroActividad')?.value || '').toLowerCase();
    cont.innerHTML = '';
    window._actividades.forEach(a => {
        if (
            a.nombre.toLowerCase().includes(filtro) ||
            a.descripcion?.toLowerCase().includes(filtro) ||
            a.precio?.toString().includes(filtro)
        ) {
            cont.innerHTML += `
                <div class="card mb-2 p-2">
                    <div><b>${a.nombre}</b> - <span class="text-muted">$${a.precio}</span></div>
                    <div>${a.descripcion || ''}</div>
                    <div>Destino: ${a.destino?.nombre || ''}</div>
                    <button class="btn btn-sm btn-warning me-2" onclick="mostrarModalEditarActividad(${a.actividad_id})">Editar</button>
                    <button class="btn btn-sm btn-danger" onclick="eliminarActividad(${a.actividad_id})">Eliminar</button>
                </div>
            `;
        }
    });
}

function mostrarModalEditarActividad(id) {
    const actividad = window._actividades.find(a => a.actividad_id === id);
    if (!actividad) return;
    document.getElementById('editActividadId').value = actividad.actividad_id;
    document.getElementById('editNombreActividad').value = actividad.nombre;
    document.getElementById('editDescripcionActividad').value = actividad.descripcion;
    document.getElementById('editPrecioActividad').value = actividad.precio;
    cargarDestinosSelectEdit('editDestinoActividad', actividad.destino?.destino_id);
    let modal = bootstrap.Modal.getInstance(document.getElementById('modalEditarActividad'));
    if (!modal) {
        modal = new bootstrap.Modal(document.getElementById('modalEditarActividad'));
    }
    modal.show();
}
window.mostrarModalEditarActividad = mostrarModalEditarActividad;

function eliminarActividad(id) {
    if (!confirm('¿Seguro que deseas eliminar esta actividad?')) return;
    fetch(`/api/actividades/${id}`, {
        method: 'DELETE',
        headers: { 'Authorization': 'Bearer ' + localStorage.getItem('jwtToken') }
    })
    .then(() => {
        alert('Actividad eliminada');
        cargarActividades();
    });
}

// ---- FUNCIONES DE TRANSPORTE ----
function cargarTransportes(){
    fetch('/api/transportes', {
        headers: { 'Authorization': 'Bearer ' + localStorage.getItem('jwtToken') }
    })
    .then(res => res.json())
    .then(transportes => {
        window._transportes = transportes;
        filtrarTransportes();
    });
}

function filtrarTransportes() {
    const cont = document.getElementById('listaTransportes');
    if (!cont) return;
    const filtro = (document.getElementById('filtroTransporte')?.value || '').toLowerCase();
    cont.innerHTML = '';
    window._transportes.forEach(t => {
        if (
            t.tipo.toLowerCase().includes(filtro) ||
            t.empresa?.toLowerCase().includes(filtro) ||
            t.precio?.toString().includes(filtro)
        ) {
            cont.innerHTML += `
                <div class="card mb-2 p-2">
                    <div><b>${t.tipo}</b> - <span class="text-muted">${t.empresa || ''}</span> <span class="text-muted">$${t.precio}</span></div>
                    <div>Destino: ${t.destino?.nombre || ''}</div>
                    <button class="btn btn-sm btn-warning me-2" onclick="mostrarModalEditarTransporte(${t.transporte_id})">Editar</button>
                    <button class="btn btn-sm btn-danger" onclick="eliminarTransporte(${t.transporte_id})">Eliminar</button>
                </div>
            `;
        }
    });
}

function mostrarModalEditarTransporte(id) {
    const transporte = window._transportes.find(t => t.transporte_id === id);
    if (!transporte) return;
    document.getElementById('editTransporteId').value = transporte.transporte_id;
    document.getElementById('editTipoTransporte').value = transporte.tipo;
    document.getElementById('editEmpresaTransporte').value = transporte.empresa;
    document.getElementById('editPrecioTransporte').value = transporte.precio;
    // Cargar destinos en el select y mostrar el modal después
    fetch('/api/destinos', {
        headers: { 'Authorization': 'Bearer ' + localStorage.getItem('jwtToken') }
    })
    .then(res => res.json())
    .then(destinos => {
        const select = document.getElementById('editDestinoTransporte');
        if (!select) return;
        select.innerHTML = '<option value="">Selecciona destino...</option>';
        destinos.forEach(d => {
            const isSelected = String(d.destino_id) === String(transporte.destino?.destino_id);
            select.innerHTML += `<option value="${d.destino_id}" ${isSelected ? 'selected' : ''}>${d.nombre}</option>`;
        });
        // Forzar el valor seleccionado en el select (por si el browser ignora el atributo selected)
        if (transporte.destino?.destino_id) {
            select.value = String(transporte.destino.destino_id);
        }
        // Log para depuración
        console.log('Valor destino seleccionado en popup transporte:', select.value);
        let modal = bootstrap.Modal.getInstance(document.getElementById('modalEditarTransporte'));
        if (!modal) {
            modal = new bootstrap.Modal(document.getElementById('modalEditarTransporte'));
        }
        modal.show();
    });
}
window.mostrarModalEditarTransporte = mostrarModalEditarTransporte;

function eliminarTransporte(id) {
    if (!confirm('¿Seguro que deseas eliminar este transporte?')) return;
    fetch(`/api/transportes/${id}`, {
        method: 'DELETE',
        headers: { 'Authorization': 'Bearer ' + localStorage.getItem('jwtToken') }
    })
    .then(() => {
        alert('Transporte eliminado');
        cargarTransportes();
    });
}

// Utilidad para cargar destinos en selects de edición
function cargarDestinosSelectEdit(selectId, selectedId) {
    fetch('/api/destinos', {
        headers: { 'Authorization': 'Bearer ' + localStorage.getItem('jwtToken') }
    })
    .then(res => res.json())
    .then(destinos => {
        const select = document.getElementById(selectId);
        if (!select) return;
        select.innerHTML = '<option value="">Selecciona destino...</option>';
        destinos.forEach(d => {
            select.innerHTML += `<option value="${d.destino_id}" ${d.destino_id === selectedId ? 'selected' : ''}>${d.nombre}</option>`;
        });
    });
}
// ---- FUNCIONES DE DESTINOS ----
function cargarDestinos(){
    fetch('/api/destinos', {
        headers: { 'Authorization': 'Bearer ' + localStorage.getItem('jwtToken') }
    })
    .then(res => res.json())
    .then(destinos => {
        window._destinos = destinos;
        filtrarDestinos();
    });
}

function filtrarDestinos() {
    const cont = document.getElementById('listaDestinos');
    if (!cont) return;
    const filtro = (document.getElementById('filtroDestino')?.value || '').toLowerCase();
    cont.innerHTML = '';
    window._destinos.forEach(d => {
        if (
            d.nombre.toLowerCase().includes(filtro) ||
            d.descripcion?.toLowerCase().includes(filtro) ||
            d.ubicacion?.toLowerCase().includes(filtro)
        ) {
            cont.innerHTML += `
                <div class="card mb-2 p-2">
                    <div><b>${d.nombre}</b> - <span class="text-muted">${d.ubicacion || ''}</span></div>
                    <div>${d.descripcion || ''}</div>
                    <button class="btn btn-sm btn-warning me-2" onclick="mostrarModalEditarDestino(${d.destino_id})">Editar</button>
                    <button class="btn btn-sm btn-danger" onclick="eliminarDestino(${d.destino_id})">Eliminar</button>
                </div>
            `;
        }
    });
}

function mostrarModalEditarDestino(id) {
    const destino = window._destinos.find(d => d.destino_id === id);
    if (!destino) return;
    document.getElementById('editDestinoId').value = destino.destino_id;
    document.getElementById('editNombreDestino').value = destino.nombre;
    document.getElementById('editDescripcionDestino').value = destino.descripcion;
    document.getElementById('editUbicacionDestino').value = destino.ubicacion;
    let modal = bootstrap.Modal.getInstance(document.getElementById('modalEditarDestino'));
    if (!modal) {
        modal = new bootstrap.Modal(document.getElementById('modalEditarDestino'));
    }
    modal.show();
}
window.mostrarModalEditarDestino = mostrarModalEditarDestino;

function eliminarDestino(id) {
    if (!confirm('¿Seguro que deseas eliminar este destino?')) return;
    fetch(`/api/destinos/${id}`, {
        method: 'DELETE',
        headers: { 'Authorization': 'Bearer ' + localStorage.getItem('jwtToken') }
    })
    .then(() => {
        alert('Destino eliminado');
        cargarDestinos();
    });
}
    // Usuarios
    const formUsuario = document.getElementById('formUsuario');
    if (formUsuario) {
        formUsuario.addEventListener('submit', crearUsuario);
        cargarUsuarios();
    }

        // Hoteles
        const formHotel = document.getElementById('formHotel');
        if (formHotel) {
            formHotel.addEventListener('submit', crearHotel);
            cargarHoteles();
        }
        const filtroHotelInput = document.getElementById('filtroHotel');
        if (filtroHotelInput) {
            filtroHotelInput.addEventListener('input', filtrarHoteles);
        }
        window._hoteles = [];

        // Guardar cambios del hotel editado
        const formEditarHotel = document.getElementById('formEditarHotel');
        if (formEditarHotel) {
            formEditarHotel.addEventListener('submit', function(e) {
                e.preventDefault();
                const id = document.getElementById('editHotelId').value;
                const nombre = document.getElementById('editNombreHotel').value;
                const tarifaAdulto = parseFloat(document.getElementById('editTarifaAdultoHotel').value);
                const tarifaNino = parseFloat(document.getElementById('editTarifaNinoHotel').value);
                const destinoId = document.getElementById('editDestinoHotel').value;
                let body = { nombre, tarifaAdulto, tarifaNino, destinoId };
                fetch(`/api/hoteles/${id}`, {
                    method: 'PUT',
                    headers: {
                        'Content-Type': 'application/json',
                        'Authorization': 'Bearer ' + localStorage.getItem('jwtToken')
                    },
                    body: JSON.stringify(body)
                })
                .then(res => {
                    if (!res.ok) throw new Error('Error al actualizar hotel');
                    return res.json();
                })
                .then(() => {
                    alert('Hotel actualizado');
                    cargarHoteles();
                    let modal = bootstrap.Modal.getInstance(document.getElementById('modalEditarHotel'));
                    if (modal) modal.hide();
                })
                .catch(() => {
                    alert('No se pudo actualizar el hotel. Verifica los datos.');
                });
            });
        }

        // Actividades
        const formActividad = document.getElementById('formActividad');
        if (formActividad) {
            formActividad.addEventListener('submit', crearActividad);
            cargarActividades();
        }
        const filtroActividadInput = document.getElementById('filtroActividad');
        if (filtroActividadInput) {
            filtroActividadInput.addEventListener('input', filtrarActividades);
        }
        window._actividades = [];

        // Guardar cambios de la actividad editada
        const formEditarActividad = document.getElementById('formEditarActividad');
        if (formEditarActividad) {
            formEditarActividad.addEventListener('submit', function(e) {
                e.preventDefault();
                const id = document.getElementById('editActividadId').value;
                const nombre = document.getElementById('editNombreActividad').value;
                const descripcion = document.getElementById('editDescripcionActividad').value;
                const precio = parseFloat(document.getElementById('editPrecioActividad').value);
                const destinoId = document.getElementById('editDestinoActividad').value;
                let body = { nombre, descripcion, precio, destinoId };
                fetch(`/api/actividades/${id}`, {
                    method: 'PUT',
                    headers: {
                        'Content-Type': 'application/json',
                        'Authorization': 'Bearer ' + localStorage.getItem('jwtToken')
                    },
                    body: JSON.stringify(body)
                })
                .then(res => {
                    if (!res.ok) throw new Error('Error al actualizar actividad');
                    return res.json();
                })
                .then(() => {
                    alert('Actividad actualizada');
                    cargarActividades();
                    let modal = bootstrap.Modal.getInstance(document.getElementById('modalEditarActividad'));
                    if (modal) modal.hide();
                })
                .catch(() => {
                    alert('No se pudo actualizar la actividad. Verifica los datos.');
                });
            });
        }

        // Transporte
        const formTransporte = document.getElementById('formTransporte');
        if (formTransporte) {
            formTransporte.addEventListener('submit', crearTransporte);
            cargarTransportes();
        }
        const filtroTransporteInput = document.getElementById('filtroTransporte');
        if (filtroTransporteInput) {
            filtroTransporteInput.addEventListener('input', filtrarTransportes);
        }
        window._transportes = [];

        // Guardar cambios del transporte editado
        const formEditarTransporte = document.getElementById('formEditarTransporte');
        if (formEditarTransporte) {
            formEditarTransporte.addEventListener('submit', function(e) {
                e.preventDefault();
                const id = document.getElementById('editTransporteId').value;
                const tipo = document.getElementById('editTipoTransporte').value;
                const empresa = document.getElementById('editEmpresaTransporte').value;
                const precio = parseFloat(document.getElementById('editPrecioTransporte').value);
                const destinoId = document.getElementById('editDestinoTransporte').value;
                if (!destinoId || destinoId === '' || destinoId === 'undefined') {
                    alert('Por favor selecciona un destino válido');
                    return;
                }
                let body = { tipo, empresa, precio, destinoId };
                console.log('JSON enviado a /api/transportes:', JSON.stringify(body));
                fetch(`/api/transportes/${id}`, {
                    method: 'PUT',
                    headers: {
                        'Content-Type': 'application/json',
                        'Authorization': 'Bearer ' + localStorage.getItem('jwtToken')
                    },
                    body: JSON.stringify(body)
                })
                .then(res => {
                    if (!res.ok) throw new Error('Error al actualizar transporte');
                    return res.json();
                })
                .then(() => {
                    alert('Transporte actualizado');
                    cargarTransportes();
                    let modal = bootstrap.Modal.getInstance(document.getElementById('modalEditarTransporte'));
                    if (modal) modal.hide();
                })
                .catch(() => {
                    alert('No se pudo actualizar el transporte. Verifica los datos.');
                });
            });
        }

    const formDestino = document.getElementById('formDestino');
    if (formDestino) {
        formDestino.addEventListener('submit', crearDestino);
        cargarDestinos();
    }

        // Filtro de destinos
        const filtroDestinoInput = document.getElementById('filtroDestino');
        if (filtroDestinoInput) {
            filtroDestinoInput.addEventListener('input', filtrarDestinos);
        }

        window._destinos = [];

        // Guardar cambios del destino editado
        const formEditarDestino = document.getElementById('formEditarDestino');
        if (formEditarDestino) {
            formEditarDestino.addEventListener('submit', function(e) {
                e.preventDefault();
                const id = document.getElementById('editDestinoId').value;
                const nombre = document.getElementById('editNombreDestino').value;
                const descripcion = document.getElementById('editDescripcionDestino').value;
                const ubicacion = document.getElementById('editUbicacionDestino').value;
                let body = { nombre, descripcion, ubicacion };
                fetch(`/api/destinos/${id}`, {
                    method: 'PUT',
                    headers: {
                        'Content-Type': 'application/json',
                        'Authorization': 'Bearer ' + localStorage.getItem('jwtToken')
                    },
                    body: JSON.stringify(body)
                })
                .then(res => {
                    if (!res.ok) throw new Error('Error al actualizar destino');
                    return res.json();
                })
                .then(() => {
                    alert('Destino actualizado');
                    cargarDestinos();
                    let modal = bootstrap.Modal.getInstance(document.getElementById('modalEditarDestino'));
                    if (modal) modal.hide();
                })
                .catch(() => {
                    alert('No se pudo actualizar el destino. Verifica los datos.');
                });
            });
        }

    // Filtro de usuarios
    const filtroInput = document.getElementById('filtroUsuario');
    if (filtroInput) {
        filtroInput.addEventListener('input', filtrarUsuarios);
    }

    window._usuarios = [];

    // Guardar cambios del usuario editado
    const formEditarUsuario = document.getElementById('formEditarUsuario');
    if (formEditarUsuario) {
        formEditarUsuario.addEventListener('submit', function(e) {
            e.preventDefault();
            const id = document.getElementById('editUserId').value;
            const name = document.getElementById('editNombreUsuario').value;
            const email = document.getElementById('editEmailUsuario').value;
            const userType = document.getElementById('editTipoUsuario').value;
            const passInput = document.getElementById('editPasswordUsuario');
            let body = { name, email, userType };
            if (passInput && passInput.value) {
                body.password = passInput.value;
            }
            fetch(`/api/usuarios/${id}`, {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': 'Bearer ' + localStorage.getItem('jwtToken')
                },
                body: JSON.stringify(body)
            })
            .then(res => {
                if (!res.ok) throw new Error('Error al actualizar usuario');
                return res.json();
            })
            .then(() => {
                alert('Usuario actualizado');
                cargarUsuarios();
                let modal = bootstrap.Modal.getInstance(document.getElementById('modalEditarUsuario'));
                if (modal) modal.hide();
            })
            .catch(() => {
                alert('No se pudo actualizar el usuario. Verifica los datos.');
            });
        });
    }

    // ---- CARGA DE DESTINOS EN SELECTS ----
    cargarDestinosSelects();

    // ---- FORMULARIOS DE CRUD ----
    document.getElementById('formDestino')?.addEventListener('submit', crearDestino);
    document.getElementById('formHotel')?.addEventListener('submit', crearHotel);
    document.getElementById('formActividad')?.addEventListener('submit', crearActividad);
    document.getElementById('formTransporte')?.addEventListener('submit', crearTransporte);


function cargarDestinosSelects() {
    fetch('/api/destinos', {
        headers: { 'Authorization': 'Bearer ' + localStorage.getItem('jwtToken') }
    })
        .then(res => res.json())
        .then(destinos => {
            console.log('Destinos cargados:', destinos);
            const selects = [
                document.getElementById('destinoHotel'),
                document.getElementById('destinoActividad'),
                document.getElementById('destinoTransporte')
            ];
            selects.forEach(select => {
                select.innerHTML = '<option value="">Selecciona destino...</option>';
                destinos.forEach(d => {
                    select.innerHTML += `<option value="${d.destino_id}">${d.nombre}</option>`;
                });
            });
        });
}

function crearUsuario(e) {
    e.preventDefault();
    const name = document.getElementById('nombreUsuario').value;
    const email = document.getElementById('emailUsuario').value;
    const password = document.getElementById('passwordUsuario').value;
    const userType = document.getElementById('tipoUsuario').value;
    fetch('/api/usuarios', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'Authorization': 'Bearer ' + localStorage.getItem('jwtToken')
        },
        body: JSON.stringify({ name, email, password, userType })
    })
    .then(res => res.json())
    .then(() => {
        alert('Usuario registrado correctamente');
        document.getElementById('formUsuario').reset();
        cargarUsuarios();
    });
}

function cargarUsuarios() {
    fetch('/api/usuarios', {
        headers: { 'Authorization': 'Bearer ' + localStorage.getItem('jwtToken') }
    })
    .then(res => res.json())
    .then(usuarios => {
        window._usuarios = usuarios;
        filtrarUsuarios();
    });
}

function cargarDestinos(){
    fetch('/api/destinos', {
        headers: { 'Authorization': 'Bearer ' + localStorage.getItem('jwtToken') }
    })
    .then(res => res.json())
    .then(destinos => {
        window._destinos = destinos;
        filtrarDestinos();
    });
}

function filtrarUsuarios() {
    const cont = document.getElementById('listaUsuarios');
    const filtro = (document.getElementById('filtroUsuario')?.value || '').toLowerCase();
    cont.innerHTML = '';
    window._usuarios.forEach(u => {
        if (
            u.name.toLowerCase().includes(filtro) ||
            u.email.toLowerCase().includes(filtro)
        ) {
            cont.innerHTML += `
                <div class="card mb-2 p-2">
                    <div><b>${u.name}</b> (${u.email}) - <span class="badge bg-${u.userType === 'ADMIN' ? 'danger' : 'primary'}">${u.userType}</span></div>
                    <button class="btn btn-sm btn-warning me-2" onclick="mostrarModalEditarUsuario(${u.user_id})">Editar</button>
                    <button class="btn btn-sm btn-danger" onclick="eliminarUsuario(${u.user_id})">Eliminar</button>
                </div>
            `;
        }
    });
}

function mostrarModalEditarUsuario(id) {
    const usuario = window._usuarios.find(u => u.user_id === id);
    if (!usuario) return;
    document.getElementById('editUserId').value = usuario.user_id;
    document.getElementById('editNombreUsuario').value = usuario.name;
    document.getElementById('editEmailUsuario').value = usuario.email;
    document.getElementById('editTipoUsuario').value = usuario.userType;
    let modal = bootstrap.Modal.getInstance(document.getElementById('modalEditarUsuario'));
    if (!modal) {
        modal = new bootstrap.Modal(document.getElementById('modalEditarUsuario'));
    }
    modal.show();
}
window.mostrarModalEditarUsuario = mostrarModalEditarUsuario;


function eliminarUsuario(id) {
    if (!confirm('¿Seguro que deseas eliminar este usuario?')) return;
    fetch(`/api/usuarios/${id}`, {
        method: 'DELETE',
        headers: { 'Authorization': 'Bearer ' + localStorage.getItem('jwtToken') }
    })
    .then(() => {
        alert('Usuario eliminado');
        cargarUsuarios();
    });
}


function crearHotel(e) {
    e.preventDefault();
    const nombre = document.getElementById('nombreHotel').value;
    const tarifaAdulto = parseFloat(document.getElementById('tarifaAdultoHotel').value);
    const tarifaNino = parseFloat(document.getElementById('tarifaNinoHotel').value);
    const destinoId = document.getElementById('destinoHotel').value;
    
    // Validación de datos
    console.log('Datos a enviar:', {nombre, tarifaAdulto, tarifaNino, destinoId: destinoId});
    
    if (!nombre || !tarifaAdulto || !tarifaNino || !destinoId) {
        alert('Por favor complete todos los campos');
        return;
    }
    
    const hotelPayload = { nombre, tarifaAdulto, tarifaNino, destinoId: destinoId };
    console.log('JSON enviado a /api/hoteles:', JSON.stringify(hotelPayload));
    fetch('/api/hoteles', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'Authorization': 'Bearer ' + localStorage.getItem('jwtToken')
        },
        body: JSON.stringify(hotelPayload)
    })
    .then(res => {
        console.log('Respuesta status:', res.status);
        if (!res.ok) {
            return res.text().then(text => {
                throw new Error(`HTTP ${res.status}: ${text}`);
            });
        }
        return res.json();
    })
    .then(data => {
        console.log('Hotel creado:', data);
        alert('Hotel creado correctamente');
        document.getElementById('formHotel').reset();
    })
    .catch(error => {
        console.error('Error al crear hotel:', error);
        alert('Error al crear hotel: ' + error.message);
    });
}

function crearActividad(e) {
    e.preventDefault();
    const nombre = document.getElementById('nombreActividad').value;
    const descripcion = document.getElementById('descripcionActividad').value;
    const precio = parseFloat(document.getElementById('precioActividad').value);
    const destinoId = document.getElementById('destinoActividad').value;
    console.log('Datos a enviar (actividad):', {nombre, descripcion, precio, destinoId});
    if (!nombre || !descripcion || !precio || !destinoId || destinoId === 'undefined') {
        alert('Por favor complete todos los campos y seleccione un destino válido');
        return;
    }
    const actividadPayload = { nombre, descripcion, precio, destinoId };
    console.log('JSON enviado a /api/actividades:', JSON.stringify(actividadPayload));
    fetch('/api/actividades', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'Authorization': 'Bearer ' + localStorage.getItem('jwtToken')
        },
        body: JSON.stringify(actividadPayload)
    })
    .then(res => {
        console.log('Respuesta status:', res.status);
        if (!res.ok) {
            return res.text().then(text => { throw new Error(`HTTP ${res.status}: ${text}`); });
        }
        return res.json();
    })
    .then(data => {
        console.log('Actividad creada:', data);
        alert('Actividad creada correctamente');
        document.getElementById('formActividad').reset();
    })
    .catch(error => {
        console.error('Error al crear actividad:', error);
        alert('Error al crear actividad: ' + error.message);
    });
}

function crearTransporte(e) {
    e.preventDefault();
    const tipo = document.getElementById('tipoTransporte').value;
    const empresa = document.getElementById('empresaTransporte').value;
    const precio = parseFloat(document.getElementById('precioTransporte').value);
    const destinoId = document.getElementById('destinoTransporte').value;
    console.log('Datos a enviar (transporte):', {tipo, empresa, precio, destinoId});
    if (!tipo || !empresa || !precio || !destinoId || destinoId === 'undefined') {
        alert('Por favor complete todos los campos y seleccione un destino válido');
        return;
    }
    const transportePayload = { tipo, empresa, precio, destinoId };
    console.log('JSON enviado a /api/transportes:', JSON.stringify(transportePayload));
    fetch('/api/transportes', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'Authorization': 'Bearer ' + localStorage.getItem('jwtToken')
        },
        body: JSON.stringify(transportePayload)
    })
    .then(res => {
        console.log('Respuesta status:', res.status);
        if (!res.ok) {
            return res.text().then(text => { throw new Error(`HTTP ${res.status}: ${text}`); });
        }
        return res.json();
    })
    .then(data => {
        console.log('Transporte creado:', data);
        alert('Transporte creado correctamente');
        document.getElementById('formTransporte').reset();
    })
    .catch(error => {
        console.error('Error al crear transporte:', error);
        alert('Error al crear transporte: ' + error.message);
    });
}

function crearDestino(e) {
    e.preventDefault();
    const nombre = document.getElementById('nombreDestino').value;
    const descripcion = document.getElementById('descripcionDestino').value;
    const ubicacion = document.getElementById('ubicacionDestino').value;
    fetch('/api/destinos', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'Authorization': 'Bearer ' + localStorage.getItem('jwtToken')
        },
        body: JSON.stringify({ nombre, descripcion, ubicacion })
    })
    .then(res => res.json())
    .then(data => {
        alert('Destino creado correctamente');
        document.getElementById('formDestino').reset();
    });
}