import java.io.EOFException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AnalizadorLexico {
    private Token token;
    private StringBuilder str_bld;
    private int asterisks;
    private int row, col;
    private int o_row, o_col;
    private int EOF;
    private boolean comment;
    private boolean stop;
    private RandomAccessFile entrada;

    public AnalizadorLexico(RandomAccessFile entrada){
        this.entrada = entrada;
        row = 1;
        col = 1;
        o_row = 1;
        o_col = 1;
        token = new Token();
    }

    public int getRow(){return row;}
    public int getCol(){return col;}

    public char leerCaracter(){
        char currentChar;
        try {
            entrada.seek(entrada.getFilePointer()-asterisks);
            currentChar = (char)entrada.readByte();
            return currentChar;
        }
        catch (EOFException e) {

            if (EOF == 1){
                EOF = 2;
            }
            else{
                EOF = 1;
            }
        }
        catch (IOException e) {
            //error lectura
        }
        return ' ';
    }

    public Token siguienteToken(){
        char c;
        int estado = 1;
        str_bld = new StringBuilder();
        comment = false;
        stop = false;
        token = new Token();
        row = o_row;
        col = o_col;

        while(!stop){
            c = leerCaracter();
            estado = delta(estado, c);

            if (EOF == 2) {
                token.lexema = Token.nombreToken.get(Token.EOF);
                token.tipo = Token.EOF;
                o_col += token.lexema.length();
                token.columna = o_col;
                token.fila = o_row;
                stop = true;
            }
        }

        return token;
    }

    public int showError(char c){
        System.err.println("Error lexico (" + o_row + "," + o_col + "): caracter '" + c + "' incorrecto");
        System.exit(-1);
        return 0;
    }

    public int returnState(int state, char c){
        str_bld.append(c);
        return state;
    }

    public int returnLexeme(int type, char c){
        if (asterisks != 0){    //asterisks are taken into account
            try {
                entrada.seek(entrada.getFilePointer() - asterisks);
                asterisks = 0;
            }
            catch(IOException e){

            }
        }
        else
            str_bld.append(c);

        if (type == -1){    //check if its id, reserved word or opmul
            String aux = str_bld.toString().toLowerCase();
            type = Token.ID;

            type = getReserved(aux);
        }

        token.lexema = str_bld.toString();
        token.tipo = type;
        token.columna = o_col;
        token.fila = o_row;
        o_col += token.lexema.length();

        //System.out.println("Token = '" + token.lexema + "' --> [" + o_row + "," + o_col + "], type = " + token.tipo);

        stop = true;
        return -1;
    }

    public int delta (int estado, char c) {
        switch (estado) {
            case 1:
                if (c == '(')
                    return returnState(2, c);
                if (c == ')')
                    return returnLexeme(Token.PARD, c); //pard
                if (c == ':')
                    return returnState(8, c);
                if (c == ';')
                    return returnLexeme(Token.PYC, c); //pyc
                if (c == '.')
                    return returnState(14, c);
                if (c == '+' || c == '-')
                    return returnLexeme(Token.OPAS, c); //opas
                if ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z'))
                    return returnState(18, c);
                if (c >= '0' && c <= '9')
                    return returnState(20, c);
                else{
                    if (c == ' ' || c == '\t' || c == '\n' || c == '\r'){
                        if (c == '\n' || c == '\r'){
                            if (c == '\n'){
                                o_col = 1;
                                o_row++;
                                col = 1;
                                row++;
                            }
                            return 1;
                        }
                        else{
                            o_col++;
                            col++;
                            return 1;
                        }
                    }
                    else {
                        return showError(c);
                    }
                }
            case 2:
                if (c == '*') {
                    comment = true;
                    o_col+=2;
                    col+=2;
                    return returnState(3, c);
                }
                else
                    asterisks=1;
                return returnLexeme(Token.PARI, c); //pari
            case 3:
                if (c == '*') {
                    o_col++;
                    col++;
                    return returnState(4, c);
                }
                else
                if (EOF != 1) {
                    if (c == '\n' || c == '\r'){
                        if (c == '\n'){
                            o_col = 1;
                            o_row++;
                            col = 1;
                            row++;
                        }
                    }
                    else{
                        o_col++;
                        col++;
                    }

                    return returnState(3, c);
                }
                else{
                    System.err.println("Error lexico: fin de fichero inesperado");
                    System.exit(-1);
                }
            case 4:
                if (c == ')') {
                    o_col++;
                    col++;
                    comment = false;
                    str_bld.setLength(0);
                    return 1;
                }
                else
                if (EOF != 1) {
                    if(c == '*'){
                        o_col++;
                        col++;
                        return returnState(4, c);
                    }
                    else{
                        return returnState(3, c);
                    }
                }
                else{
                    System.err.println("Error lexico: fin de fichero inesperado");
                    System.exit(-1);
                }
            case 8:
                if (c == '=')
                    return returnLexeme(Token.ASIG, c); //asig
                else{
                    asterisks=1;
                    return returnLexeme(Token.DOSP, c); //dosp
                }
            case 18:
                if ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || (c >= '0' && c <= '9'))
                    return returnState(18, c);
                else {
                    asterisks=1;
                    return returnLexeme(-1, c); //id o palabra reservada
                }
            case 20:
                if (c >= '0' && c <= '9')
                    return returnState(20, c);
                else{
                    if (c == '.')
                        return returnState(22, c);
                    else {
                        asterisks = 1;
                        return returnLexeme(Token.NUMENTERO, c); //numentero
                    }
                }
            case 22:
                if (c >= '0' && c <= '9')
                    return returnState(23, c);
                else{
                    asterisks = 2;
                    str_bld.setLength(str_bld.length() - 1);
                    return returnLexeme(Token.NUMENTERO, c); //numentero
                }
            case 23:
                if (c >= '0' && c <= '9')
                    return returnState(23, c);
                else {
                    asterisks = 1;
                    return returnLexeme(Token.NUMREAL, c); //numreal
                }
            default:
                //o_col++;
                //showError(c);
                System.out.println("default");
                break;
        }

        return 0;
    }

    public int getReserved(String lexeme){
        switch(lexeme){
            case "program":
                return Token.PROGRAM;
            case "var":
                return Token.VAR;
            case "endvar":
                return Token.ENDVAR;
            case "integer":
                return Token.INTEGER;
            case "real":
                return Token.REAL;
            case "begin":
                return Token.BEGIN;
            case "end":
                return Token.END;
            case "write":
                return Token.WRITE;
            default:
                break;
        }

        return Token.ID;
    }
}