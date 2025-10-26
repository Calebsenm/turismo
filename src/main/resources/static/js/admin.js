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
// Lógica para manejar formularios y peticiones AJAX del panel de administración

document.addEventListener('DOMContentLoaded', function () {
    // Usuarios
    document.getElementById('formUsuario').addEventListener('submit', crearUsuario);
    cargarUsuarios();
// Registrar usuario
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
    .then(data => {
        alert('Usuario registrado correctamente');
        document.getElementById('formUsuario').reset();
        cargarUsuarios();
    });
}

// Listar usuarios
function cargarUsuarios() {
    fetch('/api/usuarios', {
        headers: { 'Authorization': 'Bearer ' + localStorage.getItem('jwtToken') }
    })
    .then(res => res.json())
    .then(usuarios => {
        const cont = document.getElementById('listaUsuarios');
        cont.innerHTML = '';
        usuarios.forEach(u => {
            cont.innerHTML += `
                <div class="card mb-2 p-2">
                    <div><b>${u.name}</b> (${u.email}) - <span class="badge bg-${u.userType === 'ADMIN' ? 'danger' : 'primary'}">${u.userType}</span></div>
                    <button class="btn btn-sm btn-warning me-2" onclick="editarUsuario(${u.user_id})">Editar</button>
                    <button class="btn btn-sm btn-danger" onclick="eliminarUsuario(${u.user_id})">Eliminar</button>
                </div>
            `;
        });
    });
}

// Eliminar usuario
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

// Editar usuario (simple: solo tipo)
function editarUsuario(id) {
    const nuevoTipo = prompt('Nuevo tipo de usuario (CLIENT o ADMIN):');
    if (!nuevoTipo || (nuevoTipo !== 'CLIENT' && nuevoTipo !== 'ADMIN')) return alert('Tipo inválido');
    fetch(`/api/usuarios/${id}`, {
        method: 'PUT',
        headers: {
            'Content-Type': 'application/json',
            'Authorization': 'Bearer ' + localStorage.getItem('jwtToken')
        },
        body: JSON.stringify({ userType: nuevoTipo })
    })
    .then(res => res.json())
    .then(() => {
        alert('Usuario actualizado');
        cargarUsuarios();
    });
}
    // Ejemplo: cargar destinos en los selects de hotel, actividad y transporte
    cargarDestinosSelects();

    // Listeners para formularios
    document.getElementById('formDestino').addEventListener('submit', crearDestino);
    document.getElementById('formHotel').addEventListener('submit', crearHotel);
    document.getElementById('formActividad').addEventListener('submit', crearActividad);
    document.getElementById('formTransporte').addEventListener('submit', crearTransporte);
});

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
        // Actualizar lista de destinos si lo deseas
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
