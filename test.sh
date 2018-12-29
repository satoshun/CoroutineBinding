#!/bin/sh

for i in "coroutinebinding" "coroutinebinding-appcompat" "coroutinebinding-constraintlayout" "coroutinebinding-core" "coroutinebinding-drawerlayout" "coroutinebinding-material" "coroutinebinding-recyclerview" "coroutinebinding-slidingpanelayout" "coroutinebinding-swiperefreshlayout" "coroutinebinding-viewpager";
do
    ./gradlew ${i}:connectedDebugAndroidTest
done