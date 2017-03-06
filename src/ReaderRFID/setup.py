# coding=utf-8

from setuptools import setup, find_packages

import reader_rfid

VERSION = reader_rfid.__version__

setup(name = 'ReaderRFID',
    version = VERSION,
    author = "Jan Kohlíček",
    url = 'https://bitbucket.org/kohlicekjan/bpini',
    description='This project reads the RFID server and forwards it, the LED is used for status indication.',
    packages = ['reader_rfid'],
    install_requires = [
        'RPi.GPIO==0.6.3',
        'paho-mqtt==1.2',
        'pi-rc522==1.1.0'])