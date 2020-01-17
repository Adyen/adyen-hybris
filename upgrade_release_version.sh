#!/bin/bash

function usage() {
	echo "  -h help"
	echo "  -o <oldversion>"
	echo "  -n <newversion>"
}

function replace() {

declare -a fileList=(
"adyenv6b2ccheckoutaddon/resources/adyenv6b2ccheckoutaddon.build.number"
"adyenv6backoffice/resources/adyenv6backoffice.build.number"
"adyenv6core/resources/adyenv6core.build.number"
"adyenv6fulfilmentprocess/resources/adyenv6fulfilmentprocess.build.number"
"adyenv6ordermanagement/resources/adyenv6ordermanagement.build.number"
"adyenv6notification/resources/adyenv6notification.build.number"
"adyenv6core/src/com/adyen/v6/constants/Adyenv6coreConstants.java"
)

for FILENAME in "${fileList[@]}"

do
  XMLTMPFILE=$FILENAME.bak
  echo "Updating file - $FILENAME"
  cat $FILENAME > $XMLTMPFILE
  sed -i.bak s/${OLDVERSION}/${NEWVERSION}/g $FILENAME
  rm -f $XMLTMPFILE
done
}

while getopts "ho:n:" o; do
    case $o in
        h)
           usage
            ;;
        o)
          OLDVERSION=${OPTARG};;

         n)
          NEWVERSION=${OPTARG};;
    esac
done

echo "Old release version is" $OLDVERSION
echo "New release version is" $NEWVERSION

if [ -z $OLDVERSION ]; then
    echo "Please specify the 'oldversion' argument";
    usage;
    exit 1;
fi

if [ -z $NEWVERSION ]; then
    echo "Please specify the 'newversion' argument";
    usage;
    exit 1;
fi

replace

echo "Finished  ${FILENAME}"
exit $RT
