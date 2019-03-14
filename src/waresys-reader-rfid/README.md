# Waresys reader RFID

The reader RFID is used to add and remove stock items.

## Build the device

Requirements components:
 * 1x Raspberry Pi
 * 1x micro SD card
 * 1x RFID-RC522
 * 1x RGB LED
 * 3x Resistor 220 Ohm

Wiring diagram of components:

<p align="center">
    <img src="https://github.com/kohlicekjan/waresys/raw/master/docs/diagrams/reader_rfid_diagram_bb.png" width="60%" alt="Waresys reader RFID schema">
</p>

 ## Install and configuration

Requirements:
 * [Raspbian Jessie](https://www.raspberrypi.org/documentation/installation/installing-images/README.md)
 * [Python](https://www.python.org/about/gettingstarted/) v2.7+


SSH is a protocol for security reasons by default disabled.
If you need to use SSH, here is the [manual](https://www.raspberrypi.org/documentation/remote-access/ssh/).


Command to open configuration:

```bash
$ sudo raspi-config
```

In the Raspberry Pi configuration, enable:
 * Internationalisation Options -> SPI
 * Advanced Options -> GPIO

Install dependencies:
```bash
$ sudo apt-get install python-pip
$ sudo pip install -r requirements.txt
```
or better to use Pipenv:
```bash
$ sudo apt-get install python-pip
$ sudo pip install pipenv
$ sudo pipenv install pipenv
```

 ## Start

 Example of running with server address setting:
 
 ```bash
$ python ./reader_rfid/ -H 10.10.90.26
```
 
 Optional parameters:
 * `-h` - help
 * `-v` - version
 * `-d` - debug mode
 * `-H <adress>` - MQTT host to connect to [default: `localhost`]
 * `-p <port>` - port for remote MQTT host [default: `1883`]


 ## User manual

Reader RFID  indicates its status by LED:
* off => no connection
* lights green => add item mode
* lights red => item removal mode
* blinking green => action was successful
* blinking red => an unexpected error occurred
* blinking blue => The RFID tag does not have a function set

The RFID tag read by the reader RFID is automatically registered.

The functionality of the tag can then be set in the mobile application.

If the reader is in the item adding mode, if a tag that represents the item loads, a quantity is added to +1. 

The RFID tag with "mode" function is used to switch reader modes.

