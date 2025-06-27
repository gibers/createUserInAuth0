
com.oidccall.createUserInAuth0.implementation.UserImplementation.createUserInAuth0
1. replace the empty fields with null
2. créate the user in oauth0
3. map the result from oauth0 to the Entity Users. Here we map some fields from FrontUserToCreateDto to Users, because those fields are not present in oauth0.
    Ex: userFromFront.gender()
4. save the entity in the DB

------


