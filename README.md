# Sistema de envío de correo electrónico (versión simple y gratuita)

Flujo: **Usuario → Angular → Spring Boot → SendGrid → Destinatario**

```
correo-app/
├── backend/    Spring Boot 3.3.5 + Java 21 + Maven + SendGrid (sendgrid-java 4.10.3)
├── frontend/   Angular 19 (un componente + EmailService)
├── .env.example
└── .gitignore
```

> Versión simplificada para estudiantes: **sin dominios, sin Cloudflare, sin pago**. Se usa la verificación de
> "Single Sender" de SendGrid (un solo correo tuyo como remitente).

## 1. SendGrid (gratis por 60 días)

1. Crea cuenta en https://sendgrid.com (cuenta nueva = prueba gratuita de 60 días, 100 correos/día, según la documentación de Twilio SendGrid; al terminar el envío se detiene).
2. **Settings → Sender Authentication → Verify a Single Sender.** Usa tu correo real (ej. Gmail) y confirma el enlace que llega a tu bandeja.
3. **Settings → API Keys → Create API Key → Restricted Access →** activa solo **Mail Send**. Copia la clave (solo se muestra una vez).
4. Si un correo no llega: revisa spam y **Email Activity** en el panel de SendGrid.

> Un `202` solo significa que SendGrid **aceptó** la solicitud, no que el correo llegó.

## 2. Ejecutar el backend

Requisitos: Java 21 y Maven.

```bash
cd backend
# Linux/Mac
export SENDGRID_API_KEY="tu_api_key"
export SENDGRID_FROM_EMAIL="tu_correo_verificado@gmail.com"
mvn spring-boot:run
# Windows PowerShell: $env:SENDGRID_API_KEY="..."; $env:SENDGRID_FROM_EMAIL="..."
```

Pruebas automáticas (no envían correos reales): `mvn test`

## 3. Probar con Postman

Importa `backend/EmailAPI.postman_collection.json`. Variables: `baseUrl` (local: `http://localhost:8080`) y `testEmail` (un correo tuyo).

| Prueba | Body | Esperado |
|---|---|---|
| A válido | to/subject/message correctos | **202** `{"status":"success","message":"Solicitud de envío aceptada"}` |
| B correo inválido | `"to":"esto-no-es-un-correo"` | **400** `{"status":"error","message":"El correo del destinatario no es válido"}` |
| C asunto vacío | `"subject":""` | **400** |
| D sin `subject` | quitar la propiedad | **400** |
| E error de SendGrid | Arranca el backend con `SENDGRID_API_KEY=clave_falsa` (solo en tu PC) y haz la prueba A | **502** |
| F desplegado | cambia `baseUrl` a tu URL de Render | igual que A |

Header: `Content-Type: application/json`. Si falla A: revisa las variables de entorno, que el remitente esté verificado y los logs de la consola.

## 4. Ejecutar el frontend

```bash
cd frontend
npm install
npx ng serve        # http://localhost:4200
```
En desarrollo usa `src/environments/environment.development.ts` (localhost:8080).

## 5. Subir a GitHub

```bash
git init && git add . && git commit -m "Primer commit"
# crea un repo vacío en github.com y sigue sus instrucciones para `git remote add` y `git push`
```
Comprueba que **no** se suba `.env` ni ninguna API Key.

## 6. Desplegar el backend en Render (sin dominio propio)

1. https://render.com → **New → Web Service** → conecta tu repo de GitHub.
2. **Root Directory:** `backend` · **Runtime:** `Docker` (usa el `Dockerfile` incluido) · Plan **Free**.
3. **Environment Variables:** `SENDGRID_API_KEY`, `SENDGRID_FROM_EMAIL`, y `CORS_ORIGINS` (ponla después, paso 7).
4. Render define `PORT` solo; Spring ya lo lee. Revisa la pestaña **Logs**.
5. Tu URL pública será algo como `https://TU-SERVICIO.onrender.com` (la verás en el panel). Prueba con Postman.

> El plan gratis suele "dormirse" tras un tiempo sin uso; la primera petición puede tardar. Verifica los límites vigentes en render.com.

## 7. Desplegar el frontend en Vercel (sin dominio propio)

1. Edita `frontend/src/environments/environment.ts` y pon la URL real de Render en `apiUrl`. Haz commit y push.
2. https://vercel.com → **Add New → Project** → elige el repo.
3. **Root Directory:** `frontend` · **Framework:** Angular · **Build Command:** `npm run build` · **Output Directory:** `dist/frontend/browser`.
4. Vercel te dará una URL `https://algo.vercel.app` (HTTPS incluido).
5. Vuelve a Render y pon `CORS_ORIGINS=https://algo.vercel.app` (sin `/` final). Guarda y espera el redeploy.
6. Abre la URL de Vercel y envía un correo de prueba.

## Seguridad básica incluida

- API Key solo en variables de entorno del backend (nunca en Angular ni en GitHub).
- El remitente lo define el servidor; el cliente no puede cambiarlo.
- Validación de formato y longitud; mensaje enviado como texto plano.
- CORS limitado a los orígenes de `CORS_ORIGINS` (sin `*`).
- Errores sin trazas ni secretos.

**Pendiente (no implementado):** no hay *rate limiting* ni autenticación, así que cualquiera que conozca la URL del backend podría usarlo para enviar spam con tu remitente. Para una tarea académica es aceptable; si lo dejas público mucho tiempo, añade un límite de peticiones (p. ej. la librería Bucket4j) o elimina el servicio al terminar.
