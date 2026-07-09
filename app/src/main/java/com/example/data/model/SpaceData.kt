package com.example.data.model

import com.example.data.database.SavedItem
import com.example.data.database.SavedArticle

data class DiscoveryItem(
    val id: String,
    val category: String, // "planet", "star", "galaxy", "blackhole", "nebula", "mission", "satellite", "astronaut"
    val name: String,
    val shortDescription: String,
    val detailedDescription: String,
    val imageUrl: String, // Unsplash image url
    val properties: Map<String, String> = emptyMap() // e.g., "Distance" to "1.5 AU", "Mass" to "0.1 Earths"
) {
    fun toSavedItem() = SavedItem(
        id = id,
        category = category,
        name = name,
        description = shortDescription,
        detailText = detailedDescription,
        imageResName = imageUrl
    )
}

data class NewsArticle(
    val id: String,
    val title: String,
    val summary: String,
    val publishedDate: String,
    val source: String,
    val content: String,
    val imageUrl: String
) {
    fun toSavedArticle() = SavedArticle(
        id = id,
        title = title,
        summary = summary,
        publishedDate = publishedDate,
        source = source,
        content = content,
        imageUrl = imageUrl
    )
}

object SpaceData {
    val articles = listOf(
        NewsArticle(
            id = "news_1",
            title = "James Webb Telescope Discovers Atmospheres on Trappist-1 Exoplanets",
            summary = "In a historic breakthrough, the JWST has detected signs of carbon dioxide and water vapor in the atmospheres of planets surrounding the Trappist-1 system.",
            publishedDate = "July 8, 2026",
            source = "NASA Goddard",
            content = "Astronomers utilizing the James Webb Space Telescope have published robust spectral data pointing to atmospheric structures on Trappist-1d and Trappist-1e. These rocky worlds sit squarely within the star's habitable zone, making them premier targets for biosignature analysis. Using transmissions spectroscopy, researchers detected atmospheric carbon dioxide concentrations similar to ancient Earth, representing a major milestone in exoplanetary discovery.",
            imageUrl = "https://images.unsplash.com/photo-1610296669228-602fa827fc1f?auto=format&fit=crop&q=80&w=600"
        ),
        NewsArticle(
            id = "news_2",
            title = "Artemis IV Astronauts Preparing for Long-Duration Basecamp Habitat Deployment",
            summary = "NASA and international partners have finalized plans to send four astronauts to the Lunar South Pole for 45 days, laying foundations for sustainable exploration.",
            publishedDate = "June 30, 2026",
            source = "ESA / NASA",
            content = "The Artemis IV crew is in intense training at Johnson Space Center for the upcoming deployment of the Lunar Foundation Habitat. The mission will touch down in the permanently shadowed Shackleton Crater, where harvesting lunar water-ice is expected to yield raw hydrogen and oxygen for propellant and survival. The mission aims to establish continuous human presence on the lunar surface.",
            imageUrl = "https://images.unsplash.com/photo-1446776811953-b23d57bd21aa?auto=format&fit=crop&q=80&w=600"
        ),
        NewsArticle(
            id = "news_3",
            title = "Massive Solar Flare of Class X9.4 Triggers Spectacular Global Auroras",
            summary = "A supercharged solar storm originating from sunspot region AR3890 has showered Earth's magnetosphere, creating aurora borealis visible as far south as Texas.",
            publishedDate = "June 15, 2026",
            source = "NOAA Space Weather Center",
            content = "Earth's geomagnetic monitors recorded a massive Class G4 storm caused by an active coronal mass ejection (CME). The solar storm interacted with Earth's magnetosphere, painting the night skies with majestic shades of green, violet, and deep red. While high-frequency satellite communications experienced brief telemetry blackouts, the visual spectacle was hailed as the most intense and widespread aurora event in over half a century.",
            imageUrl = "https://images.unsplash.com/photo-1579783900882-c0d3dad7b119?auto=format&fit=crop&q=80&w=600"
        ),
        NewsArticle(
            id = "news_4",
            title = "Event Horizon Telescope Unveils Magnetic Fields Surrounding Sgr A*",
            summary = "In a stunning new high-resolution polarized view, the EHT shows the powerful magnetic spirals weaving near our galaxy's supermassive black hole.",
            publishedDate = "May 22, 2026",
            source = "Event Horizon Collaboration",
            content = "The Event Horizon Telescope has captured an unprecedented view of the supermassive black hole Sagittarius A* at the center of the Milky Way in polarized light. The lines represent the direction of strong magnetic fields swirling around the black hole's boundary. Astronomers believe these magnetic spirals are responsible for shooting high-energy jet streams of ionized material deep into intergalactic space.",
            imageUrl = "https://images.unsplash.com/photo-1506318137071-a8e063b4bec0?auto=format&fit=crop&q=80&w=600"
        )
    )

