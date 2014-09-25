#!/bin/sh

if [ -d "lib" ]; then
  
  java -Xms2048m -Xmx2048m -cp lib/'*' org.ross.turner.search.IndexerCLI $1 $2 $3 $4

else

  java -Xms2048m -Xmx2048m -cp ../lib/'*' org.ross.turner.search.IndexerCLI $1 $2 $3 $4 
  
fi 

