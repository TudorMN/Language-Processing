import java.util.ArrayList;
import java.util.Arrays;
import java.util.Stack;

public class AnalizadorSintacticoSLR {
    private AnalizadorLexico al;
    private Token t;
    private Stack<Integer> rules;
    private Stack<Integer> rules_to_print;
    private Action action;
    private ArrayList<Integer> parte_izq;
    private ArrayList<Integer> parte_der;

    public AnalizadorSintacticoSLR(AnalizadorLexico al){
        this.al = al;
        t = new Token();
        rules = new Stack<>();
        rules_to_print = new Stack<>();
        action = new Action();

                                                //X, S, D, L, L, V, T, T, B,SI,SI, I, I, I, E, E, F,  F,  F
        parte_izq = new ArrayList<>(Arrays.asList(0, 1, 2, 3, 3, 4, 5, 5, 6, 7, 7, 8, 8, 8, 9, 9, 10, 10, 10));
        parte_der = new ArrayList<>(Arrays.asList(1, 4, 3, 2, 1, 4, 1, 1, 4, 3, 1, 3, 4, 1, 3, 1, 1, 1, 1));
    }

    public void analizar(){
        int state;
        int j, k;
        rules.push(0);
        t = al.siguienteToken();

        while(true){
            state = rules.lastElement();

            if(action.isD(state, t.tipo)){
                j = action.parse_table_terminal[state][t.tipo].state;
                rules.push(j);
                t = al.siguienteToken();
            }
            else{
                if(action.isR(state, t.tipo)){
                    k = action.parse_table_terminal[state][t.tipo].state;
                    rules_to_print.push(k);

                    for(int i = 1; i <= parte_der.get(k); i++){
                        rules.pop();
                    }

                    rules.push(action.go_to(rules.peek(), parte_izq.get(k)));
                }
                else{
                    if(action.isAccept(state, t.tipo)){
                        break;
                    }
                    else{
                        errorSintaxis(state);
                    }
                }
            }
        }

        comprobarFinFichero();
    }

    public void comprobarFinFichero(){
        if (t.tipo==Token.EOF) {
            String sol = String.valueOf(rules_to_print.pop());

            while (!rules_to_print.empty()){
                sol += " " + rules_to_print.pop();
            }

            System.out.println(sol);
        }
        else
            if(t.tipo != Token.EOF)
                errorSintaxis(Token.EOF);
    }

    public void errorSintaxis(int i){
        String exp_str = "";

        for (int j = 0; j < 28; j++){
            if (action.parse_table_terminal[i][j] != null && action.parse_table_terminal[i][j].state != 0)
                exp_str += " " + t.nombreToken.get(j);
        }

        if (t.tipo != t.EOF){
            System.err.println("Error sintactico (" + al.getRow() + "," + al.getCol() + "): encontrado '" + t.lexema + "', esperaba" + exp_str);
        }
        else{
            System.err.println("Error sintactico: encontrado fin de fichero, esperaba" + exp_str);
        }

        System.exit(-1);
    }
}
