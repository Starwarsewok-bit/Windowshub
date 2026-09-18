# Windows Admin Companion

Small Android wrapper for Windows Admin Center on HOMESERVER.

- Opens `https://192.168.0.105:6600`
- Keeps cookies and the signed-in session
- Accepts the self-signed certificate only for that exact LAN host and port
- Blocks navigation to unrelated hosts
- Provides Android back navigation, zoom, and loading progress

## Build

Open the directory in Android Studio and build the `debug` APK, or push it to a private GitHub repository and run **Build APK** under Actions. The generated debug APK is installable after Android permits installs from the browser/files app used to open it.

This build is intentionally LAN-only. A later version can add a separate Tailscale address once port 6600 is reachable through Tailscale.
