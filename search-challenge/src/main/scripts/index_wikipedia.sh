#!/bin/sh

if [ -d "lib" ]; then
  
  java -Xms1024m -Xmx1024m -cp lib/'*' org.ross.turner.search.IndexerCLI $1 $2 $3 $4

else

  java -Xms1024m -Xmx1024m -cp ../lib/'*' org.ross.turner.search.IndexerCLI $1 $2 $3 $4 
  
fi 

