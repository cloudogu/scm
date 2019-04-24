## Running the tests

1. Start Zalenium using the docker-compose file:
    `docker-compose up`
1. Run the tests with docker:

```$sh
docker run -it --rm \
       --name jenkins-gauge \
       --network integrationtests_default \
       -v "$(pwd)":/usr/src/mymaven \
       -w /usr/src/mymaven\ 
       -e SELENIUM_REMOTE_URL=http://zalenium:4444/wd/hub \
       -u $(id -u) \
       cloudogu/gauge-java:1.0.4 \
       mvn test
```
