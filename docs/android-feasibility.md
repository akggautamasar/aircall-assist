# Android feasibility notes

## Product boundary

AirCall Assist speaks messages to the local user while an external call continues in another application. It does not attempt to inject generated audio into the third-party call stream.

## Questions to validate on physical devices

- Background push delivery while another app is foreground.
- Text-to-Speech behavior while a third-party call is active.
- Audio focus interactions with phone/VoIP calls.
- Bluetooth routing to earbuds/headsets during calls.
- Reliable detection of active calls using supported Android APIs.
- OEM battery optimization, especially Samsung and Pixel devices.
- Manual Call Assist fallback when automatic call-state detection is unavailable.

## Rule

Any behavior that depends on OEM or third-party call-app implementation must be treated as device-tested capability, not as a universal Android guarantee.
