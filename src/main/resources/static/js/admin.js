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
