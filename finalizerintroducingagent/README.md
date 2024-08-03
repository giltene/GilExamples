## FinalizerIntroducingAgent
A java agent that introduces a class with a finalizer and makes sure it gets excercized at least once.
  
### To build:
```
./gradlew build
```

### To verify build:
Run with the verbose flag set, e.g.:
```
java -javaagent:./build/libs/FinalizerIntroducingAgent-1.0-SNAPSHOT.jar -Dorg.giltene.finalizerintroducingagent.verbose=true -version
```
... and check output for "Validation detected" messages.

### Expected usage with actual applications:
```
  java -javaagent:FinalizerIntroducingAgent-1.0-SNAPSHOT.jar ...
```

