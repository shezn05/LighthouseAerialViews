# Ten live-info ideas for Lighthouse

Researched 2 November 2026. These are proposals, not implemented features. Each could be an
optional small overlay, refreshed only as often as its data changes.

| # | Idea | What the TV could show | Data and practical requirements |
| --- | --- | --- | --- |
| 1 | Tide countdown | Next high/low tide near Umhlanga and time remaining | Use a suitable Durban-area tide prediction source. [Open-Meteo marine](https://open-meteo.com/en/docs/marine-weather-api) provides modelled sea level including tides; coastal accuracy is limited, so it is not a navigation tool. The [SAN Hydrographic Office](https://sanho.co.za/) also publishes tide tables; an automated feed would need verification. |
| 2 | Surf and sea temperature | Wave height, swell period/direction and water temperature | [Open-Meteo marine](https://open-meteo.com/en/docs/marine-weather-api) has these fields. Useful as a coastal outlook; local beach conditions can differ. |
| 3 | Rain outlook | Rain chance and likely wet hours ahead | [Open-Meteo forecast](https://open-meteo.com/en/docs) provides hourly precipitation probability. Do not present hourly model output as a minute-accurate rain countdown. |
| 4 | UV level | Current/forecast UV level and today's peak | [Open-Meteo forecast](https://open-meteo.com/en/docs) includes UV variables. A compact colour label could keep this readable. |
| 5 | Air quality | Estimated AQI, PM2.5 or dust | [Open-Meteo air quality](https://open-meteo.com/en/docs/air-quality-api) provides model estimates. Its pollen fields are Europe-only, so those should not be promised for Umhlanga. |
| 6 | Moon phase | Moon phase, illumination and next full moon | The [US Naval Observatory API](https://aa.usno.navy.mil/data/api) documents moon-phase and illumination services. Daily refresh is enough. |
| 7 | Visible ISS passes | Next predicted naked-eye pass over Umhlanga | [N2YO visual passes](https://www.n2yo.com/api/) supports observer coordinates. Requires an API key; visibility is a prediction and clouds can obscure it. |
| 8 | Home power status | Grid online/offline, inverter battery or solar output | [Home Assistant sensors](https://www.home-assistant.io/integrations/sensor/) can expose compatible devices. Requires the user's own sensors/integration; no home access has been configured. The existing message API could receive a compact status. |
| 9 | Traffic-aware travel time | Time to work or King Shaka Airport | [Google Routes](https://developers.google.com/maps/documentation/routes) supports travel times and traffic. [Billing and credentials](https://developers.google.com/maps/documentation/routes/usage-and-billing) are required; refresh conservatively and use a spending limit if selected. |
| 10 | Next calendar event | Next appointment and a countdown | [Home Assistant calendar](https://www.home-assistant.io/integrations/calendar/) can read event information from a supported calendar integration and send it through the existing message API. Requires the user's chosen calendar connection. |

The easiest first additions are the marine outlook, UV and rain outlook: they fit the coastal theme
and can share the existing weather infrastructure. Avoid filling all corners with information;
let the user select a small number of overlays.

Open-Meteo's free API is for [non-commercial use](https://open-meteo.com/en/terms), with CC BY 4.0
data attribution. No accounts, paid services or new integrations were created for this research.
