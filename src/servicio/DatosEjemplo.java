package servicio;

import modelo.Categoria;
import modelo.Estado;
import modelo.Prioridad;
import modelo.Ticket;

/**
 * Carga agentes y tickets de ejemplo para demostrar el sistema.
 */
public class DatosEjemplo {

    public static void cargar(MesaAyudaService servicio) {
        servicio.registrarAgente("Carlos Mendoza", Categoria.REDES);
        servicio.registrarAgente("Lucía Paredes", Categoria.SOFTWARE);
        servicio.registrarAgente("Jorge Quispe", Categoria.HARDWARE);
        servicio.registrarAgente("María Salazar", Categoria.SAP_Y_APLICACIONES);
        servicio.registrarAgente("Andrea Ríos", Categoria.ACCESOS_Y_CUENTAS);

        servicio.crearTicket("Caída de VPN en Refinería", "Los operadores no pueden conectarse por VPN a los sistemas de planta.",
                "Ricardo Flores", Categoria.REDES, Prioridad.CRITICA);
        servicio.crearTicket("Bloqueo de cuenta de dominio", "Usuario bloqueado tras varios intentos fallidos.",
                "Patricia Vega", Categoria.ACCESOS_Y_CUENTAS, Prioridad.MEDIA);
        servicio.crearTicket("Error SAP módulo MM", "No permite registrar entrada de mercancías.",
                "Luis Camacho", Categoria.SAP_Y_APLICACIONES, Prioridad.ALTA);
        servicio.crearTicket("Laptop no enciende", "Equipo de oficina San Isidro sin respuesta al botón de encendido.",
                "Sofía Delgado", Categoria.HARDWARE, Prioridad.MEDIA);
        servicio.crearTicket("Impresora de despacho sin conexión", "No imprime guías de remisión en el terminal.",
                "Miguel Torres", Categoria.HARDWARE, Prioridad.ALTA);
        servicio.crearTicket("Actualización de Office", "Solicitud de instalación de la versión corporativa.",
                "Elena Rojas", Categoria.SOFTWARE, Prioridad.BAJA);
        servicio.crearTicket("Sin acceso a carpeta compartida", "Permisos denegados en la carpeta de Logística.",
                "Diego Salinas", Categoria.ACCESOS_Y_CUENTAS, Prioridad.MEDIA);
        servicio.crearTicket("Caída de Wi-Fi en estación de servicio", "Sin red inalámbrica en el área de caja.",
                "Valeria Núñez", Categoria.REDES, Prioridad.ALTA);
        servicio.crearTicket("Alta de usuario SAP", "Nuevo analista requiere usuario y roles de Finanzas.",
                "Héctor Ponce", Categoria.SAP_Y_APLICACIONES, Prioridad.BAJA);
        servicio.crearTicket("Lentitud en sistema de facturación", "Tiempos de respuesta superiores a 30 segundos.",
                "Natalia Cruz", Categoria.SOFTWARE, Prioridad.CRITICA);

        // Algunos tickets ya en curso para que las capturas muestren distintos estados
        Ticket t1 = servicio.atenderSiguiente();
        servicio.atenderSiguiente();
        servicio.atenderSiguiente();
        servicio.cambiarEstado(t1.getId(), Estado.RESUELTO);
    }
}
