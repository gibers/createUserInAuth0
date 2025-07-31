package com.oidccall.createUserInAuth0.mock;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.util.function.BiFunction;

public class ConvertFromResourceToObj {

  public static <T> T getObjectFromResource(String pathToResource, Class<T> clazz) {
    return ConvertFromResourceToObj.<T>getObjectMapperFunction().apply(pathToResource, clazz);
  }

  private static <T> BiFunction<String, Class<T>, T> getObjectMapperFunction() {
    return (pathToResource, clazz) -> {
      ClassPathResource resource = new ClassPathResource(pathToResource);
      try {
        return getObjectMapper().readValue(resource.getFile(), clazz);
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    };
  }

  private static ObjectMapper getObjectMapper() {
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule());
    objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    return objectMapper;
  }

}
