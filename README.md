Explicación:

ChessGame

Es la clase central del juego de ajedrez que maneja la lógica general del juego, como el turno de cada jugador y la verificación de condiciones de victoria o jaque mate. Coordina las interacciones entre las diferentes piezas y el tablero.
Board
Representa el tablero de ajedrez, incluyendo la configuración inicial y la posición actual de las piezas. Su responsabilidad es mostrar las piezas en las posiciones correctas y permitir la actualización de estas posiciones a medida que avanza el juego.
Piece
Clase base abstracta para todas las piezas de ajedrez. Define propiedades comunes a todas las piezas (como su color y posición) y métodos generales, dejando la implementación específica de movimientos a las subclases.
King
Clase que representa la pieza del Rey, uno de los elementos clave en el ajedrez. Implementa las reglas de movimiento específicas del Rey y maneja condiciones especiales como el jaque.
Queen
Clase que representa la pieza de la Reina. Esta clase define su rango de movimiento único que combina el de la Torre y el Alfil, permitiéndole moverse en múltiples direcciones sin restricciones de distancia.
Rook
Clase que representa la Torre. Define sus movimientos en línea recta a lo largo de filas y columnas, siendo una pieza fundamental para la defensa y ataques a larga distancia en el juego.
Bishop
Clase que representa al Alfil, una pieza que solo se mueve en diagonales. Esto hace que su valor estratégico dependa de la estructura del tablero y de las posiciones de otras piezas.
Knight
Clase que representa al Caballo, con movimientos únicos en forma de 'L'. Esta capacidad le permite saltar sobre otras piezas y lo convierte en un recurso táctico importante.
Pawn
Clase que representa al Peón, la única pieza con movimientos específicos de avance y captura en diagonal. También maneja reglas especiales como la promoción al llegar al final del tablero.
pakage'chessApplication'
ChessApp
Clase principal de la aplicación de ajedrez, que se encarga de inicializar y lanzar el juego. Configura la estructura y permite la interacción del usuario con la interfaz y las reglas del juego.
GameWindow
Maneja la interfaz gráfica de la ventana principal del juego, mostrando el tablero, las piezas y los controles básicos. Facilita la interacción entre el usuario y el juego.
Menu
Clase responsable del menú de opciones de la aplicación. Permite al usuario acceder a diferentes configuraciones y funciones adicionales del juego, como el reinicio o el cambio de nivel de dificultad.
Settings
Administra la configuración de la aplicación, permitiendo ajustes como la selección de temas de la interfaz y el nivel de dificultad del juego. Almacena las preferencias del usuario para futuras sesiones.
