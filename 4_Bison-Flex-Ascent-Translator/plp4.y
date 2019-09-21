%token program id pyc
%token var endvar
%token dosp
%token array cori cord of
%token coma
%token numentero ptopto
%token pointer
%token integer real
%token d_begin d_end
%token asig
%token d_write pari pard
%token opas opmul
%token numreal

%{

#include <string.h>
#include <stdio.h>
#include <stdlib.h>
#include <string>
#include <iostream>
#include <algorithm>
#include "comun.h"
using namespace std;

// Variables y Funciones del Analizador Lexico
extern int ncol, nlin, findefichero;
extern int yylex();
extern char *yytext;
extern FILE *yyin;

const int ERRYADECL=1, ERRNOSIMPLE=2, ERRNODECL=3, ERRTIPOS=4, ERRNOENTEROIZQ=5, ERRNOENTERODER=6, ERRRANGO=7;

int yyerror(char *s);
const int ENTERO = 1;
const int REAL = 2;
int a=0, b=0, c=0;
string t, aux_lexema;
TablaSimbolos* table(NULL);

%}

%%

S : program id pyc { $$.subr = ""; } B             {  
                                                      $$.trad = "int main() " + $5.trad; 
                                                      int tk = yylex();
                                                      if (tk != 0) yyerror("");
                                                      cout << $$.trad << endl;
                                                   };

D : var { $$.subr = $0.subr; } L endvar            { $$.trad = $3.trad; };

L : L { $$.subr = $0.subr; } V                     { $$.trad = $1.trad + $3.trad; }
  | { $$.subr = $0.subr; } V                       { $$.trad = $2.trad; };

V : id dosp { $$.id = $1.lexema; $$.subr = $0.subr; if (table->buscarAmbito($1.lexema)) {errorSemantico(ERRYADECL, $1.nlin, $1.ncol, $1.lexema);} } C pyc   { $$.trad = $4.trad; };

C : A { $$.arr = $0.arr + $1.trad; $$.id = $0.id; $$.subr = $0.subr; } C      { $$.trad = $3.trad; }
  | P                                                                         { 
                                                                                 $$.trad = $1.trad + " " + $0.subr + $0.id + $0.arr + ";\n";
                                                                                 string aux = string($0.subr + $0.id);
                                                                                 Simbolo s($0.id, $1.tipo, aux);
                                                                                 table->anyadir(s);
                                                                              };

A : array cori R cord of         { $$.trad = $3.trad; };

R : R coma G                     { $$.trad = $1.trad + $3.trad; }
  | G                            { $$.trad = $1.trad; };

G : numentero ptopto numentero   { 
                                    a = atoi($1.lexema);
                                    b = atoi($3.lexema);
                                    if ((b >= a) && (b >= 0) && (a >= 0)){
                                       c = b - a + 1;
                                       $$.trad = "[" + to_string(c) + "]";
                                       $$.tipo = 3;
                                    }
                                    else
                                       errorSemantico(ERRRANGO, $3.nlin, $3.ncol, $3.lexema);
                                 };

P : pointer of P                 { $$.trad = $3.trad + "*"; $$.tipo = 4; }
  | Tipo                         { $$.trad = $1.trad; $$.tipo = $1.tipo; };

Tipo : integer                   { $$.trad = "int"; $$.tipo = 1; }
     | real                      { $$.trad = "float"; $$.tipo = 2; };

B : d_begin { $$.subr = $0.subr; table = table->crearHijo(); } D { $$.subr = $0.subr; } SI d_end         { 
                                                                                                            $$.trad = "\n{\n" + $3.trad + $5.trad + "\n}\n";
                                                                                                            table = table->padre;
                                                                                                         };

SI : SI pyc {$$.subr = $0.subr; } I                    { $$.trad = $1. trad + $4.trad; }
   | {$$.subr = $0.subr; } I                           { $$.trad = $2.trad; };

