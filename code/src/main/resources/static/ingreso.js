import {baseAPI, storeUser, getUser, findResponseErrors} from "./util.js";

(() => {
    "use strict";

    /**
     * Redirige al usuario cuando inicia sesión o si ya la ha iniciado
     */
    function redirectUser()
    {
        const user = getUser();
        if(!user) return;

        switch(user.rol) {
            case "CLIENTE":
                window.location.replace("./cliente/cliente.html");
                break;
            case "PERSONAL":
                window.location.replace("./personal/personal.html");
                break;
            case "ADMINISTRADOR":
                window.location.replace("./administrador/administrador.html");
                break;
            default:
                break;
        }
    }

    redirectUser();

    // APIs a utilizar
    const loginAPI = `${baseAPI}/auth`;
    const clientAPI = `${baseAPI}/usuarios/clientes`;

    // Elementos de navegación
    const loginView = document.getElementById("view-login");
    const registerView = document.getElementById("view-register");
    const showRegister = document.getElementById("show-register");
    const showLogin = document.getElementById("show-login");

    // Inicio de sesión
    const loginForm = document.getElementById("login-form");
    const loginEmail = document.getElementById("login-email");
    const loginPassword = document.getElementById("login-password");
    const loginRole = document.getElementById("login-role");
    const loginStatus = document.getElementById("login-status");

    // Registro de estudiantes
    const registerForm = document.getElementById("register-form");
    const studentFirstName = document.getElementById("student-first-name");
    const studentLastName = document.getElementById("student-last-name");
    const studentId = document.getElementById("student-id");
    const studentPhone = document.getElementById("student-phone");
    const studentEmail = document.getElementById("student-email");
    const studentPassword = document.getElementById("student-password");
    const registerStatus = document.getElementById("register-status");

    /**
     * Cambia entre el panel de login y registro
     * @param {string} name - Panel al que se cambia "login" o "register"
     */
    function showAuthView(name) {
        const showingLogin = name === "login";
        loginView.hidden = !showingLogin;
        registerView.hidden = showingLogin;
        loginStatus.textContent = "";
        registerStatus.textContent = "";
        (showingLogin ? loginView : registerView).querySelector("h1").focus();
    }

    showRegister.addEventListener("click", () => showAuthView("register"));
    showLogin.addEventListener("click", () => showAuthView("login"));

    /**
     * Registra a un cliente/estudiante
     * @requires fetch a http://localhost:8080/api/usuarios/clientes
     */
    async function registerClient() {
        // Obtener datos del formulario
        const registerInfo = {
            nombre: studentFirstName.value,
            apellido: studentLastName.value,
            cedula: studentId.value,
            celular: studentPhone.value,
            correo: studentEmail.value,
            clave: studentPassword.value
        };

        try {
            // Enviar los datos a la API para el registro
            const response = await fetch(clientAPI, {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify(registerInfo)
            });
            if (!response.ok) {await findResponseErrors(response)}

            // Indicar registro exitoso si no hay errores
            registerStatus.textContent = "Registro exitoso. Ya puede iniciar sesión";
            registerForm.reset();

        } catch (error) {
            // Mostrar error al usuario
            registerStatus.textContent = error.message;
        }
    }

    registerForm.addEventListener("submit", async (event) => {
        event.preventDefault();
        await registerClient();
    });

    /**
     * Ingreso al sistema como cliente/estudiante, personal o adminsitrador
     * @requires fetch a http://localhost:8080/api/auth
     */
    async function login() {
        //Obtener datos del formulario
        const role = loginRole.value;
        const loginInfo = {
            identificador: loginEmail.value,
            clave: loginPassword.value
        };

        try {
            // Enviar los datos a la API segun el rol para el login
            const response = await fetch(`${loginAPI}/${role}`, {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify(loginInfo)
            });
            if (!response.ok) {await findResponseErrors(response)}

            // Redirigir al usuario si inicia sesion sin errores
            const user = await response.json();
            storeUser(user);
            redirectUser();

        } catch (error) {
            // Mostrar error al usuario
            loginStatus.textContent = error.message;
        }
    }

    loginForm.addEventListener("submit", async (event) => {
        event.preventDefault();
        await login();
    });
})();