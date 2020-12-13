# Changelog
The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/)

## [1.3.0] - 2020-12-13
### Added
 - New Ender Battery Uplink Block and Ender Battery. Ender Battery has the same capacity as the Ultimate Battery, but can be used on an Ender Battery Uplink to link the two together. Any energy received by the Uplink will be sent to the linked Ender Battery, if they're both loaded.
## [1.2.2] - 2020-12-09
### Fixed
 - Fixed missing translation string for Charger block
## [1.2.1] - 2020-12-09
### Fixed
 - Added missing drop table to Charger so it will drop itself and any battery in it
 - Added missing recipe for Charger
## [1.2.0] - 2020-12-09
### Added
 - New Charger block that accepts all tiers of batteries from this mod (none from other mods) on right-click. A Charger with a Battery in it will accept energy from other mods' pipes/cables/generators/etc to charge the Battery, and can also be used to output energy from the Battery to other mods pipes/cables/machines. Can also be charged by a battery in the player's inventory with the "Charge nearby machines" option enabled.
 - Config option "Creative Chargers" allows Chargers to generate energy out of thin air to charge contained Batteries.
### Changed
 - Option "Charge nearby machines" now respects option "Fair Charging" on the Battery, won't charge a machine that has more energy stored in it than the Battery.
## [1.1.0] - 2020-12-07
### Added
 - New feature "Charge nearby machines" toggled in Battery GUI to charge energy-capable machines in a radius around the user. Respects sidedness of the machine, ie if the machine only takes energy from the front, you'll need to stand in front of it to charge it. Spawns particles to let you know it's working.
### Changed
 - Battery GUI now shows armor slots for easy access to chargeable armor
 - Added lightning bolts to Battery inventory slots to visually indicate that you should put chargeable items in it
 - Batteries now use abbreviated FE units in their tooltip (10,000 FE = 10 kFE, etc)
## [1.0.4] - 2020-12-06
### Changed
 - Battery whitelist/blacklist now operates on the item's registry name instead of its class. This allows the battery to selectively charge items from mods that add multiple items that share the same base class.
## [1.0.3] - 2020-12-03
### Added
 - Open GUI on right-click
   - 9 inventory slots available that accept energy-capable items. Items will not actually be placed in the battery's inventory -- a ghost item will appear instead
   - If whitelisting is enabled for the battery, only items represented in its inventory will be charged. If blacklisting is enabled, the battery will charge all items except those represented in its inventory
   - 3 buttons to control where in your inventory items will be charged -- on the hotbar, worn armor, and items in the inventory (that aren't in the hotbar). Each option can be toggled individually and will show a green checkmark when enabled
   - The last button in the GUI toggles "Fair Charging", which is described in the 1.0.2 update in this changelog
 - Implemented behavior from the GUI buttons, so the battery will only charge the specified slots
 - Added test_charging config option. When enabled, right-clicking on a diamond block while holding a battery will begin charging it.
### Changed
 - Inventory slots are now "ghost slots" and only hold a reference to the item placed in the battery.
 
## [1.0.2] - 2020-12-02
### Changed
 - Batteries will not charge items that have more energy in them than the battery's current capacity. For example, a battery with 300kFE will not charge a tool that currently has 350kFE in it. This is intended to promote "fairness" for chargeable items with large energy capacity differences (ie a 12MFE jetpack won't eat up all the FE in a 1MFE battery, so the battery will still be able to charge your 400kFE tool). This is configurable in batteries.toml configuration file and will be toggleable on a per-battery basis in-game in the future.
## [1.0.1] - 2020-12-02
### Fixed
 - Batteries would always extract their max transfer rate, regardless of what the receiving item needed (even if it needed 0 energy).

## [1.0.0] - 2020-12-02
### Added
 - Mod Created
 - Added 4 tiers of battery - Basic, Advanced, Elite, and Ultimate as well as a Creative Battery.
 - Added an Example Energy Receiver Item for testing - can only be cheated in, and is only able to receive power.
 - Batteries can be toggled on/off by holding one in your hand and shift+right-clicking.