# Waresys

> This is prototype warehouse system for Industry 4.0. The System is not intended for use in distribution.

The warehouse system consists of three components: **server**, **reader RFID** and **app for Android**.
The server communicates with other components via the MQTT protocol and the REST API to which other systems can connect. Warehouse information is stored in the MongoDB database.
Reader RFID is used to add and remove stock items.
The mobile application is used to manage the warehouse system, NFC is used to quickly retrieve warehouse item information.

## Components and Architecture

<p align="center">
    <img src="https://github.com/kohlicekjan/waresys/raw/master/docs/diagrams/network.png" width="60%" alt="Waresys reader RFID schema">
</p>

### Waresys server

The Waresys server components can be found in the [waresys-server](https://github.com/kohlicekjan/waresys/tree/master/src/waresys-server)

### Waresys for Android

The Waresys client app can be found in the [waresys-android](https://github.com/kohlicekjan/waresys/tree/master/src/waresys-android)

### Waresys reader RFID

The Waresys reader RFID can be found in the [waresys-reader-rfid](https://github.com/kohlicekjan/waresys/tree/master/src/waresys-reader-rfid)