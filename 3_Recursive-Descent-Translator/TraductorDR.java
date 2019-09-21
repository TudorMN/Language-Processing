import java.util.AbstractMap.*;

public class TraductorDR {
    class Pair{
        public String th;
        public int tipo;

        public Pair(String a, int b){
            th = a;
            tipo = b;
        }

        public int getValue(){ return tipo; }
        public String getKey(){ return th; }
    }

    public Token token;
    public AnalizadorLexico lexico;
    public StringBuilder reglas;
    private boolean flag;
    private final int ERRYADECL=1,ERRNOSIMPLE=2,ERRNODECL=3,ERRTIPOS=4,ERRNOENTEROIZQ=5,ERRNOENTERODER=6,ERRRANGO=7;

    private void errorSemantico(int nerror,Token tok) {
        System.err.print("Error semantico ("+tok.fila+","+tok.columna+"): en '"+tok.lexema+"', ");
        switch (nerror) {
            case ERRYADECL: System.err.println("ya existe en este ambito");
                break;
            case ERRNOSIMPLE: System.err.println("debe ser de tipo entero o real");
                break;
            case ERRNODECL: System.err.println("no ha sido declarado");
                break;
            case ERRTIPOS: System.err.println("tipos incompatibles entero/real");
                break;
            case ERRNOENTEROIZQ: System.err.println("el operando izquierdo debe ser entero");
                break;
            case ERRNOENTERODER: System.err.println("el operando derecho debe ser entero");
                break;
            case ERRRANGO: System.err.println("rango incorrecto");
                break;
        }
        System.exit(-1);
    }

    private void appendRule(int rule){
        if (flag)
            reglas.append(" " + rule);
    }

    public TraductorDR(AnalizadorLexico al){
        lexico = al;
        token = new Token();
        reglas = new StringBuilder();
        flag = true;
    }

    public void comprobarFinFichero(){
        if (flag && token.tipo==Token.EOF){}
            //System.out.println(reglas.toString());
        else
            if(token.tipo != Token.EOF)
                errorSintaxis(Token.EOF);
    }

    public final void emparejar(int tokEsperado){
        if (token.tipo == tokEsperado)
            token = lexico.siguienteToken();
        else
            errorSintaxis(tokEsperado);
    }

    public void errorSintaxis(int... expected){
        String exp_str = "";

        for (int i : expected){
            exp_str += " " + token.nombreToken.get(i);
        }

        if (token.tipo != token.EOF){
            System.err.println("Error sintactico (" + lexico.getRow() + "," + lexico.getCol() + "): encontrado '" + token.lexema + "', esperaba" + exp_str);
        }
        else{
            System.err.println("Error sintactico: encontrado fin de fichero, esperaba" + exp_str);
        }

        System.exit(-1);
    }

    public final String S(){
        token = lexico.siguienteToken();
        reglas.append(1);
        String trad = "", underl = "";

        if (token.tipo == Token.PROGRAM) {
            emparejar(Token.PROGRAM);
            emparejar(Token.ID);
            emparejar(Token.PYC);
            TablaSimbolos symb_table = new TablaSimbolos(null);
            trad += "int main()" + B(underl, symb_table);
        }
        else
            errorSintaxis(Token.PROGRAM);

        return trad;
    }

    public final String D(String underl, TablaSimbolos symb_table){
        appendRule(2);
        String trad = "";

        if (token.tipo == Token.VAR) {
            emparejar(Token.VAR);
            trad += L(underl, symb_table);
            emparejar(Token.ENDVAR);
        }
        else
            errorSintaxis(Token.VAR);

        return trad;
    }

    public final String L(String underl, TablaSimbolos symb_table){
        appendRule(3);
        String trad = "";

        if (token.tipo == Token.ID) {
            trad += V(underl, symb_table);
            trad += Lp(underl, symb_table);
        }
        else
            errorSintaxis(Token.ID);

        return trad;
    }

    public final String Lp(String underl, TablaSimbolos symb_table){
        String trad = "";

        if (token.tipo == Token.ID) {
            appendRule(4);
            trad += V(underl, symb_table);
            trad += Lp(underl, symb_table);
        }
        else{
            if (token.tipo == Token.ENDVAR) {
                appendRule(5);
            }
            else
                errorSintaxis(Token.ID, Token.ENDVAR);
        }

        return trad;
    }

    public final String V(String underl, TablaSimbolos symb_table){
        appendRule(6);
        String trad = "";

        if (token.tipo == Token.ID) {
            String id = token.lexema;
            if (!symb_table.buscarAmbito(underl + id))
                errorSemantico(ERRYADECL, token);
            emparejar(Token.ID);
            emparejar(Token.DOSP);
            String arr = "";
            trad += C(underl, id, arr, symb_table);
            emparejar(Token.PYC);
        }
        else
            errorSintaxis(Token.ID);

        return trad;
    }

