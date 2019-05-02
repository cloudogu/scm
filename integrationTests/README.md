## Running the tests

1. Start up a local eco system with an SCM instance running
1. Start Zalenium using docker-compose:
    `docker-compose up`
1. Run the tests with docker:

```$sh
docker run -it --rm \
       --name jenkins-gauge \
       --network integrationtests_default \
       -v "$(pwd)":/usr/src/mymaven \
       -w /usr/src/mymaven\ 
       -e SELENIUM_REMOTE_URL=http://zalenium:4444/wd/hub \
       -e CES_FQDN=192.168.56.2
       -e ADMIN_USERNAME=admin
       -e ADMIN_PASSWORD=adminpw
       -e EMAIL=admin@example.com
       -e ADMIN_GROUP=cesManager
       -u $(id -u) \
       cloudogu/gauge-java:1.0.4 \
       mvn test
```

You may have to adapt the settings like `CES_FQDN`, `ADMIN_USERNAME` and so on according
to your local ecosystem.
