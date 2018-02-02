#!/bin/sh
AGCOUNT=3
FILEBASE=$(printf "vaccine_bolus_N%02d" $AGCOUNT)
CONCLIST="0.01 0.02 0.05 0.1 0.2 0.5 1.0 2.0 5.0 10.0 20.0 50.0 100.0"

for TotalConc in $CONCLIST
do
    EqualConc=$(echo "scale=4; $TotalConc / $AGCOUNT" | bc)
    FileName=$(printf "%s_C%s" $FILEBASE $TotalConc)
    printf "# Single vaccination event, three epitopes at equal concentration\n" > $FileName
    printf "0: E1, E2, E3; %s\n" $EqualConc >> $FileName
done

exit 0
