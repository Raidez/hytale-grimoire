# Grimoire

## Preface

I don't really like Hytale's current magic system. 🔮
The idea that **an item holds a single spell** doesn't match my vision of magic, which is quite different.

I'm mostly drawing from my experience as a *D&D* player, where a wizard has a **known spell list**, but remains limited to the spells they prepare in advance, as well as to the available **spell slots**.

So I adapted this system for Hytale ✨, while keeping a bit of dynamism, replacing it with more classic mechanics like **mana cost** 💧 and **cast time** ⏳.

## Quick Start

It all starts by crafting the dedicated crafting table, the **lectern** 📖, used to prepare spells.

From this table, you can craft **grimoires** 📚 as well as **spells** 📜.

The table has *3 tiers*:
- **Tier 1**: level 1 to 2 spells, basic spells
- **Tier 2**: level 3 to 4 spells, intermediate spells
- **Tier 3**: level 5 spells, ultimate spells

## Preparing spells

You have an empty grimoire and several spells: it's time to prepare them for your next adventure. 🧙

Take the grimoire in hand and interact with the lectern 📖: the grimoire will be placed on the table and you can then **prepare your spells**.

By interacting with the table 🪄, a window will open, letting you place spells into the **12 available slots** of your grimoire.

A spell's level determines how many slots it takes up in the grimoire: a level 1 spell takes up *1 slot*, while a level 5 spell takes up *5*.

So you can only fit **2 spells** of level 5 in your grimoire, or **6 spells** of level 2, or even **12 spells** of level 1.

From the same window, you can click the button ✅ to retrieve your grimoire. That's it, you're ready to head out on your adventure! 🏹

## Casting a spell

When you're holding your grimoire, the sigil of the selected spell is displayed on it. You can switch spells by **holding right-click** on the grimoire, which opens a window letting you choose the spell you want to cast.

If the sigil isn't enough to remind you of the spell, a **quick right-click** shows its name. 💡

Finally, just hold **left-click** to cast the spell, based on its *cast time* and *mana cost*.

## More grimoires

The table also lets you craft grimoires with different appearances, making them easy to tell apart.

A red grimoire 🔴 for fire spells, or a green grimoire 🟢 for healing spells.

It's up to you to pick your colors and appearances, so your grimoire looks like you! 🎨

## Credits

Thanks to [@Hytale](https://twitter.com/Hytale) for the game, and to [@HytaleModding](https://hytalemodding.dev/en) for the modding community.


## Tweaks

### VS Code debugging and hot swap

For VS Code debugging, the Java project name must match the name shown in the
**Java Projects** view. In this project, that name is `Grimoire-hytale-grimoire`.
The Java attach configuration in `.vscode/launch.json` should therefore contain:

```json
{
	"type": "java",
	"name": "Hytale: runServer",
	"request": "attach",
	"hostName": "localhost",
	"port": 5005,
	"projectName": "Grimoire-hytale-grimoire",
	"preLaunchTask": "Hytale: runServer"
}
```

The Hytale Gradle plugin requires Java 25. VS Code can use a different JVM from
the one configured in the shell, so configure the JetBrains Runtime explicitly
in `.vscode/settings.json`:

```json
{
	"java.configuration.updateBuildConfiguration": "automatic",
	"java.import.gradle.enabled": true,
	"java.import.gradle.wrapper.enabled": true,
	"java.import.gradle.java.home": "/path/to/jbrsdk-25",
	"java.jdt.ls.java.home": "/path/to/jbrsdk-25",
	"java.compile.nullAnalysis.mode": "disabled",
	"java.debug.settings.hotCodeReplace": "auto"
}
```

Replace `/path/to/jbrsdk-25` with the local Java 25 installation path. Setting
both Java paths is important: one controls the Gradle import and the other
controls the Java Language Server. After changing them, run **Developer: Reload
Window**, then **Java: Clean Java Language Server Workspace** if necessary.

The IDE classpath also expects the generated Hytale assets binary. In
`gradle.properties`, enable it:

```properties
generateAssetsBinary = true
```

Then generate the development environment:

```bash
./gradlew setupHytaleDev
```

This creates `build/generated-ide-binaries/assets/hytale-assets.jar`. The file
is large and belongs to the generated `build/` directory, so it should not be
committed.

VS Code's Java Language Server compiles sources into the generated `bin/`
directory. It contains the `.class` files and copied resources used by JDT for
automatic compilation and hot swap, while `build/` contains Gradle's build
outputs. The `bin/` directory is already ignored by Git and should not be
committed or manually removed while VS Code is running; it can be regenerated
if deleted.

Once the project imports successfully, `java.debug.settings.hotCodeReplace` set
to `auto` recompiles compatible changes and sends them to the running server.
Standard hot swap supports changes inside existing method bodies. Adding or
removing methods or fields, changing signatures, and some structural changes
still require a server restart.

For Hytale's server watchdog, avoid leaving normal breakpoints active for long
periods because they suspend the server thread. Prefer VS Code **logpoints** to
inspect values without pausing execution. A logpoint can contain expressions,
for example:

```text
transaction={event.getTransaction()} itemId={is.getItemId()} spellList={spellList}
```

This is also useful for comparing state between events without risking a server
timeout. The `hytaleJvmDoctor` task can verify the JVM setup:

```bash
./gradlew hytaleJvmDoctor
```

It should report a JetBrains Runtime, Java 25, and enhanced class redefinition
support.

