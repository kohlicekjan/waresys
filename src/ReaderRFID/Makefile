.PHONY: install install-python install-git install-lib install-spi create-cron uninstall update-system raspi-config clean clean-test clean-build clean-pyc run

help:
	@echo ""
	@echo "make			- print help"
	@echo "make help		- print help"
	@echo "make install		- install python, git, SPI-Py, lib"
	@echo "make uninstall		- uinstall SPI-Py and lib"
	@echo "make run		- run project"
	@echo "make clean		- remove pyc and build file"
	@echo "make create-cron	- create cron reboot run project"
	@echo "make set-default-ip	- print guide set default ip adresss"
	@echo "make update-system	- update system"
	@echo "make raspi-config	- open raspi-config"
	@echo ""

install: update-system install-python install-git install-spi install-lib

install-python:
	sudo apt-get install -y -qq python python3 python-dev python3-dev python-pip python3-pip python-setuptools python3-setuptools¨

install-git:
	@echo "Install GIT"
	@echo "\tPlease wait..."
	@sudo apt-get install -y -qq git-core

install-spi: install-python install-git
	@echo "Install SPI-Py"
	@echo "\tPlease wait..."
	@sudo pip install git+https://github.com/lthiery/SPI-Py
	@grep -q -F 'dtparam=spi=on' /boot/config.txt || echo 'dtparam=spi=on' >> /boot/config.txt

install-lib:
	@echo "Install lib"
	@echo "\tPlease wait..."
	@sudo pip install -r requirements.txt

create-cron:
	croncmd="@reboot sudo python $(readlink -f ./reader_rfid/) &"
	crontab -l > cron.tmp
	grep -Fq "$croncmd" cron.tmp || echo "$croncmd" >> cron.tmp
	crontab cron.tmp
	rm -f cron.tmp
	#crontab -e

set-default-ip:
	@echo "Guide:"
	@echo "\tsudo ifconfig eth0 192.168.0.1 netmask 255.255.255.0 up"
	@echo "\tsudo nano /etc/rc.local"

uninstall:
	@echo "Uninstall"
	@echo "\tPlease wait..."
	@sudo pip uninstall -y SPI-Py
	@sudo pip uninstall -y pi-rc522
	@sudo pip uninstall -y paho-mqtt
	@sudo pip uninstall -y RPi.GPIO

update-system:
	@echo "Update system"
	@echo "\tPlease wait..."
	@sudo apt-get update -qq	
	@sudo apt-get upgrade -y -qq	

raspi-config:
	@echo "Open raspi-config"
	@sudo raspi-config

clean: clean-build clean-pyc

clean-build:
	@echo "Clean build"
	@echo "\tPlease wait..."
	@rm -fr build/
	@rm -fr dist/
	@rm -fr .eggs/
	@find . -name '*.egg-info' -exec rm -fr {} +
	@find . -name '*.egg' -exec rm -rf {} +

clean-pyc:
	@echo "Clean pyc"
	@echo "\tPlease wait..."
	@find . -name '*.pyc' -exec rm -f {} +
	@find . -name '*.pyo' -exec rm -f {} +
	@find . -name '*~' -exec rm -f {} +
	@find . -name '__pycache__' -exec rm -fr {} +

run:
	python ./reader_rfid/