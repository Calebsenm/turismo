// mis-paquetes.js

document.addEventListener('DOMContentLoaded', () => {
    cargarMisPaquetes();
    const filtroInput = document.getElementById('filtroPaquete');
    if (filtroInput) {
        filtroInput.addEventListener('input', filtrarPaquetes);
    }
});

let _misPaquetes = [];

function cargarMisPaquetes() {
    fetch('/api/paquetes/mis', {
        headers: { 'Authorization': 'Bearer ' + localStorage.getItem('jwtToken') }
    })
    .then(res => res.json())
    .then(paquetes => {
        _misPaquetes = paquetes;
        console.log(_misPaquetes);
        filtrarPaquetes();
    });
}

function filtrarPaquetes() {
    const cont = document.getElementById('listaPaquetes');
    if (!cont) return;
    const filtro = (document.getElementById('filtroPaquete')?.value || '').toLowerCase();
    cont.innerHTML = '';
    _misPaquetes.forEach(p => {
        if (
            p.nombre.toLowerCase().includes(filtro) ||
            p.destino?.nombre?.toLowerCase().includes(filtro) ||
            (p.fecha && p.fecha.toString().includes(filtro))
        ) {
            cont.innerHTML += `
                <div class="card col-md-4 mb-3 p-2">
                    <div><b>${p.nombre}</b></div>
                    <div>Destino: ${p.destino?.nombre || ''}</div>
                    <div>Fecha: ${p.fecha || ''}</div>
                    <div>Precio: $${p.precio || ''}</div>
                    <button class="btn btn-danger btn-sm mt-2" onclick="eliminarPaquete(${p.paquete_id})">Eliminar</button>
                </div>
            `;
        }
    });
}

// Eliminar paquete por id
window.eliminarPaquete = function(id) {
    if (!confirm('¿Seguro que deseas eliminar este paquete?')) return;
    fetch(`/api/paquetes/${id}`, {
        method: 'DELETE',
        headers: { 'Authorization': 'Bearer ' + localStorage.getItem('jwtToken') }
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
