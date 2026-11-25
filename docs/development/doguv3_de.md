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

## Integrationen mit anderen Dogus

Im Classic- und Multinode-v2-Betrieb erkennt SCM selbst, ob andere Dogus installiert sind,
und installiert und konfiguriert daraufhin die passenden Plugins. Mit Dogu v3 geht das nicht mehr.
Deshalb wird jede Integration explizit über `scm.integrations` in der `values.yaml` aktiviert:

```yaml
scm:
  integrations:
    cockpit:
      enabled: true          # scm-cockpit-legacy-plugin (SCM-Widget im Cockpit)
    jenkins:
      enabled: true          # scm-jenkins-plugin, scm-ci-plugin
    redmine:
      enabled: true          # scm-redmine-plugin
      type: EASY_REDMINE     # REDMINE | EASY_REDMINE
    jira:
      enabled: false         # scm-jira-plugin
```

Das Chart rendert daraus pro Integration die Umgebungsvariable `SCM_INTEGRATION_<NAME>_ENABLED` sowie `SCM_INTEGRATION_REDMINE_TYPE`.
Die Init-Skripte fragen die Integrationen über `EcoSystem.isIntegrationEnabled(<dogu>)` bzw. `EcoSystem.getRedmineIntegration()` ab:

- Ist die Umgebungsvariable gesetzt, gilt ihr Wert.
- Fehlt sie (Classic, Multinode v2), wird wie bisher geprüft, ob das Dogu installiert ist.

Änderungen greifen beim nächsten Start von SCM. Das Deaktivieren einer Integration deinstalliert keine Plugins.

Gotenberg ist nicht Teil von `scm.integrations`, sondern wird als Subchart über `gotenberg.enabled` gesteuert.

Hinweis: Um sicherzustellen, dass die SCM-Änderungen nach einer Neuinstallation erhalten bleiben,
wird „pvc“ (scm-data-scm-0) nicht automatisch deinstalliert.
Bei Bedarf kann es manuell entfernt werden.

## Migration von Dogu v2

Die Migration eines v2-SCM auf v3 steuert der [k8s-dogu-operator](https://github.com/cloudogu/k8s-dogu-operator).
Das Chart bringt dafür einen Migrations-Job mit (`templates/pvc-migration.yaml`), den der Operator über `migrationJob.enabled=true` aktiviert. 

Solange er aktiv ist:
- legt das Chart den Daten-Claim des StatefulSets (`scm-data-<fullname>-0`) vorab an,
- läuft das StatefulSet mit 0 Replicas,
- kopiert der Job die Daten aus dem Claim `scm-migration` in den neuen Claim

`scm.persistence.size` muss vor der Migration mindestens so groß gesetzt werden wie das v2-Volume.
