document.addEventListener("DOMContentLoaded", () => {
    // Afficher / masquer le mot de passe sur le formulaire de connexion.
    document.querySelectorAll("[data-toggle-password]").forEach((button) => {
        button.addEventListener("click", () => {
            const input = document.getElementById(button.dataset.togglePassword);
            if (!input) return;
            const visible = input.type === "password";
            input.type = visible ? "text" : "password";
            button.innerHTML = visible ? '<i class="bi bi-eye-slash"></i>' : '<i class="bi bi-eye"></i>';
            button.setAttribute("aria-label", visible ? "Masquer le mot de passe" : "Afficher le mot de passe");
        });
    });

    // Les suggestions remplissent le champ de discussion.
    const messageInput = document.getElementById("messageInput");
    document.querySelectorAll("[data-prompt]").forEach((button) => {
        button.addEventListener("click", () => {
            if (!messageInput) return;
            messageInput.value = button.dataset.prompt + " ";
            messageInput.focus();
            messageInput.setSelectionRange(messageInput.value.length, messageInput.value.length);
        });
    });

    // Interface prête : l'envoi IA sera relié au contrôleur/API dans une prochaine étape.
    const chatForm = document.getElementById("chatForm");
    if (chatForm) {
        chatForm.addEventListener("submit", (event) => {
            event.preventDefault();
            const message = messageInput.value.trim();
            if (!message) return;
            alert("Interface prête. La connexion au service IA sera ajoutée à l'étape backend.");
            messageInput.value = "";
        });
    }
});