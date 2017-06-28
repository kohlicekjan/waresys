import time
from pirc522 import RFID


class RFID(RFID):
    def read_uid(self, wait=0.1):
        """ Načte UID jen jednou """

        self.undetected_num = 0

        while True:
            (error, data) = self.request()
            if not error:

                if (self.undetected_num > 2):
                    (error, uid) = self.anticoll()
                    if not error:
                        return uid
                    else:
                        return None

                self.undetected_num = 0
            else:
                self.undetected_num += 1

            time.sleep(wait)

    def __del__(self):
        self.cleanup()
