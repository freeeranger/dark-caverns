package com.freeranger.dark_caverns.client;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.RisingParticle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;

public final class LuminiteFlameParticle extends RisingParticle {
    private LuminiteFlameParticle(
            ClientLevel level,
            double x,
            double y,
            double z,
            double xSpeed,
            double ySpeed,
            double zSpeed
    ) {
        super(level, x, y, z, xSpeed, ySpeed, zSpeed);
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
    }

    @Override
    public void move(double x, double y, double z) {
        setBoundingBox(getBoundingBox().move(x, y, z));
        setLocationFromBoundingbox();
    }

    @Override
    public float getQuadSize(float partialTick) {
        float progress = ((float) age + partialTick) / (float) lifetime;
        return quadSize * (1.0F - progress * progress * 0.5F);
    }

    @Override
    public int getLightColor(float partialTick) {
        float progress = Mth.clamp(((float) age + partialTick) / (float) lifetime, 0.0F, 1.0F);
        int packedLight = super.getLightColor(partialTick);
        int blockLight = Math.min(240, (packedLight & 0xFF) + (int) (progress * 240.0F));
        int skyLight = packedLight >> 16 & 0xFF;
        return blockLight | skyLight << 16;
    }

    public static final class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;

        public Provider(SpriteSet sprites) {
            this.sprites = sprites;
        }

        @Override
        public Particle createParticle(
                SimpleParticleType type,
                ClientLevel level,
                double x,
                double y,
                double z,
                double xSpeed,
                double ySpeed,
                double zSpeed
        ) {
            LuminiteFlameParticle particle = new LuminiteFlameParticle(
                    level, x, y, z, xSpeed, ySpeed, zSpeed
            );
            particle.pickSprite(sprites);
            return particle;
        }
    }
}
