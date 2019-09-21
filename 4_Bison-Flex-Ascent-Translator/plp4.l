D    [0-9]
L    [a-zA-Z]
LD   [0-9a-zA-Z]

%{
#include <string.h> 
#include <string>
#include <iostream>
#include "comun.h"
#include "plp4.tab.h"
using namespace std;

int ncol = 1, nlin = 1;
int findefichero = 0;
int ret(int token);
void msgError(int nerror, int nlin, int ncol, const char *s);

%}

%x COMENTARIO

%%
%{
/* codigo local */
%}

    /* Espacios, tabulaciones y saltos de linea */
" "                 {ncol++;}
[\t]                {ncol++;}
[\n]                {nlin++; ncol=1;}

    /* Comentarios */
"(*"                    {ncol += strlen(yytext); BEGIN(COMENTARIO);}
<COMENTARIO>[^*\n]+     {ncol += strlen(yytext);}
<COMENTARIO>\n          {nlin++; ncol = 1;}
<COMENTARIO><<EOF>>     {msgError(ERRLEXEOF, -1, -1, "");}
<COMENTARIO>"*)"        {ncol += strlen(yytext); BEGIN(INITIAL);}
<COMENTARIO>[*)]        {ncol += strlen(yytext);}

    /* Palabras reservadas */
[Pp][Rr][Oo][Gg][Rr][Aa][Mm]        {return ret(program);}
[Vv][Aa][Rr]                        {return ret(var);}
[Ee][Nn][Dd][Vv][Aa][Rr]            {return ret(endvar);}
[Ii][Nn][Tt][Ee][Gg][Ee][Rr]        {return ret(integer);}
[Rr][Ee][Aa][Ll]                    {return ret(real);}
[Aa][Rr][Rr][Aa][Yy]                {return ret(array);}
[Oo][Ff]                            {return ret(of);}
[Pp][Oo][Ii][Nn][Tt][Ee][Rr]        {return ret(pointer);}
[Bb][Ee][Gg][Ii][Nn]                {return ret(d_begin);}
[Ee][Nn][Dd]                        {return ret(d_end);}
[Ww][Rr][Ii][Tt][Ee]                {return ret(d_write);}

    /* Otros lexemas */
"("                 {return ret(pari);}
")"                 {return ret(pard);}
","                 {return ret(coma);}
":"                 {return ret(dosp);}
"["                 {return ret(cori);}
"]"                 {return ret(cord);}
":="                {return ret(asig);}
";"                 {return ret(pyc);}
".."                {return ret(ptopto);}
"+"                 {return ret(opas);}
"-"                 {return ret(opas);}
"*"                 {return ret(opmul);}
"/"                 {return ret(opmul);}
[Dd][Ii][Vv]        {return ret(opmul);}
[Mm][Oo][Dd]        {return ret(opmul);}
{L}({LD})*          {return ret(id);}
{D}+                {return ret(numentero);}
{D}+(\.){D}+        {return ret(numreal);}

    /* Error de caso base */
.                   {msgError(ERRLEXICO, nlin, ncol, yytext);}

%%

int yywrap(void) {findefichero = 1; return 1;} /* para no tener que linkar con la libreria del lex */

int ret(int token){
   yylval.lexema = strdup(yytext);
   yylval.nlin = nlin;
   yylval.ncol = ncol;
   ncol += (strlen(yytext));
   return(token);
}