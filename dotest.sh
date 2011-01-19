#!/bin/bash

javac -d bin src/*.java

if type -P colordiff &>/dev/null
then
	DIFF=colordiff
else
	DIFF=diff
fi

if [ ! -d "out" ]
then
	mkdir out
fi

for i in 1 2 3 4 5 7 8 9 10 11 m
do
	file="test${i}"

	echo "===== TESTING FILE: ${file} ====="

	# Generate our output
	java -cp bin Asgn2 "data/${file}" | sed 's/Reading file data\//Reading file /' > "out/${file}.s"

	# Diff the two
	colordiff -u "out/${file}.s" "data/${file}.s"
done

#rm -rf out
