# CETC Price Calculator

A simple, polished Android calculator for the unit price list of the Civil Engineering Tests and Consultations Unit (E-JUST), in Arabic (RTL), with a Wathkkir-style One UI interface, floating frosted controls, and solid neutral light and dark backgrounds.

## Features

- **Floating controls** — glass menu and settings buttons stay accessible while scrolling.
- **Quick navigation** — a frosted right-side drawer opens the concrete cube and grout calculators directly, alongside all tests and the price database.
- **Clear input outlines** — contrasting borders identify the searchable test picker and writable quantities, without dot markers.
- **Settings** — database tools and app information are grouped in a dedicated screen, with a back button on subpages.

- **Search-first picker** — a full-screen instant search replaces the long scroll list: type any part of a test name and matching entries appear immediately. Search is Arabic-aware: alef/yaa/taa-marbuta variants, diacritics and common suffix differences (خرسانة/خرسانية) all match, and multi-word queries match in any order.
- **Typed or stepped quantities** — every quantity is a tappable numeric field (Arabic-Indic or Latin digits) with optional +/− steppers.
- **Test details** — the selected test's full description plus its minimum-quantity requirements shown in a highlighted chip.
- **Instant pricing** — unit price, quantity input, live cost, automatic 14% VAT (always applied) and a clear grand total, shown as three equally prominent cards: cost before VAT, VAT, and total with VAT.
- **Combined sample + report pricing** — concrete cube, cylinder and grout crushing entries ask for two quantities at once (number of samples and number of reports) and price them together: samples × sample price + reports × report price.
- **Minimum-quantity guard** — a warning appears when the entered quantity is below the test's minimum.
- **Dynamic price database** — prices live in a versioned CSV file (`#version=2026`). Import a new CSV any time from inside the app, export the current database, or edit, add and delete individual items. Multi-part tests are grouped where it matters: cube / cylinder / grout crushing combine their sample price and report price into one entry with two quantity inputs, while auxiliary services (e.g. cylinder leveling) stay separate entries.
- **Database version label** — the active price-list version is always visible on the main screen and the database screen.

## Screenshots



| Home | Test picker | Calculation (with VAT) |
| --- | --- | --- |
| ![Home](docs/screenshots/01-home.png) | ![Test picker](docs/screenshots/02-test-picker.png) | ![Calculation with VAT](docs/screenshots/03-calculation-with-tax.png) |

| Minimum-quantity warning | Database manager | About |
| --- | --- | --- |
| ![Minimum quantity warning](docs/screenshots/05-min-quantity-warning.png) | ![Database manager](docs/screenshots/06-database-manager.png) | ![About](docs/screenshots/07-about.png) |

| Quick menu | Dark quick menu | Dark calculation |
| --- | --- | --- |
| ![Quick menu](docs/screenshots/08-drawer-light.png) | ![Dark quick menu](docs/screenshots/09-drawer-dark.png) | ![Dark calculation](docs/screenshots/10-calculation-dark.png) |

## Changelog

### v1.2.0

- Wathkkir-style floating glass menu and settings controls.
- Right-side drawer with concrete cube and grout calculator shortcuts.
- Solid neutral backgrounds in light and dark modes, with contrasting input outlines.
- Dedicated settings screen and version information that follows the installed app.
- Removed the third-party promotional credit.

### v1.1.0 (2026-11-04)

- Full redesign in the Morphe Manager design language: Material 3 expressive color tokens, large rounded cards, springy navigation, light + dark + system themes.
- Search-first test picker with instant Arabic-aware filtering (normalization + light stemming, multi-word any-order matching).
- Concrete cube / cylinder / grout crushing priced as one paired entry each: number of samples and number of reports in a single section.
- Quantities can be typed directly (Arabic-Indic or Latin digits) or adjusted with +/− steppers.
- Cost before VAT, 14% VAT and total now shown as three equally prominent cards.
- New app icon.

### v1.0.0

- Initial release: One UI look, 56 priced tests, paired sample+report pricing, dynamic CSV database, 14% VAT.

## Price data

The bundled database (`app/src/main/assets/prices_2026.csv`, version `2026`) mirrors the unit's published price list, update **April 2026**. All prices are in EGP and exclude VAT; a 14% VAT applies on the total when an electronic invoice is requested.

---

Developer: محمد النجار
