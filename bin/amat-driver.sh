#!/bin/sh
########################################################################
# Usage: amat-driver.sh FILE1 [FILE2 ...]
########################################################################

if [ -z "${JAM_HOME}" ]
then
    echo "Environment variable JAM_HOME is not set; exiting."
    exit 1
fi

SCRIPT=`basename $0`
JAMDIR=$(cd `dirname $0`/../../jam; pwd)
JAMRUN=${JAM_HOME}/bin/jam-run.sh

if [ $# -lt 1 ]
then
    echo "Usage: $SCRIPT FILE1 [FILE2 ...]"
    exit 1
fi

$JAMRUN ${AMAT_HOME} amat.driver.AmatDriver "$@"
