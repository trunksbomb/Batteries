# Changelog
The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/)

## [Unreleased] - 2020-12-02
### Added
 - Open GUI on right-click
   - 9 inventory slots available that accept energy-capable items. Items will not actually be placed in the battery's inventory -- a ghost item will appear instead
   - If whitelisting is enabled for the battery, only items represented in its inventory will be charged. If blacklisting is enabled, the battery will charge all items except those represented in its inventory
   - 3 buttons to control where in your inventory items will be charged -- on the hotbar, worn armor, and items in the inventory (that aren't in the hotbar). Each option can be toggled individually and will show a green checkmark when enabled
   - The last button in the GUI toggles "Fair Charging", which is described in the 1.0.2 update in this changelog
 
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