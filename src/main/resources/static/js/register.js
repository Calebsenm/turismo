document.addEventListener('DOMContentLoaded', () => {
    const registerForm = document.getElementById('register-form');
    const messageDiv = document.getElementById('register-message');

    registerForm.addEventListener('submit', async (event) => {
        event.preventDefault();

        const name = document.getElementById('name').value;
        const email = document.getElementById('email').value;
        const password = document.getElementById('password').value;

        try {
            const response = await fetch('/v1/api/auth/register', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({ name, email, password })
            });

            const data = await response.json();

            if (response.ok) {
                messageDiv.textContent = '¡Registro exitoso! Serás redirigido a la página de inicio de sesión.';
                messageDiv.style.color = 'green';

                setTimeout(() => {
                    window.location.href = '/public/login';
                }, 2000);
            } else {
                // Manejar errores de validación o de usuario existente
                let errorMessage = data.message || 'Ocurrió un error.';
                if (data.details) {
                    errorMessage += ' ' + Object.values(data.details).join(', ');
                }
                messageDiv.textContent = `Error: ${errorMessage}`;
                messageDiv.style.color = 'red';
            }
        } catch (error) {
            messageDiv.textContent = 'Error de conexión. Por favor, intente más tarde.';
            messageDiv.style.color = 'red';
        }
    });
});