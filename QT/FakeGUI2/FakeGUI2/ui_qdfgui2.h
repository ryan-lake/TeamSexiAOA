/********************************************************************************
** Form generated from reading UI file 'qdfgui2.ui'
**
** Created: Thu May 5 17:46:14 2011
**      by: Qt User Interface Compiler version 4.7.2
**
** WARNING! All changes made in this file will be lost when recompiling UI file!
********************************************************************************/

#ifndef UI_QDFGUI2_H
#define UI_QDFGUI2_H

#include <QtCore/QVariant>
#include <QtGui/QAction>
#include <QtGui/QApplication>
#include <QtGui/QButtonGroup>
#include <QtGui/QDialog>
#include <QtGui/QFrame>
#include <QtGui/QHeaderView>
#include <QtGui/QLabel>
#include <QtGui/QLineEdit>
#include <QtGui/QPushButton>

QT_BEGIN_NAMESPACE

class Ui_QDFGUI2
{
public:
    QPushButton *updateButton;
    QLabel *dataLabel;
    QLabel *smoothLabel;
    QLabel *settingsLabel;
    QLabel *centerFreqLabel;
    QLabel *locationLabel;
    QLabel *powerlevelLabel;
    QLineEdit *smoothBox;
    QLineEdit *centerFreqBox;
    QLineEdit *pLBox;
    QLineEdit *locBox;
    QFrame *line;

    void setupUi(QDialog *QDFGUI2)
    {
        if (QDFGUI2->objectName().isEmpty())
            QDFGUI2->setObjectName(QString::fromUtf8("QDFGUI2"));
        QDFGUI2->resize(317, 201);
        updateButton = new QPushButton(QDFGUI2);
        updateButton->setObjectName(QString::fromUtf8("updateButton"));
        updateButton->setGeometry(QRect(10, 170, 121, 23));
        dataLabel = new QLabel(QDFGUI2);
        dataLabel->setObjectName(QString::fromUtf8("dataLabel"));
        dataLabel->setGeometry(QRect(10, 10, 51, 16));
        QFont font;
        font.setUnderline(true);
        dataLabel->setFont(font);
        smoothLabel = new QLabel(QDFGUI2);
        smoothLabel->setObjectName(QString::fromUtf8("smoothLabel"));
        smoothLabel->setGeometry(QRect(10, 120, 131, 16));
        settingsLabel = new QLabel(QDFGUI2);
        settingsLabel->setObjectName(QString::fromUtf8("settingsLabel"));
        settingsLabel->setGeometry(QRect(10, 100, 71, 16));
        settingsLabel->setFont(font);
        centerFreqLabel = new QLabel(QDFGUI2);
        centerFreqLabel->setObjectName(QString::fromUtf8("centerFreqLabel"));
        centerFreqLabel->setGeometry(QRect(150, 120, 161, 16));
        locationLabel = new QLabel(QDFGUI2);
        locationLabel->setObjectName(QString::fromUtf8("locationLabel"));
        locationLabel->setGeometry(QRect(10, 30, 171, 16));
        powerlevelLabel = new QLabel(QDFGUI2);
        powerlevelLabel->setObjectName(QString::fromUtf8("powerlevelLabel"));
        powerlevelLabel->setGeometry(QRect(190, 30, 111, 16));
        smoothBox = new QLineEdit(QDFGUI2);
        smoothBox->setObjectName(QString::fromUtf8("smoothBox"));
        smoothBox->setGeometry(QRect(10, 140, 91, 20));
        centerFreqBox = new QLineEdit(QDFGUI2);
        centerFreqBox->setObjectName(QString::fromUtf8("centerFreqBox"));
        centerFreqBox->setGeometry(QRect(150, 140, 91, 20));
        pLBox = new QLineEdit(QDFGUI2);
        pLBox->setObjectName(QString::fromUtf8("pLBox"));
        pLBox->setGeometry(QRect(180, 50, 111, 20));
        pLBox->setReadOnly(true);
        locBox = new QLineEdit(QDFGUI2);
        locBox->setObjectName(QString::fromUtf8("locBox"));
        locBox->setGeometry(QRect(10, 50, 121, 20));
        locBox->setReadOnly(true);
        line = new QFrame(QDFGUI2);
        line->setObjectName(QString::fromUtf8("line"));
        line->setGeometry(QRect(10, 80, 301, 20));
        line->setFrameShape(QFrame::HLine);
        line->setFrameShadow(QFrame::Sunken);

        retranslateUi(QDFGUI2);

        QMetaObject::connectSlotsByName(QDFGUI2);
    } // setupUi

    void retranslateUi(QDialog *QDFGUI2)
    {
        QDFGUI2->setWindowTitle(QApplication::translate("QDFGUI2", "Qualcomm Senior Design GUI", 0, QApplication::UnicodeUTF8));
        updateButton->setText(QApplication::translate("QDFGUI2", "PushButton", 0, QApplication::UnicodeUTF8));
        dataLabel->setText(QApplication::translate("QDFGUI2", "Data Table", 0, QApplication::UnicodeUTF8));
        smoothLabel->setText(QApplication::translate("QDFGUI2", "Smooth Factor", 0, QApplication::UnicodeUTF8));
        settingsLabel->setText(QApplication::translate("QDFGUI2", "Settings Table", 0, QApplication::UnicodeUTF8));
        centerFreqLabel->setText(QApplication::translate("QDFGUI2", "Center Frequency (Hz)", 0, QApplication::UnicodeUTF8));
        locationLabel->setText(QApplication::translate("QDFGUI2", "Location (Degrees)", 0, QApplication::UnicodeUTF8));
        powerlevelLabel->setText(QApplication::translate("QDFGUI2", "Power Level (Average)", 0, QApplication::UnicodeUTF8));
        smoothBox->setText(QApplication::translate("QDFGUI2", "50", 0, QApplication::UnicodeUTF8));
        centerFreqBox->setText(QApplication::translate("QDFGUI2", "18525000", 0, QApplication::UnicodeUTF8));
        pLBox->setText(QApplication::translate("QDFGUI2", ":-D No Data Yet", 0, QApplication::UnicodeUTF8));
        locBox->setText(QApplication::translate("QDFGUI2", ":-D No Data Yet", 0, QApplication::UnicodeUTF8));
    } // retranslateUi

};

namespace Ui {
    class QDFGUI2: public Ui_QDFGUI2 {};
} // namespace Ui

QT_END_NAMESPACE

#endif // UI_QDFGUI2_H
