# All The Trims
### Literally All Of Them

<img width="1770" alt="Screenshot 2023-06-16 at 1 07 23 PM" src="https://github.com/Benjamin-Norton/AllTheTrims/assets/18416784/7136a098-2701-4520-b0d6-e1c93e2f16bb">

[![Modrinth](https://img.shields.io/modrinth/dt/allthetrims?color=00AF5C&label=downloads&logo=modrinth)](https://modrinth.com/mod/allthetrims)
[![CurseForge](https://cf.way2muchnoise.eu/full_876154_downloads.svg)](https://curseforge.com/minecraft/mc-mods/all-the-trims)

## Features:
- Allows any item, including modded, to be an armour trim material.
- Makes all armour trimmable, including modded armour.
- Supports custom trim templates from datapacks or mods.
- Dynamic Trim Rendering:
  - All trims are rendered dynamically, so the texture atlas is not filled with trim textures.
  - Colour of the trim is based on a palette gradient generated from the most vibrant to the dullest colour of the item texture.
- No additional files or configuration required, just drop the mod in your mods' folder.

### Notes:
- This adds over 500,000 new armour trim combinations to just the vanilla game, with a couple of armour mods and content mods this number jumps to over a million with minimal effect on load times.

#### For Mod Developers:
- This mod uses a Dynamic Trim Renderer that handles how the custom trim palettes are rendered on the armour, any mod that uses Fabric's Armour Renderer API to render trims will not work with this mod. See the [Wiki](https://github.com/Benjamin-Norton/AllTheTrims/wiki/Dynamic-Trim-Rendering) for more information and how to add support with this mod.

### Credits:
- [Andrew6rant](https://github.com/Andrew6rant) for the item model colouring.
- [KikuGie](https://github.com/kikugie) for maintaining elytra trim compatibility.
