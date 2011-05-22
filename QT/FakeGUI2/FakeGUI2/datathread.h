#ifndef DATATHREAD_H
#define DATATHREAD_H

#include <QThread>

class DataThread : public QThread
{
    Q_OBJECT
public:
    explicit DataThread(QObject *parent = 0);
 protected:
     void run();

signals:
    void ourQDFDataReady();

public slots:
    void newSettings();
};

#endif // DATATHREAD_H
