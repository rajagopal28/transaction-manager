# transaction-manager
A Small, light-weight Rest API based Java application without any framework, which does not require any server.

## Objectives
Following are the primary objectives this project is based on
1. The REST API should be built on an embedded server which means there should be no server or container that helps these API end points run at a particular application endpoint
2. TDD should be enforced at every stage of the implementation
3. The Application implementation should be simple and scalable as if it has real time traffic with multiple users accessing the system simultaneously
4. The Application is meant to be Light weight hence Heavy frameworks such as Spring could not considered while implementing
5. Application could only rely on in memory datastores

## The Approach
Based on the aforementioned objectives the system that is being built has been built based on the following approach:
- Using Jersey-Server library to create a JAX-RS reliant REST API server which can run as a self contained, embedded server that can be started and stopped from a simple Java application.
- Using Mockito and Powemocktio frameworks the entire implementation is developed following the TDD strategy where fail tests -> refactor -> pass tests cycle was strictly carried on.
![TDD Diagram](red-green-refactor.png)
- JAX-RS application are simple, fast, reliable at the same time scaled based on the underlying data access implementations
- SQLite datastore is used to store the data without involving any sever runtime or containers to actively engage with data connections.
- Hibernate-JPA library is used to leverage the advantages javax.persistence library which helps in efficient, simple and scaled database access approaches.


## Schema
![Schema Diagram](transaction-manager-schema.png)


## References
- https://dzone.com/articles/lightweight-embedded-java-rest-server-without-a-fr
- https://dzone.com/articles/how-i-test-my-java-classes-for-thread-safety
- JAXRS-H2: https://github.com/dprasanthv/JAX-RS-JPA-Hibernate-H2-In-Memory-Database
- JPA-SQlite: https://github.com/juniorware/sqlite-jpa/