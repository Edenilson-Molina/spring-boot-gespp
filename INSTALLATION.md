# Guía rápida de instalación


## Resumen
- Instalar JDK 21 y Maven en Windows
- Registrar variables de entorno: `JAVA_HOME`, `MAVEN_HOME`, y añadir `PATH`.
- Instalar y ejecutar Oracle XE en Docker.
- Crear el usuario `app_user` en Oracle y otorgar permisos.
- Construir y ejecutar la aplicación con `mvn`.

## Requisitos previos
- Docker Desktop con sesión iniciada (necesitarás credenciales Oracle para el pull de la imagen oficial).

## 1) Instalar JDK 21
Verificar:
```powershell
java --version
# Debe mostrar Java 21
```

## 2) Instalar Apache Maven
Verificar:

```powershell
mvn --version
```

## 3) Registrar variables de entorno en Windows
Ejemplos (ajusta rutas a la instalación real):

```powershell
JAVA_HOME "C:\Program Files\Java\jdk-21"
MAVEN_HOME "C:\maven\apache-maven-3.9.11"
path "
%JAVA_HOME%\bin;
%MAVEN_HOME%\bin"
```

## 4) Instalar/ejecutar Oracle XE en Docker
La imagen oficial de Oracle está en el Container Registry de Oracle y requiere autenticación y aceptación de licencias.

1) Inicia sesión en el registro de Oracle (te pedirá credenciales Oracle Cloud):

```powershell
docker login container-registry.oracle.com
```

2) Descarga la imagen (puede cambiar el nombre/tag según la versión disponible):

```powershell
docker pull container-registry.oracle.com/database/express
```

3) Ejecuta el contenedor (ejemplo mínimo):

```powershell
docker run -d `
  --name oracle-xe `
  -p 1521:1521 -p 5500:5500 `
  -e ORACLE_PWD=MyStrongPassword123 `
  container-registry.oracle.com/database/express
```

## 5) Crear el usuario de la aplicación en Oracle
Una vez que la DB esté lista, crea `app_user` y dale permisos. Hay varias formas; aquí un ejemplo usando `docker exec` y `sqlplus` dentro del contenedor:

```powershell
# Entrar en el contenedor
docker exec -it oracle-xe sqlplus system/MyStrongPassword123@localhost:1521/XEPDB1

# Dentro del contenedor
# En el prompt de SQL*Plus ejecuta:
CREATE USER app_user IDENTIFIED BY app_pass123;
GRANT CONNECT, RESOURCE TO app_user;
ALTER USER app_user QUOTA UNLIMITED ON USERS;

# Salir de sqlplus y del contenedor
EXIT
exit
```

## 6) Configurar la aplicación (ya presente en este proyecto)
El archivo `src/main/resources/application.properties` en este proyecto ya contiene:

```properties
spring.datasource.url=jdbc:oracle:thin:@//localhost:1521/XEPDB1
spring.datasource.username=app_user
spring.datasource.password=app_pass123

spring.jpa.hibernate.ddl-auto=create-drop
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.OracleDialect
```

Ajusta las credenciales si usaste otras.

## 7) Construir y ejecutar la aplicación
Con Maven global:

```powershell
npm run build
mvn clean instal
mvn spring-boot:run
```
