package com.oidccall.createUserInAuth0.entities;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.AvailableSettings;
import org.junit.jupiter.api.Test;

class HibernateMetadataMappingTest {

  @Test
  void testHibernateMetadataBuildingWithAllEntities() {
    StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
        .applySetting(AvailableSettings.JAKARTA_JDBC_DRIVER, "org.h2.Driver")
        .applySetting(AvailableSettings.JAKARTA_JDBC_URL, "jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;MODE=PostgreSQL")
        .applySetting(AvailableSettings.DIALECT, "org.hibernate.dialect.PostgreSQLDialect")
        .build();

    try {
      MetadataSources metadataSources = new MetadataSources(registry);
      metadataSources.addAnnotatedClass(Restaurateur.class);
      metadataSources.addAnnotatedClass(Template.class);
      metadataSources.addAnnotatedClass(SeatingCapacity.class);
      metadataSources.addAnnotatedClass(ServiceCapacity.class);
      metadataSources.addAnnotatedClass(LunchServiceCapacity.class);
      metadataSources.addAnnotatedClass(DinnerServiceCapacity.class);
      metadataSources.addAnnotatedClass(Client.class);
      metadataSources.addAnnotatedClass(Reservation.class);
      metadataSources.addAnnotatedClass(Users.class);
      metadataSources.addAnnotatedClass(UsersDeleted.class);
      metadataSources.addAnnotatedClass(BatchToUser.class);

      Metadata metadata = metadataSources.buildMetadata();
      assertNotNull(metadata);
    } finally {
      StandardServiceRegistryBuilder.destroy(registry);
    }
  }
}
