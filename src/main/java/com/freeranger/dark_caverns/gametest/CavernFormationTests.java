package com.freeranger.dark_caverns.gametest;

import com.freeranger.dark_caverns.DarkCaverns;
import com.freeranger.dark_caverns.generation.CavernFormationConfiguration;
import com.freeranger.dark_caverns.registry.CustomBlocks;
import com.freeranger.dark_caverns.registry.CustomFeatures;
import java.awt.Color;
import java.awt.Font;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import javax.imageio.ImageIO;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

@GameTestHolder(DarkCaverns.MOD_ID)
@PrefixGameTestTemplate(false)
public final class CavernFormationTests {
    private CavernFormationTests() {}

    @GameTest(template = "sacred_torch")
    public static void formationsAttachScaleAndKeepClearance(GameTestHelper helper)
            throws IOException {
        BlockState material = CustomBlocks.CARFSTONE.get().defaultBlockState();
        String[] names = {"floor", "ceiling", "opposing", "column"};
        BufferedImage image = new BufferedImage(33 * 4, 100, BufferedImage.TYPE_INT_RGB);
        for (int mode = 0; mode < 4; mode++) {
            Room room = new Room(80, material);
            var config = config(material, mode);
            helper.assertTrue(place(helper, room, config), "Formation mode failed: " + names[mode]);
            int floorHeight = room.length(1);
            int ceilingHeight = room.length(-1);
            if (mode == 0)
                helper.assertTrue(
                        floorHeight >= 17 && ceilingHeight == 0,
                        "Floor spike must scale to a large gap");
            if (mode == 1)
                helper.assertTrue(
                        ceilingHeight >= 17 && floorHeight == 0,
                        "Stalactite must attach to the actual ceiling");
            if (mode == 2)
                helper.assertTrue(
                        floorHeight > 0
                                && ceilingHeight > 0
                                && 80 - floorHeight - ceilingHeight >= 3
                                && 80 - floorHeight - ceilingHeight <= 5,
                        "Opposing tips should retain 3–5 blocks of clearance");
            if (mode == 3)
                helper.assertTrue(
                        floorHeight == 80,
                        "A column must connect both boundaries without an air seam");
            room.assertAttached(helper);
            for (int x = -16; x <= 16; x++) {
                for (int y = 20; y < 120; y++) {
                    var block = room.get(new BlockPos(x, y, 0));
                    image.setRGB(mode * 33 + x + 16, 119 - y, block.isAir() ? 0x111827 : 0xa8a29e);
                }
            }
        }
        Room low = new Room(12, material);
        helper.assertTrue(place(helper, low, config(material, 0)), "Low cave cluster failed");
        helper.assertTrue(
                low.length(1) >= 2 && low.length(1) <= 5, "Low-cave formations should stay small");
        low.assertAttached(helper);
        Room tiny = new Room(6, material);
        helper.assertFalse(
                place(helper, tiny, config(material, 0)),
                "Small walking passages should not receive formations");
        Room repeated = new Room(12, material);
        place(helper, repeated, config(material, 0));
        helper.assertTrue(
                low.edits.equals(repeated.edits), "Formation shape must be deterministic");
        Path directory = Path.of("../build/reports/terrain");
        Files.createDirectories(directory);
        BufferedImage preview =
                new BufferedImage(
                        image.getWidth() * 3,
                        image.getHeight() * 3 + 24,
                        BufferedImage.TYPE_INT_RGB);
        var graphics = preview.createGraphics();
        graphics.setColor(new Color(0x111827));
        graphics.fillRect(0, 0, preview.getWidth(), preview.getHeight());
        graphics.drawImage(image, 0, 24, image.getWidth() * 3, image.getHeight() * 3, null);
        graphics.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
        graphics.setColor(new Color(0xe2e8f0));
        String[] labels = {"Stalagmite", "Stalactite", "Opposing", "Column"};
        for (int i = 0; i < labels.length; i++) graphics.drawString(labels[i], i * 99 + 10, 17);
        graphics.dispose();
        ImageIO.write(preview, "png", directory.resolve("formations.png").toFile());
        helper.succeed();
    }

