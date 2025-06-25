
```java
Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
JwtAuthenticationToken jwtAuthenticationToken = (JwtAuthenticationToken) authentication;
String tokenValue = jwtAuthenticationToken.getToken().getTokenValue();
```
