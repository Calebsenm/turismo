document.addEventListener('DOMContentLoaded', () => {
    const loginForm = document.getElementById('login-form');
    const messageDiv = document.getElementById('login-message');

    loginForm.addEventListener('submit', async (event) => {
        // Prevenir el envío tradicional del formulario
        event.preventDefault();

        const email = document.getElementById('email').value;
        const password = document.getElementById('password').value;

        try {
            const response = await fetch('/v1/api/auth/login', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({ email, password })
            });

            if (response.ok) {
                const data = await response.json();
                // Guardar el token en el almacenamiento local del navegador
                localStorage.setItem('jwtToken', data.token);

                messageDiv.textContent = '¡Inicio de sesión exitoso! Redirigiendo...';
                messageDiv.style.color = 'green';

                // Redirigir a la página principal después de 1 segundo
                setTimeout(() => {
                    window.location.href = '/public/home';
                }, 1000);
            } else {
                const errorData = await response.json();
                messageDiv.textContent = `Error: ${errorData.message || 'Credenciales inválidas.'}`;
                messageDiv.style.color = 'red';
            }
        } catch (error) {
            messageDiv.textContent = 'Error de conexión. Por favor, intente más tarde.';
            messageDiv.style.color = 'red';
        }
    });
});