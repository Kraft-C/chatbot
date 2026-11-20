/*
 * PAWLY - comportements côté navigateur.
 *
 * Important : les formulaires du chat (création de conversation et envoi d'un message)
 * sont de vrais formulaires HTML envoyés au serveur (POST). Ce script ne doit JAMAIS
 * les intercepter avec preventDefault() pour envoyer un message, sinon le chat IA ne
 * fonctionnerait plus.
 */
document.addEventListener("DOMContentLoaded", () => {

    // ---- Connexion : afficher / masquer le mot de passe ----
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

    // ---- Liens "bientôt disponible" : ne rien faire (évite le saut en haut de page) ----
    document.querySelectorAll('a[aria-disabled="true"]').forEach((link) => {
        link.addEventListener("click", (event) => event.preventDefault());
    });

    // ---- Chat : suggestions rapides -> remplissent le champ de saisie ----
    const promptInput = document.getElementById("promptInput");
    document.querySelectorAll("[data-prompt]").forEach((button) => {
        button.addEventListener("click", () => {
            if (!promptInput) return;
            promptInput.value = button.dataset.prompt;
            promptInput.focus();
            promptInput.setSelectionRange(promptInput.value.length, promptInput.value.length);
        });
    });

    // ---- Chat : défilement automatique vers le dernier message ----
    const scrollZone = document.querySelector("[data-chat-scroll]");
    if (scrollZone && document.querySelector(".chat-messages")) {
        scrollZone.scrollTop = scrollZone.scrollHeight;
    }

    // ---- Chat : curseur dans la zone de saisie quand on est dans une conversation ----
    if (promptInput && document.querySelector(".chat-dock")) {
        promptInput.focus();
    }

    // ---- Chat : retour visuel pendant l'attente de la réponse de l'IA ----
    // L'envoi n'est PAS bloqué : le formulaire part normalement vers le serveur.
    // On évite seulement le double envoi (double clic) et on affiche un état "en cours".
    document.querySelectorAll("[data-chat-form]").forEach((form) => {
        form.addEventListener("submit", (event) => {
            if (form.classList.contains("is-sending")) {
                event.preventDefault(); // second clic pendant l'envoi : on ignore
                return;
            }
            // Le navigateur ne soumet pas un formulaire invalide : ne marquer "en cours" que s'il est valide.
            if (form.checkValidity()) {
                form.classList.add("is-sending");
            }
        });
    });
    // Retour arrière du navigateur (page restaurée depuis le cache) : réinitialiser l'état.
    window.addEventListener("pageshow", () => {
        document.querySelectorAll("[data-chat-form].is-sending").forEach((form) => form.classList.remove("is-sending"));
    });

    // ---- Chat : affichage progressif (ligne par ligne) de la dernière réponse de l'IA ----
    // On mémorise l'envoi d'un message, puis on anime la réponse une fois la page rechargée.
    // Le formulaire n'est pas intercepté : le chat continue de fonctionner normalement.
    document.querySelectorAll("[data-chat-form]").forEach((form) => {
        form.addEventListener("submit", () => {
            if (form.checkValidity()) {
                try { sessionStorage.setItem("pawlyAnimer", location.pathname); } catch (e) {}
            }
        });
    });

    try {
        const aAnimer = sessionStorage.getItem("pawlyAnimer");
        // Cas d'un nouveau message dans une conversation existante : même URL qu'avant l'envoi.
        // Cas d'une nouvelle conversation : l'URL change, on anime donc dès qu'on est sur /conversations/{id}.
        if (aAnimer && /^\/conversations\/\d+$/.test(location.pathname)) {
            sessionStorage.removeItem("pawlyAnimer");
            const reponses = document.querySelectorAll("[data-message-assistant='true']");
            const derniere = reponses[reponses.length - 1];
            if (derniere && !window.matchMedia("(prefers-reduced-motion: reduce)").matches) {
                const lignes = derniere.textContent.split("\n");
                derniere.textContent = "";
                let i = 0;
                const afficherSuivante = () => {
                    if (i >= lignes.length) return;
                    derniere.textContent += (i > 0 ? "\n" : "") + lignes[i];
                    i++;
                    if (scrollZone) scrollZone.scrollTop = scrollZone.scrollHeight;
                    setTimeout(afficherSuivante, 250); // délai entre deux lignes, en millisecondes
                };
                afficherSuivante();
            }
        }
    } catch (e) {}

    // ---- Chat : menu latéral sur mobile ----
    const layout = document.querySelector("[data-chat-layout]");
    if (layout) {
        const toggle = layout.querySelector("[data-sidebar-toggle]");
        const setOpen = (open) => {
            layout.classList.toggle("is-sidebar-open", open);
            if (toggle) toggle.setAttribute("aria-label", open ? "Fermer le menu" : "Ouvrir le menu");
        };
        if (toggle) toggle.addEventListener("click", () => setOpen(!layout.classList.contains("is-sidebar-open")));
        layout.querySelectorAll("[data-sidebar-close]").forEach((el) => el.addEventListener("click", () => setOpen(false)));
        document.addEventListener("keydown", (event) => {
            if (event.key === "Escape") setOpen(false);
        });
    }
});