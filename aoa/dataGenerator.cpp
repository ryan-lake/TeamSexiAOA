#include <iostream>
#include <stdlib.h>
#include "sqlite_interface.h"



int main(){
    
    while(true){
        // Create a db instance
        Sqlite db("QDFDatabase");

        double freq;
        int dwelltime, direction, power;
	
	system("clear");

        if ( db.needUpdate() )
        {
            freq = static_cast<double>(db.getFrequency());
	    dwelltime = db.getDwellTime();
	    db.confirmUpdated();
	    cout << "Updating\n";
        }

        direction = rand() % 360;
        power = rand() % 1000;

        db.putDirection(direction, power);
	
	cout << "Current freq: " << freq << " Dwell: " << dwelltime << "\n";
	cout << "Inserting Direction: " << direction << " Power: " << power << "\n";
	sleep(3);
   }
}
