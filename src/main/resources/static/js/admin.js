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
// admin.js
// ---- FUNCIONES DE USUARIOS ----
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
    // Usuarios
    const formUsuario = document.getElementById('formUsuario');
    if (formUsuario) {
        formUsuario.addEventListener('submit', crearUsuario);
        cargarUsuarios();
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
            const selects = [
                document.getElementById('destinoHotel'),
                document.getElementById('destinoActividad'),
                document.getElementById('destinoTransporte')
            ];
            selects.forEach(select => {
                select.innerHTML = '<option value="">Selecciona destino...</option>';
                destinos.forEach(d => {
                    select.innerHTML += `<option value="${d.id}">${d.nombre}</option>`;
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
    fetch('/api/hoteles', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'Authorization': 'Bearer ' + localStorage.getItem('jwtToken')
        },
        body: JSON.stringify({ nombre, tarifaAdulto, tarifaNino, destino_id: destinoId })
    })
    .then(res => res.json())
    .then(data => {
        alert('Hotel creado correctamente');
        document.getElementById('formHotel').reset();
    });
}

function crearActividad(e) {
    e.preventDefault();
    const nombre = document.getElementById('nombreActividad').value;
    const descripcion = document.getElementById('descripcionActividad').value;
    const precio = parseFloat(document.getElementById('precioActividad').value);
    const destinoId = document.getElementById('destinoActividad').value;
    fetch('/api/actividades', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'Authorization': 'Bearer ' + localStorage.getItem('jwtToken')
        },
        body: JSON.stringify({ nombre, descripcion, precio, destino_id: destinoId })
    })
    .then(res => res.json())
    .then(data => {
        alert('Actividad creada correctamente');
        document.getElementById('formActividad').reset();
    });
}

function crearTransporte(e) {
    e.preventDefault();
    const tipo = document.getElementById('tipoTransporte').value;
    const empresa = document.getElementById('empresaTransporte').value;
    const precio = parseFloat(document.getElementById('precioTransporte').value);
    const destinoId = document.getElementById('destinoTransporte').value;
    fetch('/api/transportes', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'Authorization': 'Bearer ' + localStorage.getItem('jwtToken')
        },
        body: JSON.stringify({ tipo, empresa, precio, destino_id: destinoId })
    })
    .then(res => res.json())
    .then(data => {
        alert('Transporte creado correctamente');
        document.getElementById('formTransporte').reset();
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