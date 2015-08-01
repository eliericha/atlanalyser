#!/bin/bash

if [[ -f atlanalyser.out ]]; then
  read -p "Do you want to override existing results? " yn
  case $yn in
    [Yy]* ) ;;
    [Nn]* ) echo "Ok, will not update"; exit;;
    * ) echo "Please answer yes or no"; exit;;
  esac
fi

echo "Updating..."
scp extlame10:/infres/s3/richa/atlanalyser/fr.tpt.atlanalyser.examples/atlanalyser.log atlanalyser.out
grep MemMon atlanalyser.out| awk '{split($10,a,"GB"); print $1 "\t" a[1]}' > mem.csv
grep 'queue size' atlanalyser.out| awk '{print $1 "\t" $10}' > queue.csv
