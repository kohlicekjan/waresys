.PHONY: install install-nodejs install-lib uninstall update-system run

help:
	@echo ""
	@echo "make			- print help"
	@echo "make help		- print help"
	@echo "make install		- install nodejs and lib"
	@echo "make update-system	- update system"
	@echo ""

install: update-system install-nodejs install-lib

install-nodejs:
	sudo apt-get update 
	sudo apt-get install nodejs
	sudo apt-get install npm

install-lib:
	sudo npm install

uninstall:
	echo "command does not exist"

update-system:
	sudo apt-get update -qq	
	sudo apt-get upgrade -y -qq

run:
	echo "command does not exist"