1. čtečka RFID
	- napájet piny RFID
	- zapojit RFID do GPIO
		http://i.stack.imgur.com/pXzYv.png

	- naistalovat systém 
		- Raspbian (Lite)
			Win32DiskImager
			user: pi
			password: raspberry

		- Windows Core IoT
			Windows Core IoT Dashboard
			http://ipadresa:8080/
			user: Administrator
			password: p@ssw0rd


	- vzdáleně připojit k RPi
		- Raspbian (Lite)
			sudo raspi-config
				-Expand Filesystem
				-Change User Password
				-Internationalisation Options
				-Advanced Options
					-GPIO
					-Update


			mkdir share
			chmod 0777 share
			cd share

			sudo apt-get update
			sudo apt-get install python3
			#sudo pip install ptvsd
			sudo apt-get install samba samba-common-bin

			sudo nano /etc/samba/smb.conf
				[RPiShare]
				path=/home/pi/share
				browsable=yes
				writable=yes
				only guest=no
				create mask=0700
				directory mask=0700
				public=yes


			<!--
				 [RPiShare]
				 comment = Raspberry Pi Share
				 path = /home/pi/share
				 valid users = @users
				 create mask=0700
				 directory mask=0700
				 browsable=Yes
				 writable=Yes
				 only guest=no		 
				 public=no
				 read only = no 

				
				sudo smbpasswd -a pi
				sudo /etc/init.d/samba restart
			-->

		- Windows Core IoT
			- webové rozhraní
			- visual studio

	- načíst ID z karty
		- Raspbian
			- povolit GPIO v configu
				sudo raspi-config

		- pomocí jednoho tagu změnit stav (přidávání, odebírání)
			- stav signalizovat pomocí LED

	- poslat ID na server přes protokol MQTT
		- MQTT
			- klient
				- Paho
			- Brokers
				- Mosca
				- Mosquitto

2. server
	- Node.js
	- protokol MQTT pro čtečku RFID
	- protokol HTTP pro klienta 
		- api

	- databáze - vlastní struktura
		- SQLite
		- MangoDB - vyzkoušet

3. cloud
	- Amazon Web Services (12 měsícu zdarma)
	- Google Cloud Platform (60 dní zdarma)
	- Azure (30 dení zdarma)?
		

	Zdarma
		- Známé
			- Ubidots- https://ubidots.com/
			- Nearbus - http://www.nearbus.net/
			- Carriots - https://www.carriots.com/	

		- Neznámé
			- ThingSpeak - https://thingspeak.com/
			- https://ifttt.com/
			- https://freeboard.io/
			- https://thethings.io/
			- https://www.heroku.com/
			- https://hcp.sap.com/try.html
			- http://www.ibm.com/cloud-computing/bluemix/solutions/

4. klient
	- mobilni aplikace
	- Android - Java, Xamarin (Android, iOS, Win Phone) - .net
	
	- zakladní funkce
		- Přehled s vyhledáváním
		- Editace
		- Vkládání
		- Smazání
		- Notifikace
		


zdroje:
	http://www.root.cz/clanky/protokol-mqtt-komunikacni-standard-pro-iot/
	http://www.root.cz/
	https://www.youtube.com
	http://www.lupa.cz/