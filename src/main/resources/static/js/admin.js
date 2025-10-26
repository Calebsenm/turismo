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
        headers: { 'Authorization': 'Bearer ' + localStorage.getItem('jwt') }
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
    fetch('/api/destinos', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'Authorization': 'Bearer ' + localStorage.getItem('jwt')
        },
        body: JSON.stringify({ nombre, descripcion })
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
    // Implementa lógica similar para hoteles
}

function crearActividad(e) {
    e.preventDefault();
    // Implementa lógica similar para actividades
}

function crearTransporte(e) {
    e.preventDefault();
    // Implementa lógica similar para transporte
}
