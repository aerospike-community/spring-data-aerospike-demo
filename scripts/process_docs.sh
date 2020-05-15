#!/bin/bash

# Github does not support include directives in asciidoc files, so we need to process them manually :(
# This workaround can be removed as soon as https://github.com/github/markup/issues/1095 is fixed

source_dir=../docs
output_dir=../docs_processed

# coloring bash output: https://habr.com/ru/post/119436/
RED='\033[0;31m'         #  ${RED}
GREEN='\033[0;32m'      #  ${GREEN}
NORMAL='\033[0m'      #  ${NORMAL}

mkdir -p $output_dir

for file in ${source_dir}/*.adoc; do
  filename=${file##*/}
  echo "Processing $file"
  ruby asciidoc-coalescer.rb $file -o $output_dir/"$filename" 2>>/tmp/Error
done

result=$(</tmp/Error)
if [ -z "$result" ]
	then
		echo "$GREEN Processing documentation finished with SUCCESS $NORMAL"
	else
	  rm /tmp/Error
		echo "$RED $result"
		echo "$RED Processing documentation finished with FAILURE $NORMAL"
		exit 1
fi

exit