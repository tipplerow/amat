#!/bin/sh
########################################################################
# Usage: mut-dist.sh FILE1 [FILE2 ...]
########################################################################

SCRIPT=`basename $0`
JAMDIR=$(cd `dirname $0`/../../jam; pwd)
JAMRUN=${JAMDIR}/bin/jam-run.sh

if [ $# -lt 1 ]
then
    echo "Usage: $SCRIPT FILE1 [FILE2 ...]"
    exit 1
fi

$JAMRUN amat amat.driver.MutDist "$@"
