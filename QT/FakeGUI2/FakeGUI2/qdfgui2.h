#ifndef QDFGUI2_H
#define QDFGUI2_H

#include <QDialog>
#include <sqlite3_interface.h>
namespace Ui {
    class QDFGUI2;
}

class QDFGUI2 : public QDialog
{
    Q_OBJECT

public:
    explicit QDFGUI2(QWidget *parent = 0);
    ~QDFGUI2();
protected:
    void timerEvent(QTimerEvent *);

private:
    Ui::QDFGUI2 *ui;
    int smooth;
    int CenFreq;
    int timerDuration;


private:
   int getSmoothFromGUI();
   int getCenterFreqFromGUI();

   void setLocation(const char * cp);
   void setPowerlevel(const char * cp);

   /////////////////
public slots:
   void getValuesFromGUI();
};

#endif // QDFGUI2_H