I : id asig { $$.tipo = 0; } E                              {  
                                                               Simbolo s = table->buscar($1.lexema);
                                                               if (s.nombre != ""){
                                                                  if (s.tipo == $4.tipo)
                                                                     $$.trad = s.nomtrad + " = " + $4.trad + ";\n";
                                                                  if ((s.tipo == 2) && ($4.tipo == 1))
                                                                     $$.trad = s.nomtrad + " = itor(" + $4.trad + ");\n";
                                                                  if ((s.tipo == 1) && ($4.tipo == 2))
                                                                     errorSemantico(ERRTIPOS, $2.nlin, $2.ncol, $2.lexema); //ERRTIPOS
                                                               }
                                                               else
                                                                  errorSemantico(ERRNODECL, $1.nlin, $1.ncol, $1.lexema); //ERRNODECL
                                                            }
  | d_write pari { $$.tipo = 0; } E pard                    {  
                                                               t = "";
                                                               if ($4.tipo == 1)
                                                                  t = "d";
                                                               if ($4.tipo == 2)
                                                                  t = "f";
                                                               $$.trad = "printf(\"%" + t + "\", " + $4.trad + ");\n"; 
                                                            }
  | {$$.subr = $0.subr + "_"; } B                           { $$.trad = $2.trad; };

E : E opas { $$.tipo = $0.tipo; } T                      { 
                                                            if($1.tipo == 1 && $4.tipo == 2) {
                                                               $1.tipo = 2;
                                                               $1.trad = "itor(" + $1.trad + ")";
                                                            }

                                                            string aux = string($2.lexema);

                                                            if (aux == "+") { //OPAS == +
                                                               if ($1.tipo == 1 && $4.tipo == 1)
                                                                  $1.trad = $1.trad + " +i " + $4.trad;
                                                               if ($1.tipo == 2 && $4.tipo == 1)
                                                                  $1.trad = $1.trad + " +r itor(" + $4.trad + ")";
                                                               if ($1.tipo == 2 && $4.tipo == 2)
                                                                  $1.trad = $1.trad + " +r " + $4.trad;
                                                            }
                                                            else {
                                                               if (aux == "-"){ //OPAS == -
                                                                  if ($1.tipo == 1 && $4.tipo == 1)
                                                                        $1.trad = $1.trad + " -i " + $4.trad;
                                                                  if ($1.tipo == 2 && $4.tipo == 1)
                                                                        $1.trad = $1.trad + " -r itor(" + $4.trad + ")";
                                                                  if ($1.tipo == 2 && $4.tipo == 2)
                                                                        $1.trad = $1.trad + " -r " + $4.trad;
                                                               }
                                                            }

                                                            $$.trad = $1.trad;
                                                            $$.tipo = $1.tipo;
                                                         }
  | { $$.tipo = $0.tipo; } T                             { $$.trad = $2.trad; $$.tipo = $2.tipo; };

T : T opmul F                    { 
                                    int prev_tipo = $1.tipo;
                                    if($1.tipo == 1 && $3.f_tipo == 2) {
                                       $1.tipo = 2;
                                       $1.trad = "itor(" + $1.trad + ")";
                                    }

                                    string aux = string($2.lexema);

                                    if(equalsIgnoreCase("div", $2.lexema)) { //OPMUL == DIV
                                       if ($1.tipo == 1 && $3.f_tipo == 1)
                                          $1.trad = $1.trad + " /i " + $3.f_trad;
                                       else {
                                          if (prev_tipo == 2) { errorSemantico(ERRNOENTEROIZQ, $2.nlin, $2.ncol, $2.lexema); }
                                          if ($3.f_tipo == 2) { errorSemantico(ERRNOENTERODER, $2.nlin, $2.ncol, $2.lexema); }
                                       }
                                    }
                                    else {
                                       if (equalsIgnoreCase("mod", $2.lexema)) { //OPMUL == MOD
                                          if ($1.tipo == 1 && $3.f_tipo == 1)
                                             $1.trad = $1.trad + " % " + $3.f_trad;
                                          else {
                                             if (prev_tipo == 2) { errorSemantico(ERRNOENTEROIZQ, $2.nlin, $2.ncol, $2.lexema); }
                                             if ($3.f_tipo == 2) { errorSemantico(ERRNOENTERODER, $2.nlin, $2.ncol, $2.lexema); }
                                          }
                                       }
                                       else{
                                          if (aux == "/") { //OPMUL == /
                                             if ($1.tipo == 1 && $3.f_tipo == 1)
                                                $1.trad = "itor(" + $1.trad + ") /r itor(" + $3.f_trad + ")";
                                             if ($1.tipo == 2 && $3.f_tipo == 1)
                                                $1.trad = $1.trad + " /r itor(" + $3.f_trad + ")";
                                             if ($1.tipo == 2 && $3.f_tipo == 2)
                                                $1.trad = $1.trad + " /r " + $3.f_trad;
                                             $1.tipo = 2;
                                          }
                                          else {
                                             if (aux == "*"){ //OPMUL == *
                                                if ($1.tipo == 1 && $3.f_tipo == 1)
                                                   $1.trad = $1.trad + " *i " + $3.f_trad;
                                                if ($1.tipo == 2 && $3.f_tipo == 1)
                                                   $1.trad = $1.trad + " *r itor(" + $3.f_trad + ")";
                                                if ($1.tipo == 2 && $3.f_tipo == 2)
                                                   $1.trad = $1.trad + " *r " + $3.f_trad;
                                             }
                                          }
                                       }
                                    }

                                    $$.trad = $1.trad;
                                    $$.tipo = $1.tipo;
                                 }
  | F                            { 
                                    if ($0.tipo == 0)
                                       $$.tipo = $1.f_tipo;

                                    string aux = string($1.lexema);

                                    if ($$.tipo > $1.f_tipo)
                                       $1.f_trad = "itor(" + aux + ")";

                                    $$.trad = $1.f_trad;
                                 };

