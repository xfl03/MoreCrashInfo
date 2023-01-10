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

From Forge 1.15.2, mods can use Mixin to operate class file, which could cause crash, but it's not shown in crash report.
## What added?
This mod is designed to show mod list, coremod list and mixin list in crash report with pretty printing.  
Feel free to open an issue if other info needed.  
What added in crash report:
```
Forge Mods: 
	| ID               | Name             | Version           | Source                                | Status | 
	| ---------------- | ---------------- | ----------------- | ------------------------------------- | ------ | 
	| minecraft        | Minecraft        | 1.19.3            | client-1.19.3-20221207.122022-srg.jar | DONE   | 
	| morecrashinfo    | MoreCrashInfo    | 2.2.0             | MoreCrashInfo-Core.jar                | DONE   | 
	| forge            | Forge            | 44.1.2            | forge-1.19.3-44.1.2-universal.jar     | DONE   | 
	| rubidium         | Rubidium         | 0.6.3             | rubidium-0.6.3.jar                    | DONE   | 
Forge CoreMods:
	| ID    | Name                | Source                 | Status | 
	| ----- | ------------------- | ---------------------- | ------ | 
	| forge | field_to_method     | field_to_method.js     | Loaded | 
	| forge | field_to_instanceof | field_to_instanceof.js | Loaded | 
	| forge | add_bouncer_method  | add_bouncer_method.js  | Loaded | 
Mixin Configs:
	| Name                 | Mixin Package                    | Priority | Required | Targets | 
	| -------------------- | -------------------------------- | -------- | -------- | ------- | 
	| rubidium.mixins.json | me.jellysquid.mods.sodium.mixin. | 1000     | true     | 41      | 
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
- Minecraft 1.13.2~1.16.5 & 1.18~1.19.3 with Forge
- Java 8~19

### Tested Environment

| Minecraft |  Forge   | Java |
|:---------:|:--------:|:----:|
|  1.19.3   |  44.1.2  |  17  |
|  1.18.2   |  40.2.0  |  17  |
|  1.16.5   | 36.2.39  |  8   |
|  1.15.2   | 31.2.57  |  8   |
|  1.14.4   | 28.2.26  |  8   |
|  1.13.2   | 25.0.223 |  8   |

## Develop
This mod implemented a powerful ASM-based remapping and custom jar detection feature to support cross-version Minecraft and Forge.

Please use Java 17+ in development or building environment.

### Build
```shell
./gradlew build
```
Mod file is located at `LocatorLoader/build/libs`.