    @GameTest(template = "sacred_torch")
    public static void formationsRespectFluidsStructuresAndWorldBounds(GameTestHelper helper) {
        var material = CustomBlocks.MOLTEN_CARFSTONE.get().defaultBlockState();
        Room room = new Room(80, material);
        Map<BlockPos, BlockState> protectedBlocks =
                Map.of(
                        new BlockPos(2, 28, 0),
                        Blocks.CHEST.defaultBlockState(),
                        new BlockPos(1, 27, 0),
                        Blocks.LAVA.defaultBlockState(),
                        new BlockPos(-1, 26, 0),
                        CustomBlocks.HELLSTONE_ORE.get().defaultBlockState(),
                        new BlockPos(0, 27, 1),
                        Blocks.BEDROCK.defaultBlockState());
        room.edits.putAll(protectedBlocks);
        helper.assertTrue(
                place(helper, room, config(material, 0)),
                "Formation should grow around protected blocks");
        protectedBlocks.forEach(
                (pos, state) ->
                        helper.assertTrue(
                                room.get(pos).equals(state),
                                "Formation overwrote protected block at " + pos));
        helper.assertTrue(
                room.edits.keySet().stream().allMatch(room::inside),
                "Formation escaped its writable region");
        Room unsupported = new Room(80, Blocks.BEDROCK.defaultBlockState());
        helper.assertFalse(
                place(helper, unsupported, config(material, 0)),
                "Bedrock cannot anchor a formation");
        Room flooded = new Room(80, material);
        flooded.edits.put(new BlockPos(0, 25, 0), Blocks.LAVA.defaultBlockState());
        helper.assertFalse(
                place(helper, flooded, config(material, 0)), "A fluid origin is not an air gap");
        Room bounded = new Room(80, material);
        bounded.writableRadius = 3;
        helper.assertTrue(
                place(helper, bounded, config(material, 3)),
                "Clipped formation should retain its supported core");
        helper.assertTrue(
                bounded.edits.keySet().stream().allMatch(bounded::inside),
                "Feature wrote into an unavailable neighboring chunk");
        helper.succeed();
    }

    private static CavernFormationConfiguration config(BlockState material, int mode) {
        return new CavernFormationConfiguration(
                material, 8, 64, 8, mode == 3 ? 1 : 0, mode == 2 ? 1 : 0, mode == 1 ? 1 : 0);
    }

    private static boolean place(
            GameTestHelper helper, Room room, CavernFormationConfiguration config) {
        var biome =
                helper.getLevel()
                        .registryAccess()
                        .registryOrThrow(Registries.BIOME)
                        .getHolderOrThrow(
                                ResourceKey.create(
                                        Registries.BIOME, DarkCaverns.id("rocky_caverns")));
        var world = TerrainTestWorld.create(room::get, room.edits::put, room::inside, biome, 91);
        return CustomFeatures.SPIKE
                .get()
                .place(
                        new FeaturePlaceContext<>(
                                Optional.empty(),
                                world,
                                helper.getLevel().getChunkSource().getGenerator(),
                                RandomSource.create(91),
                                new BlockPos(0, 25, 0),
                                config));
    }

    private static final class Room {
        final int gap;
        final BlockState material;
        int writableRadius = 12;
        final Map<BlockPos, BlockState> edits = new HashMap<>();

        Room(int gap, BlockState material) {
            this.gap = gap;
            this.material = material;
        }

        BlockState get(BlockPos pos) {
            return edits.getOrDefault(
                    pos,
                    pos.getY() <= 24 || pos.getY() >= 25 + gap
                            ? material
                            : Blocks.AIR.defaultBlockState());
        }

        boolean inside(BlockPos pos) {
            return Math.abs(pos.getX()) <= writableRadius
                    && Math.abs(pos.getZ()) <= writableRadius
                    && pos.getY() > 5
                    && pos.getY() < 248;
        }

        int length(int direction) {
            int length = 0;
            for (int y = direction == 1 ? 25 : 24 + gap; y >= 25 && y <= 24 + gap; y += direction) {
                if (get(new BlockPos(0, y, 0)).isAir()) break;
                length++;
            }
            return length;
        }

        void assertAttached(GameTestHelper helper) {
            for (BlockPos pos : edits.keySet()) {
                boolean down = true;
                boolean up = true;
                for (int y = pos.getY(); y >= 24; y--)
                    down &= !get(new BlockPos(pos.getX(), y, pos.getZ())).isAir();
                for (int y = pos.getY(); y <= 25 + gap; y++)
                    up &= !get(new BlockPos(pos.getX(), y, pos.getZ())).isAir();
                helper.assertTrue(down || up, "Floating formation at " + pos);
            }
        }
    }
}
