# AirCall Assist

AirCall Assist is an Android messaging companion designed for hands-free communication while the user is participating in a call handled by another application.

## MVP goal

Receive messages in the AirCall Assist app while the app is in the background and optionally speak eligible messages aloud to the local user using Android Text-to-Speech. The external call remains independent.

## Core flow

Message sender -> backend/push -> AirCall Assist -> Call Assist policy -> TTS queue -> local audio output.

The MVP does **not** inject audio into WhatsApp, Telegram, Instagram, cellular calls, or other third-party call streams.

## Planned modules

- Android client
- Authentication
- 1-to-1 messaging
- Push delivery
- Call Assist
- TTS queue
- Trusted contacts
- Audio/Bluetooth preferences
- Backend API
- PostgreSQL data layer

## Repository status

Initial project foundation. The next implementation steps will add the Android client, backend contracts, and device-feasibility documentation.

## Engineering principles

- Use supported Android APIs.
- Do not intercept or record third-party calls.
- Do not scrape third-party messaging apps.
- Prefer local TTS for privacy and low latency.
- Make background behavior event-driven and battery-conscious.
- Document device/version limitations instead of hiding them.
