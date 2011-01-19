#!/bin/bash

if [ ! -d "out" ]
then
	mkdir out
fi

for file in $(ls data | grep -v "\.s$")
do
	echo "===== TESTING FILE: ${file} ====="

	# Generate our output
	java -cp bin Asgn2 "data/${file}" > "out/${file}.s"

	# Diff the two
	colordiff -u "out/${file}.s" "data/${file}.s"
done

#rm -rf out
