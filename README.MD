


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




