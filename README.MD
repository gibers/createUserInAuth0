

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

<h3>docker in local</h3>

To launch the Junit test in the docker container:
```bash
docker build --target test .
```

To generate and launch the docker image with the profile docker:
```bash
mvn package && docker compose up -d --build createuser
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