    public final String C(String underl, String id, String arr, TablaSimbolos symb_table) {
        String trad = "";

        if (token.tipo == Token.ARRAY) {
            appendRule(7);
            arr += A(symb_table);
            trad += C(underl, id, arr, symb_table);
        }
        else {
            if (token.tipo == Token.POINTER || token.tipo == Token.INTEGER || token.tipo == Token.REAL) {
                appendRule(8);
                int tipo = -1;
                String p = "";
                p = P(symb_table);
                String nomtrad = "";
                nomtrad = underl + id;
                trad += p + " " + nomtrad + arr + ";\n";

                if (p.equals("int"))
                    tipo = Simbolo.ENTERO;
                if (p.equals("float"))
                    tipo = Simbolo.REAL;
                if (arr.length() > 0)
                    tipo = Simbolo.ARRAY;
                if (p.contains("*"))
                    tipo = Simbolo.PUNTERO;

                Simbolo symbol = new Simbolo(id, tipo, nomtrad);
                symb_table.anyadir(symbol);
            } else
                errorSintaxis(Token.ARRAY, Token.POINTER, Token.INTEGER, Token.REAL);
        }

        return trad;
    }

    public final String A(TablaSimbolos symb_table){
        appendRule(9);
        String trad = "";

        if (token.tipo == Token.ARRAY) {
            emparejar(Token.ARRAY);
            emparejar(Token.CORI);
            trad = R(symb_table);
            emparejar(Token.CORD);
            emparejar(Token.OF);
        }
        else
            errorSintaxis(Token.ARRAY);

        return trad;
    }

    public final String R(TablaSimbolos symb_table){
        appendRule(10);
        String trad = "";

        if (token.tipo == Token.NUMENTERO) {
            trad += G(symb_table);
            trad += Rp(symb_table);
        }
        else
            errorSintaxis(Token.NUMENTERO);

        return trad;
    }

    public final String Rp(TablaSimbolos symb_table){
        String trad = "";

        if (token.tipo == Token.COMA) {
            appendRule(11);
            emparejar(Token.COMA);
            trad += G(symb_table);
            trad += Rp(symb_table);
        }
        else{
            if (token.tipo == Token.CORD) {
                appendRule(12);
            }
            else
                errorSintaxis(Token.CORD, Token.COMA);
        }

        return trad;
    }

    public final String G(TablaSimbolos symb_table){
        appendRule(13);
        String trad = "";

        if (token.tipo == Token.NUMENTERO) {
            int a = Integer.parseInt(token.lexema);
            emparejar(Token.NUMENTERO);
            emparejar(Token.PTOPTO);
            int b = Integer.parseInt(token.lexema);
            Token segnum = token;
            emparejar(Token.NUMENTERO);

            if ((b >= a) && (b >= 0) && (a >= 0)){
                String num = Integer.toString(b - a + 1);
                trad += "["+ num + "]";
            }
            else
                errorSemantico(ERRRANGO, segnum);
        }
        else
            errorSintaxis(Token.NUMENTERO);

        return trad;
    }

    public final String P(TablaSimbolos symb_table){
        String trad = "";

        if (token.tipo == Token.POINTER) {
            appendRule(14);
            emparejar(Token.POINTER);
            emparejar(Token.OF);
            trad += P(symb_table) + "*";
        }
        else{
            if (token.tipo == Token.INTEGER || token.tipo == Token.REAL){
                appendRule(15);
                trad += Tipo(symb_table);
            }
            else
                errorSintaxis(Token.POINTER, Token.INTEGER, Token.REAL);
        }

        return trad;
    }

    public final String Tipo(TablaSimbolos symb_table){
        String trad = "";

        if (token.tipo == Token.INTEGER) {
            appendRule(16);
            trad = "int";
            emparejar(Token.INTEGER);
        }
        else{
            if (token.tipo == Token.REAL){
                appendRule(17);
                trad = "float";
                emparejar(Token.REAL);
            }
            else
                errorSintaxis(Token.INTEGER, Token.REAL);
        }

        return trad;
    }

    public final String B(String underl, TablaSimbolos symb_table){
        appendRule(18);
        String trad = "";

        if (token.tipo == Token.BEGIN) {
            emparejar(Token.BEGIN);
            trad = "\n{\n" + D(underl, symb_table);
            trad += SI(underl, symb_table) + "\n}";
            emparejar(Token.END);
        }
        else
            errorSintaxis(Token.BEGIN);

        return trad;
    }

