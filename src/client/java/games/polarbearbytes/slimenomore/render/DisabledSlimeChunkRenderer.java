package games.polarbearbytes.slimenomore.render;

import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.DepthTestFunction;
import com.mojang.blaze3d.vertex.VertexFormat;
import games.polarbearbytes.slimenomore.SlimeNoMore;
import games.polarbearbytes.slimenomore.SlimeNoMoreClient;
import games.polarbearbytes.slimenomore.config.SlimeNoMoreClientConfig;
import me.shedaniel.autoconfig.AutoConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.render.*;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.profiler.Profiler;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import static net.minecraft.client.gl.RenderPipelines.*;
import static net.minecraft.client.render.VertexFormats.POSITION_TEXTURE_COLOR;

public class DisabledSlimeChunkRenderer implements IRenderer {
    @Nullable protected BlockPos lastPosition;
    protected Vec3d lastCamera;
    private final RenderContext boxRenderContext;
    private final RenderContext billboardRenderContext;
    private final Color color;
    private final List<ChunkPos> disabledChunks = new ArrayList<>();

    private boolean needsUpdated;

    public static final Identifier BILLBOARD_TEXTURE = Identifier.of(SlimeNoMore.MOD_ID, "noslime.png");
    private AbstractTexture billboardTexture;

    //private static final BlendFunction BLENDER = new BlendFunction(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA, SourceFactor.ONE, DestFactor.ZERO);
    public static final RenderPipeline boxPipeline = RenderPipeline.builder(TRANSFORMS_AND_PROJECTION_SNIPPET)
            .withVertexShader("core/position_color")
            .withFragmentShader("core/position_color")
            .withBlend(BlendFunction.TRANSLUCENT)
            .withVertexFormat(VertexFormats.POSITION_COLOR, VertexFormat.DrawMode.TRIANGLE_STRIP)
            .withLocation(Identifier.of(SlimeNoMore.MOD_ID, "pipeline"))
            .withDepthBias(-3.0f, -3.0f)
            .withCull(false)
            .withDepthWrite(false)
            .withColorWrite(true)
            .withDepthTestFunction(DepthTestFunction.LEQUAL_DEPTH_TEST)
            .build();

    public static final RenderPipeline billboardPipeline = RenderPipeline.builder(POSITION_TEX_COLOR_SNIPPET)
            .withVertexShader("core/position_tex_color")
            .withFragmentShader("core/position_tex_color")
            .withBlend(BlendFunction.TRANSLUCENT)
            .withVertexFormat(POSITION_TEXTURE_COLOR, VertexFormat.DrawMode.QUADS)
            .withSampler("Sampler0")
            .withLocation(Identifier.of(SlimeNoMore.MOD_ID, "pipeline"))
            .withCull(false)
            .withDepthWrite(true)
            .withDepthTestFunction(DepthTestFunction.LEQUAL_DEPTH_TEST)
            .build();

    public static final DisabledSlimeChunkRenderer INSTANCE = new DisabledSlimeChunkRenderer(new ArrayList<>(), new Color(200, 0, 0, 128));

    private DisabledSlimeChunkRenderer(List<ChunkPos> disabledChunks, Color color) {
        assert MinecraftClient.getInstance().world != null;
        this.color = color;

        boxRenderContext = new RenderContext(()->SlimeNoMore.MOD_ID+"/DisabledChunks_TriangleStrip", DisabledSlimeChunkRenderer.boxPipeline);
        billboardRenderContext = new RenderContext(()->SlimeNoMore.MOD_ID+"/DisabledChunks_Billboards", DisabledSlimeChunkRenderer.billboardPipeline);

        this.setDisabledChunks(disabledChunks);
    }

    public void setDisabledChunks(List<ChunkPos> disabledChunks){
        this.disabledChunks.clear();
        this.disabledChunks.addAll(disabledChunks);
        this.needsUpdated = true;
    }

    @Override
    public void render(Framebuffer framebuffer, Matrix4f positionMatrix, Matrix4f projectionMatrix, MinecraftClient client, FrameGraphBuilder frameGraphBuilder, DefaultFramebufferSet fbSet, Frustum frustum, Camera camera, BufferBuilderStorage buffers, Profiler profiler){
        Entity cameraEntity = client.getCameraEntity();
        if(cameraEntity == null || client.player == null) return;

        //UPDATE
        update(camera.getCameraPos(), cameraEntity, client);
        //DRAW
        draw(camera.getCameraPos(), client);
    }

