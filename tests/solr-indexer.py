#!/usr/bin/env python

import sys
import time
import json
import random
import requests


#SOLR_URL = "http://localhost:8983/solr/techproducts/update"
SOLR_URL = "http://localhost:8080/solr/techproducts/update"
DICT = "/usr/share/dict/words"
BATCH_SIZE = 1000
MESSAGE_WORDS = 5


def send_batch(batch):
    print batch[0]["id"]
    response = requests.post(SOLR_URL, json=batch)
    if not str(response.status_code).startswith('2'):
        raise Exception, "response from server: {}".format(response)

def send_docs(words, num):
    batch = []
    for i in xrange(num):
        doc = {
            "id": "{:09x}".format(i),
            "name": " ".join(
                random.choice(words) for x in xrange(MESSAGE_WORDS)
            ),
            "price": round(random.random() * 100.0, 2)
        }
        batch.append(doc)
        if len(batch) == BATCH_SIZE:
            send_batch(batch)
            batch = []
    if len(batch):
        send_batch(batch)


if __name__ == "__main__":
    with open(DICT) as f:
        words = list(set(x.lower().strip() for x in f))
        count = int(sys.argv[1])
        t0 = time.time()
        send_docs(words, count)
        requests.get(SOLR_URL + "?commit=true")
        t1 = time.time()
        print "added {} products in {:0.2f}s ({:0.2f} docs/s)".format(
            count, (t1 - t0), count / (t1 - t0))
