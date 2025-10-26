document.addEventListener('DOMContentLoaded', () => {
    const logoutButton = document.getElementById('logout-button');

    if (logoutButton) {
        logoutButton.addEventListener('click', () => {
            // 1. Eliminar el token del almacenamiento local
            localStorage.removeItem('jwtToken');

            // 2. Mostrar un mensaje en la consola (opcional)
            console.log('Sesión cerrada. Redirigiendo al login...');

            // 3. Redirigir al usuario a la página de inicio de sesión
            window.location.href = '/public/login';
        });
    }
});