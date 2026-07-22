# Clínica Odontológica - API Backend

API REST desarrollada con Spring Boot para gestionar pacientes, odontólogos, recepcionistas y turnos de una clínica odontológica.

# Endpoints principales

## Pacientes

Ruta base: /api-paciente

### Crear paciente

POST http://localhost:8080/api-paciente 

  {
    "nombre": "Homero",
    "apellido": "Simpson",
    "cedula": "111112",
    "fechaIngreso": "2026-05-07",
    "email": "homersimpson@up.edu.ar",
    "domicilio": {
      "calle": "Av siempre viva",
      "localidad": "Springfield",
      "provincia": "Buenos Aires"
    }
  }

### Listar pacientes

GET http://localhost:8080/api-paciente

### Buscar paciente por ID

GET http://localhost:8080/api-paciente/1

### Buscar paciente por cédula

GET http://localhost:8080/api-paciente/buscar-cedula?cedula=111112

### Buscar paciente por email

GET http://localhost:8080/api-paciente/buscar-email/homersimpson@up.edu.ar

### Actualizar paciente

PUT http://localhost:8080/api-paciente/1 

  {
    "nombre": "Marge",
    "apellido": "Simpson",
    "cedula": "111112",
    "fechaIngreso": "2026-05-07",
    "email": "margesimpson@up.edu.ar",
    "domicilio": {
      "calle": "Calle nueva 123",
      "localidad": "Springfield",
      "provincia": "Buenos Aires"
    }
  }


### Eliminar paciente

DELETE http://localhost:8080/api-paciente/1


## Odontólogos

Ruta base: /api-odontologo

### Crear odontólogo

POST http://localhost:8080/api-odontologo 
  
  {
    "nombre": "Juan",
    "apellido": "Perez",
    "matricula": "MAT123",
    "telefono": "1122334455",
    "email": "juanperez@up.edu.ar",
    "especialidad": "Ortodoncia"
  }

### Listar odontólogos

GET http://localhost:8080/api-odontologo

### Buscar odontólogo por ID

GET http://localhost:8080/api-odontologo/1

### Buscar odontólogo por matrícula

GET "http://localhost:8080/api-odontologo/buscar?matricula=MAT123"

### Actualizar odontólogo

PUT http://localhost:8080/api-odontologo/1 
  
  {
    "nombre": "Juan",
    "apellido": "Perez",
    "matricula": "MAT123",
    "telefono": "1199998888",
    "email": "juanactualizado@up.edu.ar",
    "especialidad": "Implantología"
  }


### Eliminar odontólogo

DELETE http://localhost:8080/api-odontologo/1


## Recepcionistas

Ruta base: /api-recepcionista

### Crear recepcionista

POST http://localhost:8080/api-recepcionista 
  
  {
    "nombre": "Marcos",
    "apellido": "Rodriguez",
    "email": "marcosrodriguez@up.edu.ar",
    "telefono": "9988774466",
    "usuario": "marcosrodriguez"
  }

### Listar recepcionistas

GET http://localhost:8080/api-recepcionista

### Buscar recepcionista por ID

GET http://localhost:8080/api-recepcionista/1

### Actualizar recepcionista

PUT http://localhost:8080/api-recepcionista/1 
  
  {
    "nombre": "Marcos",
    "apellido": "Rodriguez",
    "email": "marcosactualizado@up.edu.ar",
    "telefono": "1111222233",
    "usuario": "marcosactualizado"
  }

### Eliminar recepcionista

DELETE http://localhost:8080/api-recepcionista/1


## Turnos

Ruta base: /api-turno

Antes de crear un turno deben existir:
Un paciente
Un odontólogo
Un recepcionista

### Crear turno

POST http://localhost:8080/api-turno 
  
  {
    "pacienteId": 1,
    "odontologoId": 1,
    "recepcionistaId": 1,
    "fechaTurno": "2026-05-10T15:30:00",
    "estado": "PROGRAMADO",
    "observaciones": "Primera consulta"
  }

### Listar turnos

GET http://localhost:8080/api-turno

### Buscar turno por ID

GET http://localhost:8080/api-turno/1

### Buscar turnos por paciente

GET http://localhost:8080/api-turno/paciente/1

### Buscar turnos por odontólogo

GET http://localhost:8080/api-turno/odontologo/1

### Buscar turnos por recepcionista

GET http://localhost:8080/api-turno/recepcionista/1

### Actualizar turno

PUT http://localhost:8080/api-turno/1 
  
  {
    "pacienteId": 1,
    "odontologoId": 1,
    "recepcionistaId": 1,
    "fechaTurno": "2026-05-10T16:30:00",
    "estado": "COMPLETADO",
    "observaciones": "Consulta actualizada"
  }

### Cancelar turno

PUT "http://localhost:8080/api-turno/cancelar/1?recepcionistaId=1"

### Eliminar turno

DELETE "http://localhost:8080/api-turno/1?recepcionistaId=1"



# Estados válidos para Turno

Los estados aceptados son:
PROGRAMADO
CANCELADO
COMPLETADO
