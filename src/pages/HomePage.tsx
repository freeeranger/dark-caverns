import React from 'react';
import { Navbar } from '../components/Navbar';
import { Footer } from '../components/Footer';
import { PixelIcon } from '../components/PixelIcon';
import { TextureImage } from '../components/TextureImage';
import { TrailerEmbed } from '../components/TrailerEmbed';

const GATEWAY_STEPS = [
  {
    step: 'Step 1',
    title: 'Buy the map',
    description: 'Trade with an Expert Cartographer for the Forgotten Tower explorer map.',
    texture: 'textures/item/map.png'
  },
  {
    step: 'Step 2',
    title: 'Claim the key',
    description: 'Defeat the Illagers and take the Key to the Caverns from the top chest.',
    texture: 'textures/item/key_to_the_caverns.png'
  },
  {
    step: 'Step 3',
    title: 'Find cracked bedrock',
    description: 'Mine to the Overworld floor and locate Cracked Bedrock in the bottom layer.',
    texture: 'textures/block/cracked_bedrock.png'
  },
  {
    step: 'Step 4',
    title: 'Open the gateway',
    description: 'Use the key on Cracked Bedrock, then step onto the permanent gateway.',
    texture: 'textures/block/gateway_to_the_caverns.png'
  }
];

const BIOMES = [
  {
    name: 'Rocky Caverns',
    color: 'text-white',
    texture: 'textures/block/carfstone.png',
    description: 'Layered Carfstone chambers, natural bridges, exposed Luminite, and towering formations.'
  },
  {
    name: 'Molten Depths',
    color: 'text-[#ff9970]',
    texture: 'textures/block/molten_carfstone.png',
    description: 'Lava springs, ash patches, magma, Hellstone, and hostile Scorchlings.'
  },
  {
    name: 'Glimmershroom Forest',
    color: 'text-[#7dd3fc]',
    texture: 'textures/block/glimmershroom.png',
    description: 'Blue fungal light, giant glimmershrooms, Shroomie traders, and neutral Shroomlings.'
  },
  {
    name: 'Tangled Hallow',
    color: 'text-[#55ffaf]',
    texture: 'textures/block/twistwood_log.png',
    description: 'Dense Twistwood, teal surface lakes, Water Sproutlets, and a renewable forest floor.'
  }
];

const GEAR = [
  {
    name: 'Platinum',
    color: 'text-white',
    texture: 'textures/item/platinum_sword.png',
    description: 'Fast post-Diamond tools with 20 enchantability.'
  },
  {
    name: 'Hellstone',
    color: 'text-[#ff9970]',
    texture: 'textures/item/hellstone_sword.png',
    description: 'A full armor set blocks fire and lava damage.'
  },
  {
    name: 'Shroomstone',
    color: 'text-[#c084fc]',
    texture: 'textures/item/shroomstone_sword.png',
    description: 'A full set grants Jump Boost II and blocks fall damage.'
  },
  {
    name: 'Scorchsteel',
    color: 'text-[#55ffaf]',
    texture: 'textures/item/scorchsteel_chestplate.png',
    description: 'A full set conceals you from monsters while you stand still.'
  }
];

const UTILITIES = [
  {
    name: 'Luminite Helmet',
    texture: 'textures/item/luminite_helmet.png',
    description: 'Creates client-side dynamic light around its wearer.'
  },
  {
    name: 'Throwable Luminite Torch',
    texture: 'textures/item/throwable_luminite_torch.png',
    description: 'Places torches on distant walls and ledges.'
  },
  {
    name: 'Corrupted Pearl',
    texture: 'textures/item/corrupted_pearl.png',
    description: 'Moves the nearest non-player creature to the landing point.'
  },
  {
    name: 'Shroombomb',
    texture: 'textures/item/shroombomb.png',
    description: 'Creates a small configurable explosion on impact.'
  }
];

interface SectionHeadingProps {
  id: string;
  title: string;
  description: string;
  href: string;
  linkLabel: string;
}

