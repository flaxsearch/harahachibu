#!/usr/bin/env python

import os
import requests
import sys

# Name of this server, sent to the /setSpace endpoint
LOCAL_SERVERNAME = 'localhost'
# Directory holding the data store, used to get free space
DATA_DIRECTORY = '/home/mlp/data'

# Destination URL for the /setSpace endpoint
SET_SPACE_URL = 'http://localhost:8080/setSpace'

# Find the file system stats
statvfs = os.statvfs(DATA_DIRECTORY)
free_space = statvfs.f_frsize * statvfs.f_bavail
total_space = statvfs.f_frsize * statvfs.f_blocks

request_url = '%s/%s/%d/%d' % (SET_SPACE_URL, LOCAL_SERVERNAME, free_space, total_space)
response = requests.post(request_url)

if response.status_code != 200:
    print('clusterSetSpace ERROR: unexpected response code from server - %d' % response.status_code)
elif response.json()['status'] != 'OK':
    print('clusterSetSpace ERROR: non-OK response from server - %s' % response.json()['message'])
