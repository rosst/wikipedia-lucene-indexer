# README #

A simple Java command line tool that uses Lucene to index Wikipedia data. Written for a code challenge back in 2014.

Prerequisites:

1. Java JDK installed
2. Maven 3 installed

To build the project run "mvn clean install" from the search-challenge directory of the project. 

This will create a software distribution that can be found in 
./target/index-wikipedia-dump-0.0.1-dist.zip.

Unzip the distribution.This software distribution provides functionality to index and query a dump of
wikipedia articles, found here:
http://download.wikimedia.org/enwiki/latest/enwiki-latest-pages-articles.xml.bz2

Some test data consisting of 99,468 lines of the dump can be found in:
data/enwiki-latest-pages-articles-99468.xml

Run the following script to index data: bin/index_wikipedia.sh

bin/index_wikipedia.sh --input data/enwiki-latest-pages-articles-99468.xml --output index

To query the created index use bin/query_wikipedia.sh:

bin/query_wikipedia.sh --index index --maxRes 20

Note that as it is only a small dump of data consisting of the first ~100k lines of wikipedia, articles beginning with the letter "a" are returned. The example CLI tool to query the index will search in both the title and body fields.

Try some queries:

* "Aberdeen"
* "Albert Einstein"
* "Berlin"
