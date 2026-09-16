import React from 'react';
import { Navbar } from '../components/Navbar';
import { Footer } from '../components/Footer';
import { getTextureUrl } from '../utils/assets';
import { PixelIcon } from '../components/PixelIcon';

export const HomePage: React.FC = () => {
  return (
    <div className="min-h-screen bg-[#121316] text-[#d6dae5] font-sans flex flex-col antialiased">
      <Navbar currentPath="/" />

      <main className="flex-1 max-w-6xl w-full mx-auto px-4 sm:px-6 py-8 space-y-10">
        {/* Hero Section */}
        <section className="mc-box p-6 sm:p-8 space-y-5">
          <h1 className="font-pixel text-3xl sm:text-5xl text-white mc-shadow font-bold">
            Dark Caverns
          </h1>
          <p className="text-sm sm:text-base text-[#a8afc0] max-w-2xl leading-relaxed">
            A cave dimension beneath bedrock with new biomes, specialized gear, and subterranean mobs for Minecraft 1.21.1 NeoForge.
          </p>

          <div className="flex flex-wrap items-center gap-3 pt-2">
            <a
              href="./download/"
              className="mc-btn mc-btn-luminite text-sm sm:text-base px-5 py-2.5 inline-flex items-center gap-2"
            >
              <PixelIcon name="download" className="w-4 h-4" />
              <span>Download for 1.21.1</span>
            </a>
            <a
              href="./guides/"
              className="mc-btn text-sm sm:text-base px-5 py-2.5"
            >
              Getting started guide
            </a>
            <a
              href="./wiki/"
              className="mc-btn text-sm sm:text-base px-5 py-2.5"
            >
              Browse wiki
            </a>
          </div>
        </section>

        {/* Official Trailer */}
        <section className="mc-box p-1 sm:p-1.5 overflow-hidden">
          <div className="aspect-video w-full bg-black">
            <iframe
              className="w-full h-full block"
              src="https://www.youtube-nocookie.com/embed/Z3q_B4iXvOw?rel=0"
              title="Dark Caverns Trailer"
              loading="lazy"
              allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture"
              allowFullScreen
            />
          </div>
        </section>

        {/* Core Mod Features */}
        <section className="space-y-6">
          <h2 className="font-pixel text-2xl sm:text-3xl text-white mc-shadow">
            What the mod adds
          </h2>

          <div className="space-y-8">
            {/* Feature 1: The Gateway Progression */}
            <div className="space-y-3">
              <div className="flex items-center justify-between gap-3 border-b border-[#232630] pb-2.5">
                <div>
                  <h3 className="font-pixel text-lg sm:text-xl text-white">
                    Bedrock gateway
                  </h3>
                  <p className="text-xs sm:text-sm text-[#8e95a8] mt-0.5">
                    Path from the Overworld into the caverns
                  </p>
                </div>
                <a
                  href="./guides/?guide=getting-to-the-caverns"
                  className="mc-btn mc-btn-icon"
                  aria-label="Open gateway guide"
                >
                  <PixelIcon name="arrow-right" className="w-4 h-4" />
                </a>
              </div>

              <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-3">
                {[
                  {
                    step: 'Step 1',
                    title: 'Buy the map',
                    desc: 'Trade with a cartographer for the explorer map.',
                    texture: 'textures/item/map.png',
                    alt: 'Explorer Map'
                  },
                  {
                    step: 'Step 2',
                    title: 'Claim the key',
                    desc: 'Defeat Illagers inside the forest tower.',
                    texture: 'textures/item/key_to_the_caverns.png',
                    alt: 'Key to the Caverns'
                  },
                  {
                    step: 'Step 3',
                    title: 'Locate bedrock',
                    desc: 'Find cracked bedrock at the bottom of the Overworld.',
                    texture: 'textures/block/cracked_bedrock.png',
                    alt: 'Cracked Bedrock'
                  },
                  {
                    step: 'Step 4',
                    title: 'Ignite gateway',
                    desc: 'Use the key to activate the gateway and jump in!',
                    texture: 'textures/block/gateway_to_the_caverns.png',
                    alt: 'Gateway to the Caverns',
                    highlight: true
                  }
                ].map((item) => (
                  <div
                    key={item.step}
                    className="mc-box p-4 flex flex-col justify-between space-y-3"
                  >
                    <div className="flex items-center justify-between gap-2">
                      <span className="font-pixel text-xs text-[#8e95a8]">
                        {item.step}
                      </span>
                      <div className="mc-slot shrink-0">
                        <img
                          src={getTextureUrl(item.texture)}
                          alt={item.alt}
                          className="w-6 h-6 pixel-art object-cover object-top"
                        />
                      </div>
                    </div>
                    <div>
                      <h4 className={`font-pixel text-sm sm:text-base ${item.highlight ? 'text-[#55ffaf]' : 'text-white'}`}>
                        {item.title}
                      </h4>
                      <p className="text-xs sm:text-sm text-[#b0b6c6] leading-relaxed mt-1">
                        {item.desc}
                      </p>
                    </div>
                  </div>
                ))}
              </div>
            </div>

            {/* Feature 2: Cavern Biomes */}
            <div className="space-y-3">
              <div className="flex items-center justify-between gap-3 border-b border-[#232630] pb-2.5">
                <div>
                  <h3 className="font-pixel text-lg sm:text-xl text-white">
                    Cavern biomes
                  </h3>
                  <p className="text-xs sm:text-sm text-[#8e95a8] mt-0.5">
                    Cave ecosystems generated below bedrock
                  </p>
                </div>
                <a
                  href="./wiki/?category=biomes"
                  className="mc-btn mc-btn-icon"
                  aria-label="Open biomes wiki"
                >
                  <PixelIcon name="arrow-right" className="w-4 h-4" />
                </a>
              </div>

              <div className="grid sm:grid-cols-2 gap-3">
                {[
                  {
                    name: 'Rocky Caverns',
                    color: 'text-white',
                    texture: 'textures/block/carfstone.png',
                    desc: 'Dense stone caverns with stalactites, natural bridges, and platinum ore.'
                  },
                  {
                    name: 'Molten Depths',
                    color: 'text-[#ff9970]',
                    texture: 'textures/block/molten_carfstone.png',
                    desc: 'Volcanic caves with lava flows, magma shelves, and Hellstone deposits.'
                  },
                  {
                    name: 'Glimmershroom Forest',
                    color: 'text-[#7dd3fc]',
                    texture: 'textures/block/glimmershroom.png',
                    desc: 'Luminescent mushroom caves with Shroomie traders and giant fungi.'
                  },
                  {
                    name: 'Tangled Hallow',
                    color: 'text-[#55ffaf]',
                    texture: 'textures/block/twistwood_log.png',
                    desc: 'Underground Twistwood groves with teal cavern lakes and ambient tracks.'
                  }
                ].map((biome) => (
                  <div
                    key={biome.name}
                    className="mc-box p-4 flex items-start gap-3.5"
                  >
                    <div className="mc-slot mc-slot-output shrink-0">
                      <img
                        src={getTextureUrl(biome.texture)}
                        alt={biome.name}
                        className="w-8 h-8 pixel-art"
                      />
                    </div>
                    <div className="space-y-1 flex-1 min-w-0">
                      <h4 className={`font-pixel text-sm sm:text-base ${biome.color}`}>
                        {biome.name}
                      </h4>
                      <p className="text-xs sm:text-sm text-[#b0b6c6] leading-relaxed">
                        {biome.desc}
                      </p>
                    </div>
                  </div>
                ))}
              </div>
            </div>

            {/* Feature 3: Equipment Progression */}
            <div className="space-y-3">
              <div className="flex items-center justify-between gap-3 border-b border-[#232630] pb-2.5">
                <div>
                  <h3 className="font-pixel text-lg sm:text-xl text-white">
                    Equipment progression
                  </h3>
                  <p className="text-xs sm:text-sm text-[#8e95a8] mt-0.5">
                    Post-diamond gear forged at the smithing table
                  </p>
                </div>
                <a
                  href="./guides/?guide=smithing-and-gear-progression"
                  className="mc-btn mc-btn-icon"
                  aria-label="Open smithing guide"
                >
                  <PixelIcon name="arrow-right" className="w-4 h-4" />
                </a>
              </div>

              <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-3">
                {[
                  {
                    name: 'Platinum',
                    titleColor: 'text-white',
                    texture: 'textures/item/platinum_sword.png',
                    desc: 'Stats between Diamond and Netherite, with high enchantability.'
                  },
                  {
                    name: 'Hellstone',
                    titleColor: 'text-[#ff9970]',
                    texture: 'textures/item/hellstone_sword.png',
                    desc: 'Full set grants fire and lava immunity. Attacks ignite targets.'
                  },
                  {
                    name: 'Shroomstone',
                    titleColor: 'text-[#c084fc]',
                    texture: 'textures/item/shroomstone_sword.png',
                    desc: 'Full set negates fall damage with Jump Boost II. Strikes launch enemies.'
                  },
                  {
                    name: 'Scorchsteel',
                    titleColor: 'text-[#55ffaf]',
                    texture: 'textures/item/scorchsteel_chestplate.png',
                    desc: 'Standing still for one second conceals you from hostile mobs.'
                  }
                ].map((tier) => (
                  <div
                    key={tier.name}
                    className="mc-box p-4 flex flex-col justify-between space-y-3"
                  >
                    <div className="mc-slot shrink-0 self-start">
                      <img
                        src={getTextureUrl(tier.texture)}
                        alt={tier.name}
                        className="w-6 h-6 pixel-art"
                      />
                    </div>
                    <div>
                      <h4 className={`font-pixel text-sm sm:text-base ${tier.titleColor}`}>
                        {tier.name}
                      </h4>
                      <p className="text-xs sm:text-sm text-[#b0b6c6] leading-relaxed mt-1">
                        {tier.desc}
                      </p>
                    </div>
                  </div>
                ))}
              </div>
            </div>

            {/* Feature 4: Cavern Utilities */}
            <div className="space-y-3">
              <div className="flex items-center justify-between gap-3 border-b border-[#232630] pb-2.5">
                <div>
                  <h3 className="font-pixel text-lg sm:text-xl text-white">
                    Cavern utilities
                  </h3>
                  <p className="text-xs sm:text-sm text-[#8e95a8] mt-0.5">
                    Tools for subterranean navigation, lighting, and combat
                  </p>
                </div>
                <a
                  href="./wiki/?category=items"
                  className="mc-btn mc-btn-icon"
                  aria-label="Open items wiki"
                >
                  <PixelIcon name="arrow-right" className="w-4 h-4" />
                </a>
              </div>

              <div className="grid sm:grid-cols-2 gap-3">
                {[
                  {
                    name: 'Cavern Compass',
                    texture: 'textures/item/cavern_compass.png',
                    desc: 'Points directly back to your nearest Bedrock Gateway.'
                  },
                  {
                    name: 'Throwable Luminite Torch',
                    texture: 'textures/item/throwable_luminite_torch.png',
                    desc: 'Thrown like a snowball to place torches on distant walls and ceilings.'
                  },
                  {
                    name: 'Corrupted Pearl',
                    texture: 'textures/item/corrupted_pearl.png',
                    desc: 'Teleports the nearest hostile mob to where the pearl lands.'
                  },
                  {
                    name: 'Shroombomb',
                    texture: 'textures/item/shroombomb.png',
                    desc: 'Blasts stone and deepslate without destroying exposed ore blocks.'
                  }
                ].map((tool) => (
                  <div
                    key={tool.name}
                    className="mc-box p-4 flex items-start gap-3.5"
                  >
                    <div className="mc-slot shrink-0">
                      <img
                        src={getTextureUrl(tool.texture)}
                        alt={tool.name}
                        className="w-6 h-6 pixel-art"
                      />
                    </div>
                    <div className="space-y-1 flex-1 min-w-0">
                      <h4 className="font-pixel text-sm sm:text-base text-white">
                        {tool.name}
                      </h4>
                      <p className="text-xs sm:text-sm text-[#b0b6c6] leading-relaxed">
                        {tool.desc}
                      </p>
                    </div>
                  </div>
                ))}
              </div>
            </div>
          </div>
        </section>
      </main>

      <Footer currentPath="/" />
    </div>
  );
};
