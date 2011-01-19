#!/bin/bash

cd data

for file in $(ls . | grep -v "\.s$")
do
	# Generate our output
	java -cp ../bin Asgn2 "${file}" > "${file}.s.ours"

	# Diff the two
	diff "${file}.s.ours" "${file}.s"

	# Clean up
	rm "${file}.s.ours"
done