    public final String SI(String underl, TablaSimbolos symb_table){
        appendRule(19);
        String trad = "";

        if (token.tipo == Token.ID || token.tipo == Token.WRITE || token.tipo == Token.BEGIN) {
            trad = I(underl, symb_table);
            trad += M(underl, symb_table);
        }
        else
            errorSintaxis(Token.ID, Token.BEGIN, Token.WRITE);

        return trad;
    }

    public final String M(String underl, TablaSimbolos symb_table){
        String trad = "";

        if (token.tipo == Token.PYC) {
            appendRule(20);
            emparejar(Token.PYC);
            trad = I(underl, symb_table);
            trad += M(underl, symb_table);
        }
        else{
            if (token.tipo == Token.END){
                appendRule(21);
            }
            else
                errorSintaxis(Token.PYC, Token.END);
        }

        return trad;
    }

    public final String I(String underl, TablaSimbolos symb_table){
        String trad = "";

        if (token.tipo == Token.ID) {
            appendRule(22);
            Token t = token;
            emparejar(Token.ID);
            Token asig = token;
            emparejar(Token.ASIG);
            Pair e = E(symb_table);

            Simbolo s = symb_table.buscar(t.lexema);

            if (s != null) {
                if (s.tipo == e.getValue())
                    trad = s.nomtrad + " = " + e.getKey() + ";\n";
                if ((s.tipo == 2) && (e.getValue() == 1))
                    trad = s.nomtrad + " = itor(" + e.getKey() + ");\n";
                if ((s.tipo == 1) && (e.getValue() == 2))
                    errorSemantico(ERRTIPOS, asig);
            }
            else{ trad = "error!"; }
        }
        else{
            if (token.tipo == Token.WRITE){
                appendRule(23);
                emparejar(Token.WRITE);
                emparejar(Token.PARI);
                Pair e = E(symb_table);
                emparejar(Token.PARD);

                String t = "";
                if (e.getValue() == 1)
                    t = "d";
                else
                    t = "f";

                trad = "printf(\"%" + t + "\", " + e.getKey() + ");\n";
            }
            else{
                if (token.tipo == Token.BEGIN) {
                    appendRule(24);
                    underl+="_";
                    TablaSimbolos new_symb_table = new TablaSimbolos(symb_table);
                    trad += B(underl, new_symb_table);
                }
                else
                    errorSintaxis(Token.ID, Token.BEGIN, Token.WRITE);
            }
        }

        return trad;
    }

    public final Pair E(TablaSimbolos symb_table){
        appendRule(25);
        String trad = "";
        Pair r = new Pair("", 0);

        if (token.tipo == Token.NUMENTERO || token.tipo == Token.NUMREAL || token.tipo == Token.ID) {
            Pair t = T(symb_table, 0, "");
            Pair ep = Ep(symb_table, t.getValue(), t.getKey());
            r = new Pair(ep.getKey(), ep.getValue());
        }
        else
            errorSintaxis(Token.ID, Token.NUMENTERO, Token.NUMREAL);

        return r;
    }

    public final Pair Ep(TablaSimbolos symb_table, int tipo, String th){
        String trad = "";
        Pair r = new Pair(th, tipo);

        if (token.tipo == Token.OPAS) {
            appendRule(26);
            String opas = token.lexema;
            emparejar(Token.OPAS);
            Pair t = T(symb_table, 0, "");
            String t_trad = t.getKey();
            int t_tipo = t.getValue();

            if(tipo == 1 && t_tipo == 2) {
                tipo = 2;
                th = "itor(" + th + ")";
            }

            if (opas.equals("+")) { //OPAS == +
                if (tipo == 1 && t_tipo == 1)
                    th = th + " +i " + t_trad;
                if (tipo == 2 && t_tipo == 1)
                    th = th + " +r itor(" + t_trad + ")";
                if (tipo == 2 && t_tipo == 2)
                    th = th + " +r " + t_trad;
            }
            else {
                if (opas.equals("-")){ //OPAS == -
                    if (tipo == 1 && t_tipo == 1)
                        th = th + " -i " + t_trad;
                    if (tipo == 2 && t_tipo == 1)
                        th = th + " -r itor(" + t_trad + ")";
                    if (tipo == 2 && t_tipo == 2)
                        th = th + " -r " + t_trad;
                }
            }

            r = Ep(symb_table, tipo, th);
        }
        else{
            if (token.tipo == Token.PYC || token.tipo == Token.PARD || token.tipo == Token.END){
                appendRule(27);
            }
            else
                errorSintaxis(Token.PYC, Token.END, Token.PARD, Token.OPAS);
        }

        return r;
    }

