/// <reference types="vite/client" />
interface ImportMetaEnv {
    readonly VITE_WS_URL: string; // ajoute toutes tes variables VITE_ ici
    // readonly VITE_ANOTHER_VAR: string;
}

interface ImportMeta {
    readonly env: ImportMetaEnv;
}