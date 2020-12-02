# Changelog
The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/)

## [1.0.2] - 2020-12-03
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