    public void update(Vec3d cameraPos, Entity entity, MinecraftClient client){
        //Only update if we are rendering or mesh data needs updated
        if(!shouldRender() || !shouldUpdate(entity)) return;
        lastPosition = BlockPos.ofFloored(Math.floor(entity.getX()), Math.floor(entity.getY()), Math.floor(entity.getZ()));

        build(cameraPos, client);
        TextureManager textureManager = MinecraftClient.getInstance().getTextureManager();

        billboardTexture = textureManager.getTexture(BILLBOARD_TEXTURE);
        //billboardTexture.setUseMipmaps(true);

        lastCamera = cameraPos;
    }

    public void draw(Vec3d cameraPos, MinecraftClient client) throws RuntimeException {
        SlimeNoMoreClientConfig config = AutoConfig.getConfigHolder(SlimeNoMoreClientConfig.class).getConfig();
        if(config.renderBox && boxRenderContext.isStarted() && boxRenderContext.isUploaded()) {
            boxRenderContext.draw(null, client, null, billboardTexture.getSampler());
        }
        if(config.renderBillboard && billboardRenderContext.isStarted() && billboardRenderContext.isUploaded()) {
            billboardRenderContext.draw(null, client, billboardTexture.getGlTextureView(),billboardTexture.getSampler());
        }
    }

    public boolean shouldRender(){
        return SlimeNoMoreClient.renderDisabledDisplay;
    }

    public boolean shouldUpdate(Entity entity){
        if(this.needsUpdated || this.lastPosition == null) return true;

        int ex = (int) Math.floor(entity.getX());
        int ez = (int) Math.floor(entity.getZ());
        int lx = this.lastPosition.getX();
        int lz = this.lastPosition.getZ();

        return Math.abs(lx - ex) > 16 || Math.abs(lz - ez) > 16;
    }


    public void build(Vec3d cameraPos, MinecraftClient client){
        BufferBuilder boxBuilder = boxRenderContext.init();
        BufferBuilder billboardBuilder = billboardRenderContext.init();
        MatrixStack matrices = new MatrixStack();
        matrices.push();

        for(ChunkPos chunkPos : this.disabledChunks) {
            buildBox(chunkPos, cameraPos, boxBuilder, client);
            buildBillboard(chunkPos, cameraPos, billboardBuilder, client);
        }
        try {
            BuiltBuffer boxMeshData = boxBuilder.endNullable();
            if (boxMeshData != null) {
                boxRenderContext.upload(boxMeshData);
                boxMeshData.close();
            }

            BuiltBuffer billboardMeshData = billboardBuilder.endNullable();
            if (billboardMeshData != null) {
                billboardRenderContext.upload(billboardMeshData);
                billboardMeshData.close();
            }
        } catch (Exception e) {
            SlimeNoMore.LOGGER.error("DisabledSlimeChunkRenderer#build Exception: {}", e.getMessage());
        }

        matrices.pop();
    }

    private void buildBox(ChunkPos chunkPos, Vec3d cameraPos, BufferBuilder builder, MinecraftClient client){
        assert client != null && client.world != null;
        float minX = chunkPos.x << 4;
        float minZ = chunkPos.z << 4;
        float maxX = minX + 16;
        float maxZ = minZ + 16;
        float minY = client.world.getBottomY();
        float maxY = client.world.getTopYInclusive();

        float x1 = (float)(minX - cameraPos.x);
        float y1 = (float)(minY - cameraPos.y);
        float z1 = (float)(minZ - cameraPos.z);
        float x2 = (float)(maxX - cameraPos.x);
        float y2 = (float)(maxY - cameraPos.y);
        float z2 = (float)(maxZ - cameraPos.z);

        drawFilledBox(new MatrixStack(), builder, x1, y1, z1, x2, y2, z2,
                (float) color.getRed() / 255, (float) color.getGreen() / 255, (float) color.getBlue() / 255, (float) color.getAlpha() / 255);
    }

