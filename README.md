# Spring Security Registration & Email Verification

A Spring Boot application demonstrating a secure user registration and account verification workflow using Spring Security, Spring Data JPA, verification tokens, and application events.

## Features

* User registration
* Password encryption with Spring Security
* Persistent user accounts using Spring Data JPA
* Email-based account verification
* Verification tokens with expiration handling
* Event-driven registration completion workflow
* Verification status handling
* Secured application endpoints
* Separation of controller, service, repository, entity, and registration components

## Architecture

The application follows a layered architecture:

* **Controller** — Handles HTTP requests and responses
* **Service** — Contains application and business logic
* **Repository** — Handles database persistence through Spring Data JPA
* **Entity** — Represents persistent database objects
* **Model/DTO** — Represents data received from the client
* **Event** — Decouples registration from the verification email workflow
* **Registration** — Contains verification-related constants, messages, and statuses
* **Security Configuration** — Defines authentication and authorization rules

## Registration Flow

1. A user submits the registration request.
2. The application creates and persists the user.
3. A registration completion event is published.
4. The event listener generates a verification token.
5. A verification link is sent to the user.
6. The user opens the verification link.
7. The application validates the token.
8. The user's account is activated when the token is valid.
