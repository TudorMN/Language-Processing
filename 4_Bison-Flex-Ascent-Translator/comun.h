#include <iostream>
#include <vector>
#include <algorithm>
using namespace std;

class Simbolo{
   public:
      string nombre;
      int tipo;
      string nomtrad;

      Simbolo(){
         nombre = "";
         tipo = 0;
         nomtrad = "";
      }

      Simbolo(string a, int b, string c){
         nombre = a;
         tipo = b;
         nomtrad = c;
      }

      Simbolo& operator=(const Simbolo& s){
         if(this == &s)
            return *this;
         this->nombre = s.nombre;
         this->nomtrad = s.nomtrad;
         this->tipo = s.tipo;
         return *this;
      }
};

class TablaSimbolos{
   public:
      TablaSimbolos* padre;
      vector<Simbolo> simbolos;

      TablaSimbolos(TablaSimbolos* padre){
         this->padre = padre;
      }

      TablaSimbolos* crearHijo(){
         TablaSimbolos* hijo = new TablaSimbolos(this);
         return hijo;
      }

      bool buscarAmbito(string nombr){
         string nombre = string(nombr);
         transform(nombre.begin(), nombre.end(), nombre.begin(), ::tolower);

         for(size_t i = 0; i < this->simbolos.size(); i++){
            if(this->simbolos[i].nombre == nombre){
               return true;
            }
         }
         return false;
      }

      bool anyadir(Simbolo s){
         for(size_t i = 0; i < this->simbolos.size(); i++){
            if(this->simbolos[i].nombre == s.nombre){
               return false;
            }
         }
         this->simbolos.push_back(s);
         return true;
      }

      Simbolo buscar(char* nombr){
         string nombre = string(nombr);
         transform(nombre.begin(), nombre.end(), nombre.begin(), ::tolower);

         for(size_t i = 0; i < this->simbolos.size(); i++){
            if(this->simbolos[i].nombre == nombre){
               return this->simbolos[i];
            }
         }
         if(this->padre != NULL){ 
            return this->padre->buscar(nombr);
         }
      }
};

typedef struct {
   char *lexema;
   string subr, trad, arr, id, f_trad;
   int nlin, ncol;
   int tipo, f_tipo;
   string cod;
} MITIPO;

#define YYSTYPE MITIPO

#define ERRLEXICO    1
#define ERRSINT      2
#define ERREOF       3
#define ERRLEXEOF    4

void msgError(int nerror, int nlin, int ncol, const char *s);
bool equalsIgnoreCase(string s1, char* lexema);
void errorSemantico(int nerror, int nlin, int ncol, const char *s);