// Este script se debe incluir en la parte superior (en el <head>) 
// de todas las páginas que requieran autenticación.

const jwtToken = localStorage.getItem('jwtToken');

if (!jwtToken) {
    // Si no hay token, no se puede estar aquí. Redirigir al login.
    console.log('No se encontró token. Redirigiendo al login...');
    // Usamos window.location.replace para que el usuario no pueda "volver atrás" a la página protegida.
    window.location.replace('/public/login');
}