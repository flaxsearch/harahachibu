# harahachibu
An HTTP proxy for Elasticsearch, Solr (etc.) which allows configurable endpoints to be refused (with a configurable status) depending on external conditions. The primary purpose of this is to prevent documents being added when free disk space falls below a certain level, and thus avoid running out of disk space entirely (which can lead to data loss and be a pain to fix).

It is named after the [Confucian teaching](https://en.wikipedia.org/wiki/Hara_hachi_bun_me) that one should only eat until one is 80% full.
