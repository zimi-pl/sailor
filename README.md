# Sailor

Sailor is a library which should help to write code in clean architecture fashion.
Idea here is to create abstractions which developers use in their day to day work.
In their code developers should base on this abstractions rather than external libraries to achieve their goals.

## Getting started

Everything you need to do is to add this library and then use one of abstractions instead of real implementations.
Obviously code would be not usesable if there are only abstractions. So in sailor you will be able to use fake, in memory 
versions of those abstractions.

## Where the name Sailor came from?

Idea came from Clean Architecture. In this concept you have ports/adapters to external things.
This library should help you to easily go from one port to the other. That's why it felt good to name it Sailor.

## Why use Sailor?

The goal of this library is not to replace other frameworks. It's rather to help you isolate your domain
from implementations of your ports/adapters. Domain of your application should not know about DB, Message bus,
dependency injection, servers and clients (and any other tools) you're using.

Instead sailor is trying to define ways you're using your tools and provide API so that your domain does not know things about it.


## Tools

### Repository

The most common abstraction is repository.
In order to use sailor to prepare repository for you need prepare interface for this repository.
In the basic scenario it will be something like this:

```java
public interface StudentRepository extends Repository<Student> {

    Contract<Student> CONTRACT = Contract.repository(Student.class);
    
}
```

#### Why not to use Spring (or any other) simply?

In Spring you need to put all annotations in your Repository/Model.
Here idea is to let this code be separated from tools you're using. Also Sailor won't give you any tools for adding for example indices.
The reason is that it should be "technical detail". It does not change logic of an application.
It's definitely required so your application is fast and functional. But it's not not be important for logic of your application.

When logic in your application is ready there is no reason not to use Spring or any other framework behind the scene.

In meantime we are defining `Contract` which is general behavior/capabilities of database which is important from perspective of application logic.

When it's done you have an option to prepare in-memory repository of it. To do so you need run following code:

```java
Repository<Student> studentRepository = MemoryPort.port(StudentRepository.class);
```

See working example in `RepositoryFilterExample.java`

### Dependency injection

In tests generally we would like to avoid heavy initilizations. That's why the other abstraction was for dependency injection.
It's as easy as:
* create context
* register beans
* ask for creation of more complicated beans

```java
        Context context = Context.create();

        context.register(StudentRepository.class, MemoryPort.port(StudentRepository.class));

        StudentService studentService = context.getBean(StudentService.class);
```

See DependencyInjectionExample.java for more.

### Server

Client server communication is something required when we are doing web applications.
Unfortunately there is too much thinking about correct HTTP status and too less about data provided in a response.
Which causes in code we too often checking for status code.

Since HTTP status is not something interesting from clean architecture perspective here we try to isolate from that part.
In order to do that we need to create Service which will be kind of visible for external world.

When we have one of server implementation we can setup service:

```java
Server server = (...);
server.setupService(calendarService);
```

See ServerExample.java for more.

### Client

Once we setup service in some server we would like to get client easily to this.

```java
CalendarService calendarService = ServiceClientBuilder.client(CalendarService.class, server.baseUrl(), client);
```

This way we can easily call CalendarService from other microservice.

## Summary

This library does not need to have more feature than any other you saw in your life.
It's about creating abstraction and allow to test fairly complex application without having db, server, message bus or dependency injection framework.

Hopefully in future it will allow write tests in a way where you can decide if it should be (sociable) unit, integration or even end-to-end test. 