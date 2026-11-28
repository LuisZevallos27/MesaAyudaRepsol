package vista;

import java.util.Scanner;

public class Consola {

    private final Scanner sc = new Scanner(System.in);

    public String leerTexto(String mensaje) {
        System.out.print(mensaje);
        return sc.nextLine().trim();
    }

    public int leerEntero(String mensaje) {
        while (true) {
            System.out.print(mensaje);
            String linea = sc.nextLine().trim();
            try {
                return Integer.parseInt(linea);
            } catch (NumberFormatException e) {
                System.out.println(">> Ingrese un numero entero valido.");
            }
        }
    }

    public <E extends Enum<E>> E elegirEnum(String titulo, E[] valores) {
        System.out.println(titulo);
        for (int i = 0; i < valores.length; i++) {
            System.out.println("  " + (i + 1) + ". " + valores[i]);
        }
        while (true) {
            int opcion = leerEntero("Elija una opcion: ");
            if (opcion >= 1 && opcion <= valores.length) {
                return valores[opcion - 1];
            }
            System.out.println(">> Opcion fuera de rango.");
        }
    }
}
