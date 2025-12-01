3.2 HERRAMIENTAS EN EL BACKEND PARA LA IMPLEMENTACIÓN:

Las principales herramientas utilizadas en el Backend (Spring Boot con Java) son las siguientes:

Spring Boot
 Descripción: Un framework de código abierto basado en Java que simplifica la creación de aplicaciones Spring independientes y de grado de producción, con un mínimo de configuración.

Uso: Proporciona la base para la aplicación, incluyendo el servidor web embebido, la configuración automática y las capacidades de "starter" para integrar fácilmente otras librerías.

Spring Web (spring-boot-starter-web)
Descripción: Módulo de Spring Boot para construir aplicaciones web, incluyendo APIs RESTful.
Uso: Manejo de solicitudes HTTP, enrutamiento, creación de controladores REST, y gestión de la serialización/deserialización de JSON.

Spring Data JPA (spring-boot-starter-data-jpa)
Descripción: Facilita la implementación de repositorios basados en el paradigma de Spring Data, utilizando JPA (Java Persistence API) para el mapeo objeto-relacional y Hibernate como proveedor ORM.
Uso: Abstracción de la capa de acceso a datos, permitiendo interactuar con la base de datos MySQL a través de objetos Java (entidades) sin escribir SQL directamente.

Spring Security (spring-boot-starter-security)
Descripción: Un potente framework que proporciona servicios de autenticación y autorización para aplicaciones Java.

Uso: Implementación de la seguridad a nivel de aplicación, incluyendo el control de acceso a los endpoints, la gestión de usuarios y la protección contra vulnerabilidades comunes.

JSON Web Tokens (JWT) - jjwt-api, jjwt-impl, jjwt-jackson
Descripción: Un estándar abierto (RFC 7519) que define una forma compacta y autónoma de transmitir información de forma segura entre partes como un objeto JSON.
Uso: Autenticación y autorización basada en tokens. Una vez que un usuario inicia sesión, se le emite un JWT que se utiliza para verificar su identidad en solicitudes posteriores.

MySQL Connector/J (mysql-connector-j)
Descripción: El controlador JDBC oficial para MySQL.
Uso: Permite a la aplicación Java conectarse y comunicarse con la base de datos MySQL para realizar operaciones de almacenamiento y recuperación de datos.

Lombok (org.projectlombok:lombok)
Descripción: Una librería de Java que genera automáticamente constructores, getters, setters, métodos `equals`, `hashCode` y `toString`, entre otros, en tiempo de compilación.
Uso: Reduce el código repetitivo (`boilerplate code`) en las clases de modelo y DTO, haciendo el código más conciso y legible.

ModelMapper (org.modelmapper:modelmapper)
Descripción: Una biblioteca de mapeo de objetos que convierte automáticamente objetos de un tipo a otro.
Uso: Simplifica la conversión entre objetos de dominio (entidades) y objetos de transferencia de datos (DTOs), o viceversa, lo que es común en aplicaciones de capas.

Spring Boot Starter Validation (spring-boot-starter-validation)
Descripción: Módulo de Spring Boot que integra la API de Bean Validation (JSR 380) con Hibernate Validator como implementación.
Uso: Validación de los datos de entrada en los controladores y DTOs para asegurar la integridad y conformidad de los datos.

iText 7 Core (com.itextpdf:itext7-core)
Descripción: Una biblioteca de Java para la creación, manipulación y edición de documentos PDF.
Uso: Generación de documentos PDF dinámicos, como reportes o detalles de paquetes turísticos, a partir de los datos de la aplicación.

Spring Boot Starter Actuator (spring-boot-starter-actuator)
Descripción: Proporciona puntos finales (endpoints) listos para usar para monitorear e interactuar con la aplicación Spring Boot.
Uso: Ayuda a monitorear el estado de la aplicación, métricas, información del entorno y otros detalles operacionales en producción.

Spring Boot Starter Thymeleaf (spring-boot-starter-thymeleaf)
Descripción: Integración de Thymeleaf con Spring Boot para el renderizado de vistas en el lado del servidor.
Uso: Renderizado de las plantillas HTML (vistas) que son servidas al navegador, combinando datos del backend con la estructura HTML definida.