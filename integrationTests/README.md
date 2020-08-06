## Running the tests

1. Start up a local eco system with an SCM instance running
1. Prepare selenium docker image:
    `docker pull elgalu/selenium`
1. Start Zalenium using docker-compose:
    `docker-compose up`

Then you can run the tests either with docker:

```sh
docker run -it --rm \
       --name jenkins-gauge \
       --network integrationtests_default \
       -v "$(pwd)":/usr/src/mymaven \
       -w /usr/src/mymaven \
       -e SELENIUM_REMOTE_URL=http://zalenium:4444/wd/hub \
       -e CES_FQDN=192.168.56.2 \
       -e ADMIN_USERNAME=admin \
       -e ADMIN_PASSWORD=adminpw \
       -e EMAIL=admin@example.com \
       -e ADMIN_GROUP=cesManager \
       -u $(id -u) \
       cloudogu/gauge-java:1.0.4 \
       mvn test
```

Or you can run them directly with maven, provided you have at least Java 11 and Gauge
installed locally:

```sh
SELENIUM_REMOTE_URL=http://localhost:4444/wd/hub \
CES_FQDN=192.168.56.2 \
ADMIN_USERNAME=admin \
ADMIN_PASSWORD=adminpw \
EMAIL=admin@example.com \
ADMIN_GROUP=cesManager \
mvn test
```

You may have to adapt the settings like `CES_FQDN`, `ADMIN_USERNAME` and so on according
to your local ecosystem.
