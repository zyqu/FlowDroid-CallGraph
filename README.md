# FlowDroid-CallGraph
Dump the call graph by the static analysis of FlowDroid [https://github.com/secure-software-engineering/soot-infoflow-android/wiki]

The output is organized as Map(Node, Set(Node)), where key is the source node and value is the set of neighbors 

Generate the jar file from eclipse and execute:

  >>java -jar static-cfg.jar apk-file-path android-sdk-platforms-path

The call graph is dumped in json in the file static-cfg-[apk-file-name]

Make sure you have the AndroidCallbacks.txt and SourcesAndSinks.txt in the working directory. 

Dependencies:

Soot bundle (also contains Heros and Jasmin): http://soot-build.cs.uni-paderborn.de/nightly/soot/soot-trunk.jar

soot-infoflow: https://github.com/secure-software-engineering/soot-infoflow/releases/download/FlowDroid_1.5/soot-infoflow.jar

soot-infoflow-android: https://github.com/secure-software-engineering/soot-infoflow-android/releases/download/FlowDroid_1.5/soot-infoflow-android.jar

Libraries for Logging: https://github.com/secure-software-engineering/soot-infoflow-android/raw/develop/lib/slf4j-api-1.7.5.jar AND https://github.com/secure-software-engineering/soot-infoflow-android/raw/develop/lib/slf4j-simple-1.7.5.jar

Android XML parser library: https://github.com/secure-software-engineering/soot-infoflow-android/raw/develop/lib/axml-2.0.jar

Google GSON: https://github.com/google/gson
