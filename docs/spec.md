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
			PRVNI SPUSTENI DLOUHO POCKAT
			VYTVOŘIT SOUBOR "ssh" VE SLOZCE "/boot/" PRO POVOLENÍ 

			sudo raspi-config
				- Expand Filesystem
				- Change User Password
				- Internationalisation Options
					- SPI
				- Advanced Options
					- GPIO	
					- Update

			sudo apt-get update
			sudo apt-get dist-upgrade


			mkdir share
			chmod 777 share
			cd share
	
			sudo apt-get install samba samba-common-bin
			sudo nano /etc/samba/smb.conf
				[RPiShare]
				path=/home/pi/share
				browsable=yes
				writable=yes
				only guest=no
				create mask=0700
				directory mask=0700
				public=no

			sudo smbpasswd -a pi

		- Windows Core IoT
			- webové rozhraní
			- visual studio

	- načíst ID z karty
		- pomocí jednoho tagu změnit stav (přidávání, odebírání)
			- stav signalizovat pomocí LED

	- automatické nastavení a instalace
		- nastvení configu
		- aktualiza
		- instalce baličků
		- instalce modulů
		- nastaveni automatické ho spustení Reader_RFID
		- zajistit jen jedno spusteni

	- komunikace
		- MQTT protokol

	?
		přidat config 
		vylepšit logováni
		přeformátovat kód

2. server
	- Node.js

	- MQTT Broker Server
		- druh
			- Mosca 
				- nodejs modul
				- uložiště
					- Redis - nevím
					- MongoDB - už používám jasná volba

			- Mosquitto - specialni aplikace od Eclipse

		- výhody
			Jedná se o publikování / odběr protokol
			Má několik kvalitě úrovní služeb (QoS)
			Má at-nejméně jednou a přesně-once sémantika
			Má nízkou režii (2 bajty na minimum)
			To podporuje offline posílání zpráv
			To udrží zprávy, jako klíč / hodnota úložiště

	- logging
		- winston - nejpoužívanější, hodně přispůsobení 
		- bunyan - format v json (nelze formatovat), rotate file 
			- používáho hodně modulů
			- přešel jsem na něj



	-Framework
		- Express - nejpoužívajší, nejobecnější faramework
		- Restify - framework určený pro API, používá ho Netflix

	- Auth
		-http://passportjs.org/docs/other-api

	- API
		- REST - standart 
			- GET: /api/item
			- GET: /api/item/1
			- POST: /api/item
			- PUT: /api/item/1
			- DELETE: /api/item/1
		- Socket.io
	
	- DB 
		- druh
			- MangoDB 
				- NoSQL 
				- můžu nainstalovat kam chci
				- mongod.bat
					START mongod.exe --dbpath "C:\Users\K\Desktop\BPINI_Tools\MongoDB\bin\data"
				- module (http://voidcanvas.com/mongoose-vs-mongodb-native/)
					- mongoose - použivanější, schema (omezení), využívá mongodb
					- mongodb - rychlejší

				- UI admin
					- mongo-express = použivanější
						- npm install mongo-express

			- SQLite
				- nevyhovuje požadavku = vzdálené připojení

		- struktura
			- item (_id, name, description, amount, created, updated)
			- tag (_id, uid, type, item, created, updated)

		- testy
			- moduly
			- integrační testy
				- api
					-item
						-zalozeni (OK, chyba...)
						-hledani (OK, CHYBA...)
					-card
					-auth

		configurace
			-config
				- extra jednoduché použití
			-nconf
				- nejpoužívajší
				- nevidím přínos

	?		
		testy
		update tagu.type -> změnit i tag.item
		update změnit tag.updated, item.updated
		dokumetace na adrese /api = swagger
		zkusit z npm projekt json-server 

3. klient
	- mobilní aplikace

	- vývojová platforma
		- Android 
			- Java 

		- xamarin
	
	- zakladní funkce
		- Přehled s vyhledáváním položek
		- Editace položek
		- Vkládání položek
		- Smazání položek
		- Změna typu tagu



zdroje:
	http://stackoverflow.com/
	
	-MQTT
	https://www.root.cz/clanky/protokol-mqtt-komunikacni-standard-pro-iot/

	-API
	https://scotch.io/tutorials/build-a-restful-api-using-node-and-express-4
	https://scotch.io/tutorials/authenticate-a-node-js-api-with-json-web-tokens
	https://www.zdrojak.cz/clanky/javascript-na-serveru-rest-api/
	https://www.zdrojak.cz/clanky/javascript-na-serveru-implementace-rest-api/
	https://www.zdrojak.cz/clanky/rest-architektura-pro-webove-api/
	http://www.restapitutorial.com/

	http://mongoosejs.com/docs/
	http://mongoosejs.com/docs/api.html#schema_string_SchemaString-minlength
