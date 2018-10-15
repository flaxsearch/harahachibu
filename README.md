# Hara hachi bu (腹八分)

An HTTP proxy for Elasticsearch, Solr (etc.) which allows configurable endpoints to be refused (with a configurable status) depending on external conditions. The primary purpose of this is to prevent documents being added when free disk space falls below a certain level, and thus avoid running out of disk space entirely (which can lead to data loss and be a pain to fix).

It is named after the [Confucian teaching](https://en.wikipedia.org/wiki/Hara_hachi_bun_me) that one should only eat until one is 80% full.


## Building and running the application

To build the application, use maven:

    mvn clean package
    
This will create a runnable jar file in the harahachibu/target folder. This can then be run from the command line
using:

    cd harahachibu
    java -jar target/harahachibu-1.0-SNAPSHOT.jar server config/proxy.yml
    
You can then use the server as an proxy in front of Solr or Elasticsearch to check for disk space when adding or
updating new documents.
    

## Configuration

The `harahachibu/config/proxy.yml` file contains some configuration for the application. This can be broken into
the following sections:

- `server` - contains details of the DropWizard server configuration. Further details for this can be found in
the [DropWizard manual](http://www.dropwizard.io/0.9.2/docs/manual/configuration.html).

- `logging` - as above, contains logging configuration. Once again, full details can be found in the
[DropWizard manual](http://www.dropwizard.io/0.9.2/docs/manual/configuration.html).

- `proxy` - contains configuration for the URLs to be proxied:
	- `errorStatus` is the HTTP error code that should be returned when the server cannot be updated because
    the disks have been detected to be full [Default: 500].
    - `checkUrls` should be a list of URLs which trigger a disk space check. The URLs are regular expressions, so
    may use full regular expression syntax. The URL may be prefixed by a method (ie. POST or PUT) - this
    is **not** a regular expression. If using a method, you should add one line per method to check for. Without
    a method, all calls to matching URLs will trigger a space check.
    - `destinationServer` - the prefix for the destination server when passing the URLs. For example,
    `http://localhost:8983` or `http://localhost:9200`. HTTPS is not currently supported.
    
- `diskSpace` - contains configuration for the disk space checker:
	- `checkerType` indicates the type of checker to run. This can be `elasticsearch`, `solr`, `cluster`
	or `solrmetrics`, to
	check Elasticsearch (using the `/cluster/_stats` endpoint), a local Solr instance, a clustered Solr instance
	(more details below), or a Solr cluster via the Metrics API.
	- `threshold` indicates the minimum space that should be available on the file system in order for a request to
	pass. This may be given as a percentage, or using K, M or G as suffixes for kilobytes, megabytes or gigabytes
	respectively - ie. `5%`, `4G`.
	- `configuration` - this is a map of further configuration required for the checkers. Further details below.
	 

### Elasticsearch configuration

To configure the Elasticsearch disk space checker, the `configuration` map needs two properties:

- `baseURL` - the base URL for the ElasticSearch server (eg. `http://localhost:9200`). When checking, this will be
used as the root of the cluster statistics URL (eg. `http://localhost:9200/cluster/_stats`).
- `cacheMs` - the time, in milliseconds, to cache the Elasticsearch cluster results. The call to the cluster
stats URL may slow down your requests, so caching the data will reduce the number of times the request is made.


### Local Solr configuration

To configure the local Solr disk space checker, the `configuration` map needs to be given the path to the data
directory using the following property:

- `dataDirectory` - (eg. `/data/solr/myDocuments`). This will be used to check the local filesystem for free space.


### Solr Metrics configuration

To configure the Solr Metrics configuration, the `configuration` map
needs to be given the following properties:

- `solrUrl` - the base URL for the Solr instance (eg. `http://localhost:8983`).
When checking, this will be used as the root of the metrics API URL.
- `collection` - the collection whose node file systems should be
checked.

This configuration will only work with Solr instances where the metrics
API is available - eg. Solr 7.x forwards.

The URL built will use filter parameters to keep the response as small
as possible. (For those interested, the URL looks something like
`http://localhost:8983/solr/admin/metrics?prefix=CORE.fs&prefix=CORE.collection&prefix=CORE.coreName`)


### Clustered Solr configuration

When using a clustered Solr configuration, each machine in the cluster will be expected to regularly send its current
free disk space to the application. This should be done using the `/setSpace` endpoint, like so:

    http://localhost:8080/setSpace/[hostname]/[freeSpace]/[totalSpace]
    
For example:

    http://localhost:8080/setSpace/192.168.0.1/122665120000/206289466000
    
The request should be a POST, and the free and total space values should be given in bytes.

The servers expected to be sending their space settings should be set in the `configuration` map using the
`clusterServers` setting, like so:

    clusterServers: [ 192.168.0.1, 192.168.0.2 ]
    
or

    clusterServers:
      - 192.168.0.1
      - 192.168.0.2

Note that these do not have to be actual server names - they correspond to the
server names sent to the `/setSpace` endpoint
([see below](#setting-free-space-for-a-cluster)). Any server names sent to
`/setSpace` that aren't in the `clusterServers` list will be rejected.

If the disk checker does not have space details for **all** of the servers in 
the cluster, it will automatically return a fail status.


#### Setting free space for a cluster

There is an example Python script in `scripts/clusterSetSpace.py`, which can be used as a base. The script should
be added to your cron list (or Windows equivalent), and set to send frequently (I would suggest every minute).

The endpoint it calls on the application is /setSpace. This will return a JSON-structured body like so:

```
% curl -XPOST http://localhost:8080/setSpace/localhost/122692435968/206289465344
{"status":"OK","message":null}
```

The `status` property may be either "OK" or "ERROR". In the case it returns "ERROR", the message will be populated
with more details.

**Note:** there are no authentication checks on `/setSpace` at this time.
We're expecting the app to be running behind a firewall or similar security
measure - after all, you don't want your search engine open to any passerby,
do you? :-)


## Using a custom disk space checker

You can also implement your own disk space checker. This will need to
implement the `uk.co.flax.harahachibu.services.DiskSpaceChecker`
interface, and should have a no-argument constructor. To specify your
own disk space checker, set the `diskSpace/checkerType` property in
the configuration to your own class name:

    diskSpace:
      checkerType: my.spacechecker.CustomDiskSpaceChecker
      
If your checker will require an HTTP client, the `requiresHttpClient()` method
should return `true` - the client will be set using the `setHttpClient()`
method at initialisation time (the client is created and managed by 
DropWizard, giving you one less thing to worry about).
Configuration will be passed in using the
`configure()` method, and the DiskSpaceThreshold parsed from the configuration
file will be passed using `setThreshold()`.
