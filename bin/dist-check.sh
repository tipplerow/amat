#!/bin/sh
########################################################################
# Usage: dist-check.sh FILE DISTANCE
########################################################################

SCRIPT=`basename $0`
JAMDIR=$(cd `dirname $0`/../../jam; pwd)
JAMRUN=${JAMDIR}/bin/jam-run.sh

if [ $# -lt 1 ]
then
    echo "Usage: $SCRIPT FILE DISTANCE"
    exit 1
fi

$JAMRUN amat amat.driver.DistCheck "$@"
