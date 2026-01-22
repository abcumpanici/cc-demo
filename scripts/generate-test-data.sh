#!/bin/bash

ROWS=1000
OUTPUT="test.csv"

while [[ $# -gt 0 ]]; do
    case $1 in
        --rows)
            ROWS="$2"
            shift 2
            ;;
        --output)
            OUTPUT="$2"
            shift 2
            ;;
        *)
            echo "Usage: $0 --rows <number> --output <filename>"
            exit 1
            ;;
    esac
done

echo "id,date,amount" > "$OUTPUT"
for i in $(seq 1 $ROWS); do
    DATE=$(date -v-${i}d +%Y%m%d 2>/dev/null || date -d "-${i} days" +%Y%m%d)
    AMOUNT=$(echo "scale=2; ($RANDOM % 10000) / 100" | bc)
    echo "$i,$DATE,$AMOUNT" >> "$OUTPUT"
done

echo "Generated $ROWS rows in $OUTPUT"
