from setuptools import setup, find_packages
#from distutils.core import setup

setup(name = "ReaderRFID",
      version ="0.1dev",
      description='This project reads the RFID server and forwards it, the LED is used for status indication.',
      #long_description='',
      author = "Jan Kohlíček",
      author_email = "kohlicekjan@gmail.com",
      url = 'https://bitbucket.org/kohlicekjan/bpini',
      license = 'BSD',
      packages = find_packages(exclude=['tests', 'tests.*']),
      #install_requires = ['numpy==1.8.1', 'scikit-learn==0.14.1'],
      entry_points = {'console_scripts' : {'main = read_rfid.module:main'}},
      #classifiers = []
      )
