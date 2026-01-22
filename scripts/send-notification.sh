#!/bin/bash

FILENAME=""

while [[ $# -gt 0 ]]; do
    case $1 in
        --filename)
            FILENAME="$2"
            shift 2
            ;;
        *)
            echo "Usage: $0 --filename <filename>"
            exit 1
            ;;
    esac
done

if [ -z "$FILENAME" ]; then
    echo "Error: --filename is required"
    echo "Usage: $0 --filename <filename>"
    exit 1
fi

TIMESTAMP=$(date -u +%Y-%m-%dT%H:%M:%SZ)
MESSAGE="{\"filename\":\"$FILENAME\",\"timestamp\":\"$TIMESTAMP\"}"

echo "Sending notification: $MESSAGE"

docker exec -i kafka kafka-console-producer \
    --bootstrap-server localhost:9092 \
    --topic file-notifications \
    --property "parse.key=false" \
    --property "value.serializer=org.apache.kafka.common.serialization.StringSerializer" \
    <<< "$MESSAGE"

echo "Notification sent for file: $FILENAME"
