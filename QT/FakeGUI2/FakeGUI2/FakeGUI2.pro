#-------------------------------------------------
#
# Project created by QtCreator 2011-05-04T12:00:45
#
#-------------------------------------------------

QT       += core gui

TARGET = FakeGUI2
TEMPLATE = app


SOURCES +=\
        qdfgui2.cpp \
    main.cpp \
    sqlite3_interface.cpp

HEADERS  += qdfgui2.h \
    sqlite3_interface.h

LIBS += -lsqlite3
FORMS    += qdfgui2.ui
