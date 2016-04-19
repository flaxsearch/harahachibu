#!/usr/bin/env python

import sys
import time
import json
import random
import requests


#SOLR_URL = "http://localhost:8983/solr/techproducts/select"
SOLR_URL = "http://localhost:8080/solr/techproducts/select"
DICT = "/usr/share/dict/words"


def run_searches(words, terms, count):
    total_found = 0
    qtimes = []
    for i in xrange(count):
        queryterms = []
        for t in xrange(terms):
            queryterms.append(random.choice(words))

        t0 = time.time()
        response = requests.get(SOLR_URL, params={
            "q": " ".join(queryterms),
            "q.op": "OR",
            "rows": 20,
            "wt": "json"
        })
        t1 = time.time()

        qtimes.append(t1 - t0)
        if i % 1000 == 0:
            print i

        assert response.status_code == 200
        total_found += response.json()['response']['numFound']

    return total_found, sum(qtimes) / len(qtimes)

def main():
    with open(DICT) as f:
        words = list(set(x.lower().strip() for x in f))
        count = int(sys.argv[2])
        terms = int(sys.argv[1])
        t0 = time.time()
        total_found, meanqtime = run_searches(words, terms, count)
        t1 = time.time()
        print "{} hits from {} queries in {:0.2f}s ({:0.2f} q/s)".format(
            total_found, count, (t1 - t0), count / (t1 - t0))
        print "mean query time {:0.2f}ms".format(1000 * meanqtime)

if __name__ == "__main__":
    main()
