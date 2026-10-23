# scm - Dogu v3

## Einleitung
Die wesentliche Dogu v3-Unterstützung für den SCM-Manager wurde in den folgenden Bereichen hinzugefügt:

### resources/dogu_v3
- Fügt Dogu v3-spezifische Start- und Dienstkonto-Hook-Skripte hinzu.
- Bereitet das von `doguctl` in Kubernetes erwartete `dogu.json`-Layout vor.
- Stellt sicher, dass persistente SCM-Verzeichnisse dem SCM-Laufzeitbenutzer gehören.
- Passt die Erstellung, Löschung, Existenzprüfung und Rotationsverwaltung von Dienstkonten für den Dogu v3-Dienstkonto-Sidecar an.

### resources/var/tmp/scm/init.script.d
- Passen die SCM-Initialisierungsskripte an die Dogu v3-Laufzeitumgebung an.
- Stellt sicher, dass die SCM-Konfiguration beim Start für das Ökosystem verfügbar ist.
- Enthält die Konfigurationsverwaltung für Integrationen, die nun innerhalb des SCM-Deployments bereitgestellt werden.

### scm/k8s/helm
- Implementiert das Helm-basierte Dogu v3-Deployment für SCM.
- Definiert Kubernetes-Ressourcen wie StatefulSet, Services, Exposure, Konfiguration, Netzwerkrichtlinien und die Integration von Service-Konten.
- Fügt Chart-Werte und Metadaten für die SCM-Image-Konfiguration, Persistenz, Sicherheitskontexte, Ressourcen und die Dogu-Konfiguration hinzu.
- Bündelt Gotenberg als Teil des SCM-Helm-Releases, wodurch ein separates Gotenberg-Dogu überflüssig wird.

Darüber hinaus wurden die Dateien `Makefile` und `Dockerfile` aktualisiert, um das Erstellen, Veröffentlichen, Installieren und Deinstallieren der Dogu v3-Variante zu unterstützen.

## Entwicklung

### Setup: 
   `.env` aus `.env.template` befüllen.
### Deployen/Aktualisieren:
  ```
  make scm-v3-install
  ```
  Baut und pusht das Image in die Dev-Registry und macht `helm upgrade --install` mit
  `--set-string scm.image.*` + `imagePullPolicy=Always`.

###  Deinstallieren (PVCs bleiben erhalten):
  ```
  make scm-v3-uninstall
  ```

## `isInDoguListEnv` und `INSTALLED_DOGUS_FOR_SCM`

In der SCM-Initialisierung prüft `EcoSystem.isInstalledMN()` zunächst, 
ob das betroffene Dogu in der Umgebungsvariable `INSTALLED_DOGUS_FOR_SCM` enthalten ist. 
Die Logik stammt aus `isInDoguListEnv(String doguName)` und 
prüft die Liste der installierten Dogus als durch Kommas oder Leerzeichen getrennte Namen.

Beispiele:

- `INSTALLED_DOGUS_FOR_SCM="cockpit"` -> `cockpit` wird als installiert erkannt
- `INSTALLED_DOGUS_FOR_SCM="cockpit,sonarqube"` -> `sonarqube` und `cockpit` werden als installiert erkannt

Damit kann SCM in einer Dogu-v3-/Multinode-Umgebung feststellen, ob ein Dogu in der installierten Dogu-Liste enthalten ist, auch wenn das `dogu.json`-Layout oder der Registry-Pfad im aktuellen Container noch nicht verfügbar ist.

### Beispiel für das Helm-Deployment

Im `StatefulSet` kann die Liste der installierten Dogus als eine einzelne Variable gesetzt werden:

```yaml
env:
 - name: INSTALLED_DOGUS_FOR_SCM
   value: "cockpit,sonarqube"
```

Die zugehörige Helm-Definition erfolgt über die Werte:

```yaml
scm:
 installed:
   dogus:
     - cockpit
     - jenkins
```

Die Variable wird beim Rendern in eine CSV-Liste umgewandelt und anschließend in Groovy als Namen-Liste ausgewertet.

Hinweis: Um sicherzustellen, dass die SCM-Änderungen nach einer Neuinstallation erhalten bleiben,
wird „pvc“ (scm-data-scm-0) nicht automatisch deinstalliert.
Bei Bedarf kann es manuell entfernt werden.