public class Action {

    public char op;
    public int state;

    public Action(){
        op = ' ';
        state = -1;
    }

    public Action(char op, int state){
        this.op = op;
        this.state = state;
    }

    public boolean isD(int state, int type){

        try{
            if (parse_table_terminal[state][type].op == 'd')
                return true;
        }
        catch(Exception e){

        }

        return false;
    }

    public boolean isR(int state, int type){
        try {
            if (parse_table_terminal[state][type].op == 'r')
                return true;
        }
        catch(Exception e){

        }

        return false;
    }

    public int go_to(int p, int A){
        return parse_table_nonterminal[p][A];
    }

    public boolean isAccept(int state, int type){
        try{
            if (parse_table_terminal[state][type].op == 'a')
                return true;
        }
        catch(Exception e){

        }

        return false;
    }

    public static final Action[][] parse_table_terminal = new Action[38][28];
    public static final int[][] parse_table_nonterminal = new int[38][11];
    static{
        //parse_table_terminal
        //'d' operation
        parse_table_terminal[14][0] = new Action('d', 17);
        parse_table_terminal[19][1] = new Action('d', 20);
        parse_table_terminal[30][2] = new Action('d', 31);
        parse_table_terminal[13][3] = new Action('d', 16);

        parse_table_terminal[3][4] = new Action('d', 4);
        parse_table_terminal[9][4] = new Action('d', 11);
        parse_table_terminal[32][4] = new Action('d', 33);

        parse_table_terminal[18][5] = new Action('d', 25);
        parse_table_terminal[19][5] = new Action('d', 25);

        parse_table_terminal[0][6] = new Action('d', 2);
        parse_table_terminal[6][7] = new Action('d', 8);
        parse_table_terminal[27][8] = new Action('d', 28);
        parse_table_terminal[31][9] = new Action('d', 34);
        parse_table_terminal[31][10] = new Action('d', 35);

        parse_table_terminal[4][11] = new Action('d', 6);
        parse_table_terminal[7][11] = new Action('d', 6);
        parse_table_terminal[11][11] = new Action('d', 6);

        parse_table_terminal[9][12] = new Action('d', 10);

        parse_table_terminal[7][13] = new Action('d', 14);
        parse_table_terminal[11][13] = new Action('d', 14);

        parse_table_terminal[2][14] = new Action('d', 3);
        parse_table_terminal[7][14] = new Action('d', 13);
        parse_table_terminal[8][14] = new Action('d', 30);
        parse_table_terminal[11][14] = new Action('d', 13);
        parse_table_terminal[16][14] = new Action('d', 21);
        parse_table_terminal[17][14] = new Action('d', 21);
        parse_table_terminal[25][14] = new Action('d', 21);
        parse_table_terminal[27][14] = new Action('d', 30);

        parse_table_terminal[16][15] = new Action('d', 23);
        parse_table_terminal[17][15] = new Action('d', 23);
        parse_table_terminal[25][15] = new Action('d', 23);

        parse_table_terminal[16][16] = new Action('d', 22);
        parse_table_terminal[17][16] = new Action('d', 22);
        parse_table_terminal[25][16] = new Action('d', 22);

        //'a' accepted
        parse_table_terminal[1][17] = new Action('a', 0);

        //'r' operation
        parse_table_terminal[21][1] = new Action('r', 18);
        parse_table_terminal[22][1] = new Action('r', 17);
        parse_table_terminal[23][1] = new Action('r', 16);
        parse_table_terminal[24][1] = new Action('r', 15);
        parse_table_terminal[26][1] = new Action('r', 14);

        parse_table_terminal[10][4] = new Action('r', 8);
        parse_table_terminal[12][4] = new Action('r', 9);
        parse_table_terminal[15][4] = new Action('r', 13);
        parse_table_terminal[18][4] = new Action('r', 11);
        parse_table_terminal[20][4] = new Action('r', 12);
        parse_table_terminal[21][4] = new Action('r', 18);
        parse_table_terminal[22][4] = new Action('r', 17);
        parse_table_terminal[23][4] = new Action('r', 16);
        parse_table_terminal[24][4] = new Action('r', 15);
        parse_table_terminal[26][4] = new Action('r', 14);
        parse_table_terminal[34][4] = new Action('r', 6);
        parse_table_terminal[35][4] = new Action('r', 7);
        parse_table_terminal[37][4] = new Action('r', 10);

        parse_table_terminal[21][5] = new Action('r', 18);
        parse_table_terminal[22][5] = new Action('r', 17);
        parse_table_terminal[23][5] = new Action('r', 16);
        parse_table_terminal[24][5] = new Action('r', 15);
        parse_table_terminal[26][5] = new Action('r', 14);

        parse_table_terminal[29][8] = new Action('r', 3);
        parse_table_terminal[33][8] = new Action('r', 5);
        parse_table_terminal[36][8] = new Action('r', 4);

        parse_table_terminal[28][11] = new Action('r', 2);

        parse_table_terminal[10][12] = new Action('r', 8);
        parse_table_terminal[12][12] = new Action('r', 9);
        parse_table_terminal[15][12] = new Action('r', 13);
        parse_table_terminal[18][12] = new Action('r', 11);
        parse_table_terminal[20][12] = new Action('r', 12);
        parse_table_terminal[21][12] = new Action('r', 18);
        parse_table_terminal[22][12] = new Action('r', 17);
        parse_table_terminal[23][12] = new Action('r', 16);
        parse_table_terminal[24][12] = new Action('r', 15);
        parse_table_terminal[26][12] = new Action('r', 14);
        parse_table_terminal[37][12] = new Action('r', 10);

        parse_table_terminal[28][13] = new Action('r', 2);

        parse_table_terminal[28][14] = new Action('r', 2);
        parse_table_terminal[29][14] = new Action('r', 3);
        parse_table_terminal[33][14] = new Action('r', 5);
        parse_table_terminal[36][14] = new Action('r', 4);

        parse_table_terminal[5][17] = new Action('r', 1);
        parse_table_terminal[10][17] = new Action('r', 8);

        //parse_table_nonterminal
        parse_table_nonterminal[0][1] = 1;
        parse_table_nonterminal[6][2] = 7;
        parse_table_nonterminal[8][3] = 27;
        parse_table_nonterminal[8][4] = 36;
        parse_table_nonterminal[27][4] = 29;
        parse_table_nonterminal[31][5] = 32;
        parse_table_nonterminal[4][6] = 5;
        parse_table_nonterminal[7][6] = 15;
        parse_table_nonterminal[11][6] = 15;
        parse_table_nonterminal[7][7] = 9;
        parse_table_nonterminal[7][8] = 37;
        parse_table_nonterminal[11][8] = 12;
        parse_table_nonterminal[16][9] = 18;
        parse_table_nonterminal[17][9] = 19;
        parse_table_nonterminal[16][10] = 24;
        parse_table_nonterminal[17][10] = 24;
        parse_table_nonterminal[25][10] = 26;
    }
}