package servicio;

import modelo.Categoria;
import modelo.Estado;
import modelo.Prioridad;
import modelo.Ticket;

public class DatosEjemplo {

    public static void cargar(MesaAyudaService servicio) {
        servicio.registrarAgente("Carlos Mendoza", Categoria.REDES);
        servicio.registrarAgente("Lucia Paredes", Categoria.SOFTWARE);
        servicio.registrarAgente("Jorge Quispe", Categoria.HARDWARE);
        servicio.registrarAgente("Maria Salazar", Categoria.SAP_Y_APLICACIONES);
        servicio.registrarAgente("Andrea Rios", Categoria.ACCESOS_Y_CUENTAS);

        servicio.crearTicket("Caida de VPN en Refineria", "Los operadores no pueden conectarse por VPN a los sistemas de planta.",
                "Ricardo Flores", Categoria.REDES, Prioridad.CRITICA);
        servicio.crearTicket("Bloqueo de cuenta de dominio", "Usuario bloqueado tras varios intentos fallidos.",
                "Patricia Vega", Categoria.ACCESOS_Y_CUENTAS, Prioridad.MEDIA);
        servicio.crearTicket("Error SAP modulo MM", "No permite registrar entrada de mercancias.",
                "Luis Camacho", Categoria.SAP_Y_APLICACIONES, Prioridad.ALTA);
        servicio.crearTicket("Laptop no enciende", "Equipo de oficina San Isidro sin respuesta al boton de encendido.",
                "Sofia Delgado", Categoria.HARDWARE, Prioridad.MEDIA);
        servicio.crearTicket("Impresora de despacho sin conexion", "No imprime guias de remision en el terminal.",
                "Miguel Torres", Categoria.HARDWARE, Prioridad.ALTA);
        servicio.crearTicket("Actualizacion de Office", "Solicitud de instalacion de la version corporativa.",
                "Elena Rojas", Categoria.SOFTWARE, Prioridad.BAJA);
        servicio.crearTicket("Sin acceso a carpeta compartida", "Permisos denegados en la carpeta de Logistica.",
                "Diego Salinas", Categoria.ACCESOS_Y_CUENTAS, Prioridad.MEDIA);
        servicio.crearTicket("Caida de Wi-Fi en estacion de servicio", "Sin red inalambrica en el area de caja.",
                "Valeria Nunez", Categoria.REDES, Prioridad.ALTA);
        servicio.crearTicket("Alta de usuario SAP", "Nuevo analista requiere usuario y roles de Finanzas.",
                "Hector Ponce", Categoria.SAP_Y_APLICACIONES, Prioridad.BAJA);
        servicio.crearTicket("Lentitud en sistema de facturacion", "Tiempos de respuesta superiores a 30 segundos.",
                "Natalia Cruz", Categoria.SOFTWARE, Prioridad.CRITICA);

        Ticket t1 = servicio.atenderSiguiente();
        servicio.atenderSiguiente();
        servicio.atenderSiguiente();
        servicio.cambiarEstado(t1.getId(), Estado.RESUELTO);
    }
}