    public final Pair T(TablaSimbolos symb_table, int tipo, String th){
        appendRule(28);
        String trad = "";
        Pair r = new Pair(th, tipo);

        if (token.tipo == Token.NUMENTERO || token.tipo == Token.NUMREAL || token.tipo == Token.ID){
            String f_trad = token.lexema;
            int f_tipo = F(symb_table);
            if (f_tipo == 3){
                Simbolo s = symb_table.buscar(f_trad);
                f_trad = s.nomtrad;
                f_tipo = s.tipo;
            }

            if (tipo == 0)
                tipo = f_tipo;

            if (tipo > f_tipo)
                f_trad = "itor(" + f_trad + ")";

            th = th + f_trad;

            r = Tp(symb_table, tipo, th);
        }
        else
            errorSintaxis(Token.ID, Token.NUMENTERO, Token.NUMREAL);

        return r;
    }

    public final Pair Tp(TablaSimbolos symb_table, int tipo, String th){
        Pair r = new Pair(th, tipo);

        if (token.tipo == Token.OPMUL) {
            appendRule(29);
            String opmul = token.lexema;
            Token opmulerror = token;
            emparejar(Token.OPMUL);
            String f_trad = token.lexema;
            Token der = token;
            int f_tipo = F(symb_table);
            int prev_tipo = tipo;

            if (f_tipo == 3){
                Simbolo s = symb_table.buscar(f_trad);
                f_trad = s.nomtrad;
                f_tipo = s.tipo;
            }

            if(tipo == 1 && f_tipo == 2) {
                tipo = 2;
                th = "itor(" + th + ")";
            }

            if(opmul.equalsIgnoreCase("div")) { //OPMUL == DIV
                if (tipo == 1 && f_tipo == 1)
                    th = th + " /i " + f_trad;
                else {
                    if (prev_tipo == 2)
                        errorSemantico(ERRNOENTEROIZQ, opmulerror);
                    if (f_tipo == 2)
                        errorSemantico(ERRNOENTERODER, opmulerror);
                }
            }
            else {
                if (opmul.equalsIgnoreCase("mod")) { //OPMUL == MOD
                    if (tipo == 1 && f_tipo == 1)
                        th = th + " % " + f_trad;
                    else {
                        if (prev_tipo == 2)
                            errorSemantico(ERRNOENTEROIZQ, opmulerror);
                        if (f_tipo == 2)
                            errorSemantico(ERRNOENTERODER, opmulerror);
                    }
                }
                else{
                    if (opmul.equals("/")) { //OPMUL == /
                        if (tipo == 1 && f_tipo == 1)
                            th = "itor(" + th + ") /r itor(" + f_trad + ")";
                        if (tipo == 2 && f_tipo == 1)
                            th = th + " /r itor(" + f_trad + ")";
                        if (tipo == 2 && f_tipo == 2)
                            th = th + " /r " + f_trad;
                        tipo = 2;
                    }
                    else {
                        if (opmul.equals("*")){ //OPMUL == *
                            if (tipo == 1 && f_tipo == 1)
                                th = th + " *i " + f_trad;
                            if (tipo == 2 && f_tipo == 1)
                                th = th + " *r itor(" + f_trad + ")";
                            if (tipo == 2 && f_tipo == 2)
                                th = th + " *r " + f_trad;
                        }
                    }
                }
            }

            r = Tp(symb_table, tipo, th);
        }
        else{
            if (token.tipo == Token.OPAS || token.tipo == Token.PYC || token.tipo == Token.END || token.tipo == Token.PARD){
                appendRule(30);
            }
            else
                errorSintaxis(Token.PYC, Token.END, Token.PARD, Token.OPAS, Token.OPMUL);
        }

        return r;
    }

    public final int F(TablaSimbolos symb_table){
        String trad = "";

        if (token.tipo == Token.NUMENTERO) {
            appendRule(31);
            emparejar(Token.NUMENTERO);
            return 1;
        }
        else{
            if (token.tipo == Token.NUMREAL){
                appendRule(32);
                emparejar(Token.NUMREAL);
                return 2;
            }
            else{
                if (token.tipo == Token.ID) {
                    appendRule(33);
                    Simbolo s = symb_table.buscar(token.lexema);
                    Token id = token;
                    emparejar(Token.ID);
                    if (s != null){
                        if (s.tipo <= 2)
                            return 3;
                        else
                            errorSemantico(ERRNOSIMPLE, id);
                    }
                    else
                        errorSemantico(ERRNODECL, id);
                }
                else
                    errorSintaxis(Token.ID, Token.NUMENTERO, Token.NUMREAL);
            }
        }

        return 0;
    }
}
