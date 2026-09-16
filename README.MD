

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

### docker in local

(slow) To launch the Junit test in the docker container (this starts the postgresql service automatically):
```bash
docker compose --env-file for-dco-docker.env run --rm --build test && 
docker image rm createuserinauth0-test && 
docker ps |rg "createuserinauth0-.*" -i | awk  ' {print $NF }' | xargs -r docker stop
```

(fast) To launch the Junit test in local (postgresql service should be started beforehand):
```bash
mvn clean test -Dspring.profiles.active=dev
```

-----------------------------------------------------

To generate and launch the docker image with the profile docker (stage test won't be executed):
```bash
docker compose --env-file for-dco-docker.env up -d --build createuser
```

The Dockerfile builds the two local dependencies itself:

- `../restoCheckerDtos`
- `../feignCallsLib`

So the jars in `libs/` do not need to be regenerated manually before running Docker.

-----------------------------------------------------

These two commands are equivalent because they use the same Dockerfile and the same build context:

```bash
cd ..
docker build -f createUserInAuth0/Dockerfile --target builder --progress=plain -t createuserinauth0-builder-check .
```

```bash
docker build -f Dockerfile --target builder --progress=plain -t createuserinauth0-builder-check ..
```

In both cases:

- the Dockerfile is `Auth0CallToCreateUsers/createUserInAuth0/Dockerfile`;
- the build context is `Auth0CallToCreateUsers`;
- the `.dockerignore` used by Docker is `Auth0CallToCreateUsers/.dockerignore`.

-----------------------------------------------------

To launch the integration tests:
```bash
test-integration/test-em-all1.bash
```

![img.png](img.png)

-----------------------------------------------------

For zsh users, here is the shortcut, stop all services, delete postgresql images/container, delete the volume, start postgresql:
```bash
dcstop; dcrm -f postgresql; docker volume rm createuserinauth0_postgres_data; dcupb postgresql;
```

-----------------------------------------------------

```bash
cd /home/mordoc/DossierTravail/JavaProjets/Spring-Boot/Auth0CallToCreateUsers/createUserInAuth0
docker compose --env-file for-dco-docker.env ps
```
```bash
cd /home/mordoc/DossierTravail/JavaProjets/Spring-Boot/Auth0CallToCreateUsers/createUserInAuth0
docker compose --env-file for-dco-docker.env start postgresql
docker compose --env-file for-dco-docker.env start createuser
```
```bash
cd /home/mordoc/DossierTravail/JavaProjets/Spring-Boot/Auth0CallToCreateUsers/createUserInAuth0
docker compose --env-file for-dco-docker.env up -d --build createuser
docker compose --env-file for-dco-docker.env up -d createuser
docker compose --env-file for-dco-docker.env ps
```

- Pour voir les logs:
```bash
cd /home/mordoc/DossierTravail/JavaProjets/Spring-Boot/Auth0CallToCreateUsers/createUserInAuth0
docker compose --env-file for-dco-docker.env logs -f createuser
```




