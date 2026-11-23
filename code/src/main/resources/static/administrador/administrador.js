import {baseAPI, authFetch} from "../util.js";

(() => {
    "use strict";

    // APIs a utilizar
    const adminAPI = `${baseAPI}/auth/administrador`;
    const staffAPI = `${baseAPI}/usuarios/personal`;

    // Registrar personal
    const staffForm = document.getElementById("staff-form");
    const staffFirstName = document.getElementById("staff-first-name");
    const staffLastName = document.getElementById("staff-last-name");
    const staffId = document.getElementById("staff-id");
    const staffPhone = document.getElementById("staff-phone");
    const staffEmail = document.getElementById("staff-email");
    const staffPassword = document.getElementById("staff-password");
    const staffStatus = document.getElementById("staff-status");

    // Cambiar contraseña
    const passwordForm = document.getElementById("password-form");
    const currentPassword = document.getElementById("current-password");
    const newPassword = document.getElementById("new-password");
    const confirmPassword = document.getElementById("confirm-password");
    const passwordStatus = document.getElementById("password-status");

    /**
     * Registra a personal
     * @requires fetch a http://localhost:8080/api/usuarios/personal
     */
    async function registerStaff() {
        // Obtener datos del formulario
        const registerInfo = {
            nombre: staffFirstName.value,
            apellido: staffLastName.value,
            cedula: staffId.value,
            celular: staffPhone.value,
            correo: staffEmail.value,
            clave: staffPassword.value
        };

        try {
            // Enviar los datos a la API para el registro
            await authFetch(staffAPI, {
                method: "POST",
                body: JSON.stringify(registerInfo)
            });

            // Indicar registro exitoso si no hay errores
            staffStatus.textContent = "Registro exitoso de empleado";
            staffForm.reset();

        } catch (error) {
            // Mostrar error al usuario
            staffStatus.textContent = error.message;
        }
    }

    staffForm.addEventListener("submit", async (event) => {
        event.preventDefault();
        await registerStaff();
    });

    /**
     * Cambia clave de administrador
     * @requires fetch a http://localhost:8080/api/auth/administrador
     */
    async function changePassword() {
        // Obtener datos del formulario
        const registerInfo = {
            actual: currentPassword.value,
            nueva: newPassword.value,
            confirmacion: confirmPassword.value
        };

        try {
            // Enviar los datos a la API para el cambio de clave
            await authFetch(`${adminAPI}/clave`, {
                method: "POST",
                body: JSON.stringify(registerInfo)
            });

            // Indicar cambio exitoso si no hay errores
            passwordStatus.textContent = "Cambio de contraseña exitoso";
            passwordForm.reset();

        } catch (error) {
            // Mostrar error al usuario
            passwordStatus.textContent = error.message;
        }
    }

    passwordForm.addEventListener("submit", async (event) => {
        event.preventDefault();
        await changePassword();
    });

})();