    val discoveries = listOf(
        // --- PLANETS ---
        DiscoveryItem(
            id = "mars",
            category = "planet",
            name = "Mars",
            shortDescription = "The iron-rich Red Planet, home to deep canyons and towering volcanoes.",
            detailedDescription = "Mars is the fourth planet from the Sun and has long captivated human imagination. Characterized by its signature reddish tint, which is caused by iron oxide (rust) on its dusty surface, Mars hosts some of the most dramatic landscapes in the solar system. Olympus Mons is the tallest volcano known to science, and Valles Marineris is a canyon network that would span the entire United States. Mars possesses polar ice caps, ancient dried riverbeds, and evidence of subsurface liquid water, making it the primary objective for human colonization and the search for ancient microbial biosignatures.",
            imageUrl = "https://images.unsplash.com/photo-1614728894747-a83421e2b9c9?auto=format&fit=crop&q=80&w=600",
            properties = mapOf(
                "Distance from Sun" to "1.52 AU (227.9M km)",
                "Year Duration" to "687 Earth Days",
                "Gravity" to "3.71 m/s² (0.38g)",
                "Temperature Range" to "-140°C to 20°C",
                "Moons" to "2 (Phobos, Deimos)"
            )
        ),
        DiscoveryItem(
            id = "jupiter",
            category = "planet",
            name = "Jupiter",
            shortDescription = "The colossal gas giant, twice as massive as all other planets combined.",
            detailedDescription = "Jupiter is the undisputed king of the solar system, a colossal gas giant primarily made of hydrogen and helium. Its marbled outer atmosphere is a turbulent masterpiece of colorful cloud belts, high-speed winds, and intense vortex storms. The most famous is the Great Red Spot—a massive hurricane larger than Earth itself that has been raging for centuries. Lacking a defined solid surface, Jupiter has a heavy, metallic core surrounded by an ocean of liquid metallic hydrogen. It commands an incredibly strong magnetic field and hosts a massive orbital system of 95 moons, including Europa with its subsurface oceans.",
            imageUrl = "https://images.unsplash.com/photo-1614313913007-2b4ae8ce32d6?auto=format&fit=crop&q=80&w=600",
            properties = mapOf(
                "Distance from Sun" to "5.20 AU (778.5M km)",
                "Year Duration" to "11.8 Earth Years",
                "Gravity" to "24.79 m/s² (2.53g)",
                "Diameter" to "139,820 km",
                "Moons" to "95 (Io, Europa, Ganymede...)"
            )
        ),
        DiscoveryItem(
            id = "saturn",
            category = "planet",
            name = "Saturn",
            shortDescription = "The ringed wonder, a beautiful gas giant with a vast ring system of ice and rock.",
            detailedDescription = "Saturn is the sixth planet from the sun and is renowned for its majestic, expansive planetary ring system. These rings are composed of billions of pieces of ice, silicate rock, and cosmic dust, ranging from microscopic grains to mountain-sized chunks. Despite its immense size, Saturn is the least dense planet in the solar system; it is mostly composed of hydrogen and helium and would actually float in a giant bathtub. Its golden-hued atmosphere contains ultra-fast winds reaching 1,800 km/h. Its largest moon, Titan, is the only moon in the solar system with a thick atmosphere and liquid hydrocarbon lakes.",
            imageUrl = "https://images.unsplash.com/photo-1614314107204-b81606258a6a?auto=format&fit=crop&q=80&w=600",
            properties = mapOf(
                "Distance from Sun" to "9.58 AU (1.43B km)",
                "Year Duration" to "29.4 Earth Years",
                "Gravity" to "10.44 m/s² (1.06g)",
                "Ring Span" to "282,000 km",
                "Moons" to "146 (Titan, Enceladus...)"
            )
        ),

        // --- STARS ---
        DiscoveryItem(
            id = "betelgeuse",
            category = "star",
            name = "Betelgeuse",
            shortDescription = "A colossal red supergiant star on the brink of a spectacular supernova.",
            detailedDescription = "Betelgeuse is a massive red supergiant star located roughly 640 light-years away in the constellation Orion. It is one of the largest stars visible to the naked eye. If placed at the center of our solar system, its outer surface would engulf Mercury, Venus, Earth, Mars, and expand past Jupiter's orbit. Betelgeuse is nearing the end of its stellar lifecycle. It has exhausted its hydrogen fuel and is currently fusing heavier elements, causing it to pulsate and fluctuate in brightness. Astronomers estimate it will collapse in a cataclysmic supernova explosion anytime within the next 100,000 years, shining brighter than the full moon for months.",
            imageUrl = "https://images.unsplash.com/photo-1518531933037-91b2f5f229cc?auto=format&fit=crop&q=80&w=600",
            properties = mapOf(
                "Distance" to "640 Light Years",
                "Star Class" to "M1-M2 Supergiant",
                "Radius" to "Approx. 764-900 Solar Radii",
                "Mass" to "15-20 Solar Masses",
                "Luminosity" to "120,000x Sun"
            )
        ),
        DiscoveryItem(
            id = "sirius",
            category = "star",
            name = "Sirius (The Dog Star)",
            shortDescription = "The brightest star in the night sky, a beautiful blue-white binary system.",
            detailedDescription = "Sirius, colloquially known as the 'Dog Star' due to its position in Canis Major, is the brightest star in the night sky. Situated only 8.6 light-years away, it is one of Earth's closest stellar neighbors. Sirius is actually a binary star system. Sirius A is a main-sequence star twice as massive as the Sun and emits a radiant white-blue glow. Sirius B, its companion, was once a massive star that collapsed into a tiny, incredibly dense white dwarf, about the physical size of Earth but holding the mass of the Sun. This pairing creates a fascinating cosmic dance observed since ancient civilizations.",
            imageUrl = "https://images.unsplash.com/photo-1506703719100-a0f3a48c0f86?auto=format&fit=crop&q=80&w=600",
            properties = mapOf(
                "Distance" to "8.6 Light Years",
                "Star Class" to "A1V Main Sequence (A) / DA2 (B)",
                "Mass" to "2.06x Sun",
                "Surface Temp" to "9,940 K",
                "Visual Magnitude" to "-1.46"
            )
        ),

        // --- GALAXIES ---
        DiscoveryItem(
            id = "andromeda",
            category = "galaxy",
            name = "Andromeda Galaxy (M31)",
            shortDescription = "Our majestic spiral neighbor, racing toward a merger with the Milky Way.",
            detailedDescription = "The Andromeda Galaxy is a grand design spiral galaxy located 2.5 million light-years from Earth. It is the closest major galaxy to our Milky Way and the largest member of the Local Group. Spanning an astronomical 220,000 light-years, Andromeda contains an estimated one trillion stars—more than double the count of our own galaxy. On a clear night in dark skies, it is the most distant object visible to the naked human eye. Driven by mutual gravitational attraction, Andromeda and the Milky Way are hurtling toward each other, set to clash and merge into a spectacular elliptical supergalaxy in 4.5 billion years.",
            imageUrl = "https://images.unsplash.com/photo-1543722530-d2c3201371e7?auto=format&fit=crop&q=80&w=600",
            properties = mapOf(
                "Distance" to "2.5M Light Years",
                "Diameter" to "220,000 Light Years",
                "Star Population" to "1 Trillion",
                "Galaxy Type" to "SA(s)b Spiral",
                "Velocity" to "300 km/s (Approaching)"
            )
        ),
        DiscoveryItem(
            id = "sombrero",
            category = "galaxy",
            name = "Sombrero Galaxy (M104)",
            shortDescription = "A striking galaxy with a prominent dust ring, resembling a Mexican hat.",
            detailedDescription = "The Sombrero Galaxy is a brilliant unbarred spiral galaxy located in the constellation Virgo, roughly 28 million light-years away. It has a bright, massive central bulge and a striking, dark dust lane traversing its outer disk, giving it the appearance of a celestial Sombrero. This galaxy is highly active, hosting an incredibly rich population of nearly 2,000 globular clusters (ten times more than the Milky Way) and a supermassive black hole at its core weighing one billion solar masses. It is a favorite target of both amateur astronomers and space observatories due to its sharp symmetry.",
            imageUrl = "https://images.unsplash.com/photo-1462331940025-496dfbfc7564?auto=format&fit=crop&q=80&w=600",
            properties = mapOf(
                "Distance" to "28M Light Years",
                "Diameter" to "50,000 Light Years",
                "Constellation" to "Virgo",
                "Bulge Mass" to "1 Billion Solar Masses",
                "Visual Magnitude" to "+8.0"
            )
        ),

        // --- BLACK HOLES ---
        DiscoveryItem(
            id = "sagitarius_a",
            category = "blackhole",
            name = "Sagittarius A*",
            shortDescription = "The supermassive heart of our Milky Way galaxy.",
            detailedDescription = "Sagittarius A* (Sgr A*) is the supermassive black hole located at the absolute dynamic core of our Milky Way galaxy. Discovered by tracking the fast, tight orbits of nearby 'S-stars', Sgr A* has a mass equal to 4 million suns compressed into a sphere no larger than the orbit of Mercury. In May 2022, the Event Horizon Telescope published the first direct visual capture of Sgr A*'s shadow—revealing a ring of superheated gas and plasma glowing under intense gravity and magnetic field forces. It remains relatively dormant, consuming small quantities of interstellar gas, but acts as the gravitational anchor for our cosmic home.",
            imageUrl = "https://images.unsplash.com/photo-1451187580459-43490279c0fa?auto=format&fit=crop&q=80&w=600",
            properties = mapOf(
                "Mass" to "4.15M Solar Masses",
                "Radius" to "12M km (0.08 AU)",
                "Distance from Earth" to "26,673 Light Years",
                "Constellation" to "Sagittarius",
                "Detection Method" to "Stellar Orbit Tracking"
            )
        ),

        // --- NEBULAS ---
        DiscoveryItem(
            id = "orion_nebula",
            category = "nebula",
            name = "Orion Nebula (M42)",
            shortDescription = "A vast interstellar nursery spawning thousands of newborn stars.",
            detailedDescription = "The Orion Nebula is a massive, diffuse nebula hanging below Orion's Belt in the night sky. Situated 1,342 light-years away, it is the closest region of major star formation to Earth, offering a window into how planetary systems are born. This stellar nursery is sculpted by intense radiation streams from the Trapezium Cluster—a group of hot, massive young stars at its heart. The nebula's striking red, green, and purple hues are created by ionized hydrogen and oxygen gas glowing under stellar winds. Within its dust columns, planetary disks (proplyds) are condensing, paving the way for future worlds.",
            imageUrl = "https://images.unsplash.com/photo-1502134249126-9f3755a50d78?auto=format&fit=crop&q=80&w=600",
            properties = mapOf(
                "Distance" to "1,342 Light Years",
                "Diameter" to "24 Light Years",
                "Constellation" to "Orion",
                "Gas Composition" to "90% Hydrogen, 9% Helium",
                "Star Count" to "Over 3,000"
            )
        ),

        // --- MISSIONS ---
        DiscoveryItem(
            id = "voyager_1",
            category = "mission",
            name = "Voyager 1",
            shortDescription = "Humanity's most distant ambassador, cruising through deep interstellar space.",
            detailedDescription = "Launched by NASA in September 1977, Voyager 1 is a legendary space probe designed to conduct close flybys of Jupiter and Saturn. After completing its primary planetary tour, its trajectory carried it beyond the solar wind's reach. In August 2012, Voyager 1 became the first human-made object to cross the Heliopause and enter interstellar space. Carrying the famous Golden Record—a time capsule of Earth's sounds, languages, and music—Voyager 1 continues to beam back valuable data about the cosmic ray density and magnetic conditions outside our solar system, traveling at a staggering 61,000 km/h.",
            imageUrl = "https://images.unsplash.com/photo-1541185933-ef5d8ed016c2?auto=format&fit=crop&q=80&w=600",
            properties = mapOf(
                "Launch Date" to "Sept 5, 1977",
                "Current Distance" to "Approx. 163 AU (24.4B km)",
                "Status" to "Active (Interstellar Space)",
                "Data Transmission Speed" to "160 bits per second",
                "Power Source" to "RTG (Plutonium-238)"
            )
        ),

        // --- SATELLITES ---
        DiscoveryItem(
            id = "iss",
            category = "satellite",
            name = "International Space Station",
            shortDescription = "A microgravity research laboratory flying 400 km above Earth.",
            detailedDescription = "The International Space Station (ISS) is a state-of-the-art modular space station in Low Earth Orbit. A joint project between NASA, Roscosmos, ESA, JAXA, and CSA, it has been continuously occupied since November 2000. Operating as a microgravity and space environment research laboratory, the station has hosted hundreds of scientific experiments in biology, physics, meteorology, and astrobiology. Spanning the length of a football field, the ISS circles Earth every 92.9 minutes at a speed of 28,000 km/h, representing one of the greatest feats of international engineering and cooperative space exploration.",
            imageUrl = "https://images.unsplash.com/photo-1454789548928-9efd52dc4031?auto=format&fit=crop&q=80&w=600",
            properties = mapOf(
                "Altitude" to "Approx. 400-420 km",
                "Speed" to "27,600 km/h",
                "Orbits per Day" to "15.5 Orbits",
                "Dimensions" to "109m x 73m",
                "Crew Capacity" to "7-10 Astronauts"
            )
        ),

        // --- ASTRONAUTS ---
        DiscoveryItem(
            id = "neil_armstrong",
            category = "astronaut",
            name = "Neil Armstrong",
            shortDescription = "The first human to set foot on another celestial body.",
            detailedDescription = "Neil Alden Armstrong was an American astronaut and aeronautical engineer who served as the commander of the historic Apollo 11 lunar landing mission in July 1969. On July 21, 1969, he became the first person to walk on the Moon, famously declaring: 'That's one small step for [a] man, one giant leap for mankind.' A former Navy fighter pilot and test pilot, Armstrong's cool composure under extreme stress was crucial during the descent of the lunar module Eagle, which he manually steered away from boulder-strewn craters with only seconds of fuel remaining, cementing his place in human history.",
            imageUrl = "https://images.unsplash.com/photo-1534447677768-be436bb09401?auto=format&fit=crop&q=80&w=600",
            properties = mapOf(
                "Lunar Landing Date" to "July 20, 1969",
                "Apollo Mission" to "Apollo 11",
                "Time on Lunar Surface" to "2 Hours, 31 Minutes",
                "Birthplace" to "Wapakoneta, Ohio, USA",
                "Life Span" to "1930 - 2012"
            )
        )
    )
}
