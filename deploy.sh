#!/bin/bash
IP=$1
#if [[ $IP == "" ]]; then
#    IP="192.168.10.116"
#fi
if [[ ! -d deploy ]]; then
    mkdir deploy
fi
cp build/libs/*.jar deploy/
cp config.properties deploy/
cp run deploy/
rsync -iaL --exclude=resources/private/ resources/ deploy/resources/
rsync -ia --delete lib/ deploy/lib/
#rsync -a --info=progress2 --delete deploy/ jetson@$IP:Projects/survey-ai-run/