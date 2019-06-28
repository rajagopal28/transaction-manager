# transaction-manager

* [Context](#context)
* [Objectives](#objectives)
* [Approach](#the-approach)
* [Schema](#schema)
* [API System](#api-system-flow)
* [API Flows](#api-flows)
* [TDD - Red->Green->Refactor cycle](#tdd---red->green->refactor-cycle:)
* [Setup](#setup-and-launch)
* [Libraries Used](#libraries-used)
* [Challenges](#challenges)
* [References](#references)

## Context
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
- Using Mockito and PowerMocktio frameworks the entire implementation is developed following the TDD strategy where fail tests -> refactor -> pass tests cycle was strictly carried on.
- JAX-RS application are simple, fast, reliable at the same time scaled based on the underlying data access implementations
- SQLite datastore is used to store the data without involving any sever runtime or containers to actively engage with data connections.
- Hibernate-JPA library is used to leverage the advantages javax.persistence library which helps in efficient, simple and scaled database access approaches.


## Schema
![Schema Diagram](transaction-manager-schema.png)

## API System flow
![Flow Diagram](api-system-flow.png)


## API Flows
- GET /api/alerts  - to list all active alerts
- POST /api/alerts {delay: .., description: .., reference_id: ..} - to post an alert with the given delay
- PUT /api/alert/<reference_id> - to revoke the alert with given reference_id


## TDD - Red->Green->Refactor cycle:
![TDD Diagram](red-green-refactor.png)

## Setup and launch

### installing the packages
With Tests: 
```bash
$ mvn clean install -U
```

### running tests
Unit tests: 
```bash
$ mvn  test
```
Integration tests: 
```bash
$ mvn integration-test
```

### running server
```bash
$ java  com.revolut.assesment.project.server.EmbeddedJettyServer
 ```

## Libraries Used
Following are the Libraries that are used as part of source and test cycles
![Dependency Diagram](dependency-libraries.png)


## Challenges

## References
- Jersey-Server based Embedded Server supporting JAX-RS: https://dzone.com/articles/lightweight-embedded-java-rest-server-without-a-fr
- Enabling CORS for the Above embedded server approach: https://crunchify.com/what-is-cross-origin-resource-sharing-cors-how-to-add-it-to-your-java-jersey-web-server/
- https://dzone.com/articles/how-i-test-my-java-classes-for-thread-safety
- JAXRS-H2: https://github.com/dprasanthv/JAX-RS-JPA-Hibernate-H2-In-Memory-Database
- JPA-SQlite: https://github.com/juniorware/sqlite-jpa/