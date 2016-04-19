import sys
import requests
import random


POST_URL = 'http://localhost:9191/'


def main(count):
    for i in xrange(count):
        message = '{:x} {:x} {:x} {:x} {:x} {:x}'.format(
            int(random.random() * 10**12),
            int(random.random() * 10**12),
            int(random.random() * 10**12),
            int(random.random() * 10**12),
            int(random.random() * 10**12),
            int(random.random() * 10**12)
        )

        response = requests.post(POST_URL, data=message)
        print i, response.status_code

if __name__ == '__main__':
    main(int(sys.argv[1]))
