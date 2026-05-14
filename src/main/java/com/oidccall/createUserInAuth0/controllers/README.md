
```java
Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
JwtAuthenticationToken jwtAuthenticationToken = (JwtAuthenticationToken) authentication;
String tokenValue = jwtAuthenticationToken.getToken().getTokenValue();
```

1) createUser()
com.oidccall.createUserInAuth0.controllers.UserController.createUser
Aucun token n'est nécessaire.
Cette méthode n'est plus appelée. Elle était créée à l'époque oú la création de l'utilisateur était réalisée par le composant front-end: login-form.component.ts


2) getListUsersWithEmailNeverVerified()
com.oidccall.createUserInAuth0.controllers.UserController.getListUsersWithEmailNeverVerified
Il faut le token FUNCTIONAL_API_CLIENT pour appeler cette méthode.
Cette méthode est appelé par le batch, position B_.
Cette méthode retourne une liste d'utilisateurs dont l'email n'a jamais été vérifié, et dont sa création s'est faite avant la date passée en 
   paramêtre.

3) getUser()
com.oidccall.createUserInAuth0.controllers.UserController#getUser
Il faut le token de l'utilisateur passé en paramêtre pour appeler cette méthode.
Méthode appelée par le font-end, par le resolver: userResolver, juste avant de charger le composant ProfileComponent.
La Méthode appelle la méthode de la feignCallsLib: com.oidccall.feigncallslib.feignCalls.ApiV2UsersRequestLib.getUserApiV2Users
qui appelle auth0: https://auth0.com/docs/api/management/v2/users/get-users-by-id
pour récupérer les informations de l'utilisateur.

-> Est il nécessaire d'avoir une telle méthode, sachant que les informations de l'utilisateur sont déjà récupérées lors de la connexion de l'utilisateur?
Oui, car les informations du userResolver ne sont pas complètes.

