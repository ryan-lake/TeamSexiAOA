#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>

/*
TO Test this will require that the adb.exe be installed on a linux box
*/
	
/*
adb pull <remote> <local>
adb push <local> <remote> 

In the commands, <local> and <remote> refer to the paths to
the target files/directory on your development machine (local) 
and on the emulator/device instance (remote).
*/

	int ADBPush(char* directoryADB,  char*  directoryOfDatabase){
		
		int i =0;
		char str[256];
		i = chdir(directoryADB);

		//Just put adb in the same directory?
		strcpy(str,"adb push \"");
		strcat(str,directoryOfDatabase);
		strcat(str , "QDFDatabase\" ""\"/data/data/act.QDF/databases/QDFDatabase\"");
		i=i+system(str);

		return i;
	}

	int ADBPull(char* directoryADB,  char*  directoryOfDatabase){
			
		int i =0;
		char str[256];//buffer
		i=chdir(directoryADB);
		
		//Just put adb in the same directory?
		strcpy(str,"adb pull \"/data/data/act.QDF/databases/QDFDatabase\" \"");
		strcat(str,directoryOfDatabase);
		strcat(str, "QDFDatabase\"");
		/*Test*/
		strcpy(str,"adb devices");
		i=i+system(str);
		i=i+system("PAUSE");
		return i;	
	
	}