const SectionHeading: React.FC<SectionHeadingProps> = ({ id, title, description, href, linkLabel }) => (
  <div className="section-heading">
    <div className="min-w-0">
      <h2 id={id} className="font-pixel text-2xl text-white mc-shadow sm:text-3xl">{title}</h2>
      <p className="mt-1 max-w-2xl text-sm text-[#a0a7ba]">{description}</p>
    </div>
    <a href={href} className="mc-btn section-heading-action" aria-label={linkLabel}>
      <span>{linkLabel}</span>
      <PixelIcon name="arrow-right" className="h-4 w-4" />
    </a>
  </div>
);

const GatewayJourney: React.FC = () => (
  <section className="space-y-5" aria-labelledby="gateway-heading">
    <SectionHeading
      id="gateway-heading"
      title="Break through bedrock"
      description="The route into the dimension begins with an explorer map and ends at a permanent gateway."
      href="./guides/?guide=getting-to-the-caverns"
      linkLabel="Gateway guide"
    />
    <ol className="gateway-journey mc-box">
      {GATEWAY_STEPS.map((item) => (
        <li key={item.step} className="gateway-step">
          <div className="flex items-center justify-between gap-3">
            <span className="font-pixel text-xs text-[#8e95a8]">{item.step}</span>
            <span className="mc-slot shrink-0">
              <TextureImage
                texture={item.texture}
                alt={item.title}
                className="h-6 w-6 pixel-art"
                width={24}
                height={24}
              />
            </span>
          </div>
          <h3 className="font-pixel text-base text-white">{item.title}</h3>
          <p className="text-sm text-[#aeb5c5]">{item.description}</p>
        </li>
      ))}
    </ol>
  </section>
);

const BiomeOverview: React.FC = () => (
  <section className="space-y-5" aria-labelledby="biomes-heading">
    <SectionHeading
      id="biomes-heading"
      title="Four biomes in one dimension"
      description="The cave floor changes from bare Carfstone to molten ash, fungal light, and dense underground forest."
      href="./wiki/?category=biomes"
      linkLabel="Biome reference"
    />
    <div className="biome-list mc-box">
      {BIOMES.map((biome) => (
        <article key={biome.name} className="biome-row">
          <span className="mc-slot mc-slot-output shrink-0">
            <TextureImage
              texture={biome.texture}
              alt={biome.name}
              className="h-8 w-8 pixel-art"
              width={32}
              height={32}
            />
          </span>
          <div className="min-w-0">
            <h3 className={`font-pixel text-base sm:text-lg ${biome.color}`}>{biome.name}</h3>
            <p className="mt-1 text-sm text-[#aeb5c5]">{biome.description}</p>
          </div>
        </article>
      ))}
    </div>
  </section>
);

const EquipmentPath: React.FC = () => (
  <section className="space-y-5" aria-labelledby="equipment-heading">
    <SectionHeading
      id="equipment-heading"
      title="Choose what your armor solves"
      description="Luminite Dust carries Diamond equipment into Platinum, then into specialized cavern gear."
      href="./guides/?guide=smithing-and-gear-progression"
      linkLabel="Smithing guide"
    />
    <div className="equipment-path mc-box">
      {GEAR.map((tier, index) => (
        <article key={tier.name} className="equipment-tier">
          <div className="flex items-center gap-3">
            <span className="mc-slot shrink-0">
              <TextureImage
                texture={tier.texture}
                alt={tier.name}
                className="h-6 w-6 pixel-art"
                width={24}
                height={24}
              />
            </span>
            <div className="min-w-0">
              <div className="flex items-center gap-2">
                <span className="font-mono text-[11px] text-[#747c90]">{index + 1}</span>
                <h3 className={`font-pixel text-base ${tier.color}`}>{tier.name}</h3>
              </div>
              <p className="mt-1 text-sm text-[#aeb5c5]">{tier.description}</p>
            </div>
          </div>
        </article>
      ))}
    </div>
  </section>
);

