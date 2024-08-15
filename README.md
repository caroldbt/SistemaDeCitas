<h1>Sistema de Gestión de Citas</h1>

Este es un proyecto simple de sistema de gestión de citas desarrollado en Java utilizando JavaFX para la interfaz gráfica de usuario y SQLite como base de datos.

<h2>Características</h2>

- Crear, editar y eliminar citas.
- Visualizar la lista de citas.
- Gestión de especialidades médicas.
- Validación de entradas y manejo básico de errores.

<h2>Estructura del Proyecto</h2>

El proyecto está dividido en los siguientes archivos:

- **DatabaseHelper.java**: Maneja todas las operaciones relacionadas con la base de datos.
- **MainApp.java**: Contiene la lógica de la interfaz gráfica y la interacción con el usuario.
- **Citas.java**: Modelo de datos para representar las citas.

<h2>Requisitos</h2>

- Java 8 o superior.
- JavaFX.
- SQLite (El driver JDBC de SQLite está incluido en el proyecto).

<h2>Configuración del Proyecto</h2>

1. Clona el repositorio o descarga los archivos del proyecto.
2. Abre el proyecto en tu IDE favorito (IntelliJ, Eclipse, etc.).
3. Asegúrate de que los archivos `DatabaseHelper.java`, `MainApp.java`, y `Citas.java` están en el paquete `com.mirpoyecto.sistemadecitas`.
4. Ejecuta el proyecto.

<h2>Uso</h2>

1. **Agregar una cita**: Completa el formulario con el nombre del cliente, fecha, hora y especialidad, luego presiona el botón "Agregar cita".
2. **Editar una cita**: Selecciona la cita en la tabla, presiona "Editar", modifica los campos y guarda los cambios.
3. **Eliminar una cita**: Selecciona la cita en la tabla y presiona "Eliminar".
4. **Ver especialidades**: Presiona el botón "Ver Especialidades" para mostrar la lista de especialidades disponibles.

<h2>Autor</h2>

Este proyecto fue desarrollado por Carol Burgos.

<h2>Licencia</h2>

Este proyecto está licenciado bajo la Licencia MIT.
