# MoreCrashInfo
Display more info in crash report.  
## Why make it?
Forge 1.13.2 - 1.14.3 removed mod list and coremod list in crash report, while users need them to analyze what caused crash.  
Forge 1.14.4 added mod list back, but it is not formatted. It's hard to read when plenty of mods loaded. Meanwhile, coremods list is still not shown.  
Mod list added in Forge 1.14.4 crash report can be shown below:
```
Mod List: 
	CustomSkinLoader_Forge-14.11-SNAPSHOT-89.jar CustomSkinLoader {customskinloader@14.11-SNAPSHOT-89 DONE}
	MoreCrashInfo-1.0.2.jar MoreCrashInfo {morecrashinfo@1.0.2 DONE}
	forge-1.14.4-28.0.23-universal.jar Forge {forge@28.0.23 DONE}
```
## What added?
This mod is designed to take mod list and coremod list back with pretty printing.  
Feel free to open an issue if other info needed.  
What added in crash report is shown below:
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