const UtilityLoadout: React.FC = () => (
  <section className="space-y-5" aria-labelledby="utilities-heading">
    <SectionHeading
      id="utilities-heading"
      title="Tools made for cavern travel"
      description="Carry light farther, move creatures, and clear space without leaving the dimension."
      href="./wiki/?category=items"
      linkLabel="Item reference"
    />
    <div className="utility-list">
      {UTILITIES.map((item) => (
        <article key={item.name} className="utility-row">
          <span className="mc-slot shrink-0">
            <TextureImage
              texture={item.texture}
              alt={item.name}
              className="h-6 w-6 pixel-art"
              width={24}
              height={24}
            />
          </span>
          <div className="min-w-0">
            <h3 className="font-pixel text-sm text-white sm:text-base">{item.name}</h3>
            <p className="mt-1 text-sm text-[#aeb5c5]">{item.description}</p>
          </div>
        </article>
      ))}
    </div>
  </section>
);

const HomeActions: React.FC = () => (
  <div className="home-actions">
    <a href="./download/" className="mc-btn mc-btn-luminite min-h-11 min-w-0 gap-2 px-3 text-xs sm:px-5 sm:text-sm">
      <PixelIcon name="download" className="h-4 w-4" />
      <span>Download</span>
    </a>
    <a href="./guides/" className="mc-btn min-h-11 min-w-0 px-3 text-xs sm:px-5 sm:text-sm">
      Browse guides
    </a>
  </div>
);

export const HomePage: React.FC = () => {
  return (
    <div className="min-h-screen bg-[#121316] text-[#d6dae5] font-sans flex flex-col antialiased">
      <Navbar currentPath="/" />

      <main className="flex-1 max-w-6xl w-full mx-auto px-4 sm:px-6 py-6 sm:py-10 space-y-14 sm:space-y-20">
        <section className="home-hero mc-box" aria-labelledby="home-title">
          <div className="home-hero-copy">
            <h1 id="home-title" className="font-pixel text-4xl text-white mc-shadow font-bold sm:text-6xl">
              Dark Caverns
            </h1>
            <p className="max-w-xl text-base leading-relaxed text-[#bbc2d1] sm:text-lg">
              Descend beneath bedrock into a cave dimension with four biomes, specialized equipment, native creatures, and its own route home.
            </p>
            <ul className="compatibility-line" aria-label="Supported game configuration">
              <li>Minecraft 1.21.1</li>
              <li>NeoForge</li>
              <li>GeckoLib required</li>
            </ul>
            <HomeActions />
          </div>

          <div className="home-hero-media">
            <img
              src="https://media.forgecdn.net/attachments/383/120/2021-07-26_09.png"
              alt="A wide view across jagged Dark Caverns formations with glowing ore in the rock"
              width="1920"
              height="1080"
              loading="eager"
              fetchPriority="high"
            />
          </div>
        </section>

        <section className="trailer-section" aria-labelledby="trailer-heading">
          <div className="max-w-md space-y-3">
            <h2 id="trailer-heading" className="font-pixel text-2xl text-white mc-shadow sm:text-3xl">
              See the dimension in motion
            </h2>
            <p className="text-sm text-[#a0a7ba] sm:text-base">
              The trailer shows the gateway, cavern scale, biomes, creatures, and equipment progression.
            </p>
          </div>
          <div className="mc-box aspect-video min-w-0 overflow-hidden p-1 sm:p-1.5">
            <TrailerEmbed />
          </div>
        </section>

        <GatewayJourney />
        <BiomeOverview />
        <EquipmentPath />
        <UtilityLoadout />

        <section className="home-final-cta mc-box" aria-labelledby="final-cta-heading">
          <h2 id="final-cta-heading" className="font-pixel text-2xl text-white mc-shadow sm:text-3xl">
            Ready to explore?
          </h2>
          <HomeActions />
        </section>
      </main>

      <Footer currentPath="/" />
    </div>
  );
};