    public static void drawFilledBox(MatrixStack matrices, VertexConsumer vertexConsumers, float minX, float minY, float minZ, float maxX, float maxY, float maxZ, float red, float green, float blue, float alpha) {
        Matrix4f matrix4f = matrices.peek().getPositionMatrix();
        vertexConsumers.vertex(matrix4f, minX, minY, minZ).color(red, green, blue, alpha);
        vertexConsumers.vertex(matrix4f, minX, minY, minZ).color(red, green, blue, alpha);
        vertexConsumers.vertex(matrix4f, minX, minY, minZ).color(red, green, blue, alpha);
        vertexConsumers.vertex(matrix4f, minX, minY, maxZ).color(red, green, blue, alpha);
        vertexConsumers.vertex(matrix4f, minX, maxY, minZ).color(red, green, blue, alpha);
        vertexConsumers.vertex(matrix4f, minX, maxY, maxZ).color(red, green, blue, alpha);
        vertexConsumers.vertex(matrix4f, minX, maxY, maxZ).color(red, green, blue, alpha);
        vertexConsumers.vertex(matrix4f, minX, minY, maxZ).color(red, green, blue, alpha);
        vertexConsumers.vertex(matrix4f, maxX, maxY, maxZ).color(red, green, blue, alpha);
        vertexConsumers.vertex(matrix4f, maxX, minY, maxZ).color(red, green, blue, alpha);
        vertexConsumers.vertex(matrix4f, maxX, minY, maxZ).color(red, green, blue, alpha);
        vertexConsumers.vertex(matrix4f, maxX, minY, minZ).color(red, green, blue, alpha);
        vertexConsumers.vertex(matrix4f, maxX, maxY, maxZ).color(red, green, blue, alpha);
        vertexConsumers.vertex(matrix4f, maxX, maxY, minZ).color(red, green, blue, alpha);
        vertexConsumers.vertex(matrix4f, maxX, maxY, minZ).color(red, green, blue, alpha);
        vertexConsumers.vertex(matrix4f, maxX, minY, minZ).color(red, green, blue, alpha);
        vertexConsumers.vertex(matrix4f, minX, maxY, minZ).color(red, green, blue, alpha);
        vertexConsumers.vertex(matrix4f, minX, minY, minZ).color(red, green, blue, alpha);
        vertexConsumers.vertex(matrix4f, minX, minY, minZ).color(red, green, blue, alpha);
        vertexConsumers.vertex(matrix4f, maxX, minY, minZ).color(red, green, blue, alpha);
        vertexConsumers.vertex(matrix4f, minX, minY, maxZ).color(red, green, blue, alpha);
        vertexConsumers.vertex(matrix4f, maxX, minY, maxZ).color(red, green, blue, alpha);
        vertexConsumers.vertex(matrix4f, maxX, minY, maxZ).color(red, green, blue, alpha);
        vertexConsumers.vertex(matrix4f, minX, maxY, minZ).color(red, green, blue, alpha);
        vertexConsumers.vertex(matrix4f, minX, maxY, minZ).color(red, green, blue, alpha);
        vertexConsumers.vertex(matrix4f, minX, maxY, maxZ).color(red, green, blue, alpha);
        vertexConsumers.vertex(matrix4f, maxX, maxY, minZ).color(red, green, blue, alpha);
        vertexConsumers.vertex(matrix4f, maxX, maxY, maxZ).color(red, green, blue, alpha);
        vertexConsumers.vertex(matrix4f, maxX, maxY, maxZ).color(red, green, blue, alpha);
        vertexConsumers.vertex(matrix4f, maxX, maxY, maxZ).color(red, green, blue, alpha);
    }

    private void buildBillboard(ChunkPos chunkPos, Vec3d cameraPos, BufferBuilder builder, MinecraftClient client){
        assert client != null && client.world != null && client.player != null;
        float minX = chunkPos.x << 4;
        float minZ = chunkPos.z << 4;
        float maxX = minX + 16;
        float maxZ = minZ + 16;

        MatrixStack billboardMatrices = new MatrixStack();
        billboardMatrices.translate(
                (minX + maxX) / 2.0 - cameraPos.x,
                client.player.getY() + 1f - cameraPos.y,
                (minZ + maxZ) / 2.0 - cameraPos.z
        );

        float yaw = client.player.getYaw();
        billboardMatrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-yaw));

        Matrix4f billboardMatrix = billboardMatrices.peek().getPositionMatrix();

        builder.vertex(billboardMatrix, -2, 2, 0).color(255, 255, 255, 128).texture(0, 0);
        builder.vertex(billboardMatrix, -2, -2, 0).color(255, 255, 255, 128).texture(0, 1);
        builder.vertex(billboardMatrix, 2, -2, 0).color(255, 255, 255, 128).texture(1, 1);
        builder.vertex(billboardMatrix, 2, 2, 0).color(255, 255, 255, 128).texture(1, 0);
    }
}
