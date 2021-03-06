# v6.X Changelog

## v6.0 - Minecraft 1.13
- v6.0.68 - "Block Statistics" config nodes for Pickup and Block Mine statistics, defaulting to false!
- v6.0.67 - more sapling block fixes - in some cases the bottom block would stay
- v6.0.66 - actually use the sapling blocks we calculated
- v6.0.65 - fix acacia bottom determination
- v6.0.64 - implement implementation method to check for log types and use where appropriate
- v6.0.63 - rewrite the bottom block logic by introducing a sapling block logic - this will help with "Destroy Only Blocks Above" finally doing what it should
- v6.0.62 - Update to an mcMMO version that is more suitable (1.5 => 2.1)
- v6.0.61 - Add bStats and a working command autocomplete system
- v6.0.60 - Custom tree definitions now are in a list of lists in the config, grouped together properly and saved together, in the file and in the API
- v6.0.58 - Finalize the custom tree system so it is operational for now.
- v6.0.57 - Implement addcustom and removecustom in accordance to the 1.13 API - everything is based on strings like minecraft:dirt, should work backwards compatibly with properly defined material values!
- v6.0.56 - Fix mushroom implementation 1.13 compatibility
- v6.0.55 - Fix plugin.yml typos
- v6.0.54 - Fixed a bug where items not explicitly in the tools list could be used to cut down a tree
- v6.0.53 - Please let's use the jenkins again
- v6.0.10 - Update bukkit version to 1.14 (still compatible with 1.13)
- v6.0.9 - Update integration with CoreProtect to use new API call
- v6.0.8 - Fixed a bug with felling trees growing next to short grass, or hoppers/dispensers in tree farms
- v6.0.7 - Fixed a bug where mushroom blocks dropped from felling mushroom trees with silk touch would not stack
- v6.0.6 - Fixed a bug with felling mushroom trees growing on grass
- v6.0.5 - Fixed a bug with felling trees growing on podzol
- v6.0.4 - Fixed a bug where red mushroom trees drop brown mushrooms
- v6.0.3 - Fixed a bug where felled trees drop the wrong sapling
- v6.0.2 - Fixed a bug where red mushroom trees aren't fully cut down
- v6.0.1 - Update integration with CoreProtect API
- v6.0.0 - Update TreeAssist to natively support 1.13
