# Sistema de Gestión para Clínica Odontológica — Arquitectura Monolítica

Proyecto desarrollado en **Java con Spring Boot** para administrar las operaciones principales de una clínica odontológica.

El sistema permite gestionar:

* Pacientes.
* Odontólogos.
* Recepcionistas.
* Turnos odontológicos.
* Domicilios de pacientes.
* Agenda y disponibilidad de los profesionales.

La aplicación permite registrar, consultar, actualizar y eliminar información mediante una **API REST**, manteniendo la relación entre cada turno, el paciente correspondiente y el odontólogo encargado de la atención.

## Arquitectura

Esta versión del proyecto fue desarrollada utilizando una **arquitectura monolítica en capas**, separando las responsabilidades principales del sistema:

* Controller.
* Service.
* Repository.
* Entity.
* DTO.
* Manejo global de excepciones.
* Validaciones.
* Logging.

## Tecnologías utilizadas

* Java 17.
* Spring Boot.
* Spring Data JPA.
* Hibernate.
* Maven.
* Base de datos H2.
* API REST.
* JUnit.
* Log4j.

## Funcionalidades principales

* CRUD de pacientes.
* CRUD de odontólogos.
* CRUD de recepcionistas.
* Registro y administración de turnos.
* Asociación de turnos con pacientes y odontólogos.
* Validación de datos ingresados.
* Manejo de errores y respuestas HTTP.
* Persistencia de información en base de datos.
* Consulta de la agenda odontológica.

Este repositorio contiene la **versión monolítica** del sistema. Posteriormente, el mismo proyecto será desarrollado utilizando una **arquitectura de microservicios**, separando pacientes, odontólogos, recepcionistas y turnos en servicios independientes.

El objetivo es demostrar la evolución de una aplicación desde una arquitectura monolítica hacia una solución distribuida, escalable y preparada para incorporar herramientas como API Gateway, Eureka Server, Config Server y comunicación entre microservicios.
