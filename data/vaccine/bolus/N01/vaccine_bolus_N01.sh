#!/bin/sh
FILEBASE=vaccine_bolus_N01
CONCLIST="0.01 0.02 0.05 0.1 0.2 0.5 1.0 2.0 5.0 10.0 20.0 50.0 100.0"

for Conc in $CONCLIST
do
    FileName=$(printf "%s_C%s" $FILEBASE $Conc)
    printf "# Single vaccination event, single epitope\n" > $FileName
    printf "0: E1, %s\n" $Conc >> $FileName
done

exit 0
