

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

<h3>docker in local</h3>

(slow) To launch the Junit test in the docker container (this starts the postgresql service automatically):
```bash
docker compose run --rm --build test && docker image rm createuserinauth0-test
```

(fast) To launch the Junit test in local (postgresql service should be previously started):
```bash
mvn clean test -Dspring.profiles.active=dev
```

To generate and launch the docker image with the profile docker (stage test won't be executed):
```bash
docker compose up -d --build createuser
```

To launch the integration tests:
```bash
test-integration/test-em-all1.bash
```

![img.png](img.png)


For zsh users, here is the shortcut, stop all services, delete postgresql images/container, delete the volume, start postgresql:
```bash
dcstop; dcrm -f postgresql; docker volume rm createuserinauth0_postgres_data; dcupb postgresql;
```




