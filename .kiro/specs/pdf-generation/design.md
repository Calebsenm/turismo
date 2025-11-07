# Documento de Diseño - Generación de PDF de Paquetes Turísticos

## Visión General

La funcionalidad de generación de PDF se implementará como una extensión del sistema existente de gestión de paquetes turísticos. Se integrará con la arquitectura Spring Boot existente, agregando un nuevo endpoint REST y un servicio especializado para la generación de documentos PDF usando la librería iText.

## Arquitectura

### Componentes Principales

1. **PdfController**: Nuevo controlador REST que manejará las solicitudes de generación de PDF
2. **PdfService**: Servicio que orquestará la generación del documento PDF
3. **PdfGenerator**: Componente utilitario que manejará la creación del documento PDF usando iText
4. **PdfTemplate**: Clase que definirá la estructura y formato del documento

### Flujo de Datos

```
Usuario → Frontend → PdfController → PdfService → PdfGenerator → iText → PDF Response
                                        ↓
                                   PaqueteService (datos)
```

## Componentes e Interfaces

### 1. PdfController

**Responsabilidad**: Manejar las solicitudes HTTP para generación de PDF

**Endpoints**:
- `GET /api/paquetes/{id}/pdf` - Generar y descargar PDF del paquete

**Métodos**:
```java
@GetMapping("/{id}/pdf")
public ResponseEntity<byte[]> generarPdfPaquete(
    @PathVariable Long id,
    @RequestHeader("Authorization") String authHeader
)
```

### 2. PdfService

**Responsabilidad**: Lógica de negocio para la generación de PDF

**Métodos principales**:
```java
public byte[] generarPdfPaquete(Long paqueteId, String userEmail)
public void validarAccesoPaquete(Long paqueteId, String userEmail)
public PaqueteCompleto obtenerDatosCompletos(Long paqueteId)
```

### 3. PdfGenerator

**Responsabilidad**: Generación técnica del documento PDF

**Métodos principales**:
```java
public byte[] crearDocumentoPdf(PaqueteCompleto paquete)
private void agregarEncabezado(Document document)
private void agregarInformacionPaquete(Document document, PaqueteEntity paquete)
private void agregarDetallesDestino(Document document, DestinoEntity destino)
private void agregarHoteles(Document document, Set<HotelEntity> hoteles)
private void agregarTransportes(Document document, Set<TransporteEntity> transportes)
private void agregarActividades(Document document, Set<ActividadEntity> actividades)
private void agregarPiePagina(Document document)
```

### 4. PaqueteCompleto (DTO)

**Responsabilidad**: Encapsular todos los datos necesarios para el PDF

```java
public class PaqueteCompleto {
    private PaqueteEntity paquete;
    private UsuarioEntity usuario;
    private DestinoEntity destino;
    private Set<HotelEntity> hoteles;
    private Set<TransporteEntity> transportes;
    private Set<ActividadEntity> actividades;
}
```

## Modelos de Datos

### Datos Incluidos en el PDF

**Información del Paquete**:
- Nombre del paquete
- Descripción
- Fechas de inicio y fin
- Costo total
- Origen
- Tipo de paquete
- Número de adultos y niños

**Información del Destino**:
- Nombre del destino
- Descripción
- Ubicación

**Hoteles**:
- Nombre del hotel
- Tarifa por adulto
- Tarifa por niño

**Transportes**:
- Tipo de transporte
- Empresa
- Precio

**Actividades**:
- Nombre de la actividad
- Descripción
- Precio

**Información del Usuario**:
- Nombre completo
- Email (opcional)

## Manejo de Errores

### Tipos de Errores

1. **Paquete no encontrado** (404)
   - Mensaje: "Paquete no encontrado"
   - Acción: Retornar ResponseEntity.notFound()

2. **Acceso no autorizado** (403)
   - Mensaje: "No tiene permisos para acceder a este paquete"
   - Acción: Retornar ResponseEntity.status(FORBIDDEN)

3. **Error en generación de PDF** (500)
   - Mensaje: "Error interno al generar el PDF"
   - Acción: Log del error y ResponseEntity.status(INTERNAL_SERVER_ERROR)

4. **Token inválido** (401)
   - Mensaje: "Token de autenticación inválido"
   - Acción: Retornar ResponseEntity.status(UNAUTHORIZED)

### Estrategia de Logging

```java
// Logs de información
log.info("Generando PDF para paquete ID: {} por usuario: {}", paqueteId, userEmail);

// Logs de error
log.error("Error al generar PDF para paquete {}: {}", paqueteId, e.getMessage(), e);
```

## Estrategia de Testing

### Pruebas Unitarias

1. **PdfControllerTest**
   - Validar respuestas HTTP correctas
   - Verificar manejo de errores
   - Comprobar headers de respuesta

2. **PdfServiceTest**
   - Validar lógica de negocio
   - Verificar validaciones de acceso
   - Comprobar obtención de datos completos

3. **PdfGeneratorTest**
   - Verificar generación correcta del PDF
   - Validar formato y contenido
   - Comprobar manejo de datos faltantes

### Pruebas de Integración

1. **PdfIntegrationTest**
   - Flujo completo de generación de PDF
   - Integración con base de datos
   - Validación de autenticación JWT

## Consideraciones Técnicas

### Dependencias Requeridas

```xml
<dependency>
    <groupId>com.itextpdf</groupId>
    <artifactId>itextkernel</artifactId>
    <version>7.2.5</version>
</dependency>
<dependency>
    <groupId>com.itextpdf</groupId>
    <artifactId>itextlayout</artifactId>
    <version>7.2.5</version>
</dependency>
```

### Configuración

- **Tamaño máximo de archivo**: 10MB
- **Timeout de generación**: 30 segundos
- **Formato de papel**: A4
- **Encoding**: UTF-8 para soporte de caracteres especiales

### Seguridad

1. **Autenticación**: Validación de JWT token
2. **Autorización**: Solo el propietario del paquete puede generar el PDF
3. **Validación de entrada**: Validar ID del paquete
4. **Rate limiting**: Máximo 10 PDFs por usuario por minuto

### Rendimiento

1. **Caching**: No se implementará cache inicial (los paquetes pueden cambiar)
2. **Lazy loading**: Usar fetch EAGER solo para datos necesarios en el PDF
3. **Optimización de consultas**: Una sola consulta para obtener todos los datos relacionados
4. **Gestión de memoria**: Generar PDF en memoria y liberar recursos inmediatamente

### Formato del PDF

1. **Encabezado**: Logo, título "Paquete Turístico", fecha de generación
2. **Secciones**: Información del paquete, destino, servicios incluidos
3. **Pie de página**: Información de contacto, número de página
4. **Fuentes**: Arial para títulos, Times New Roman para contenido
5. **Colores**: Azul para títulos (#2E86AB), negro para contenido