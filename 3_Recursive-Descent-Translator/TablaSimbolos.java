import java.util.ArrayList;

public class TablaSimbolos {
    public TablaSimbolos padre;
    public ArrayList<Simbolo> simbolos;

    public TablaSimbolos(TablaSimbolos padre) {
        this.padre = padre;
        simbolos = new ArrayList<Simbolo>();
    }

    public boolean buscarAmbito(Simbolo s) {
        for (Simbolo ss:simbolos)
            if (ss.nombre.equalsIgnoreCase(s.nombre))
                return true;
        return false;
    }

    public boolean anyadir(Simbolo s) {
        if (buscarAmbito(s))
            return false;
        simbolos.add(s);
        return true;
    }

    public boolean buscarAmbito(String nombre) {
        for (Simbolo s:simbolos)
            if (s.nomtrad.equalsIgnoreCase(nombre))
                return false;

        return true;
    }

    Simbolo buscar(String nombre) {
        for (Simbolo s:simbolos)
            if (s.nombre.equalsIgnoreCase(nombre)) return s;

        if (padre != null)
            return padre.buscar(nombre);
        else
            return null;
    }
}