# MoreCrashInfo
Display more info in crash report.  
## Why make it?
Forge 1.13.2 - 1.14.3 removed mod list and coremod list in crash report, while users need them to analyze what caused crash.  
Forge 1.14.4 added mod list back, but it is not formatted. It's hard to read when plenty of mods loaded. Meanwhile, coremods list is still not shown.  
Mod list added in Forge 1.14.4 crash report:
```
Mod List: 
	CustomSkinLoader_Forge-14.11-SNAPSHOT-89.jar CustomSkinLoader {customskinloader@14.11-SNAPSHOT-89 DONE}
	MoreCrashInfo-1.0.2.jar MoreCrashInfo {morecrashinfo@1.0.2 DONE}
	forge-1.14.4-28.0.23-universal.jar Forge {forge@28.0.23 DONE}
```
## What added?
This mod is designed to take mod list and coremod list back with pretty printing.  
Feel free to open an issue if other info needed.  
What added in crash report:
```
Forge Mods: 
	| ID               | Name             | Version           | Source                                       | Status | 
	| ---------------- | ---------------- | ----------------- | -------------------------------------------- | ------ | 
	| minecraft        | Minecraft        | 1.14.4            | Not Found                                    | NONE   | 
	| customskinloader | CustomSkinLoader | 14.11-SNAPSHOT-89 | CustomSkinLoader_Forge-14.11-SNAPSHOT-89.jar | DONE   | 
	| morecrashinfo    | MoreCrashInfo    | 1.0.2             | MoreCrashInfo-1.0.2.jar                      | DONE   | 
	| forge            | Forge            | 28.0.23           | forge-1.14.4-28.0.23-universal.jar           | DONE   | 
Forge CoreMods: 
	| ID               | Name                      | Source                       | Status | 
	| ---------------- | ------------------------- | ---------------------------- | ------ | 
	| customskinloader | transformers              | transformers.js              | Loaded | 
	| forge            | fieldtomethodtransformers | fieldtomethodtransformers.js | Loaded | 
```
## What's more?
We are trying to analyze some crash automaticly.  
Now, we can help you to solve `java.lang.VerifyError`.  
You can open an issue to submit crash report, I will try my best to find the way to solve.  
What added for some crash:
```
Possible Reason:
	Bytecode in class 'me.xfl03.crashmaker.CrashMaker' failed to verify. 
	CoreMod 'MoreCrashInfo' modified that class, which may cause crash.
Possible Solution:
	Please remove or update 'MoreCrashInfo(MoreCrashInfo-1.0.3.jar)' and try again.

Error Info:
	Class: me.xfl03.crashmaker.CrashMaker
	Owner: MoreCrashInfo
	Audit: xf:fml:morecrashinfo:CrashMakerTransformer
```

## Compatibility
- Minecraft 1.15+ with Forge
- Java 8+

In addition, this mod can be loaded in 1.13.2-1.14.4, but not work. We are trying to find out the reason.

### Tested Environment
- Minecraft 1.16.5, Forge 36.2.39 and Java 8
- Minecraft 1.19.3, Forge 44.1.2 and Java 17

## Develop
This mod implemented a powerful ASM-based remapping and custom jar detection feature to support cross-version Minecraft and Forge.

Please use Java 17+ in development or building environment.

### Build
```shell
./gradlew build
```
Mod file is located at `LocatorLoader/build/libs`.