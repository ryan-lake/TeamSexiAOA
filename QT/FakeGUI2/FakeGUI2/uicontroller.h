////////////////////////
#ifndef UICONTROLLER_H
#define UICONTROLLER_H
#include "qdfgui2.h"

using namespace Ui;

class UIController
{

public: UIController();

public: int centerFreq;
public: int smooth;

public: int location;
        int powerlevel;

/*


public:
    void setSmoothHz(int smooth, int hz);
 */


  //////////////////////////
//Test to set the location
//public: void static (*setLoc)(const char *) ;//Set this as a callback
/*
  public:
    void regLocFun(void(*pt2func)(const char *));
    */

};
#endif // UICONTROLER_H
/*
 //////////////////////
void (UIController::*setFre)(int) = NULL;
void (UIController::*setLoc)(int) = NULL;

int DoIt  (float a, char b, char c){ printf("DoIt\n");   return a+b+c; }

void PassPtr(int (*pt2Func)(float, char, char))
{
   int result = (*pt2Func)(12, 'a', 'b');     // call using function pointer
   cout << result << endl;
}

// execute example code - 'DoIt' is a suitable function like defined above in 2.1-4
void Pass_A_Function_Pointer()
{
   cout << endl << "Executing 'Pass_A_Function_Pointer'" << endl;
   PassPtr(&DoIt);
}
*/
