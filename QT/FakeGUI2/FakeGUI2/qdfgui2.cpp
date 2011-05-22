#include "qdfgui2.h"
#include "ui_qdfgui2.h"

/*
For our purposes this is the Gui Class
*/

//constructor
QDFGUI2::QDFGUI2(QWidget *parent) : QDialog(parent), ui(new Ui::QDFGUI2)
{
    ui->setupUi(this);
    //Register signal
    connect( ui->updateButton, SIGNAL( clicked() ), this, SLOT(getValuesFromGUI()) );
    timerDuration = 2200;
    startTimer(timerDuration);
}
//destructor
QDFGUI2::~QDFGUI2()
{
    delete ui;
}

//Slots
void QDFGUI2::getValuesFromGUI()
{
    Sqlite db("QDFDatabase");
    QString text;
    smooth = getSmoothFromGUI();
    CenFreq = getCenterFreqFromGUI();
    db.setConfig(CenFreq,smooth);
    while(!db.updated())
    {
        startTimer(500000);
        ui->smoothBox->setText("Updating...");
        ui->centerFreqBox->setText("Updating");
        sleep(1);
    }
    startTimer(100);
    ui->smoothBox->setText(text.setNum(smooth));
    ui->centerFreqBox->setText(text.setNum(CenFreq));
}

void QDFGUI2::timerEvent(QTimerEvent *)
{
    Sqlite db("QDFDatabase");
    QString text;
    startTimer(9999999);
    int* data = db.getResult();
    ui->locBox->setText(text.setNum(data[0]));
    ui->pLBox->setText(text.setNum(data[1]));
    startTimer(timerDuration);
}

//functions
int QDFGUI2::getSmoothFromGUI(){
    return ui->smoothBox->text().toInt();
}

int QDFGUI2::getCenterFreqFromGUI(){
    return ui->centerFreqBox->text().toInt();
}

void QDFGUI2::setLocation(const char * str){
    QString qt = QString(str);
    ui->locBox->setText(qt);
}

void QDFGUI2::setPowerlevel(const char * str){
    QString qt = QString(str);
    ui->pLBox->setText(qt);
}



//////////////////////

/*

UIController QDFGUI2::getController(){
    return *QDFGUI2::uicon;
}
*/

////////


