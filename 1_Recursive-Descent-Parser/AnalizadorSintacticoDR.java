public class AnalizadorSintacticoDR {

    public Token token;
    public AnalizadorLexico lexico;
    public StringBuilder reglas;
    private boolean flag;

    private void appendRule(int rule){
        if (flag)
            reglas.append(" " + rule);
    }

    public AnalizadorSintacticoDR(AnalizadorLexico al){
        lexico = al;
        token = new Token();
        reglas = new StringBuilder();
        flag = true;
    }

    public void comprobarFinFichero(){
        if (flag)
            System.out.println(reglas.toString());
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

    public final void S(){
        token = lexico.siguienteToken();
        reglas.append(1);

        if (token.tipo == Token.PROGRAM) {
            emparejar(Token.PROGRAM);
            emparejar(Token.ID);
            emparejar(Token.PYC);
            B();
        }
        else
            errorSintaxis(Token.PROGRAM);
    }

    public final void D(){
        appendRule(2);

        if (token.tipo == Token.VAR) {
            emparejar(Token.VAR);
            L();
            emparejar(Token.ENDVAR);
        }
        else
            errorSintaxis(Token.VAR);
    }

    public final void L(){
        appendRule(3);

        if (token.tipo == Token.ID) {
            V();
            Lp();
        }
        else
            errorSintaxis(Token.ID);
    }

    public final void Lp(){
        if (token.tipo == Token.ID) {
            appendRule(4);
            V();
            Lp();
        }
        else{
            if (token.tipo == Token.ENDVAR) {
                appendRule(5);
            }
            else
                errorSintaxis(Token.ID, Token.ENDVAR);
        }
    }

    public final void V(){
        appendRule(6);

        if (token.tipo == Token.ID) {
            emparejar(Token.ID);
            emparejar(Token.DOSP);
            C();
            emparejar(Token.PYC);
        }
        else
            errorSintaxis(Token.ID);
    }

    public final void C() {
        if (token.tipo == Token.ARRAY) {
            appendRule(7);
            A();
            C();
        } else {
            if (token.tipo == Token.POINTER || token.tipo == Token.INTEGER || token.tipo == Token.REAL) {
                appendRule(8);
                P();
            } else
                errorSintaxis(Token.ARRAY, Token.POINTER, Token.INTEGER, Token.REAL);
        }
    }

    public final void A(){
        appendRule(9);

        if (token.tipo == Token.ARRAY) {
            emparejar(Token.ARRAY);
            emparejar(Token.CORI);
            R();
            emparejar(Token.CORD);
            emparejar(Token.OF);
        }
        else
            errorSintaxis(Token.ARRAY);
    }

    public final void R(){
        appendRule(10);

        if (token.tipo == Token.NUMENTERO) {
            G();
            Rp();
        }
        else
            errorSintaxis(Token.NUMENTERO);
    }

    public final void Rp(){
        if (token.tipo == Token.COMA) {
            appendRule(11);
            emparejar(Token.COMA);
            G();
            Rp();
        }
        else{
            if (token.tipo == Token.CORD) {
                appendRule(12);
            }
            else
                errorSintaxis(Token.CORD, Token.COMA);
        }
    }

    public final void G(){
        appendRule(13);

        if (token.tipo == Token.NUMENTERO) {
            emparejar(Token.NUMENTERO);
            emparejar(Token.PTOPTO);
            emparejar(Token.NUMENTERO);
        }
        else
            errorSintaxis(Token.NUMENTERO);
    }

    public final void P(){
        if (token.tipo == Token.POINTER) {
            appendRule(14);
            emparejar(Token.POINTER);
            emparejar(Token.OF);
            P();
        }
        else{
            if (token.tipo == Token.INTEGER || token.tipo == Token.REAL){
                appendRule(15);
                Tipo();
            }
            else
                errorSintaxis(Token.POINTER, Token.INTEGER, Token.REAL);
        }
    }

    public final void Tipo(){
        if (token.tipo == Token.INTEGER) {
            appendRule(16);
            emparejar(Token.INTEGER);
        }
        else{
            if (token.tipo == Token.REAL){
                appendRule(17);
                emparejar(Token.REAL);
            }
            else
                errorSintaxis(Token.INTEGER, Token.REAL);
        }
    }

    public final void B(){
        appendRule(18);

        if (token.tipo == Token.BEGIN) {
            emparejar(Token.BEGIN);
            D();
            SI();
            emparejar(Token.END);
        }
        else
            errorSintaxis(Token.BEGIN);
    }

    public final void SI(){
        appendRule(19);

        if (token.tipo == Token.ID || token.tipo == Token.WRITE || token.tipo == Token.BEGIN) {
            I();
            M();
        }
        else
            errorSintaxis(Token.ID, Token.BEGIN, Token.WRITE);
    }

    public final void M(){
        if (token.tipo == Token.PYC) {
            appendRule(20);
            emparejar(Token.PYC);
            I();
            M();
        }
        else{
            if (token.tipo == Token.END){
                appendRule(21);
            }
            else
                errorSintaxis(Token.PYC, Token.END);
        }
    }

    public final void I(){
        if (token.tipo == Token.ID) {
            appendRule(22);
            emparejar(Token.ID);
            emparejar(Token.ASIG);
            E();
        }
        else{
            if (token.tipo == Token.WRITE){
                appendRule(23);
                emparejar(Token.WRITE);
                emparejar(Token.PARI);
                E();
                emparejar(Token.PARD);
            }
            else{
                if (token.tipo == Token.BEGIN) {
                    appendRule(24);
                    B();
                }
                else
                    errorSintaxis(Token.ID, Token.BEGIN, Token.WRITE);
            }
        }
    }

    public final void E(){
        appendRule(25);

        if (token.tipo == Token.NUMENTERO || token.tipo == Token.NUMREAL || token.tipo == Token.ID) {
            T();
            Ep();
        }
        else
            errorSintaxis(Token.ID, Token.NUMENTERO, Token.NUMREAL);
    }

    public final void Ep(){
        if (token.tipo == Token.OPAS) {
            appendRule(26);
            emparejar(Token.OPAS);
            T();
            Ep();
        }
        else{
            if (token.tipo == Token.PYC || token.tipo == Token.PARD || token.tipo == Token.END){
                appendRule(27);
            }
            else
                errorSintaxis(Token.PYC, Token.END, Token.PARD, Token.OPAS);
        }
    }

    public final void T(){
        appendRule(28);

        if (token.tipo == Token.NUMENTERO || token.tipo == Token.NUMREAL || token.tipo == Token.ID){
            F();
            Tp();
        }
        else
            errorSintaxis(Token.ID, Token.NUMENTERO, Token.NUMREAL);
    }

    public final void Tp(){
        if (token.tipo == Token.OPMUL) {
            appendRule(29);
            emparejar(Token.OPMUL);
            F();
            Tp();
        }
        else{
            if (token.tipo == Token.OPAS || token.tipo == Token.PYC || token.tipo == Token.END || token.tipo == Token.PARD){
                appendRule(30);
            }
            else
                errorSintaxis(Token.PYC, Token.END, Token.PARD, Token.OPAS, Token.OPMUL);
        }
    }

    public final void F(){
        if (token.tipo == Token.NUMENTERO) {
            appendRule(31);
            emparejar(Token.NUMENTERO);
        }
        else{
            if (token.tipo == Token.NUMREAL){
                appendRule(32);
                emparejar(Token.NUMREAL);
            }
            else{
                if (token.tipo == Token.ID) {
                    appendRule(33);
                    emparejar(Token.ID);
                }
                else
                    errorSintaxis(Token.ID, Token.NUMENTERO, Token.NUMREAL);
            }
        }
    }
}
