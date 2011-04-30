/* system example : DIR */
#include "ADBWrapper.h"

int main ()
{
	int g = 0;

	g = ADBPull("C:\\Program Files\\Android\\android-sdk-windows\\platform-tools\\","C:\\Documents and Settings\\m.LAP1\\My Documents\\UCCS\\Spring 11\\Senior Design\\EmulatorWorkspace\\");


	return g;
}
	/*
  int i;
  printf ("Checking if processor is available...");
  if (system(NULL)) puts ("Ok");
    else exit (1);
  printf ("Executing command DIR...\n");
  i=system ("dir");
  printf ("The value returned was: %d.\n",i);
   i=system ("PAUSE");
   */