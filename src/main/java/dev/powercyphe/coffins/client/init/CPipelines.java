package dev.powercyphe.coffins.client.init;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.renderpearl.api.pipeline.*;
import dev.powercyphe.coffins.common.Coffins;
import net.minecraft.client.renderer.BindGroupLayouts;

import static net.minecraft.client.renderer.RenderPipelines.*;

public interface CPipelines {

    RenderPipeline PING_PARTICLE = register(RenderPipeline.builder(GLOBALS_SNIPPET)
            .withLocation(Coffins.id("pipeline/ping_particle"))
            .withBindGroupLayout(BindGroupLayouts.PROJECTION)
            .withBindGroupLayout(BindGroupLayouts.DYNAMIC_TRANSFORMS)
            .withBindGroupLayout(BindGroupLayouts.SAMPLER0)
            .withVertexShader("core/position_tex_color")
            .withFragmentShader("core/position_tex_color")
            .withColorTargetState(new ColorTargetState(BlendFunction.TRANSLUCENT))
            .withVertexBinding(0, DefaultVertexFormat.PARTICLE)
            .withPrimitiveTopology(PrimitiveTopology.QUADS)
            .withDepthStencilState(new DepthStencilState(CompareOp.ALWAYS_PASS, false))
            .withCull(false)
            .build());

    RenderPipeline GUI_TEXTURED_WHITE = register(RenderPipeline.builder(GUI_TEXTURED_SNIPPET)
            .withLocation(Coffins.id("pipeline/gui_textured_white"))
            .withFragmentShader(Coffins.id("core/position_tex_white"))
            .build());

    static void init() {}

}