F : numentero                    { $$.f_trad = $1.lexema; $$.f_tipo = 1; }
  | numreal                      { $$.f_trad = $1.lexema; $$.f_tipo = 2; }
  | id                           { 
                                    Simbolo s = table->buscar($1.lexema);
                                    if (s.nombre != ""){
                                       if (s.tipo <= 2){
                                          $$.f_trad = s.nomtrad;
                                          $$.f_tipo = s.tipo;
                                       }
                                       else{ errorSemantico(ERRNOSIMPLE, $1.nlin, $1.ncol, $1.lexema); } //ERRNOSIMPLE
                                    }
                                    else{ errorSemantico(ERRNODECL, $1.nlin, $1.ncol, $1.lexema); } //ERRNODECL
                                 };

%%

void msgError(int nerror, int nlin, int ncol, const char *s){
   switch (nerror) {
      case ERRLEXICO: fprintf(stderr, "Error lexico (%d,%d): caracter '%s' incorrecto\n", nlin, ncol, s);
         break;
      case ERRSINT: fprintf(stderr, "Error sintactico (%d,%d): en '%s'\n", nlin, ncol, s);
         break;
      case ERREOF: fprintf(stderr, "Error sintactico: fin de fichero inesperado\n");
         break;
      case ERRLEXEOF: fprintf(stderr, "Error lexico: fin de fichero inesperado\n");
         break;
   }

   exit(1);
}

int yyerror(char *s){
   if (findefichero) {
      msgError(ERREOF, 0, 0, "");
   }
   else{  
      msgError(ERRSINT, nlin, ncol-strlen(yytext), yytext);
   }
}

bool equalsIgnoreCase(string s1, char* lexema){
   string s2 = string(lexema);
   transform(s2.begin(), s2.end(), s2.begin(), ::tolower);

   if (s1 == s2)
      return true;

   return false;
}

void errorSemantico(int nerror, int nlin, int ncol, const char *s) {
   switch (nerror) {
      case ERRYADECL: fprintf(stderr, "Error semantico (%d,%d): en '%s', ya existe en este ambito", nlin, ncol, s);
         break;
      case ERRNOSIMPLE: fprintf(stderr, "Error semantico (%d,%d): en '%s', debe ser de tipo entero o real", nlin, ncol, s);
         break;
      case ERRNODECL: fprintf(stderr, "Error semantico (%d,%d): en '%s', no ha sido declarado", nlin, ncol, s);
         break;
      case ERRTIPOS: fprintf(stderr, "Error semantico (%d,%d): en '%s', tipos incompatibles entero/real", nlin, ncol, s);
         break;
      case ERRNOENTEROIZQ: fprintf(stderr, "Error semantico (%d,%d): en '%s', el operando izquierdo debe ser entero", nlin, ncol, s);
         break;
      case ERRNOENTERODER: fprintf(stderr, "Error semantico (%d,%d): en '%s', el operando derecho debe ser entero", nlin, ncol, s);
         break;
      case ERRRANGO: fprintf(stderr, "Error semantico (%d,%d): en '%s', rango incorrecto", nlin, ncol, s);
         break;
   }

   exit(1);
}

int main(int argc, char *argv[]){
   FILE *fent;

   if (argc == 2) {
      fent = fopen(argv[1], "rt");
      if (fent) {
         yyin = fent;
         yyparse();
         fclose(fent);
      }
      else
         fprintf(stderr, "No puedo abrir el fichero\n");
   }
   else
      fprintf(stderr, "Uso: ejemplo <nombre de fichero>\n");
}