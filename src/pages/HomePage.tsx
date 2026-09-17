import React from 'react';
import { Navbar } from '../components/Navbar';
import { Footer } from '../components/Footer';
import { Button } from '../components/Button';
import { TextureImage } from '../components/TextureImage';
import { TrailerEmbed } from '../components/TrailerEmbed';
import { LINKS } from '../utils/constants';

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
    description: 'Rocky Caverns has layered Carfstone chambers, stone bridges, exposed Luminite, and tall formations.'
  },
  {
    name: 'Molten Depths',
    color: 'text-[#ff9970]',
    texture: 'textures/block/molten_carfstone.png',
    description: 'Molten Depths has lava springs, ash patches, magma, Hellstone, and hostile Scorchlings.'
  },
  {
    name: 'Glimmershroom Forest',
    color: 'text-[#7dd3fc]',
    texture: 'textures/block/glimmershroom.png',
    description: 'Giant glimmershrooms cast blue light over Shroomie traders and neutral Shroomlings.'
  },
  {
    name: 'Tangled Hallow',
    color: 'text-[#55ffaf]',
    texture: 'textures/block/twistwood_log.png',
    description: 'Dense Twistwood Logs and Twistwood Leaves surround teal surface lakes covered with Water Sproutlets.'
  }
];

const GEAR = [
  {
    name: 'Platinum',
    color: 'text-white',
    texture: 'textures/item/platinum_sword.png',
    description: 'Platinum tools mine faster than Netherite tools and have 20 enchantability.'
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
    description: 'The helmet creates client-side dynamic light around its wearer.'
  },
  {
    name: 'Throwable Luminite Torch',
    texture: 'textures/item/throwable_luminite_torch.png',
    description: 'The throwable torch places light on distant walls and ledges.'
  },
  {
    name: 'Corrupted Pearl',
    texture: 'textures/item/corrupted_pearl.png',
    description: 'The pearl moves the nearest non-player creature to its landing point.'
  },
  {
    name: 'Shroombomb',
    texture: 'textures/item/shroombomb.png',
    description: 'The bomb creates a small configurable explosion on impact.'
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
  <div className="flex flex-col items-start gap-4 sm:flex-row sm:items-end sm:justify-between">
    <div className="min-w-0">
      <h2 id={id} className="font-pixel text-2xl text-white mc-shadow sm:text-3xl">{title}</h2>
      <p className="mt-1 max-w-2xl text-sm text-[#a0a7ba]">{description}</p>
    </div>
    <Button
      href={href}
      size="sm"
      className="shrink-0"
      aria-label={linkLabel}
      icon="arrow-right"
      iconPosition="right"
    >
      {linkLabel}
    </Button>
  </div>
);

const GatewayJourney: React.FC = () => (
  <section className="space-y-5" aria-labelledby="gateway-heading">
    <SectionHeading
      id="gateway-heading"
      title="Break through bedrock"
      description="An Expert Cartographer sells the Forgotten Tower map. The tower holds the key that opens Cracked Bedrock."
      href="./guides/?guide=getting-to-the-caverns"
      linkLabel="Gateway guide"
    />
    <ol className="mc-box grid grid-cols-1 overflow-hidden sm:grid-cols-2 lg:grid-cols-4 [content-visibility:auto] [contain-intrinsic-size:1px_420px] sm:[contain-intrinsic-size:1px_280px]">
      {GATEWAY_STEPS.map((item) => (
        <li
          key={item.step}
          className="flex min-w-0 flex-col gap-3 p-5 border-b border-[#2a2c34] last:border-b-0 sm:border-b sm:[&:nth-child(odd)]:border-r sm:[&:nth-last-child(-n+2)]:border-b-0 lg:border-b-0 lg:border-r lg:last:border-r-0"
        >
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
          <h3 className="font-pixel text-base text-white">
            {item.title}
          </h3>
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
      description="Each biome has its own stone, plants, water, ore, and creatures."
      href="./wiki/?category=biomes"
      linkLabel="Biome reference"
    />
    <div className="mc-box overflow-hidden sm:grid sm:grid-cols-2 [content-visibility:auto] [contain-intrinsic-size:1px_420px] sm:[contain-intrinsic-size:1px_280px]">
      {BIOMES.map((biome) => (
        <article
          key={biome.name}
          className="flex min-w-0 gap-4 p-5 border-b border-[#2a2c34] last:border-b-0 sm:border-b sm:odd:border-r sm:nth-last-[-n+2]:border-b-0"
        >
          <span className="mc-slot shrink-0">
            <TextureImage
              texture={biome.texture}
              alt={biome.name}
              className="h-8 w-8 pixel-art"
              width={32}
              height={32}
            />
          </span>
          <div className="min-w-0">
            <h3 className={`font-pixel text-base sm:text-lg ${biome.color}`}>
              {biome.name}
            </h3>
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
      title="Choose what your gear solves"
      description="Use Luminite Dust at a Smithing Table to upgrade Diamond gear to Platinum and other cavern gear."
      href="./guides/?guide=smithing-and-gear-progression"
      linkLabel="Smithing guide"
    />
    <div className="mc-box grid grid-cols-1 overflow-hidden sm:grid-cols-2 lg:grid-cols-4 [content-visibility:auto] [contain-intrinsic-size:1px_420px] sm:[contain-intrinsic-size:1px_280px]">
      {GEAR.map((tier) => (
        <article
          key={tier.name}
          className="grid grid-cols-[44px_minmax(0,1fr)] content-start items-start gap-4 min-w-0 p-5 border-b border-[#2a2c34] last:border-b-0 sm:border-b sm:[&:nth-child(odd)]:border-r sm:[&:nth-last-child(-n+2)]:border-b-0 lg:border-b-0 lg:border-r lg:last:border-r-0"
        >
          <span className="mc-slot shrink-0">
            <TextureImage
              texture={tier.texture}
              alt={tier.name}
              className="h-6 w-6 pixel-art"
              width={24}
              height={24}
            />
          </span>
          <div className="min-w-0 pt-0.5">
            <h3 className={`font-pixel text-base leading-tight ${tier.color}`}>
              {tier.name}
            </h3>
            <p className="mt-2 text-sm text-[#aeb5c5]">{tier.description}</p>
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
      description="These items light distant ledges, teleport creatures, and create small explosions."
      href="./wiki/?category=items"
      linkLabel="Item reference"
    />
    <div className="grid grid-cols-1 sm:grid-cols-2 gap-3 [content-visibility:auto] [contain-intrinsic-size:1px_420px] sm:[contain-intrinsic-size:1px_280px]">
      {UTILITIES.map((item) => (
        <article key={item.name} className="flex min-w-0 gap-4 p-4 bg-[#17181c] border border-[#2a2c34]">
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
            <h3 className="font-pixel text-sm text-white sm:text-base">
              {item.name}
            </h3>
            <p className="mt-1 text-sm text-[#aeb5c5]">{item.description}</p>
          </div>
        </article>
      ))}
    </div>
  </section>
);

const HomeActions: React.FC = () => (
  <div className="grid w-full max-w-[24rem] grid-cols-2 gap-3">
    <Button
      href="./download/"
      variant="luminite"
      className="min-w-0 px-3 sm:px-5"
      icon="download"
    >
      Download
    </Button>
    <Button
      href={LINKS.discord}
      className="min-w-0 px-3 sm:px-5"
      aria-label="Join Discord, opens in a new tab"
      icon="discord"
    >
      Join Discord
    </Button>
  </div>
);

export const HomePage: React.FC = () => {
  return (
    <div className="min-h-screen bg-[#121316] text-[#d6dae5] font-sans flex flex-col antialiased">
      <Navbar currentPath="/" />

      <main className="flex-1 max-w-6xl w-full mx-auto px-4 sm:px-6 py-6 sm:py-10 space-y-14 sm:space-y-20">
        <section className="mc-box grid overflow-hidden md:grid-cols-[minmax(0,0.85fr)_minmax(24rem,1.15fr)]" aria-labelledby="home-title">
          <div className="flex flex-col justify-center gap-5 p-6 sm:p-9">
            <h1 id="home-title" className="font-pixel text-4xl text-white mc-shadow font-bold sm:text-6xl">
              Dark Caverns
            </h1>
            <p className="max-w-xl text-base leading-relaxed text-[#bbc2d1] sm:text-lg">
              Dark Caverns adds four cave biomes below the Overworld. Its gateway links both dimensions, and the caves contain new gear and creatures.
            </p>
            <HomeActions />
          </div>

          <div className="min-w-0 overflow-hidden aspect-video md:aspect-auto bg-[#0c0d10] border-t-2 md:border-t-0 md:border-l-2 border-black">
            <img
              src="https://media.forgecdn.net/attachments/383/120/2021-07-26_09.png"
              alt="A wide view across jagged Dark Caverns formations with glowing ore in the rock"
              className="block w-full h-full object-cover object-[center_58%]"
              width="1920"
              height="1080"
              loading="eager"
              fetchPriority="high"
            />
          </div>
        </section>

        <section className="grid md:grid-cols-[minmax(0,0.65fr)_minmax(24rem,1.35fr)] items-center gap-6 md:gap-8" aria-labelledby="trailer-heading">
          <div className="max-w-md space-y-3">
            <h2 id="trailer-heading" className="font-pixel text-2xl text-white mc-shadow sm:text-3xl">
              Watch the trailer
            </h2>
            <p className="text-sm text-[#a0a7ba] sm:text-base">
              See the gateway, each biome, the native creatures, and the gear system.
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

        <section className="mc-box flex flex-col sm:p-8 md:flex-row items-start md:items-center justify-between gap-6 p-6 border-[#0c433f] shadow-[inset_2px_2px_0_#1e4e49,inset_-2px_-2px_0_#0c0d0f]" aria-labelledby="final-cta-heading">
          <h2 id="final-cta-heading" className="font-pixel text-2xl text-white mc-shadow sm:text-3xl">
            Enter the Dark Caverns
          </h2>
          <HomeActions />
        </section>
      </main>

      <Footer currentPath="/" />
    </div>
  );
};
